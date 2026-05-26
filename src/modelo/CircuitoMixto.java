package modelo;

public class CircuitoMixto extends Circuito {
    private Circuito izquierdo;
    private Circuito derecho;
    private boolean esSerie; // true = serie, false = paralelo

    public CircuitoMixto(Circuito izq, Circuito der, boolean esSerie) {
        this.izquierdo = izq;
        this.derecho = der;
        this.esSerie = esSerie;
    }

    @Override
    public double calcularEquivalente(LeyCombinacion ley) {
        if (esSerie) {
            return izquierdo.calcularEquivalente(ley) + derecho.calcularEquivalente(ley);
        } else {
            double inv = 1.0/izquierdo.calcularEquivalente(ley) + 1.0/derecho.calcularEquivalente(ley);
            return 1.0 / inv;
        }
    }

    @Override
    public String obtenerDescripcion() {
        String conector = esSerie ? " en serie con " : " en paralelo con ";
        return "(" + izquierdo.obtenerDescripcion() + conector + derecho.obtenerDescripcion() + ")";
    }
}