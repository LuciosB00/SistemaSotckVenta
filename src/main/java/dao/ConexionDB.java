package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    private static final String URL = "jdbc:sqlite:stock.db";
    public static Connection conectar() {
        try {
            return DriverManager.getConnection(URL);

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al conectar con SQLite", e
            );
        }
    }
}