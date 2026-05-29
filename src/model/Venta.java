package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class Venta {
    private int id;
    private LocalDateTime fecha;
    private List<DetalleVenta> detalles;
    private double total;
    private EstadoVenta estado;
    private double montoPagado;
    public Venta() {
        this.fecha = LocalDateTime.now();
        this.detalles = new ArrayList<>();
        this.total = 0;
        this.estado = EstadoVenta.EN_ESPERA;
        this.montoPagado = 0;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDateTime getFecha() { return fecha; }
    public List<DetalleVenta> getDetalles() { return detalles; }
    public double getTotal() { return total; }
    public EstadoVenta getEstado() {return estado;}
    public void setEstado(EstadoVenta estado) {this.estado = estado;}
    public double getMontoPagado() {return montoPagado;}
    public void setMontoPagado(double montoPagado) {this.montoPagado = montoPagado;}
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