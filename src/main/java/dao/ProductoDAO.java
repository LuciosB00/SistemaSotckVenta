package dao;

import model.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {
    public void insertar(Producto producto) {
        String sql =
                """
                INSERT INTO producto(nombre, precio, stock, activo)
                VALUES (?, ?, ?, 1)
                """;
        try (
                Connection conn = ConexionDB.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, producto.getNombre());
            ps.setBigDecimal(2, producto.getPrecio());
            ps.setInt(3, producto.getStock());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo guardar el producto", e);
        }
    }

    public List<Producto> listar() {
        String sql =
                """
                SELECT id, nombre, precio, stock, activo
                FROM producto
                WHERE activo = 1
                ORDER BY nombre
                """;
        return consultarProductos(sql);
    }

    public List<Producto> listarStockBajo(int limite) {
        List<Producto> productos = new ArrayList<>();
        String sql =
                """
                SELECT id, nombre, precio, stock, activo
                FROM producto
                WHERE activo = 1
                  AND stock <= ?
                ORDER BY stock ASC, nombre
                """;
        try (
                Connection conn = ConexionDB.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapearProducto(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudieron listar productos con bajo stock", e);
        }
        return productos;
    }

    public void actualizar(Producto producto) {
        String sql =
            """
            UPDATE producto
            SET nombre = ?,
                precio = ?,
                stock = ?
            WHERE id = ?
            """;
        try (
                Connection conn = ConexionDB.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, producto.getNombre());
            ps.setBigDecimal(2, producto.getPrecio());
            ps.setInt(3, producto.getStock());
            ps.setInt(4, producto.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo actualizar el producto", e);
        }
    }

    public void eliminar(int id) {
        String sql =
            """
            UPDATE producto
            SET activo = 0
            WHERE id = ?
            """;
        try (
                Connection conn = ConexionDB.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo desactivar el producto", e);
        }
    }

    private List<Producto> consultarProductos(String sql) {
        List<Producto> productos = new ArrayList<>();
        try (
                Connection conn = ConexionDB.conectar();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)
        ) {
            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudieron listar los productos", e);
        }
        return productos;
    }

    private Producto mapearProducto(ResultSet rs) throws SQLException {
        return new Producto(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getBigDecimal("precio"),
                rs.getInt("stock"),
                rs.getInt("activo") == 1
        );
    }
}
