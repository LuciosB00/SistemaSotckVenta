package service;

import dao.ProductoDAO;
import dao.VentaDAO;
import model.Producto;
import model.Venta;

import java.util.ArrayList;
import java.util.List;

public class StockService {
    private VentaDAO ventaDAO = new VentaDAO();
    private ProductoDAO productoDAO = new ProductoDAO();
    private List<Venta> ventas = new ArrayList<>();
    private int contadorVentas = 1;
    public Producto crearProducto(String nombre,
                                  double precio,
                                  int stock) {
        Producto producto = new Producto(
                0,
                nombre,
                precio,
                stock
        );
        productoDAO.insertar(producto);
        return producto;
    }
    public List<Producto> listarProductos() {
        return productoDAO.listar();
    }
    public void registrarVenta(Venta venta) {
        venta.setId(contadorVentas++);
        for (var detalle : venta.getDetalles()) {
            productoDAO.actualizarStock(
                    detalle.getProducto().getId(),
                    detalle.getProducto().getStock()
            );
        }
        ventaDAO.insertar(venta);
    }
    public List<Venta> listarVentas() {
        return ventaDAO.listarVentas();
    }
    public void actualizarProducto(Producto producto) {
        productoDAO.actualizar(producto);
    }
    public void eliminarProducto(int id) {
        productoDAO.eliminar(id);
    }

}