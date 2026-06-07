package pe.edu.utp.sigg.dto;

import java.math.BigDecimal;
import java.util.List;
/**
 * @author FRANCK
 */
/**
 * DTO que representa un pedido enviado al módulo Cocina.
 *
 * Este objeto agrupa:
 * - Número de pedido.
 * - Mesa asociada.
 * - Lista de platos.
 * - Tiempo estimado.
 * - Subtotal.
 * - Observaciones.
 * - Estado del pedido.
 */
public class PedidoCocinaDTO {

    // Identificador temporal del pedido.
    private int idPedido;

    // Código visual del pedido. Ejemplo: P-001.
    private String codigoPedido;

    // Identificador de la mesa seleccionada.
    private int idMesa;

    // Texto visible de la mesa. Ejemplo: Mesa 4.
    private String nombreMesa;

    // Lista de platos agregados al pedido.
    private List<PedidoItemDTO> items;

    // Observaciones ingresadas por el mesero.
    private String observaciones;

    // Tiempo estimado total del pedido.
    private int tiempoEstimado;

    // Subtotal del pedido.
    private BigDecimal subtotal;

    // Estado del pedido en cocina: PENDIENTE, EN_PREPARACION, LISTO.
    private String estado;

    /**
     * Constructor completo para crear un pedido enviado a cocina.
     */
    public PedidoCocinaDTO(int idPedido, String codigoPedido, int idMesa, String nombreMesa,
                           List<PedidoItemDTO> items, String observaciones,
                           int tiempoEstimado, BigDecimal subtotal, String estado) {
        this.idPedido = idPedido;
        this.codigoPedido = codigoPedido;
        this.idMesa = idMesa;
        this.nombreMesa = nombreMesa;
        this.items = items;
        this.observaciones = observaciones;
        this.tiempoEstimado = tiempoEstimado;
        this.subtotal = subtotal;
        this.estado = estado;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public String getCodigoPedido() {
        return codigoPedido;
    }

    public int getIdMesa() {
        return idMesa;
    }

    public String getNombreMesa() {
        return nombreMesa;
    }

    public List<PedidoItemDTO> getItems() {
        return items;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public int getTiempoEstimado() {
        return tiempoEstimado;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public String getEstado() {
        return estado;
    }

    /**
     * Devuelve una clase CSS según el estado del pedido.
     */
    public String getClaseEstado() {
        return estado.toLowerCase().replace("_", "-");
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}