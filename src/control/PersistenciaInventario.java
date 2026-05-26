package control;

import modelo.Inventario;
import java.io.*;

public class PersistenciaInventario {
    public static void guardar(Inventario inv, String ruta) throws IOException {
        inv.guardar(ruta);
    }

    public static Inventario cargar(String ruta) throws IOException, ClassNotFoundException {
        Inventario inv = new Inventario();
        inv.cargar(ruta);
        return inv;
    }
}