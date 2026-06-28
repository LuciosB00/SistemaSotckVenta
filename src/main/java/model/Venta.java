package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Venta {
    private int id;
    private LocalDateTime fecha;
    private List<DetalleVenta> detalles;
    private BigDecimal total;
    private EstadoVenta estado;
    private BigDecimal montoPagado;

    public Venta() {
        this.fecha = LocalDateTime.now();
        this.detalles = new ArrayList<>();
        this.total = BigDecimal.ZERO;
        this.estado = EstadoVenta.EN_ESPERA;
        this.montoPagado = BigDecimal.ZERO;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public List<DetalleVenta> getDetalles() { return detalles; }
    public BigDecimal getTotal() { return total; }
    public EstadoVenta getEstado() { return estado; }
    public void setEstado(EstadoVenta estado) { this.estado = estado; }
    public BigDecimal getMontoPagado() { return montoPagado; }
    public void setMontoPagado(BigDecimal montoPagado) { this.montoPagado = montoPagado; }

    public BigDecimal getSaldoPendiente() {
        BigDecimal saldo = total.subtract(montoPagado);
        return saldo.signum() < 0 ? BigDecimal.ZERO : saldo;
    }

    public void agregarDetalle(DetalleVenta detalle) {
        detalles.add(detalle);
        total = total.add(detalle.getSubtotal());
        detalle.getProducto().reducirStock(detalle.getCantidad());
    }

    public void quitarDetalle(int indice) {
        DetalleVenta detalle = detalles.remove(indice);
        total = total.subtract(detalle.getSubtotal());
        Producto producto = detalle.getProducto();
        producto.setStock(producto.getStock() + detalle.getCantidad());
    }

    public void agregarDetalleHistorial(DetalleVenta detalle) {
        detalles.add(detalle);
        total = total.add(detalle.getSubtotal());
    }

    @Override
    public String toString() {
        return "Venta #" + id + " - Total: $" + total + " - Fecha: " + fecha;
    }
}
