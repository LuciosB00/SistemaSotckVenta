package ui;

import service.StockService;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private StockService service;
    public MainFrame(StockService service) {
        this.service = service;
        setTitle("Sistema de Stock y Ventas");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initComponents();
    }
    private void initComponents() {
        JButton btnProductos = new JButton("Gestión de Productos");
        JButton btnVentas = new JButton("Ventas");
        JButton btnSalir = new JButton("Salir");
        btnProductos.addActionListener(e ->
                new ProductoFrame(service).setVisible(true)
        );
        btnSalir.addActionListener(e -> System.exit(0));
        setLayout(new GridLayout(3, 1, 10, 10));
        add(btnProductos);
        add(btnVentas);
        add(btnSalir);
    }
}