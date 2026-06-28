package ui;

import model.Producto;
import service.StockService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ReportesFrame extends JFrame {
    private StockService service;

    public ReportesFrame(StockService service) {
        this.service = service;
        setTitle("Reportes");
        setSize(760, 520);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel indicadores = new JPanel(new GridLayout(2, 2, 10, 10));
        indicadores.add(new JLabel("Total vendido: " + Formato.moneda(service.obtenerTotalVendido())));
        indicadores.add(new JLabel("Cantidad ventas: " + service.obtenerCantidadVentas()));
        indicadores.add(new JLabel("Mas vendido: " + service.obtenerProductoMasVendido()));
        indicadores.add(new JLabel("Total adeudado: " + Formato.moneda(service.obtenerTotalAdeudado())));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Stock bajo", crearTablaStockBajo());
        tabs.addTab("Ventas por mes", crearLista(service.obtenerVentasPorMes()));
        tabs.addTab("Ventas por estado", crearLista(service.obtenerVentasPorEstado()));

        setLayout(new BorderLayout(8, 8));
        add(indicadores, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    private JScrollPane crearTablaStockBajo() {
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"Producto", "Precio", "Stock"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (Producto producto : service.listarProductosConStockBajo()) {
            modelo.addRow(new Object[]{
                    producto.getNombre(),
                    Formato.moneda(producto.getPrecio()),
                    producto.getStock()
            });
        }
        JTable tabla = new JTable(modelo);
        tabla.setAutoCreateRowSorter(true);
        return new JScrollPane(tabla);
    }

    private JScrollPane crearLista(java.util.List<String> datos) {
        DefaultListModel<String> modelo = new DefaultListModel<>();
        for (String dato : datos) {
            modelo.addElement(dato);
        }
        if (modelo.isEmpty()) {
            modelo.addElement("Sin datos");
        }
        return new JScrollPane(new JList<>(modelo));
    }
}
