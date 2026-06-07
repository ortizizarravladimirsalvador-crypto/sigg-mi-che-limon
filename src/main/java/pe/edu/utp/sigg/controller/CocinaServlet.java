package pe.edu.utp.sigg.controller;
/**
 * @author FRANCK
 */
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import pe.edu.utp.sigg.dto.PedidoCocinaDTO;
import pe.edu.utp.sigg.service.CocinaService;
//hola mundo
/**
 * CocinaServlet controla la pantalla de pedidos enviados a cocina.
 *
 * Permite:
 * - Ver pedidos pendientes.
 * - Iniciar preparación.
 * - Marcar pedidos como listos.
 */
@WebServlet(name = "CocinaServlet", urlPatterns = {"/cocina"})
public class CocinaServlet extends HttpServlet {

    // Servicio encargado de la lógica del módulo Cocina.
    private final CocinaService cocinaService = new CocinaService();

    /**
     * Muestra la cola de pedidos de cocina desde MySQL.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();

        // Lee pedidos reales desde la base de datos.
        List<PedidoCocinaDTO> pedidosCocina = cocinaService.listarPedidosParaCocina();

        // Recupera mensajes temporales.
        request.setAttribute("mensajeExito", session.getAttribute("mensajeExito"));
        request.setAttribute("mensajeError", session.getAttribute("mensajeError"));
        session.removeAttribute("mensajeExito");
        session.removeAttribute("mensajeError");

        // Envía datos hacia cocina.jsp.
        request.setAttribute("pedidosCocina", pedidosCocina);
        request.setAttribute("totalPedidos", cocinaService.contarPedidosActivos());

        request.getRequestDispatcher("/WEB-INF/views/cocina/cocina.jsp")
               .forward(request, response);
    }

    /**
     * Procesa acciones de cocina:
     * - iniciar preparación
     * - marcar pedido listo
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();

        String accion = request.getParameter("accion");
        int idPedido = parseEntero(request.getParameter("idPedido"));

        try {
            if ("iniciarPreparacion".equals(accion)) {

                cocinaService.iniciarPreparacion(idPedido);
                session.setAttribute("mensajeExito", "Pedido en preparación.");

            } else if ("marcarListo".equals(accion)) {

                cocinaService.marcarPedidoListo(idPedido);
                session.setAttribute("mensajeExito", "Pedido marcado como listo.");

            }

        } catch (RuntimeException e) {
            session.setAttribute("mensajeError", e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/cocina");
    }

    /**
     * Convierte texto a entero de forma segura.
     */
    private int parseEntero(String valor) {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException | NullPointerException e) {
            return 0;
        }
    }
}