package model;

import java.math.BigDecimal;

public class DetalleVenta {
    private Producto producto;
    private int cantidad;
    private BigDecimal subtotal;

    public DetalleVenta(Producto producto, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("Cantidad invalida");
        }
        this.producto = producto;
        this.cantidad = cantidad;
        this.subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(cantidad));
    }

    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public BigDecimal getSubtotal() { return subtotal; }
}
