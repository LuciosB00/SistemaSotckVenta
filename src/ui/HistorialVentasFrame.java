package ui;

import model.DetalleVenta;
import model.Venta;
import service.StockService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class HistorialVentasFrame extends JFrame {
    private StockService service;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    public HistorialVentasFrame(StockService service) {
        this.service = service;
        setTitle("Historial de Ventas");
        setSize(800, 400);
        setLocationRelativeTo(null);
        initComponents();
        cargarVentas();
    }
    private void initComponents() {
        modeloTabla = new DefaultTableModel(
                new Object[]{
                        "Venta ID",
                        "Producto",
                        "Cantidad",
                        "Subtotal"
                },
                0
        );
        tabla = new JTable(modeloTabla);
        JScrollPane scroll =
                new JScrollPane(tabla);

        setLayout(new BorderLayout());
        add(scroll, BorderLayout.CENTER);
    }
    private void cargarVentas() {
        modeloTabla.setRowCount(0);
        for (Venta venta : service.listarVentas()) {
            for (DetalleVenta detalle :
                    venta.getDetalles()) {
                modeloTabla.addRow(new Object[]{
                        venta.getId(),
                        detalle.getProducto()
                                .getNombre(),

                        detalle.getCantidad(),
                        detalle.getSubtotal()
                });
            }
        }
    }
}