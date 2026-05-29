package ui;

import service.StockService;

import javax.swing.*;
import java.awt.*;

public class ReportesFrame extends JFrame {
    private StockService service;
    public ReportesFrame(StockService service) {
        this.service = service;
        setTitle("Reportes");
        setSize(400, 300);
        setLocationRelativeTo(null);
        initComponents();
    }
    private void initComponents() {
        JLabel lblTotal =
                new JLabel(
                        "Total vendido: $"
                                + service.obtenerTotalVendido()
                );
        JLabel lblVentas =
                new JLabel(
                        "Cantidad ventas: "
                                + service.obtenerCantidadVentas()
                );
        JLabel lblProducto =
                new JLabel(
                        "Más vendido: "
                                + service.obtenerProductoMasVendido()
                );
        JPanel panel = new JPanel();
        panel.setLayout(
                new GridLayout(3, 1, 10, 10)
        );
        panel.add(lblTotal);
        panel.add(lblVentas);
        panel.add(lblProducto);
        add(panel);
    }
}