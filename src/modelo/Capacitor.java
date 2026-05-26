package modelo;

public class Capacitor extends ComponentePasivo {
    public Capacitor(double faradios) {
        super(faradios, new LeyCapacitor());
    }

    @Override
    public String getTipo() {
        return "C";
    }
}