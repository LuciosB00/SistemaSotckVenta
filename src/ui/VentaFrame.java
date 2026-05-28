package ui;

import model.DetalleVenta;
import model.Producto;
import model.Venta;
import service.StockService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VentaFrame extends JFrame {
    private StockService service;
    private JComboBox<Producto> comboProductos;
    private JTextField txtCantidad;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JLabel lblTotal;
    private Venta ventaActual;
    public VentaFrame(StockService service) {
        this.service = service;
        this.ventaActual = new Venta();
        setTitle("Ventas");
        setSize(700, 500);
        setLocationRelativeTo(null);
        initComponents();
    }
    private void initComponents() {
        // PANEL SUPERIOR
        JPanel panelSuperior = new JPanel(new GridLayout(1, 5, 5, 5));
        comboProductos = new JComboBox<>();
        for (Producto p : service.listarProductos()) {
            comboProductos.addItem(p);
        }
        txtCantidad = new JTextField();
        JButton btnAgregar = new JButton("Agregar");
        panelSuperior.add(new JLabel("Producto"));
        panelSuperior.add(comboProductos);
        panelSuperior.add(new JLabel("Cantidad"));
        panelSuperior.add(txtCantidad);
        panelSuperior.add(btnAgregar);
        // TABLA
        modeloTabla = new DefaultTableModel(
                new Object[]{"Producto", "Cantidad", "Subtotal"}, 0
        );
        tabla = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tabla);
        // PANEL INFERIOR
        JPanel panelInferior = new JPanel(new BorderLayout());
        lblTotal = new JLabel("Total: $0");
        JButton btnConfirmar = new JButton("Confirmar Venta");
        panelInferior.add(lblTotal, BorderLayout.WEST);
        panelInferior.add(btnConfirmar, BorderLayout.EAST);
        // EVENTOS
        btnAgregar.addActionListener(e -> agregarProducto());
        btnConfirmar.addActionListener(e -> confirmarVenta());
        // LAYOUT
        setLayout(new BorderLayout());
        add(panelSuperior, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }
    private void agregarProducto() {
        try {
            Producto producto = (Producto) comboProductos.getSelectedItem();
            int cantidad = Integer.parseInt(txtCantidad.getText());
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this,
                        "La cantidad debe ser mayor a 0");
                return;
            }
            if (producto == null) return;
            if (cantidad > producto.getStock()) {
                JOptionPane.showMessageDialog(this,
                        "Stock insuficiente");
                return;
            }
            DetalleVenta detalle = new DetalleVenta(producto, cantidad);
            ventaActual.agregarDetalle(detalle);
            modeloTabla.addRow(new Object[]{
                    producto.getNombre(),
                    cantidad,
                    detalle.getSubtotal()
            });
            actualizarTotal();
            txtCantidad.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Cantidad inválida");
        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(this,
                    ex.getMessage());
        }
    }
    private void actualizarTotal() {
        lblTotal.setText("Total: $" + ventaActual.getTotal());
    }
    private void confirmarVenta() {
        if (ventaActual.getDetalles().isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "No hay productos en la venta");
            return;
        }
        service.registrarVenta(ventaActual);
        JOptionPane.showMessageDialog(this,
                "Venta registrada correctamente");
        limpiarVenta();
    }
    private void limpiarVenta() {
        ventaActual = new Venta();
        modeloTabla.setRowCount(0);
        actualizarTotal();
        comboProductos.repaint();
        txtCantidad.setText("");
    }
}