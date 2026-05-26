package modelo;

public abstract class Circuito {
    public abstract double calcularEquivalente(LeyCombinacion ley);
    public abstract String obtenerDescripcion();

    @Override
    public String toString() {
        return String.format("%.4f", calcularEquivalente(null)) + " Ohmios (depende del tipo)";
        // Nota: el cálculo real requiere pasar la ley, pero aquí es solo para depuración.
    }
}