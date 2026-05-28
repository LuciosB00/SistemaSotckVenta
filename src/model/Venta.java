package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Venta {
    private int id;
    private LocalDateTime fecha;
    private List<DetalleVenta> detalles;
    private double total;
    public Venta() {
        this.fecha = LocalDateTime.now();
        this.detalles = new ArrayList<>();
        this.total = 0;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDateTime getFecha() { return fecha; }
    public List<DetalleVenta> getDetalles() { return detalles; }
    public double getTotal() { return total; }
    public void agregarDetalle(DetalleVenta detalle) {
        detalles.add(detalle);
        total += detalle.getSubtotal();
        detalle.getProducto().reducirStock(detalle.getCantidad());
    }
    public void agregarDetalleHistorial(DetalleVenta detalle) {
        detalles.add(detalle);
        total += detalle.getSubtotal();
    }
    @Override
    public String toString() {
        return "Venta #" + id + " - Total: $" + total + " - Fecha: " + fecha;
    }
}