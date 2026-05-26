package modelo;

public class Inductor extends ComponentePasivo {
    public Inductor(double henrios) {
        super(henrios, new LeyInductor());
    }

    @Override
    public String getTipo() {
        return "L";
    }
}