package pe.edu.utp.sigg.service;
/**
 * @author FRANCK
 */
import java.util.List;
import pe.edu.utp.sigg.dao.PedidoDAO;
import pe.edu.utp.sigg.dto.PedidoCocinaDTO;

/**
 * Servicio encargado de manejar la lógica del módulo Cocina.
 *
 * CocinaService coordina las reglas de negocio de cocina,
 * pero no ejecuta SQL directamente. Para eso usa PedidoDAO.
 */
public class CocinaService {

    // DAO encargado de consultar y actualizar pedidos en MySQL.
    private final PedidoDAO pedidoDAO = new PedidoDAO();

    /**
     * Lista los pedidos activos que deben mostrarse en cocina.
     *
     * Estados visibles:
     * - PENDIENTE
     * - EN_PREPARACION
     * - LISTO
     */
    public List<PedidoCocinaDTO> listarPedidosParaCocina() {
        return pedidoDAO.listarPedidosParaCocina();
    }

    /**
     * Cuenta pedidos activos para mostrar el indicador superior.
     */
    public int contarPedidosActivos() {
        return pedidoDAO.contarPedidosActivos();
    }

    /**
     * Inicia la preparación de un pedido.
     *
     * Regla:
     * Solo un pedido PENDIENTE puede pasar a EN_PREPARACION.
     */
    public void iniciarPreparacion(int idPedido) {
        if (idPedido <= 0) {
            throw new IllegalArgumentException("Pedido inválido para iniciar preparación.");
        }

        pedidoDAO.iniciarPreparacion(idPedido);
    }

    /**
     * Marca un pedido como listo.
     *
     * Regla:
     * Un pedido PENDIENTE o EN_PREPARACION puede pasar a LISTO.
     * La mesa seguirá ocupada hasta que Caja cobre el pedido.
     */
    public void marcarPedidoListo(int idPedido) {
        if (idPedido <= 0) {
            throw new IllegalArgumentException("Pedido inválido para marcar como listo.");
        }

        pedidoDAO.marcarPedidoListo(idPedido);
    }
}