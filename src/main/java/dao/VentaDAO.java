package dao;

import model.DetalleVenta;
import model.EstadoVenta;
import model.Producto;
import model.Venta;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {
    public void insertar(Venta venta) {
        String sqlVenta =
                """
                INSERT INTO venta(fecha, estado, total, monto_pagado)
                VALUES (?, ?, ?, ?)
                """;
        String sqlStock =
                """
                UPDATE producto
                SET stock = stock - ?
                WHERE id = ?
                  AND stock >= ?
                  AND activo = 1
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
        try (Connection conn = ConexionDB.conectar()) {
            conn.setAutoCommit(false);
            try (
                    PreparedStatement psVenta = conn.prepareStatement(
                            sqlVenta,
                            Statement.RETURN_GENERATED_KEYS
                    );
                    PreparedStatement psStock = conn.prepareStatement(sqlStock);
                    PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle)
            ) {
                psVenta.setString(1, venta.getFecha().toString());
                psVenta.setString(2, venta.getEstado().name());
                psVenta.setBigDecimal(3, venta.getTotal());
                psVenta.setBigDecimal(4, venta.getMontoPagado());
                psVenta.executeUpdate();

                int ventaId = obtenerIdGenerado(psVenta);
                venta.setId(ventaId);

                for (DetalleVenta d : venta.getDetalles()) {
                    psStock.setInt(1, d.getCantidad());
                    psStock.setInt(2, d.getProducto().getId());
                    psStock.setInt(3, d.getCantidad());
                    int filasActualizadas = psStock.executeUpdate();
                    if (filasActualizadas == 0) {
                        throw new SQLException(
                                "Stock insuficiente o producto inactivo: " + d.getProducto().getNombre()
                        );
                    }

                    psDetalle.setInt(1, ventaId);
                    psDetalle.setInt(2, d.getProducto().getId());
                    psDetalle.setInt(3, d.getCantidad());
                    psDetalle.setBigDecimal(4, d.getProducto().getPrecio());
                    psDetalle.setBigDecimal(5, d.getSubtotal());
                    psDetalle.executeUpdate();
                }
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (Exception e) {
            throw new RuntimeException("No se pudo registrar la venta", e);
        }
    }

    public List<Venta> listarVentas() {
        List<Venta> ventas = new ArrayList<>();
        String sqlVenta =
            """
            SELECT id, fecha, estado, total, monto_pagado
            FROM venta
            ORDER BY fecha DESC, id DESC
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
                Venta venta = mapearVenta(rsVentas);
                try (PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle)) {
                    psDetalle.setInt(1, venta.getId());
                    try (ResultSet rsDetalle = psDetalle.executeQuery()) {
                        while (rsDetalle.next()) {
                            String nombreProducto = rsDetalle.getString("nombre");
                            if (nombreProducto == null) {
                                nombreProducto = "PRODUCTO ELIMINADO";
                            }
                            Producto producto = new Producto(
                                    rsDetalle.getInt("producto_id"),
                                    nombreProducto,
                                    rsDetalle.getBigDecimal("precio_unitario"),
                                    0,
                                    true
                            );
                            venta.agregarDetalleHistorial(new DetalleVenta(
                                    producto,
                                    rsDetalle.getInt("cantidad")
                            ));
                        }
                    }
                }
                ventas.add(venta);
            }
        } catch (Exception e) {
            throw new RuntimeException("No se pudieron listar las ventas", e);
        }
        return ventas;
    }

    public void actualizarEstadoYPago(int ventaId,
                                      EstadoVenta estado,
                                      BigDecimal montoPagado) {
        String sql =
                """
                UPDATE venta
                SET estado = ?,
                    monto_pagado = ?
                WHERE id = ?
                """;
        try (
                Connection conn = ConexionDB.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, estado.name());
            ps.setBigDecimal(2, montoPagado);
            ps.setInt(3, ventaId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo actualizar la venta", e);
        }
    }

    public BigDecimal obtenerTotalVendido() {
        return consultarBigDecimal(
                """
                SELECT SUM(total) AS valor
                FROM venta
                """
        );
    }

    public BigDecimal obtenerTotalAdeudado() {
        return consultarBigDecimal(
                """
                SELECT SUM(
                    CASE
                        WHEN total > monto_pagado THEN total - monto_pagado
                        ELSE 0
                    END
                ) AS valor
                FROM venta
                """
        );
    }

    public int obtenerCantidadVentas() {
        String sql =
            """
            SELECT COUNT(*) AS cantidad
            FROM venta
            """;
        try (
                Connection conn = ConexionDB.conectar();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)
        ) {
            if (rs.next()) {
                return rs.getInt("cantidad");
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo obtener la cantidad de ventas", e);
        }
        return 0;
    }

    public String obtenerProductoMasVendido() {
        String sql =
            """
            SELECT p.nombre,
                   SUM(dv.cantidad) AS total_vendido
            FROM detalle_venta dv
            LEFT JOIN producto p
                ON dv.producto_id = p.id
            GROUP BY dv.producto_id
            ORDER BY total_vendido DESC
            LIMIT 1
            """;
        try (
                Connection conn = ConexionDB.conectar();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)
        ) {
            if (rs.next()) {
                String nombre = rs.getString("nombre");
                if (nombre == null) {
                    nombre = "PRODUCTO ELIMINADO";
                }
                return nombre + " (" + rs.getInt("total_vendido") + " vendidos)";
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo obtener el producto mas vendido", e);
        }
        return "Sin ventas";
    }

    public List<String> obtenerVentasPorMes() {
        return consultarResumen(
                """
                SELECT substr(fecha, 1, 7) AS etiqueta,
                       SUM(total) AS valor
                FROM venta
                GROUP BY substr(fecha, 1, 7)
                ORDER BY etiqueta DESC
                LIMIT 12
                """,
                "etiqueta",
                "valor",
                "mes"
        );
    }

    public List<String> obtenerVentasPorEstado() {
        return consultarResumen(
                """
                SELECT estado AS etiqueta,
                       COUNT(*) AS valor
                FROM venta
                GROUP BY estado
                ORDER BY estado
                """,
                "etiqueta",
                "valor",
                "estado"
        );
    }

    private int obtenerIdGenerado(PreparedStatement psVenta) throws SQLException {
        try (ResultSet rs = psVenta.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        throw new SQLException("No se pudo obtener el ID de la venta");
    }

    private Venta mapearVenta(ResultSet rs) throws SQLException {
        Venta venta = new Venta();
        venta.setId(rs.getInt("id"));
        venta.setFecha(LocalDateTime.parse(rs.getString("fecha")));
        venta.setEstado(EstadoVenta.valueOf(rs.getString("estado")));
        venta.setMontoPagado(rs.getBigDecimal("monto_pagado"));
        return venta;
    }

    private BigDecimal consultarBigDecimal(String sql) {
        try (
                Connection conn = ConexionDB.conectar();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)
        ) {
            if (rs.next()) {
                BigDecimal valor = rs.getBigDecimal("valor");
                return valor == null ? BigDecimal.ZERO : valor;
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo obtener el indicador", e);
        }
        return BigDecimal.ZERO;
    }

    private List<String> consultarResumen(String sql,
                                          String columnaEtiqueta,
                                          String columnaValor,
                                          String tipo) {
        List<String> resumen = new ArrayList<>();
        try (
                Connection conn = ConexionDB.conectar();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)
        ) {
            while (rs.next()) {
                resumen.add(rs.getString(columnaEtiqueta) + ": " + rs.getString(columnaValor));
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo obtener el resumen por " + tipo, e);
        }
        return resumen;
    }
}
