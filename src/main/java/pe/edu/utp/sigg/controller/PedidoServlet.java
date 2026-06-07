package pe.edu.utp.sigg.controller;
/**
 * @author FRANCK
 */
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import pe.edu.utp.sigg.dto.PedidoCocinaDTO;
import pe.edu.utp.sigg.dto.PedidoItemDTO;
import pe.edu.utp.sigg.model.Mesa;
import pe.edu.utp.sigg.model.StockPlato;
import pe.edu.utp.sigg.service.PedidoService;
import pe.edu.utp.sigg.dto.CategoriaStockDTO;

/**
 * PedidoServlet controla la pantalla de registro de pedidos.
 *
 * El pedido se arma temporalmente en sesión.
 * Al enviar a cocina, se registra oficialmente en MySQL.
 */
@WebServlet(name = "PedidoServlet", urlPatterns = {"/pedidos"})
public class PedidoServlet extends HttpServlet {

    // Servicio encargado de la lógica de pedidos.
    private final PedidoService pedidoService = new PedidoService();

    /**
     * Muestra la pantalla de pedidos con datos desde MySQL.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();

        // Obtiene el pedido temporal armado por el mesero.
        List<PedidoItemDTO> pedidoItems = obtenerPedidoTemporal(session);

        // Recupera mesa seleccionada.
        Integer mesaSeleccionada = (Integer) session.getAttribute("mesaSeleccionada");
        if (mesaSeleccionada == null) {
            mesaSeleccionada = 0;
        }

        // Recupera mensajes temporales.
        request.setAttribute("mensajeExito", session.getAttribute("mensajeExito"));
        request.setAttribute("mensajeError", session.getAttribute("mensajeError"));
        session.removeAttribute("mensajeExito");
        session.removeAttribute("mensajeError");

        // Datos desde MySQL.
        // Mesas libres obtenidas desde MySQL.
        List<Mesa> mesasLibres = pedidoService.listarMesasLibres();

        // Lista plana usada para algunos cálculos internos.
        List<StockPlato> platosDisponibles = pedidoService.listarPlatosDisponibles();

        // Lista agrupada por categoría para mejorar la experiencia visual en pedidos.jsp.
        List<CategoriaStockDTO> categoriasPlatos = pedidoService.listarPlatosDisponiblesAgrupados();

        int totalItems = pedidoService.calcularTotalItems(pedidoItems);
        int tiempoEstimado = pedidoService.calcularTiempoEstimado(pedidoItems);
        BigDecimal subtotal = pedidoService.calcularSubtotal(pedidoItems)
                .setScale(2, RoundingMode.HALF_UP);

        // Envía datos a la vista.
        request.setAttribute("mesasLibres", mesasLibres);
        request.setAttribute("platosDisponibles", platosDisponibles);
        // Envía productos agrupados para renderizar la carta por categorías.
        request.setAttribute("categoriasPlatos", categoriasPlatos);
        request.setAttribute("totalPlatosDisponibles", pedidoService.contarPlatosDisponibles());
        request.setAttribute("pedidoItems", pedidoItems);
        request.setAttribute("mesaSeleccionada", mesaSeleccionada);
        request.setAttribute("itemsPedido", totalItems);
        request.setAttribute("tiempoEstimado", tiempoEstimado);
        request.setAttribute("subtotal", subtotal.toPlainString());

        request.getRequestDispatcher("/WEB-INF/views/pedidos/pedidos.jsp")
               .forward(request, response);
    }

    /**
     * Procesa acciones del pedido:
     * - agregar plato
     * - limpiar pedido
     * - enviar a cocina
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        List<PedidoItemDTO> pedidoItems = obtenerPedidoTemporal(session);

        String accion = request.getParameter("accion");

        try {
            if ("limpiar".equals(accion)) {

                pedidoItems.clear();
                session.removeAttribute("mesaSeleccionada");
                session.setAttribute("mensajeExito", "Pedido temporal limpiado correctamente.");

                response.sendRedirect(request.getContextPath() + "/pedidos");
                return;

            } else if ("agregar".equals(accion)) {

                int idMesa = parseEntero(request.getParameter("idMesa"));
                int idPlato = parseEntero(request.getParameter("idPlato"));

                if (idMesa <= 0) {
                    throw new IllegalArgumentException("Selecciona una mesa antes de agregar platos.");
                }

                if (idPlato <= 0) {
                    throw new IllegalArgumentException("Selecciona un plato válido.");
                }

                session.setAttribute("mesaSeleccionada", idMesa);

                pedidoService.agregarPlatoAlPedido(pedidoItems, idPlato);

                session.setAttribute("mensajeExito", "Plato agregado al pedido.");

                response.sendRedirect(request.getContextPath() + "/pedidos");
                return;

            } else if ("enviarCocina".equals(accion)) {

                int idMesa = parseEntero(request.getParameter("idMesa"));
                String observaciones = request.getParameter("observaciones");

                if (idMesa <= 0) {
                    Integer mesaSesion = (Integer) session.getAttribute("mesaSeleccionada");
                    idMesa = mesaSesion == null ? 0 : mesaSesion;
                }

                PedidoCocinaDTO pedido = pedidoService.prepararPedidoParaRegistro(
                        idMesa,
                        pedidoItems,
                        observaciones
                );

                String codigoPedido = pedidoService.registrarPedidoEnBaseDatos(pedido);

                // Limpia pedido temporal después de registrar en MySQL.
                pedidoItems.clear();
                session.removeAttribute("mesaSeleccionada");

                session.setAttribute("mensajeExito", "Pedido " + codigoPedido + " enviado a cocina.");

                response.sendRedirect(request.getContextPath() + "/cocina");
                return;
            }

            } catch (RuntimeException e) {
                // Captura errores controlados del flujo de pedidos.
                // Ejemplo: mesa no seleccionada, plato agotado o error al registrar en BD.
                session.setAttribute("mensajeError", e.getMessage());
            }

        response.sendRedirect(request.getContextPath() + "/pedidos");
    }

    /**
     * Obtiene la lista temporal del pedido desde sesión.
     * Si no existe, la crea.
     */
    @SuppressWarnings("unchecked")
    private List<PedidoItemDTO> obtenerPedidoTemporal(HttpSession session) {
        List<PedidoItemDTO> pedidoItems = (List<PedidoItemDTO>) session.getAttribute("pedidoItems");

        if (pedidoItems == null) {
            pedidoItems = new ArrayList<>();
            session.setAttribute("pedidoItems", pedidoItems);
        }

        return pedidoItems;
    }

    /**
     * Convierte texto a entero evitando errores por valores nulos.
     */
    private int parseEntero(String valor) {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException | NullPointerException e) {
            return 0;
        }
    }
}