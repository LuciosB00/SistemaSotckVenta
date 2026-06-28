package model;

import java.math.BigDecimal;

public class Producto {
    private int id;
    private String nombre;
    private BigDecimal precio;
    private int stock;
    private boolean activo;

    public Producto(int id, String nombre, BigDecimal precio, int stock) {
        this(id, nombre, precio, stock, true);
    }

    public Producto(int id, String nombre, BigDecimal precio, int stock, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.activo = activo;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public BigDecimal getPrecio() { return precio; }
    public int getStock() { return stock; }
    public boolean isActivo() { return activo; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public void setStock(int stock) { this.stock = stock; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public void reducirStock(int cantidad) {
        if (cantidad > stock) {
            throw new IllegalArgumentException("Stock insuficiente");
        }
        stock -= cantidad;
    }

    @Override
    public String toString() {
        return nombre + " | $" + precio + " | Stock: " + stock;
    }
}
