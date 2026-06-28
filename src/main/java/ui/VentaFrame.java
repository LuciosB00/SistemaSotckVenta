package ui;

import model.DetalleVenta;
import model.EstadoVenta;
import model.Producto;
import model.Venta;
import service.StockService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;

public class VentaFrame extends JFrame {
    private StockService service;
    private JComboBox<Producto> comboProductos;
    private JComboBox<EstadoVenta> comboEstado;
    private JTextField txtCantidad;
    private JTextField txtMontoPagado;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JLabel lblTotal;
    private Venta ventaActual;

    public VentaFrame(StockService service) {
        this.service = service;
        this.ventaActual = new Venta();
        setTitle("Ventas");
        setSize(820, 520);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel panelSuperior = new JPanel(new GridLayout(2, 5, 5, 5));
        comboProductos = new JComboBox<>();
        cargarProductos();
        txtCantidad = new JTextField();
        JButton btnAgregar = new JButton("Agregar");
        JButton btnQuitar = new JButton("Quitar linea");

        comboEstado = new JComboBox<>(EstadoVenta.values());
        txtMontoPagado = new JTextField("0");

        panelSuperior.add(new JLabel("Producto"));
        panelSuperior.add(comboProductos);
        panelSuperior.add(new JLabel("Cantidad"));
        panelSuperior.add(txtCantidad);
        panelSuperior.add(btnAgregar);
        panelSuperior.add(new JLabel("Estado"));
        panelSuperior.add(comboEstado);
        panelSuperior.add(new JLabel("Monto pagado"));
        panelSuperior.add(txtMontoPagado);
        panelSuperior.add(btnQuitar);

        modeloTabla = new DefaultTableModel(
                new Object[]{"Producto", "Cantidad", "Precio", "Subtotal"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tabla);

        JPanel panelInferior = new JPanel(new BorderLayout());
        lblTotal = new JLabel("Total: " + Formato.moneda(BigDecimal.ZERO));
        JButton btnConfirmar = new JButton("Confirmar Venta");
        panelInferior.add(lblTotal, BorderLayout.WEST);
        panelInferior.add(btnConfirmar, BorderLayout.EAST);

        btnAgregar.addActionListener(e -> agregarProducto());
        btnQuitar.addActionListener(e -> quitarLinea());
        btnConfirmar.addActionListener(e -> confirmarVenta());

        setLayout(new BorderLayout(8, 8));
        add(panelSuperior, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }

    private void agregarProducto() {
        try {
            Producto producto = (Producto) comboProductos.getSelectedItem();
            if (producto == null) {
                JOptionPane.showMessageDialog(this, "No hay productos activos para vender");
                return;
            }
            int cantidad = Integer.parseInt(txtCantidad.getText());
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0");
                return;
            }
            if (cantidad > producto.getStock()) {
                JOptionPane.showMessageDialog(this, "Stock insuficiente");
                return;
            }
            DetalleVenta detalle = new DetalleVenta(producto, cantidad);
            ventaActual.agregarDetalle(detalle);
            modeloTabla.addRow(new Object[]{
                    producto.getNombre(),
                    cantidad,
                    Formato.moneda(producto.getPrecio()),
                    Formato.moneda(detalle.getSubtotal())
            });
            actualizarTotal();
            txtCantidad.setText("");
        } catch (NumberFormatException ex) {
            mostrarError("Cantidad invalida");
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void quitarLinea() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una linea");
            return;
        }
        ventaActual.quitarDetalle(fila);
        modeloTabla.removeRow(fila);
        actualizarTotal();
    }

    private void actualizarTotal() {
        lblTotal.setText("Total: " + Formato.moneda(ventaActual.getTotal()));
    }

    private void confirmarVenta() {
        if (ventaActual.getDetalles().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay productos en la venta");
            return;
        }
        try {
            ventaActual.setEstado((EstadoVenta) comboEstado.getSelectedItem());
            ventaActual.setMontoPagado(parseDinero(txtMontoPagado.getText()));
            service.registrarVenta(ventaActual);
            JOptionPane.showMessageDialog(this, "Venta registrada correctamente");
            limpiarVenta();
        } catch (NumberFormatException ex) {
            mostrarError("Monto pagado invalido");
        } catch (RuntimeException ex) {
            mostrarError(obtenerMensajeError(ex));
            cargarProductos();
        }
    }

    private void limpiarVenta() {
        ventaActual = new Venta();
        modeloTabla.setRowCount(0);
        actualizarTotal();
        cargarProductos();
        comboEstado.setSelectedItem(EstadoVenta.EN_ESPERA);
        txtCantidad.setText("");
        txtMontoPagado.setText("0");
    }

    private void cargarProductos() {
        comboProductos.removeAllItems();
        for (Producto p : service.listarProductos()) {
            comboProductos.addItem(p);
        }
    }

    private BigDecimal parseDinero(String texto) {
        return new BigDecimal(texto.trim().replace(",", "."));
    }

    private String obtenerMensajeError(RuntimeException ex) {
        Throwable causa = ex.getCause();
        if (causa != null && causa.getMessage() != null) {
            return causa.getMessage();
        }
        return ex.getMessage();
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
