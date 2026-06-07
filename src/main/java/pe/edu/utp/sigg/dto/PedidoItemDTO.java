package pe.edu.utp.sigg.dto;

import java.math.BigDecimal;
/**
 * @author FRANCK
 */
/**
 * DTO que representa un plato agregado temporalmente al pedido actual.
 *
 * DTO significa Data Transfer Object.
 * Sirve para transportar datos preparados hacia la vista pedidos.jsp.
 */
public class PedidoItemDTO {

    // Identificador del plato seleccionado.
    private int idPlato;

    // Nombre del plato mostrado en el resumen.
    private String nombrePlato;

    // Precio unitario del plato.
    private BigDecimal precio;

    // Tiempo estimado de preparación del plato.
    private int tiempoPreparacion;

    // Cantidad agregada al pedido.
    private int cantidad;

    // Stock disponible del plato en la jornada.
    private int stockDisponible;

    /**
     * Constructor completo para crear un ítem del pedido.
     */
    public PedidoItemDTO(int idPlato, String nombrePlato, BigDecimal precio,
                         int tiempoPreparacion, int cantidad, int stockDisponible) {
        this.idPlato = idPlato;
        this.nombrePlato = nombrePlato;
        this.precio = precio;
        this.tiempoPreparacion = tiempoPreparacion;
        this.cantidad = cantidad;
        this.stockDisponible = stockDisponible;
    }

    /**
     * Aumenta en 1 la cantidad del plato dentro del pedido.
     */
    public void incrementarCantidad() {
        this.cantidad++;
    }

    /**
     * Calcula el subtotal del ítem.
     * Ejemplo: precio 25.00 x cantidad 2 = 50.00.
     */
    public BigDecimal getSubtotal() {
        return precio.multiply(BigDecimal.valueOf(cantidad));
    }

    public int getIdPlato() {
        return idPlato;
    }

    public String getNombrePlato() {
        return nombrePlato;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public int getTiempoPreparacion() {
        return tiempoPreparacion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public int getStockDisponible() {
        return stockDisponible;
    }
}