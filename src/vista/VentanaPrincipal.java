package vista;

import control.*;
import modelo.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Map;

public class VentanaPrincipal extends JFrame {
    private Inventario inventario;
    private CalculadorCombinaciones calculador;
    private JTable tablaInventario;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> cmbTipo;
    private JTextField txtValor, txtCantidad, txtDeseado, txtTolerancia;
    private JTextArea areaResultado;
    private JButton btnAgregar, btnEliminar, btnDisenar;

    public VentanaPrincipal() {
        inventario = new Inventario();
        calculador = new CalculadorCombinaciones();
        cargarInventarioAlInicio();
        initGUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                guardarInventario();
            }
        });
    }

    private void initGUI() {
        setTitle("Diseñador de Circuitos - RLC");
        setSize(900, 700);
        setLayout(new BorderLayout());

        // Panel superior: gestión de inventario
        JPanel panelInventario = new JPanel(new BorderLayout());
        panelInventario.setBorder(BorderFactory.createTitledBorder("Inventario de componentes"));

        modeloTabla = new DefaultTableModel(new String[]{"Tipo", "Valor", "Cantidad"}, 0);
        tablaInventario = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaInventario);
        panelInventario.add(scrollTabla, BorderLayout.CENTER);

        JPanel panelControles = new JPanel(new FlowLayout());
        cmbTipo = new JComboBox<>(new String[]{"Resistencia (R)", "Inductor (L)", "Capacitor (C)"});
        txtValor = new JTextField(10);
        txtCantidad = new JTextField(5);
        btnAgregar = new JButton("Agregar");
        btnEliminar = new JButton("Eliminar");

        panelControles.add(new JLabel("Tipo:"));
        panelControles.add(cmbTipo);
        panelControles.add(new JLabel("Valor:"));
        panelControles.add(txtValor);
        panelControles.add(new JLabel("Cantidad:"));
        panelControles.add(txtCantidad);
        panelControles.add(btnAgregar);
        panelControles.add(btnEliminar);
        panelInventario.add(panelControles, BorderLayout.SOUTH);

        // Panel central: parámetros de diseño
        JPanel panelDiseno = new JPanel(new GridLayout(3, 2, 10, 10));
        panelDiseno.setBorder(BorderFactory.createTitledBorder("Diseño de circuito"));
        panelDiseno.add(new JLabel("Valor deseado:"));
        txtDeseado = new JTextField();
        panelDiseno.add(txtDeseado);
        panelDiseno.add(new JLabel("Tolerancia (%):"));
        txtTolerancia = new JTextField("5");
        panelDiseno.add(txtTolerancia);
        btnDisenar = new JButton("Diseñar circuito");
        panelDiseno.add(btnDisenar);
        panelDiseno.add(new JLabel("")); // placeholder

        // Área de resultados
        areaResultado = new JTextArea(15, 50);
        areaResultado.setEditable(false);
        JScrollPane scrollResultado = new JScrollPane(areaResultado);
        scrollResultado.setBorder(BorderFactory.createTitledBorder("Resultado"));

        // Organización de paneles
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.add(panelDiseno, BorderLayout.NORTH);
        panelCentro.add(scrollResultado, BorderLayout.CENTER);

        add(panelInventario, BorderLayout.WEST);
        add(panelCentro, BorderLayout.CENTER);

        // Eventos
        btnAgregar.addActionListener(e -> agregarComponente());
        btnEliminar.addActionListener(e -> eliminarComponente());
        btnDisenar.addActionListener(e -> disenarCircuito());

        actualizarTabla();
        pack();
        setLocationRelativeTo(null);
    }

    private void agregarComponente() {
        try {
            String tipoSeleccionado = (String) cmbTipo.getSelectedItem();
            double valor = Double.parseDouble(txtValor.getText());
            int cantidad = Integer.parseInt(txtCantidad.getText());
            if (valor <= 0 || cantidad <= 0) throw new NumberFormatException();

            ComponentePasivo comp = null;
            if (tipoSeleccionado.startsWith("Resistencia")) comp = new Resistencia(valor);
            else if (tipoSeleccionado.startsWith("Inductor")) comp = new Inductor(valor);
            else if (tipoSeleccionado.startsWith("Capacitor")) comp = new Capacitor(valor);

            if (comp != null) {
                inventario.agregar(comp, cantidad);
                actualizarTabla();
                JOptionPane.showMessageDialog(this, "Componente agregado");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese valores numéricos válidos (>0)");
        }
    }

    private void eliminarComponente() {
        int fila = tablaInventario.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un componente de la tabla");
            return;
        }
        String tipo = (String) modeloTabla.getValueAt(fila, 0);
        double valor = (double) modeloTabla.getValueAt(fila, 1);
        int cantidadActual = (int) modeloTabla.getValueAt(fila, 2);

        String input = JOptionPane.showInputDialog(this, "Cantidad a eliminar (1-" + cantidadActual + "):");
        if (input == null) return;
        try {
            int cantidad = Integer.parseInt(input);
            if (cantidad <= 0 || cantidad > cantidadActual) throw new NumberFormatException();
            ComponentePasivo comp = null;
            if (tipo.equals("R")) comp = new Resistencia(valor);
            else if (tipo.equals("L")) comp = new Inductor(valor);
            else if (tipo.equals("C")) comp = new Capacitor(valor);
            if (comp != null) {
                inventario.eliminar(comp, cantidad);
                actualizarTabla();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Cantidad inválida");
        }
    }

    private void disenarCircuito() {
        try {
            double deseado = Double.parseDouble(txtDeseado.getText());
            double toleranciaPorcentaje = Double.parseDouble(txtTolerancia.getText());
            String tipoSeleccionado = (String) cmbTipo.getSelectedItem();
            LeyCombinacion ley = null;
            if (tipoSeleccionado.startsWith("Resistencia")) ley = new LeyResistencia();
            else if (tipoSeleccionado.startsWith("Inductor")) ley = new LeyInductor();
            else if (tipoSeleccionado.startsWith("Capacitor")) ley = new LeyCapacitor();

            if (ley == null) {
                areaResultado.setText("Tipo no soportado");
                return;
            }

            Circuito mejor = calculador.mejorCircuito(inventario, deseado, toleranciaPorcentaje, ley, 4);
            if (mejor == null) {
                areaResultado.setText("No se encontró ningún circuito dentro de la tolerancia especificada.\n"
                        + "Pruebe aumentando la tolerancia o agregando más componentes al inventario.");
            } else {
                double valorObtenido = mejor.calcularEquivalente(ley);
                double errorAbs = Math.abs(valorObtenido - deseado);
                double errorRel = (errorAbs / deseado) * 100;
                areaResultado.setText("Circuito encontrado:\n" + mejor.obtenerDescripcion()
                        + "\n\nValor obtenido: " + valorObtenido
                        + "\nValor deseado: " + deseado
                        + "\nError absoluto: " + errorAbs
                        + "\nError relativo: " + String.format("%.2f", errorRel) + "%"
                        + "\nDentro de tolerancia: " + (errorAbs <= deseado * toleranciaPorcentaje/100 ? "Sí" : "No"));
            }
        } catch (NumberFormatException ex) {
            areaResultado.setText("Error: Ingrese valores numéricos válidos para deseado y tolerancia.");
        }
    }

    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        Map<String, Integer> stock = inventario.getStock();
        for (Map.Entry<String, Integer> entry : stock.entrySet()) {
            String[] partes = entry.getKey().split(":");
            String tipo = partes[0];
            double valor = Double.parseDouble(partes[1]);
            int cant = entry.getValue();
            modeloTabla.addRow(new Object[]{tipo, valor, cant});
        }
    }

    private void cargarInventarioAlInicio() {
        try {
            inventario = PersistenciaInventario.cargar("datos/inventario.dat");
        } catch (FileNotFoundException e) {
            // No existe archivo, se usa inventario vacío
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al cargar inventario: " + e.getMessage());
        }
    }

    private void guardarInventario() {
        try {
            PersistenciaInventario.guardar(inventario, "datos/inventario.dat");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar inventario: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}