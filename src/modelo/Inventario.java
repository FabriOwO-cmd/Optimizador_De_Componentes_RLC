package modelo;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Inventario {
    private Map<String, Integer> stock; // clave: "R:1000" -> cantidad

    public Inventario() {
        stock = new HashMap<>();
    }

    public void agregar(ComponentePasivo c, int cantidad) {
        String clave = c.toString();
        stock.put(clave, stock.getOrDefault(clave, 0) + cantidad);
    }

    public void eliminar(ComponentePasivo c, int cantidad) {
        String clave = c.toString();
        int actual = stock.getOrDefault(clave, 0);
        if (actual <= cantidad) {
            stock.remove(clave);
        } else {
            stock.put(clave, actual - cantidad);
        }
    }

    public int getCantidad(ComponentePasivo c) {
        return stock.getOrDefault(c.toString(), 0);
    }

    public Map<String, Integer> getStock() {
        return new HashMap<>(stock);
    }

    // Devuelve una lista de componentes (cada uno con cantidad 1) para poder iterar en combinaciones
    public Map<ComponentePasivo, Integer> obtenerComponentesDisponibles() {
        Map<ComponentePasivo, Integer> disponibles = new HashMap<>();
        for (Map.Entry<String, Integer> entry : stock.entrySet()) {
            String[] partes = entry.getKey().split(":");
            String tipo = partes[0];
            double valor = Double.parseDouble(partes[1]);
            ComponentePasivo comp = null;
            switch (tipo) {
                case "R": comp = new Resistencia(valor); break;
                case "L": comp = new Inductor(valor); break;
                case "C": comp = new Capacitor(valor); break;
            }
            if (comp != null) {
                disponibles.put(comp, entry.getValue());
            }
        }
        return disponibles;
    }

    public void guardar(String archivo) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo))) {
            oos.writeObject(stock);
        }
    }

    @SuppressWarnings("unchecked")
    public void cargar(String archivo) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            stock = (Map<String, Integer>) ois.readObject();
        }
    }
}