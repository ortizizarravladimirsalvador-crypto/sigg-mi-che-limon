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
import pe.edu.utp.sigg.model.Mesa;
import pe.edu.utp.sigg.util.ConexionBD;

/**
 * DAO encargado de acceder a la tabla mesas en MySQL.
 *
 * DAO significa Data Access Object.
 * Su responsabilidad es ejecutar consultas SQL y convertir resultados
 * de la base de datos en objetos Java.
 */
public class MesaDAO {

    /**
     * Lista todas las mesas registradas en la base de datos.
     *
     * @return Lista de mesas desde MySQL.
     */
    public List<Mesa> listarMesas() {
        List<Mesa> mesas = new ArrayList<>();

        String sql = """
                     SELECT id_mesa, numero_mesa, capacidad, estado
                     FROM mesas
                     ORDER BY numero_mesa
                     """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Mesa mesa = new Mesa();

                mesa.setIdMesa(rs.getInt("id_mesa"));
                mesa.setNumeroMesa(rs.getInt("numero_mesa"));
                mesa.setCapacidad(rs.getInt("capacidad"));
                mesa.setEstado(rs.getString("estado"));

                mesas.add(mesa);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar mesas desde la base de datos.", e);
        }

        return mesas;
    }

    /**
     * Cambia el estado de una mesa.
     *
     * Ejemplo:
     * LIBRE → OCUPADA
     * OCUPADA → LIBRE
     *
     * @param idMesa ID de la mesa.
     * @param nuevoEstado Estado nuevo de la mesa.
     */
    public void actualizarEstadoMesa(int idMesa, String nuevoEstado) {
        String sql = """
                     UPDATE mesas
                     SET estado = ?
                     WHERE id_mesa = ?
                     """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            ps.setInt(2, idMesa);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el estado de la mesa.", e);
        }
    }

    /**
     * Busca una mesa específica por su ID.
     *
     * @param idMesa ID de la mesa.
     * @return Mesa encontrada o null si no existe.
     */
    public Mesa buscarPorId(int idMesa) {
        String sql = """
                     SELECT id_mesa, numero_mesa, capacidad, estado
                     FROM mesas
                     WHERE id_mesa = ?
                     """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idMesa);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Mesa mesa = new Mesa();

                    mesa.setIdMesa(rs.getInt("id_mesa"));
                    mesa.setNumeroMesa(rs.getInt("numero_mesa"));
                    mesa.setCapacidad(rs.getInt("capacidad"));
                    mesa.setEstado(rs.getString("estado"));

                    return mesa;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar mesa por ID.", e);
        }

        return null;
    }
}