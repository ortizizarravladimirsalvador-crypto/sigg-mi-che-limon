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
import pe.edu.utp.sigg.model.Mesa;
import pe.edu.utp.sigg.service.MesaService;

/**
 * MesaServlet controla la pantalla de gestión de mesas.
 *
 * Ahora obtiene las mesas reales desde MySQL usando:
 * MesaServlet → MesaService → MesaDAO → MySQL.
 */
@WebServlet(name = "MesaServlet", urlPatterns = {"/mesas"})
public class MesaServlet extends HttpServlet {

    // Servicio que contiene la lógica de mesas.
    private final MesaService mesaService = new MesaService();

    /**
     * Muestra la pantalla de mesas usando datos reales de la base de datos.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Obtiene la lista real de mesas desde MySQL.
        List<Mesa> mesas = mesaService.listarMesas();

        // Calcula indicadores desde la información real.
        int mesasLibres = mesaService.contarPorEstado(mesas, "LIBRE");
        int mesasOcupadas = mesaService.contarPorEstado(mesas, "OCUPADA");
        int mesasReservadas = mesaService.contarPorEstado(mesas, "RESERVADA");

        // Envía datos a la vista JSP.
        request.setAttribute("mesas", mesas);
        request.setAttribute("mesasLibres", mesasLibres);
        request.setAttribute("mesasOcupadas", mesasOcupadas);
        request.setAttribute("mesasReservadas", mesasReservadas);

        request.getRequestDispatcher("/WEB-INF/views/mesas/mesas.jsp")
               .forward(request, response);
    }
}