package model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VentaTest {
    @Test
    void calculaTotalSaldoYRestauraStockAlQuitarDetalle() {
        Producto producto = new Producto(1, "Teclado", new BigDecimal("100.50"), 10);
        Venta venta = new Venta();

        venta.agregarDetalle(new DetalleVenta(producto, 2));
        venta.setMontoPagado(new BigDecimal("50.00"));

        assertEquals(new BigDecimal("201.00"), venta.getTotal());
        assertEquals(new BigDecimal("151.00"), venta.getSaldoPendiente());
        assertEquals(8, producto.getStock());

        venta.quitarDetalle(0);

        assertEquals(new BigDecimal("0.00"), venta.getTotal());
        assertEquals(10, producto.getStock());
    }
}
