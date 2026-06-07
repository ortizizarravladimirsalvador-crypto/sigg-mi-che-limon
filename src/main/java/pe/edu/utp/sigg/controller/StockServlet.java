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
import pe.edu.utp.sigg.model.StockPlato;
import pe.edu.utp.sigg.service.StockService;
import pe.edu.utp.sigg.dto.CategoriaStockDTO;

/**
 * StockServlet controla la pantalla de stock diario.
 *
 * Detecta si la jornada del día actual fue iniciada.
 */
@WebServlet(name = "StockServlet", urlPatterns = {"/stock"})
public class StockServlet extends HttpServlet {

    // Servicio que contiene la lógica de stock.
    private final StockService stockService = new StockService();

    /**
     * Muestra la pantalla de stock diario.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();

        // Recupera mensajes temporales.
        request.setAttribute("mensajeExito", session.getAttribute("mensajeExito"));
        request.setAttribute("mensajeError", session.getAttribute("mensajeError"));
        session.removeAttribute("mensajeExito");
        session.removeAttribute("mensajeError");

        // Verifica si existe jornada de stock para hoy.
        boolean jornadaIniciada = stockService.existeStockParaHoy();

        List<StockPlato> stock = jornadaIniciada
                ? stockService.listarStockDiario()
                : List.of();
        List<CategoriaStockDTO> categoriasStock = jornadaIniciada
        ? stockService.listarStockDiarioAgrupado()
        : List.of();

        int platosDisponibles = stockService.contarDisponibles(stock);
        int platosCriticos = stockService.contarCriticos(stock);
        int platosAgotados = stockService.contarAgotados(stock);

        request.setAttribute("jornadaIniciada", jornadaIniciada);
        request.setAttribute("stock", stock);
        request.setAttribute("categoriasStock", categoriasStock);
        request.setAttribute("platosDisponibles", platosDisponibles);
        request.setAttribute("platosCriticos", platosCriticos);
        request.setAttribute("platosAgotados", platosAgotados);

        request.getRequestDispatcher("/WEB-INF/views/stock/stock.jsp")
               .forward(request, response);
    }

    /**
     * Procesa acciones del módulo Stock.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String accion = request.getParameter("accion");

        try {
            if ("iniciarJornada".equals(accion)) {
                stockService.iniciarJornada();
                session.setAttribute("mensajeExito", "Jornada de stock iniciada correctamente.");
            }

        } catch (RuntimeException e) {
            session.setAttribute("mensajeError", e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/stock");
    }
}