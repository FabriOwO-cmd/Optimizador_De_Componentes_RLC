package control;

import modelo.*;
import java.util.*;

public class CalculadorCombinaciones {
    private List<Circuito> mejorCircuito;
    private double mejorError;
    private int mejorCantidad;
    private double tolerancia; // en valor absoluto (ohmios, henrios, faradios)

    public Circuito mejorCircuito(Inventario inv, double deseado, double toleranciaPorcentaje, LeyCombinacion ley, int maxComponentes) {
        this.tolerancia = deseado * toleranciaPorcentaje / 100.0;
        this.mejorCircuito = null;
        this.mejorError = Double.MAX_VALUE;
        this.mejorCantidad = Integer.MAX_VALUE;

        Map<ComponentePasivo, Integer> disponibles = inv.obtenerComponentesDisponibles();
        List<ComponentePasivo> listaComp = new ArrayList<>();
        for (Map.Entry<ComponentePasivo, Integer> entry : disponibles.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                listaComp.add(entry.getKey());
            }
        }

        // Búsqueda recursiva de todas las combinaciones serie/paralelo con hasta maxComponentes
        buscar(listaComp, new ArrayList<>(), 0, deseado, ley, maxComponentes);

        if (mejorCircuito == null) return null;
        // Construir el circuito final a partir de la lista de componentes simples
        if (mejorCircuito.size() == 1) return mejorCircuito.get(0);
        // Si hay más de uno, combinarlos en serie (como simplificación, se puede mejorar)
        return new CircuitoSerie(mejorCircuito);
    }

    private void buscar(List<ComponentePasivo> disponibles, List<Circuito> actuales, int inicio, double deseado, LeyCombinacion ley, int maxComp) {
        if (actuales.size() > maxComp) return;

        // Evaluar circuito actual (serie de los actuales)
        if (!actuales.isEmpty()) {
            Circuito serie = new CircuitoSerie(new ArrayList<>(actuales));
            double valor = serie.calcularEquivalente(ley);
            double error = Math.abs(valor - deseado);
            int cantidad = actuales.size();
            if (error <= tolerancia) {
                if (error < mejorError - 1e-9 || (Math.abs(error - mejorError) < 1e-9 && cantidad < mejorCantidad)) {
                    mejorError = error;
                    mejorCantidad = cantidad;
                    mejorCircuito = new ArrayList<>(actuales);
                }
            }
        }

        // También evaluar combinaciones en paralelo de los actuales
        if (actuales.size() >= 2) {
            Circuito paralelo = new CircuitoParalelo(new ArrayList<>(actuales));
            double valor = paralelo.calcularEquivalente(ley);
            double error = Math.abs(valor - deseado);
            int cantidad = actuales.size();
            if (error <= tolerancia) {
                if (error < mejorError - 1e-9 || (Math.abs(error - mejorError) < 1e-9 && cantidad < mejorCantidad)) {
                    mejorError = error;
                    mejorCantidad = cantidad;
                    mejorCircuito = new ArrayList<>(actuales);
                }
            }
        }

        // Explorar añadiendo una resistencia más
        for (int i = inicio; i < disponibles.size(); i++) {
            ComponentePasivo comp = disponibles.get(i);
            actuales.add(new ResistenciaSimple(comp));
            buscar(disponibles, actuales, i + 1, deseado, ley, maxComp);
            actuales.remove(actuales.size() - 1);
        }
    }
}