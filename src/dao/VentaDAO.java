package dao;

import model.DetalleVenta;
import model.Producto;
import model.Venta;

import model.Producto;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class VentaDAO {
    public void insertar(Venta venta) {
        String sqlVenta =
                """
                INSERT INTO venta(fecha, total)
                VALUES (?, ?)
                """;
        String sqlDetalle =
                """
                INSERT INTO detalle_venta(
                    venta_id,
                    producto_id,
                    cantidad,
                    precio_unitario,
                    subtotal
                )
                VALUES (?, ?, ?, ?, ?)
                """;
        try (
                Connection conn = ConexionDB.conectar()
        ) {
            conn.setAutoCommit(false);
            // INSERTAR VENTA
            PreparedStatement psVenta =
                    conn.prepareStatement(
                            sqlVenta,
                            Statement.RETURN_GENERATED_KEYS
                    );
            psVenta.setString(
                    1,
                    venta.getFecha().toString()
            );
            psVenta.setDouble(
                    2,
                    venta.getTotal()
            );
            psVenta.executeUpdate();
            // OBTENER ID GENERADO
            var rs = psVenta.getGeneratedKeys();
            int ventaId = 0;
            if (rs.next()) {
                ventaId = rs.getInt(1);
            }
            // INSERTAR DETALLES
            PreparedStatement psDetalle =
                    conn.prepareStatement(sqlDetalle);
            for (DetalleVenta d : venta.getDetalles()) {
                psDetalle.setInt(1, ventaId);
                psDetalle.setInt(
                        2,
                        d.getProducto().getId()
                );
                psDetalle.setInt(
                        3,
                        d.getCantidad()
                );
                psDetalle.setDouble(
                        4,
                        d.getProducto().getPrecio()
                );
                psDetalle.setDouble(
                        5,
                        d.getSubtotal()
                );
                psDetalle.executeUpdate();
            }
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    public List<Venta> listarVentas() {
        List<Venta> ventas = new ArrayList<>();
        String sqlVenta =
            """
            SELECT * FROM venta
            """;
        String sqlDetalle =
            """
            SELECT dv.*, p.nombre
            FROM detalle_venta dv
            LEFT JOIN producto p
                ON dv.producto_id = p.id
            WHERE venta_id = ?
            """;
        try (
                Connection conn = ConexionDB.conectar();
                Statement st = conn.createStatement();
                ResultSet rsVentas = st.executeQuery(sqlVenta)
        ) {
            while (rsVentas.next()) {
                Venta venta = new Venta();
                venta.setId(
                        rsVentas.getInt("id")
                );
                PreparedStatement psDetalle =
                        conn.prepareStatement(sqlDetalle);
                psDetalle.setInt(
                        1,
                        venta.getId()
                );
                ResultSet rsDetalle =
                        psDetalle.executeQuery();
                while (rsDetalle.next()) {
                    String nombreProducto =
                            rsDetalle.getString("nombre");
                    if (nombreProducto == null) {
                        nombreProducto =
                                "PRODUCTO ELIMINADO";
                    }
                    Producto producto =
                            new Producto(
                                    rsDetalle.getInt("producto_id"),
                                    nombreProducto,
                                    rsDetalle.getDouble("precio_unitario"),
                                    0
                            );
                    DetalleVenta detalle =
                            new DetalleVenta(
                                    producto,
                                    rsDetalle.getInt("cantidad")
                            );
                    venta.agregarDetalleHistorial(detalle);
                }
                ventas.add(venta);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return ventas;
    }
}