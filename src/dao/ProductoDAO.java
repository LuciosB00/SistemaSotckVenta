package dao;

import model.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {
    public void insertar(Producto producto) {
        String sql =
                """
                
                INSERT INTO producto(nombre, precio, stock)
                VALUES (?, ?, ?)
                
                """;
        try (
                Connection conn = ConexionDB.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, producto.getNombre());
            ps.setDouble(2, producto.getPrecio());
            ps.setInt(3, producto.getStock());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<Producto> listar() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM producto";
        try (
                Connection conn = ConexionDB.conectar();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)
        ) {
            while (rs.next()) {
                Producto p = new Producto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getInt("stock")
                );
                productos.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return productos;
    }
}