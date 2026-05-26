package modelo;

public class ResistenciaSimple extends Circuito {
    private ComponentePasivo componente;

    public ResistenciaSimple(ComponentePasivo componente) {
        this.componente = componente;
    }

    @Override
    public double calcularEquivalente(LeyCombinacion ley) {
        return componente.getValor();
    }

    @Override
    public String obtenerDescripcion() {
        return componente.toString();
    }
}