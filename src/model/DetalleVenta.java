package model;

public class DetalleVenta {
    private Producto producto;
    private int cantidad;
    private double subtotal;
    public DetalleVenta(Producto producto, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("Cantidad inválida");
        }
        this.producto = producto;
        this.cantidad = cantidad;
        this.subtotal = producto.getPrecio() * cantidad;
    }
    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public double getSubtotal() { return subtotal; }
}