package pe.edu.utp.sigg.model;
/**
 * @author FRANCK
 */
import java.math.BigDecimal;

/**
 * Clase modelo que representa un plato del menú del restaurante.
 *
 * En el sistema SIGG, un plato tiene nombre, categoría, precio
 * y tiempo estimado de preparación.
 */
public class Plato {

    // Identificador único del plato.
    private int idPlato;

    // Nombre comercial del plato.
    private String nombrePlato;

    // Categoría del plato: cebiche, arroz, fritura, bebida, etc.
    private String categoria;

    // Precio de venta del plato.
    private BigDecimal precio;

    // Tiempo aproximado de preparación en minutos.
    private int tiempoPreparacion;
    
    // Unidad de medida del producto: PLATO, PORCION, LITRO o BOTELLA.
    private String unidadMedida;

    /**
     * Constructor vacío requerido para crear objetos sin datos iniciales.
     */
    
    public Plato() {
    }

    /**
     * Constructor completo para crear platos con sus datos principales.
     */
    public Plato(int idPlato, String nombrePlato, String categoria, BigDecimal precio, int tiempoPreparacion, String unidadMedida) {
        this.idPlato = idPlato;
        this.nombrePlato = nombrePlato;
        this.categoria = categoria;
        this.precio = precio;
        this.tiempoPreparacion = tiempoPreparacion;
        this.unidadMedida = unidadMedida;
    }

    public int getIdPlato() {
        return idPlato;
    }

    public void setIdPlato(int idPlato) {
        this.idPlato = idPlato;
    }

    public String getNombrePlato() {
        return nombrePlato;
    }

    public void setNombrePlato(String nombrePlato) {
        this.nombrePlato = nombrePlato;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public int getTiempoPreparacion() {
        return tiempoPreparacion;
    }

    public void setTiempoPreparacion(int tiempoPreparacion) {
        this.tiempoPreparacion = tiempoPreparacion;
    }
    
    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }
}