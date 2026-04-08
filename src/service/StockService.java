package service;
import model.Producto;
import model.Venta;

import java.util.ArrayList;
import java.util.List;

public class StockService {
    private List<Producto> productos = new ArrayList<>();
    private List<Venta> ventas = new ArrayList<>();
    private int contadorProductos = 1;
    private int contadorVentas = 1;
    public Producto crearProducto(String nombre, double precio, int stock) {
        Producto p = new Producto(contadorProductos++, nombre, precio, stock);
        productos.add(p);
        return p;
    }
    public List<Producto> listarProductos() {
        return productos;
    }
    public Producto buscarProductoPorId(int id) {
        return productos.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }
    public void registrarVenta(Venta venta) {
        venta.setId(contadorVentas++);
        ventas.add(venta);
    }
    public List<Venta> listarVentas() {
        return ventas;
    }
}