import ui.MainFrame;

import dao.CrearTabla;
import service.StockService;

public class Main {
    public static void main(String[] args) {
        CrearTabla.crear();
        StockService service = new StockService();
        javax.swing.SwingUtilities.invokeLater(() ->
                new MainFrame(service).setVisible(true)
        );
    }
}
