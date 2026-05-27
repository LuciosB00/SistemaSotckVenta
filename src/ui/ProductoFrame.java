package ui;

import model.Producto;
import service.StockService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ProductoFrame extends JFrame {
    private StockService service;
    private JTextField txtNombre;
    private JTextField txtPrecio;
    private JTextField txtStock;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    public ProductoFrame(StockService service) {
        this.service = service;
        setTitle("Gestión de Productos");
        setSize(600, 400);
        setLocationRelativeTo(null);
        initComponents();
        cargarTabla();
    }
    private void initComponents() {
        // Panel formulario
        JPanel panelForm = new JPanel(new GridLayout(4, 2, 5, 5));
        panelForm.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelForm.add(txtNombre);
        panelForm.add(new JLabel("Precio:"));
        txtPrecio = new JTextField();
        panelForm.add(txtPrecio);
        panelForm.add(new JLabel("Stock:"));
        txtStock = new JTextField();
        panelForm.add(txtStock);
        JButton btnAgregar = new JButton("Agregar Producto");
        getRootPane().setDefaultButton(btnAgregar);
        panelForm.add(btnAgregar);
        // Tabla
        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Precio", "Stock"}, 0);
        tabla = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tabla);
        // Evento botón
        btnAgregar.addActionListener(e -> agregarProducto());
        setLayout(new BorderLayout());
        add(panelForm, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }
    private void agregarProducto() {
        try {
            String nombre = txtNombre.getText();
            double precio = Double.parseDouble(txtPrecio.getText());
            int stock = Integer.parseInt(txtStock.getText());
            service.crearProducto(nombre, precio, stock);
            cargarTabla();
            limpiarCampos();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Precio y Stock deben ser números",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        for (Producto p : service.listarProductos()) {
            modeloTabla.addRow(new Object[]{
                    p.getId(),
                    p.getNombre(),
                    p.getPrecio(),
                    p.getStock()
            });
        }
    }
    private void limpiarCampos() {
        txtNombre.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        txtNombre.requestFocus();
    }
}