package ui;

import model.Producto;
import service.StockService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;

public class ProductoFrame extends JFrame {
    private static final int LIMITE_STOCK_BAJO = 5;

    private StockService service;
    private JTextField txtNombre;
    private JTextField txtPrecio;
    private JTextField txtStock;
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public ProductoFrame(StockService service) {
        this.service = service;
        setTitle("Gestion de Productos");
        setSize(700, 440);
        setLocationRelativeTo(null);
        initComponents();
        cargarTabla();
    }

    private void initComponents() {
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

        JButton btnAgregar = new JButton("Agregar");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Desactivar");
        panelForm.add(btnAgregar);
        JPanel acciones = new JPanel(new GridLayout(1, 2, 5, 5));
        acciones.add(btnEditar);
        acciones.add(btnEliminar);
        panelForm.add(acciones);
        getRootPane().setDefaultButton(btnAgregar);

        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Precio", "Stock", "Alerta"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.setAutoCreateRowSorter(true);
        tabla.setDefaultRenderer(Object.class, new StockRenderer());
        tabla.getColumnModel().getColumn(2).setCellRenderer(new MonedaRenderer());
        JScrollPane scroll = new JScrollPane(tabla);

        btnAgregar.addActionListener(e -> agregarProducto());
        btnEditar.addActionListener(e -> editarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());

        setLayout(new BorderLayout(8, 8));
        add(panelForm, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        tabla.getSelectionModel().addListSelectionListener(e -> cargarSeleccion());
    }

    private void agregarProducto() {
        try {
            service.crearProducto(
                    txtNombre.getText(),
                    parseDinero(txtPrecio.getText()),
                    Integer.parseInt(txtStock.getText())
            );
            cargarTabla();
            limpiarCampos();
        } catch (NumberFormatException ex) {
            mostrarError("Precio y stock deben ser numeros validos");
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void editarProducto() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto");
            return;
        }
        try {
            int filaModelo = tabla.convertRowIndexToModel(fila);
            Producto producto = new Producto(
                    (int) modeloTabla.getValueAt(filaModelo, 0),
                    txtNombre.getText(),
                    parseDinero(txtPrecio.getText()),
                    Integer.parseInt(txtStock.getText())
            );
            service.actualizarProducto(producto);
            cargarTabla();
            limpiarCampos();
        } catch (NumberFormatException e) {
            mostrarError("Datos invalidos");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    private void eliminarProducto() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto");
            return;
        }
        int filaModelo = tabla.convertRowIndexToModel(fila);
        int id = (int) modeloTabla.getValueAt(filaModelo, 0);
        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "El producto quedara inactivo y no aparecera en ventas. El historial se conserva.",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );
        if (confirmacion == JOptionPane.YES_OPTION) {
            service.eliminarProducto(id);
            cargarTabla();
            limpiarCampos();
        }
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        for (Producto p : service.listarProductos()) {
            modeloTabla.addRow(new Object[]{
                    p.getId(),
                    p.getNombre(),
                    p.getPrecio(),
                    p.getStock(),
                    p.getStock() <= LIMITE_STOCK_BAJO ? "Stock bajo" : ""
            });
        }
    }

    private void cargarSeleccion() {
        if (tabla.getSelectedRow() == -1) {
            return;
        }
        int filaModelo = tabla.convertRowIndexToModel(tabla.getSelectedRow());
        txtNombre.setText(modeloTabla.getValueAt(filaModelo, 1).toString());
        BigDecimal precio = (BigDecimal) modeloTabla.getValueAt(filaModelo, 2);
        txtPrecio.setText(precio.toPlainString());
        txtStock.setText(modeloTabla.getValueAt(filaModelo, 3).toString());
    }

    private BigDecimal parseDinero(String texto) {
        return new BigDecimal(texto.trim().replace(",", "."));
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        txtNombre.requestFocus();
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static class StockRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column) {
            Component c = super.getTableCellRendererComponent(
                    table,
                    value,
                    isSelected,
                    hasFocus,
                    row,
                    column
            );
            int filaModelo = table.convertRowIndexToModel(row);
            int stock = (int) table.getModel().getValueAt(filaModelo, 3);
            if (!isSelected && stock <= LIMITE_STOCK_BAJO) {
                c.setBackground(new Color(255, 243, 205));
            } else if (!isSelected) {
                c.setBackground(Color.WHITE);
            }
            return c;
        }
    }

    private static class MonedaRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column) {
            Object texto = value instanceof BigDecimal
                    ? Formato.moneda((BigDecimal) value)
                    : value;
            return super.getTableCellRendererComponent(
                    table,
                    texto,
                    isSelected,
                    hasFocus,
                    row,
                    column
            );
        }
    }
}
