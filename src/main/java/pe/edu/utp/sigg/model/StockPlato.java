package pe.edu.utp.sigg.model;
/**
 * @author FRANCK
 */
/**
 * Clase modelo que representa el stock diario de un producto de carta.
 *
 * Puede representar platos, guarniciones, litros de bebida o botellas.
 */
public class StockPlato {

    // Identificador del registro de stock diario.
    private int idStock;

    // Producto asociado al stock.
    private Plato plato;

    // Cantidad definida al iniciar la jornada.
    private int cantidadInicial;

    // Cantidad disponible durante la jornada.
    private int cantidadDisponible;

    // Estado del producto en la jornada: ACTIVO o INACTIVO.
    private String estadoJornada;

    public StockPlato() {
    }

    /**
     * Constructor antiguo, se mantiene para compatibilidad.
     */
    public StockPlato(int idStock, Plato plato, int cantidadInicial, int cantidadDisponible) {
        this.idStock = idStock;
        this.plato = plato;
        this.cantidadInicial = cantidadInicial;
        this.cantidadDisponible = cantidadDisponible;
        this.estadoJornada = "ACTIVO";
    }

    /**
     * Constructor completo con estado de jornada.
     */
    public StockPlato(int idStock, Plato plato, int cantidadInicial, int cantidadDisponible, String estadoJornada) {
        this.idStock = idStock;
        this.plato = plato;
        this.cantidadInicial = cantidadInicial;
        this.cantidadDisponible = cantidadDisponible;
        this.estadoJornada = estadoJornada;
    }

    /**
     * Calcula cuánto se ha vendido o consumido.
     */
    public int getCantidadVendida() {
        return cantidadInicial - cantidadDisponible;
    }

    /**
     * Calcula el porcentaje disponible para la barra visual.
     */
    public int getPorcentajeDisponible() {
        if (cantidadInicial <= 0) {
            return 0;
        }

        return (cantidadDisponible * 100) / cantidadInicial;
    }

    /**
     * Estado funcional del stock.
     */
    public String getEstado() {
        if ("INACTIVO".equals(estadoJornada)) {
            return "INACTIVO";
        }

        if (cantidadDisponible <= 0) {
            return "AGOTADO";
        }

        if (cantidadDisponible <= 5) {
            return "CRITICO";
        }

        return "DISPONIBLE";
    }

    /**
     * Clase CSS basada en el estado.
     */
    public String getClaseEstado() {
        return getEstado().toLowerCase();
    }

    public int getIdStock() {
        return idStock;
    }

    public void setIdStock(int idStock) {
        this.idStock = idStock;
    }

    public Plato getPlato() {
        return plato;
    }

    public void setPlato(Plato plato) {
        this.plato = plato;
    }

    public int getCantidadInicial() {
        return cantidadInicial;
    }

    public void setCantidadInicial(int cantidadInicial) {
        this.cantidadInicial = cantidadInicial;
    }

    public int getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(int cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public String getEstadoJornada() {
        return estadoJornada;
    }

    public void setEstadoJornada(String estadoJornada) {
        this.estadoJornada = estadoJornada;
    }
}