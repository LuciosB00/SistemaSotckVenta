package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class CrearTabla {
    public static void crear() {
        String sqlProducto =
                """
                CREATE TABLE IF NOT EXISTS producto (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    precio REAL NOT NULL,
                    stock INTEGER NOT NULL,
                    activo INTEGER NOT NULL DEFAULT 1
                );
                """;
        String sqlVenta =
                """
                CREATE TABLE IF NOT EXISTS venta (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    fecha TEXT NOT NULL,
                    estado TEXT NOT NULL,
                    total REAL NOT NULL,
                    monto_pagado REAL NOT NULL
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
            agregarColumnaSiNoExiste(conn, "producto", "activo", "INTEGER NOT NULL DEFAULT 1");
            System.out.println("Tablas creadas correctamente");
        } catch (Exception e) {
            throw new RuntimeException("No se pudieron crear o migrar las tablas", e);
        }
    }

    private static void agregarColumnaSiNoExiste(Connection conn,
                                                String tabla,
                                                String columna,
                                                String definicion) throws Exception {
        try (
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("PRAGMA table_info(" + tabla + ")")
        ) {
            while (rs.next()) {
                if (columna.equalsIgnoreCase(rs.getString("name"))) {
                    return;
                }
            }
        }
        try (Statement st = conn.createStatement()) {
            st.execute("ALTER TABLE " + tabla + " ADD COLUMN " + columna + " " + definicion);
        }
    }
}
