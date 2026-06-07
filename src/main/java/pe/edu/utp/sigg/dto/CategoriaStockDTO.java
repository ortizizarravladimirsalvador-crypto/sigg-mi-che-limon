package pe.edu.utp.sigg.dto;

import java.util.List;
import pe.edu.utp.sigg.model.StockPlato;
/**
 * @author FRANCK
 */
/**
 * DTO que agrupa productos de stock por categoría.
 *
 * Se usa para que la vista pedidos.jsp no reciba una lista plana,
 * sino bloques ordenados como:
 * - Platos Marinos
 * - Sudados
 * - Platos Criollos
 * - Guarniciones
 * - Bebidas
 */
public class CategoriaStockDTO {

    // Nombre visible de la categoría.
    private String nombreCategoria;

    // Clase CSS para dar estilo visual a la categoría.
    private String claseCss;

    // Productos disponibles dentro de esta categoría.
    private List<StockPlato> productos;

    /**
     * Constructor completo del DTO.
     */
    public CategoriaStockDTO(String nombreCategoria, String claseCss, List<StockPlato> productos) {
        this.nombreCategoria = nombreCategoria;
        this.claseCss = claseCss;
        this.productos = productos;
    }

    /**
     * Retorna la cantidad de productos de la categoría.
     */
    public int getCantidadProductos() {
        return productos == null ? 0 : productos.size();
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public String getClaseCss() {
        return claseCss;
    }

    public List<StockPlato> getProductos() {
        return productos;
    }
}