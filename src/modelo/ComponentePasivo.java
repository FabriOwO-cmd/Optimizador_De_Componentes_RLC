package modelo;

public abstract class ComponentePasivo {
    protected double valor;
    protected LeyCombinacion ley;

    public ComponentePasivo(double valor, LeyCombinacion ley) {
        this.valor = valor;
        this.ley = ley;
    }

    public double getValor() {
        return valor;
    }

    public LeyCombinacion getLey() {
        return ley;
    }

    // Método auxiliar para identificar tipo (usado en GUI)
    public abstract String getTipo();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ComponentePasivo that = (ComponentePasivo) obj;
        return Double.compare(that.valor, valor) == 0 && getTipo().equals(that.getTipo());
    }

    @Override
    public int hashCode() {
        return 31 * getTipo().hashCode() + Double.hashCode(valor);
    }

    @Override
    public String toString() {
        return getTipo() + ":" + valor;
    }
}