package pe.edu.utp.sigg.dao;
/**
 * @author FRANCK
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pe.edu.utp.sigg.model.Plato;
import pe.edu.utp.sigg.model.StockPlato;
import pe.edu.utp.sigg.util.ConexionBD;

/**
 * DAO encargado de acceder al stock diario de platos en MySQL.
 *
 * Esta clase consulta la tabla stock_diario_plato y la une con platos
 * y categorias_plato para construir objetos completos que pueda usar el sistema.
 */
public class StockDAO {

    /**
     * Lista el stock diario de platos correspondiente al día actual.
     *
     * Usa CURDATE() para obtener solo el stock de la jornada actual.
     *
     * @return lista de platos con su stock diario.
     */
    public List<StockPlato> listarStockDiarioActual() {
        List<StockPlato> stock = new ArrayList<>();

        String sql = """
                     SELECT 
                         s.id_stock,
                         s.cantidad_inicial,
                         s.cantidad_disponible,
                         s.estado_jornada,
                         p.id_plato,
                         p.nombre_plato,
                         c.nombre_categoria,
                         p.precio,
                         p.tiempo_preparacion_min,
                         p.unidad_medida
                     FROM stock_diario_plato s
                     INNER JOIN platos p ON s.id_plato = p.id_plato
                     INNER JOIN categorias_plato c ON p.id_categoria = c.id_categoria
                     WHERE s.fecha = CURDATE()
                       AND p.estado = 'A'
                     ORDER BY p.id_plato
                     """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                // Crea el plato asociado al registro de stock.
                Plato plato = new Plato(
                        rs.getInt("id_plato"),
                        rs.getString("nombre_plato"),
                        rs.getString("nombre_categoria"),
                        rs.getBigDecimal("precio"),
                        rs.getInt("tiempo_preparacion_min"),
                        rs.getString("unidad_medida")
                );

                // Crea el objeto de stock diario con el plato incluido.
                StockPlato item = new StockPlato(
                        rs.getInt("id_stock"),
                        plato,
                        rs.getInt("cantidad_inicial"),
                        rs.getInt("cantidad_disponible")
                );

                stock.add(item);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar stock diario desde la base de datos.", e);
        }

        return stock;
    }

    /**
     * Busca el stock diario de un plato específico.
     *
     * Se usará más adelante al registrar pedidos para validar disponibilidad.
     *
     * @param idPlato ID del plato.
     * @return StockPlato encontrado o null si no existe stock para hoy.
     */
    public StockPlato buscarStockActualPorPlato(int idPlato) {
        String sql = """
                     SELECT 
                         s.id_stock,
                         s.cantidad_inicial,
                         s.cantidad_disponible,
                         p.id_plato,
                         p.nombre_plato,
                         c.nombre_categoria,
                         p.precio,
                         p.tiempo_preparacion_min,
                         p.unidad_medida
                     FROM stock_diario_plato s
                     INNER JOIN platos p ON s.id_plato = p.id_plato
                     INNER JOIN categorias_plato c ON p.id_categoria = c.id_categoria
                     WHERE s.fecha = CURDATE()
                       AND p.id_plato = ?
                       AND p.estado = 'A'
                     """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idPlato);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Plato plato = new Plato(
                            rs.getInt("id_plato"),
                            rs.getString("nombre_plato"),
                            rs.getString("nombre_categoria"),
                            rs.getBigDecimal("precio"),
                            rs.getInt("tiempo_preparacion_min"),
                            rs.getString("unidad_medida")
                    );

                    return new StockPlato(
                            rs.getInt("id_stock"),
                            plato,
                            rs.getInt("cantidad_inicial"),
                            rs.getInt("cantidad_disponible")
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar stock del plato en la base de datos.", e);
        }

        return null;
    }

    /**
     * Descuenta stock disponible de un plato.
     *
     * Este método se usará cuando el pedido sea enviado a cocina.
     *
     * @param idPlato ID del plato vendido.
     * @param cantidad cantidad a descontar.
     */
    public void descontarStock(int idPlato, int cantidad) {
        String sql = """
                     UPDATE stock_diario_plato
                     SET cantidad_disponible = cantidad_disponible - ?
                     WHERE id_plato = ?
                       AND fecha = CURDATE()
                       AND cantidad_disponible >= ?
                     """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, cantidad);
            ps.setInt(2, idPlato);
            ps.setInt(3, cantidad);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new RuntimeException("No hay stock suficiente para descontar el plato seleccionado.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al descontar stock del plato.", e);
        }
    }
        /**
     * Verifica si ya existe stock registrado para la fecha actual.
     *
     * Esto permite saber si el administrador ya inició la jornada de stock.
     *
     * @return true si existe stock para hoy; false si no existe.
     */
    public boolean existeStockParaHoy() {
        String sql = """
                     SELECT COUNT(*) AS total
                     FROM stock_diario_plato
                     WHERE fecha = CURDATE()
                     """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total") > 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar si existe stock para hoy.", e);
        }

        return false;
    }

    /**
     * Inicia la jornada copiando la configuración base al stock diario.
     *
     * Toma los datos de configuracion_stock_plato y crea los registros
     * correspondientes en stock_diario_plato para la fecha actual.
     *
     * Ejemplo:
     * configuracion_stock_plato:
     * Ceviche Personal = 20
     *
     * stock_diario_plato de hoy:
     * Ceviche Personal = inicial 20, disponible 20
     */
    public void iniciarJornadaDesdeConfiguracionBase() {
        String sql = """
                     INSERT INTO stock_diario_plato (
                         id_plato,
                         fecha,
                         cantidad_inicial,
                         cantidad_disponible,
                         estado_jornada
                     )
                     SELECT
                         c.id_plato,
                         CURDATE(),
                         c.cantidad_base,
                         c.cantidad_base,
                         'ACTIVO'
                     FROM configuracion_stock_plato c
                     INNER JOIN platos p ON c.id_plato = p.id_plato
                     WHERE c.activo_base = 'S'
                       AND p.estado = 'A'
                     """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al iniciar la jornada de stock desde configuración base.", e);
        }
    }
}