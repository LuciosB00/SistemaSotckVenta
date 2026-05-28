package dao;

import java.sql.Connection;
import java.sql.Statement;

public class CrearTabla {
    public static void crear() {
        String sqlProducto =
                """
                CREATE TABLE IF NOT EXISTS producto (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    precio REAL NOT NULL,
                    stock INTEGER NOT NULL
                );
                """;
        String sqlVenta =
                """
                CREATE TABLE IF NOT EXISTS venta (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    fecha TEXT NOT NULL,
                    total REAL NOT NULL
                );
                """;
        String sqlDetalleVenta =
                """
                CREATE TABLE IF NOT EXISTS detalle_venta (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    venta_id INTEGER NOT NULL,
                    producto_id INTEGER NOT NULL,
                    cantidad INTEGER NOT NULL,
                    precio_unitario REAL NOT NULL,
                    subtotal REAL NOT NULL,
                    FOREIGN KEY (venta_id) REFERENCES venta(id),
                    FOREIGN KEY (producto_id) REFERENCES producto(id)
                );
                """;
        try (
                Connection conn = ConexionDB.conectar();
                Statement st = conn.createStatement()
        ) {
            st.execute(sqlProducto);
            st.execute(sqlVenta);
            st.execute(sqlDetalleVenta);
            System.out.println("Tablas creadas correctamente");
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}