package pe.edu.utp.sigg.controller;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.utp.sigg.model.Mesa;
import pe.edu.utp.sigg.model.StockPlato;
import pe.edu.utp.sigg.service.MesaService;
import pe.edu.utp.sigg.service.StockService;

/**
 * @author FRANCK
 */
/**
 * DashboardServlet controla el acceso al panel principal del sistema.
 *
 * En la arquitectura MVC:
 * - Este Servlet es el Controlador.
 * - dashboard.jsp es la Vista.
 * - MesaService y StockService preparan datos para mostrar indicadores.
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {

    // Servicio temporal para obtener información de mesas.
    private final MesaService mesaService = new MesaService();

    // Servicio temporal para obtener información de stock diario.
    private final StockService stockService = new StockService();

    /**
     * Muestra el dashboard principal del sistema.
     *
     * Por ahora usa datos temporales desde los Services.
     * Más adelante estos Services consultarán la base de datos mediante DAO.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Obtiene las mesas temporales desde MesaService.
        List<Mesa> mesas = mesaService.listarMesas();

        // Obtiene el stock temporal desde StockService.
        List<StockPlato> stock = stockService.listarStockDiario();

        // Datos temporales del dashboard.
        String ventasHoy = "S/ 0.00";
        int pedidosActivos = 2;

        // Indicadores calculados desde los Services para mantener coherencia.
        int mesasOcupadas = mesaService.contarPorEstado(mesas, "OCUPADA");
        int platosCriticos = stockService.contarCriticos(stock);

        // Envía indicadores a la vista dashboard.jsp.
        request.setAttribute("ventasHoy", ventasHoy);
        request.setAttribute("pedidosActivos", pedidosActivos);
        request.setAttribute("mesasOcupadas", mesasOcupadas);
        request.setAttribute("platosCriticos", platosCriticos);

        // Redirige internamente hacia la vista protegida del dashboard.
        request.getRequestDispatcher("/WEB-INF/views/dashboard/dashboard.jsp")
               .forward(request, response);
    }
}