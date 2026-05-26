package modelo;

import java.util.List;
import java.util.stream.Collectors;

public class CircuitoParalelo extends Circuito {
    private List<Circuito> componentes;

    public CircuitoParalelo(List<Circuito> componentes) {
        this.componentes = componentes;
    }

    @Override
    public double calcularEquivalente(LeyCombinacion ley) {
        double sumaInversas = 0;
        for (Circuito c : componentes) {
            sumaInversas += 1.0 / c.calcularEquivalente(ley);
        }
        return 1.0 / sumaInversas;
    }

    @Override
    public String obtenerDescripcion() {
        return "(" + componentes.stream().map(Circuito::obtenerDescripcion).collect(Collectors.joining(" || ")) + ") en paralelo";
    }
}