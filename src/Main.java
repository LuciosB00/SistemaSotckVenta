import model.DetalleVenta;
import model.Producto;
import model.Venta;
import service.StockService;

import java.sql.Connection;
import java.sql.DriverManager;

import service.StockService;
import ui.MainFrame;

public class Main {

    public static void main(String[] args) {

        StockService service = new StockService();

        javax.swing.SwingUtilities.invokeLater(() ->
                new MainFrame(service).setVisible(true)
        );
    }
}
