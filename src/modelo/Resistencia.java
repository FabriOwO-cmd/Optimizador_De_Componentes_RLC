package modelo;

public class Resistencia extends ComponentePasivo {
    public Resistencia(double ohmios) {
        super(ohmios, new LeyResistencia());
    }

    @Override
    public String getTipo() {
        return "R";
    }
}