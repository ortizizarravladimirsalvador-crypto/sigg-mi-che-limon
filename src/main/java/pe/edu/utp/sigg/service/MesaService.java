package pe.edu.utp.sigg.service;
/**
 * @author FRANCK
 */
import java.util.ArrayList;
import java.util.List;
import pe.edu.utp.sigg.dao.MesaDAO;
import pe.edu.utp.sigg.model.Mesa;

/**
 * Servicio encargado de manejar la lógica relacionada con mesas.
 *
 * El Service no ejecuta SQL directamente.
 * Para obtener datos usa MesaDAO.
 */
public class MesaService {

    // DAO encargado de consultar la tabla mesas.
    private final MesaDAO mesaDAO = new MesaDAO();

    /**
     * Lista todas las mesas desde la base de datos.
     */
    public List<Mesa> listarMesas() {
        return mesaDAO.listarMesas();
    }

    /**
     * Lista solo las mesas libres.
     *
     * Este método será usado por el módulo Pedidos.
     */
    public List<Mesa> listarMesasLibres() {
        List<Mesa> mesas = mesaDAO.listarMesas();
        List<Mesa> mesasLibres = new ArrayList<>();

        for (Mesa mesa : mesas) {
            if ("LIBRE".equals(mesa.getEstado())) {
                mesasLibres.add(mesa);
            }
        }

        return mesasLibres;
    }

    /**
     * Busca una mesa por ID desde la base de datos.
     */
    public Mesa buscarPorId(int idMesa) {
        return mesaDAO.buscarPorId(idMesa);
    }

    /**
     * Cambia el estado de una mesa en la base de datos.
     */
    public void cambiarEstadoMesa(int idMesa, String nuevoEstado) {
        mesaDAO.actualizarEstadoMesa(idMesa, nuevoEstado);
    }

    /**
     * Cuenta cuántas mesas tienen un estado específico.
     */
    public int contarPorEstado(List<Mesa> mesas, String estado) {
        int total = 0;

        for (Mesa mesa : mesas) {
            if (estado.equals(mesa.getEstado())) {
                total++;
            }
        }

        return total;
    }
}