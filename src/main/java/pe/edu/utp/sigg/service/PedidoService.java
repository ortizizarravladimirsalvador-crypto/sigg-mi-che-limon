package pe.edu.utp.sigg.service;
/**
 * @author FRANCK
 */

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import pe.edu.utp.sigg.dao.PedidoDAO;
import pe.edu.utp.sigg.dto.PedidoCocinaDTO;
import pe.edu.utp.sigg.dto.PedidoItemDTO;
import pe.edu.utp.sigg.model.Mesa;
import pe.edu.utp.sigg.model.StockPlato;
import java.util.LinkedHashMap;
import java.util.Map;
import pe.edu.utp.sigg.dto.CategoriaStockDTO;

/**
 * Servicio encargado de manejar la lógica del módulo Pedidos.
 *
 * El Service coordina la operación entre:
 * - Mesas.
 * - Stock.
 * - Registro de pedidos.
 */
public class PedidoService {

    // Servicio para consultar mesas desde MySQL.
    private final MesaService mesaService = new MesaService();

    // Servicio para consultar stock desde MySQL.
    private final StockService stockService = new StockService();

    // DAO para registrar pedidos en MySQL.
    private final PedidoDAO pedidoDAO = new PedidoDAO();

    /**
     * Obtiene mesas libres desde MySQL.
     */
    public List<Mesa> listarMesasLibres() {
        return mesaService.listarMesasLibres();
    }

    /**
     * Obtiene platos disponibles desde MySQL.
     */
    public List<StockPlato> listarPlatosDisponibles() {
        return stockService.listarPlatosDisponibles();
    }
    
    /**
    * Agrupa los productos disponibles por categoría.
    *
    * Esta lógica se hace en Java para mantener una buena arquitectura:
    * el JSP solo muestra los datos, no decide cómo organizarlos.
    *
    * @return lista de categorías con sus productos disponibles.
    */
   public List<CategoriaStockDTO> listarPlatosDisponiblesAgrupados() {
       List<StockPlato> platosDisponibles = listarPlatosDisponibles();

       // LinkedHashMap conserva el orden en que insertamos las categorías.
       Map<String, List<StockPlato>> grupos = new LinkedHashMap<>();

       // Orden oficial de visualización en el módulo Pedidos.
       grupos.put("Platos Marinos", new ArrayList<>());
       grupos.put("Sudados", new ArrayList<>());
       grupos.put("Platos Criollos", new ArrayList<>());
       grupos.put("Guarniciones", new ArrayList<>());
       grupos.put("Bebidas", new ArrayList<>());

       // Agrupa cada producto según su categoría.
       for (StockPlato item : platosDisponibles) {
           String categoria = item.getPlato().getCategoria();

           if (!grupos.containsKey(categoria)) {
               grupos.put(categoria, new ArrayList<>());
           }

           grupos.get(categoria).add(item);
       }

       // Convierte el mapa en una lista de DTOs para enviarla al JSP.
       List<CategoriaStockDTO> categorias = new ArrayList<>();

       for (Map.Entry<String, List<StockPlato>> grupo : grupos.entrySet()) {
           if (!grupo.getValue().isEmpty()) {
               categorias.add(new CategoriaStockDTO(
                       grupo.getKey(),
                       obtenerClaseCategoria(grupo.getKey()),
                       grupo.getValue()
               ));
           }
       }

       return categorias;
   }

   /**
    * Define la clase CSS que tendrá cada categoría.
    *
    * Esto permite aplicar colores y separadores visuales sin poner lógica
    * compleja dentro del JSP.
    */
   private String obtenerClaseCategoria(String categoria) {
       switch (categoria) {
           case "Platos Marinos":
               return "categoria-marinos";
           case "Sudados":
               return "categoria-sudados";
           case "Platos Criollos":
               return "categoria-criollos";
           case "Guarniciones":
               return "categoria-guarniciones";
           case "Bebidas":
               return "categoria-bebidas";
           default:
               return "categoria-general";
       }
   }

    /**
     * Busca el stock real de un plato desde MySQL.
     */
    public StockPlato buscarStockPorIdPlato(int idPlato) {
        return stockService.buscarStockPorIdPlato(idPlato);
    }

