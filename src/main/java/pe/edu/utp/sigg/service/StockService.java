package pe.edu.utp.sigg.service;

import java.util.ArrayList;
import java.util.List;
import pe.edu.utp.sigg.dao.StockDAO;
import pe.edu.utp.sigg.model.StockPlato;
import java.util.LinkedHashMap;
import java.util.Map;
import pe.edu.utp.sigg.dto.CategoriaStockDTO;

/**
 * Servicio encargado de manejar la lógica del stock diario.
 *
 * En la arquitectura por capas:
 * - El Servlet recibe la solicitud del navegador.
 * - El Service aplica reglas de negocio.
 * - El DAO consulta o modifica la base de datos.
 *
 * Este servicio controla:
 * - Verificar si existe stock para la jornada actual.
 * - Iniciar la jornada de stock.
 * - Listar productos disponibles.
 * - Calcular indicadores de stock.
 *
 * @author FRANCK
 */
public class StockService {

    // DAO encargado de consultar y modificar el stock en MySQL.
    private final StockDAO stockDAO = new StockDAO();

    /**
     * Verifica si ya existe stock registrado para la fecha actual.
     *
     * Esta validación evita crear dos veces la jornada del mismo día.
     *
     * @return true si ya existe stock para hoy; false si aún no existe.
     */
    public boolean existeStockParaHoy() {
        return stockDAO.existeStockParaHoy();
    }

    /**
     * Inicia la jornada de stock copiando la configuración base
     * hacia la tabla stock_diario_plato.
     *
     * Ejemplo:
     * Si la configuración base dice que Ceviche Personal tiene 20,
     * entonces al iniciar la jornada se crea el stock de hoy con 20 disponibles.
     */
    public void iniciarJornada() {
        if (existeStockParaHoy()) {
            throw new IllegalArgumentException("La jornada de stock de hoy ya fue iniciada.");
        }

        stockDAO.iniciarJornadaDesdeConfiguracionBase();
    }

    /**
     * Lista el stock diario real desde MySQL.
     *
     * Este método se usa en el módulo Stock para mostrar la disponibilidad
     * de productos de la jornada actual.
     *
     * @return lista de productos con su stock del día.
     */
    public List<StockPlato> listarStockDiario() {
        return stockDAO.listarStockDiarioActual();
    }

    /**
     * Lista solo productos activos y con cantidad disponible mayor a cero.
     *
     * Este método se usa en el módulo Pedidos para evitar que el mesero
     * agregue productos agotados o inactivos.
     *
     * @return lista de productos disponibles para vender.
     */
    public List<StockPlato> listarPlatosDisponibles() {
        List<StockPlato> stock = listarStockDiario();
        List<StockPlato> disponibles = new ArrayList<>();

        for (StockPlato item : stock) {
            boolean estaActivo = "ACTIVO".equals(item.getEstadoJornada());
            boolean tieneStock = item.getCantidadDisponible() > 0;

            if (estaActivo && tieneStock) {
                disponibles.add(item);
            }
        }

        return disponibles;
    }

    /**
     * Busca el stock actual de un producto usando su ID.
     *
     * Este método se usará al agregar productos al pedido y al descontar stock.
     *
     * @param idPlato identificador del producto.
     * @return stock encontrado o null si no existe.
     */
    public StockPlato buscarStockPorIdPlato(int idPlato) {
        return stockDAO.buscarStockActualPorPlato(idPlato);
    }

    /**
     * Descuenta stock de un producto.
     *
     * Se ejecuta cuando un pedido se registra correctamente.
     *
     * @param idPlato identificador del producto.
     * @param cantidad cantidad a descontar.
     */
    public void descontarStock(int idPlato, int cantidad) {
        stockDAO.descontarStock(idPlato, cantidad);
    }

    /**
     * Cuenta productos disponibles.
     *
     * @param stock lista de stock diario.
     * @return cantidad de productos en estado DISPONIBLE.
     */
    public int contarDisponibles(List<StockPlato> stock) {
        int total = 0;

        for (StockPlato item : stock) {
            if ("DISPONIBLE".equals(item.getEstado())) {
                total++;
            }
        }

        return total;
    }

    /**
     * Cuenta productos en estado crítico.
     *
     * @param stock lista de stock diario.
     * @return cantidad de productos en estado CRITICO.
     */
    public int contarCriticos(List<StockPlato> stock) {
        int total = 0;

        for (StockPlato item : stock) {
            if ("CRITICO".equals(item.getEstado())) {
                total++;
            }
        }

        return total;
    }

    /**
     * Cuenta productos agotados.
     *
     * @param stock lista de stock diario.
     * @return cantidad de productos en estado AGOTADO.
     */
    public int contarAgotados(List<StockPlato> stock) {
        int total = 0;

        for (StockPlato item : stock) {
            if ("AGOTADO".equals(item.getEstado())) {
                total++;
            }
        }

        return total;
    }
        /**
     * Agrupa el stock diario por categoría.
     *
     * Esta agrupación se hace en Java para mantener una arquitectura limpia:
     * el JSP solo se encarga de mostrar los datos.
     *
     * @return lista de categorías con sus productos de stock.
     */
    public List<CategoriaStockDTO> listarStockDiarioAgrupado() {
        List<StockPlato> stock = listarStockDiario();

        // LinkedHashMap mantiene el orden visual definido.
        Map<String, List<StockPlato>> grupos = new LinkedHashMap<>();

        // Orden oficial de categorías para el sistema.
        grupos.put("Platos Marinos", new ArrayList<>());
        grupos.put("Sudados", new ArrayList<>());
        grupos.put("Platos Criollos", new ArrayList<>());
        grupos.put("Guarniciones", new ArrayList<>());
        grupos.put("Bebidas", new ArrayList<>());

        // Agrupa cada producto según la categoría que viene de MySQL.
        for (StockPlato item : stock) {
            String categoria = item.getPlato().getCategoria();

            if (!grupos.containsKey(categoria)) {
                grupos.put(categoria, new ArrayList<>());
            }

            grupos.get(categoria).add(item);
        }

        // Convierte el Map en una lista de DTOs para la vista.
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
}