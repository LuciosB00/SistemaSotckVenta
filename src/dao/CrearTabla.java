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
        try (
                Connection conn = ConexionDB.conectar();
                Statement st = conn.createStatement()
        ) {
            st.execute(sqlProducto);
            System.out.println("Tabla producto creada");
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}