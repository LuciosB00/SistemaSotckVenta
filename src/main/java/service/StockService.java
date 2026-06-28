package service;

import dao.ProductoDAO;
import dao.VentaDAO;
import model.EstadoVenta;
import model.Producto;
import model.Venta;

import java.math.BigDecimal;
import java.util.List;

public class StockService {
    private static final int LIMITE_STOCK_BAJO = 5;

    private final VentaDAO ventaDAO = new VentaDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();

    public Producto crearProducto(String nombre,
                                  BigDecimal precio,
                                  int stock) {
        validarProducto(nombre, precio, stock);
        Producto producto = new Producto(
                0,
                nombre.trim(),
                precio,
                stock
        );
        productoDAO.insertar(producto);
        return producto;
    }

    public List<Producto> listarProductos() {
        return productoDAO.listar();
    }

    public List<Producto> listarProductosConStockBajo() {
        return productoDAO.listarStockBajo(LIMITE_STOCK_BAJO);
    }

    public void registrarVenta(Venta venta) {
        validarVenta(venta);
        ventaDAO.insertar(venta);
    }

    public List<Venta> listarVentas() {
        return ventaDAO.listarVentas();
    }

    public void actualizarVenta(int ventaId,
                                EstadoVenta estado,
                                BigDecimal montoPagado) {
        if (montoPagado == null || montoPagado.signum() < 0) {
            throw new IllegalArgumentException("El monto pagado no puede ser negativo");
        }
        ventaDAO.actualizarEstadoYPago(ventaId, estado, montoPagado);
    }

    public void actualizarProducto(Producto producto) {
        validarProducto(
                producto.getNombre(),
                producto.getPrecio(),
                producto.getStock()
        );
        producto.setNombre(producto.getNombre().trim());
        productoDAO.actualizar(producto);
    }

    public void eliminarProducto(int id) {
        productoDAO.eliminar(id);
    }

    public BigDecimal obtenerTotalVendido() {
        return ventaDAO.obtenerTotalVendido();
    }

    public BigDecimal obtenerTotalAdeudado() {
        return ventaDAO.obtenerTotalAdeudado();
    }

    public int obtenerCantidadVentas() {
        return ventaDAO.obtenerCantidadVentas();
    }

    public String obtenerProductoMasVendido() {
        return ventaDAO.obtenerProductoMasVendido();
    }

    public List<String> obtenerVentasPorMes() {
        return ventaDAO.obtenerVentasPorMes();
    }

    public List<String> obtenerVentasPorEstado() {
        return ventaDAO.obtenerVentasPorEstado();
    }

    private void validarProducto(String nombre,
                                 BigDecimal precio,
                                 int stock) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (precio == null || precio.signum() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
    }

    private void validarVenta(Venta venta) {
        if (venta.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("No hay productos en la venta");
        }
        if (venta.getMontoPagado().signum() < 0) {
            throw new IllegalArgumentException("El monto pagado no puede ser negativo");
        }
    }
}
