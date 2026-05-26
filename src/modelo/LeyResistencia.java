package modelo;

public class LeyResistencia implements LeyCombinacion {
    @Override
    public double combinarSerie(double a, double b) {
        return a + b;
    }
    @Override
    public double combinarParalelo(double a, double b) {
        return 1.0 / (1.0/a + 1.0/b);
    }
}