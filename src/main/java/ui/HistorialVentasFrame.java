package ui;

import model.DetalleVenta;
import model.EstadoVenta;
import model.Venta;
import service.StockService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class HistorialVentasFrame extends JFrame {
    private StockService service;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField txtFiltroProducto;
    private JTextField txtFiltroFecha;
    private JComboBox<String> comboFiltroEstado;
    private JComboBox<EstadoVenta> comboEstado;
    private JTextField txtMontoPagado;
    private List<Venta> ventas = new ArrayList<>();

    public HistorialVentasFrame(StockService service) {
        this.service = service;
        setTitle("Historial de Ventas");
        setSize(1000, 520);
        setLocationRelativeTo(null);
        initComponents();
        cargarVentas();
    }

    private void initComponents() {
        JPanel filtros = new JPanel(new GridLayout(2, 4, 5, 5));
        txtFiltroProducto = new JTextField();
        txtFiltroFecha = new JTextField();
        comboFiltroEstado = new JComboBox<>();
        comboFiltroEstado.addItem("TODOS");
        for (EstadoVenta estado : EstadoVenta.values()) {
            comboFiltroEstado.addItem(estado.name());
        }
        JButton btnFiltrar = new JButton("Filtrar");

        filtros.add(new JLabel("Producto"));
        filtros.add(new JLabel("Fecha contiene"));
        filtros.add(new JLabel("Estado"));
        filtros.add(new JLabel(""));
        filtros.add(txtFiltroProducto);
        filtros.add(txtFiltroFecha);
        filtros.add(comboFiltroEstado);
        filtros.add(btnFiltrar);

        modeloTabla = new DefaultTableModel(
                new Object[]{
                        "Venta ID",
                        "Fecha",
                        "Estado",
                        "Producto",
                        "Cantidad",
                        "Subtotal",
                        "Total",
                        "Pagado",
                        "Saldo"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.setAutoCreateRowSorter(true);
        JScrollPane scroll = new JScrollPane(tabla);

        JPanel edicion = new JPanel(new GridLayout(1, 6, 5, 5));
        comboEstado = new JComboBox<>(EstadoVenta.values());
        txtMontoPagado = new JTextField();
        JButton btnActualizar = new JButton("Actualizar venta");
        edicion.add(new JLabel("Nuevo estado"));
        edicion.add(comboEstado);
        edicion.add(new JLabel("Monto pagado"));
        edicion.add(txtMontoPagado);
        edicion.add(btnActualizar);

        btnFiltrar.addActionListener(e -> pintarVentas());
        btnActualizar.addActionListener(e -> actualizarVentaSeleccionada());
        tabla.getSelectionModel().addListSelectionListener(e -> cargarVentaSeleccionada());

        setLayout(new BorderLayout(8, 8));
        add(filtros, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(edicion, BorderLayout.SOUTH);
    }

    private void cargarVentas() {
        ventas = service.listarVentas();
        pintarVentas();
    }

    private void pintarVentas() {
        modeloTabla.setRowCount(0);
        String filtroProducto = txtFiltroProducto.getText().trim().toLowerCase();
        String filtroFecha = txtFiltroFecha.getText().trim();
        String filtroEstado = comboFiltroEstado.getSelectedItem().toString();

        for (Venta venta : ventas) {
            if (!"TODOS".equals(filtroEstado) && !venta.getEstado().name().equals(filtroEstado)) {
                continue;
            }
            if (!filtroFecha.isEmpty() && !Formato.fecha(venta.getFecha()).contains(filtroFecha)) {
                continue;
            }
            for (DetalleVenta detalle : venta.getDetalles()) {
                if (!filtroProducto.isEmpty()
                        && !detalle.getProducto().getNombre().toLowerCase().contains(filtroProducto)) {
                    continue;
                }
                modeloTabla.addRow(new Object[]{
                        venta.getId(),
                        Formato.fecha(venta.getFecha()),
                        venta.getEstado(),
                        detalle.getProducto().getNombre(),
                        detalle.getCantidad(),
                        Formato.moneda(detalle.getSubtotal()),
                        Formato.moneda(venta.getTotal()),
                        Formato.moneda(venta.getMontoPagado()),
                        Formato.moneda(venta.getSaldoPendiente())
                });
            }
        }
    }

    private void cargarVentaSeleccionada() {
        if (tabla.getSelectedRow() == -1) {
            return;
        }
        int ventaId = obtenerVentaIdSeleccionada();
        Venta venta = buscarVenta(ventaId);
        if (venta != null) {
            comboEstado.setSelectedItem(venta.getEstado());
            txtMontoPagado.setText(venta.getMontoPagado().toPlainString());
        }
    }

    private void actualizarVentaSeleccionada() {
        if (tabla.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una venta");
            return;
        }
        try {
            int ventaId = obtenerVentaIdSeleccionada();
            service.actualizarVenta(
                    ventaId,
                    (EstadoVenta) comboEstado.getSelectedItem(),
                    parseDinero(txtMontoPagado.getText())
            );
            cargarVentas();
            JOptionPane.showMessageDialog(this, "Venta actualizada");
        } catch (NumberFormatException ex) {
            mostrarError("Monto pagado invalido");
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private int obtenerVentaIdSeleccionada() {
        int filaModelo = tabla.convertRowIndexToModel(tabla.getSelectedRow());
        return (int) modeloTabla.getValueAt(filaModelo, 0);
    }

    private Venta buscarVenta(int ventaId) {
        for (Venta venta : ventas) {
            if (venta.getId() == ventaId) {
                return venta;
            }
        }
        return null;
    }

    private BigDecimal parseDinero(String texto) {
        return new BigDecimal(texto.trim().replace(",", "."));
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