    /**
     * Agrega un plato al pedido temporal que vive en sesión.
     *
     * El pedido temporal existe solo mientras el mesero arma la comanda.
     * Cuando se envía a cocina, se guarda definitivamente en MySQL.
     */
    public void agregarPlatoAlPedido(List<PedidoItemDTO> pedidoItems, int idPlato) {
        StockPlato stockPlato = buscarStockPorIdPlato(idPlato);

        if (stockPlato == null) {
            throw new IllegalArgumentException("El plato seleccionado no existe.");
        }

        if (stockPlato.getCantidadDisponible() <= 0) {
            throw new IllegalArgumentException("El plato seleccionado está agotado.");
        }

        for (PedidoItemDTO item : pedidoItems) {
            if (item.getIdPlato() == idPlato) {

                if (item.getCantidad() + 1 > stockPlato.getCantidadDisponible()) {
                    throw new IllegalArgumentException("No hay stock suficiente para agregar más unidades de este plato.");
                }

                item.incrementarCantidad();
                return;
            }
        }

        PedidoItemDTO nuevoItem = new PedidoItemDTO(
                stockPlato.getPlato().getIdPlato(),
                stockPlato.getPlato().getNombrePlato(),
                stockPlato.getPlato().getPrecio(),
                stockPlato.getPlato().getTiempoPreparacion(),
                1,
                stockPlato.getCantidadDisponible()
        );

        pedidoItems.add(nuevoItem);
    }

    /**
     * Crea un DTO listo para ser registrado como pedido real.
     */
    public PedidoCocinaDTO prepararPedidoParaRegistro(int idMesa,
                                                      List<PedidoItemDTO> pedidoItems,
                                                      String observaciones) {
        if (idMesa <= 0) {
            throw new IllegalArgumentException("Selecciona una mesa antes de enviar el pedido a cocina.");
        }

        if (pedidoItems == null || pedidoItems.isEmpty()) {
            throw new IllegalArgumentException("Agrega al menos un plato antes de enviar el pedido a cocina.");
        }

        Mesa mesa = mesaService.buscarPorId(idMesa);

        if (mesa == null) {
            throw new IllegalArgumentException("La mesa seleccionada no existe.");
        }

        if (!"LIBRE".equals(mesa.getEstado())) {
            throw new IllegalArgumentException("La mesa seleccionada ya no está libre.");
        }

        // Copia de ítems para evitar que se alteren al limpiar el pedido temporal.
        List<PedidoItemDTO> itemsCopia = new ArrayList<>(pedidoItems);

        BigDecimal subtotal = calcularSubtotal(itemsCopia).setScale(2, RoundingMode.HALF_UP);
        int tiempoEstimado = calcularTiempoEstimado(itemsCopia);

        return new PedidoCocinaDTO(
                0,
                "",
                idMesa,
                "Mesa " + mesa.getNumeroMesa(),
                itemsCopia,
                observaciones,
                tiempoEstimado,
                subtotal,
                "PENDIENTE"
        );
    }

    /**
     * Registra el pedido real en MySQL.
     *
     * Por ahora usamos idUsuarioMesero = 2 porque el login todavía es temporal.
     * Cuando conectemos login real, saldrá de la sesión del usuario.
     */
    public String registrarPedidoEnBaseDatos(PedidoCocinaDTO pedido) {
        int idUsuarioMeseroTemporal = 2;
        return pedidoDAO.registrarPedido(pedido, idUsuarioMeseroTemporal);
    }

    /**
     * Calcula la cantidad total de ítems agregados.
     */
    public int calcularTotalItems(List<PedidoItemDTO> pedidoItems) {
        int total = 0;

        for (PedidoItemDTO item : pedidoItems) {
            total += item.getCantidad();
        }

        return total;
    }

    /**
     * Calcula el tiempo estimado total.
     */
    public int calcularTiempoEstimado(List<PedidoItemDTO> pedidoItems) {
        int total = 0;

        for (PedidoItemDTO item : pedidoItems) {
            total += item.getTiempoPreparacion() * item.getCantidad();
        }

        return total;
    }

    /**
     * Calcula el subtotal del pedido.
     */
    public BigDecimal calcularSubtotal(List<PedidoItemDTO> pedidoItems) {
        BigDecimal total = BigDecimal.ZERO;

        for (PedidoItemDTO item : pedidoItems) {
            total = total.add(item.getSubtotal());
        }

        return total;
    }

    /**
     * Retorna la cantidad de platos disponibles para mostrar indicador.
     */
    public int contarPlatosDisponibles() {
        return listarPlatosDisponibles().size();
    }
}