package modelo;

import java.util.List;
import java.util.stream.Collectors;

public class CircuitoSerie extends Circuito {
    private List<Circuito> componentes;

    public CircuitoSerie(List<Circuito> componentes) {
        this.componentes = componentes;
    }

    @Override
    public double calcularEquivalente(LeyCombinacion ley) {
        double total = 0;
        for (Circuito c : componentes) {
            total += c.calcularEquivalente(ley);
        }
        return total;
    }

    @Override
    public String obtenerDescripcion() {
        return "(" + componentes.stream().map(Circuito::obtenerDescripcion).collect(Collectors.joining(" + ")) + ") en serie";
    }
}