package pe.edu.utp.sigg.dao;
/**
 * @author FRANCK
 */
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import pe.edu.utp.sigg.dto.PedidoCocinaDTO;
import pe.edu.utp.sigg.dto.PedidoItemDTO;
import pe.edu.utp.sigg.util.ConexionBD;

/**
 * DAO encargado de registrar y consultar pedidos en MySQL.
 *
 * Responsabilidades principales:
 * - Insertar pedidos.
 * - Insertar detalle de pedido.
 * - Descontar stock.
 * - Cambiar mesa a OCUPADA.
 * - Listar pedidos para cocina.
 */
public class PedidoDAO {

    /**
     * Registra un pedido completo en la base de datos.
     *
     * Este método usa transacción porque varias operaciones deben ejecutarse juntas:
     * 1. Crear pedido.
     * 2. Crear detalle.
     * 3. Descontar stock.
     * 4. Cambiar mesa a OCUPADA.
     *
     * Si algo falla, se hace rollback y no queda información incompleta.
     */
    public String registrarPedido(PedidoCocinaDTO pedido, int idUsuarioMesero) {
        String codigoPedido = generarCodigoPedido();

        String sqlPedido = """
                           INSERT INTO pedidos (
                               codigo_pedido,
                               id_mesa,
                               id_usuario_mesero,
                               estado,
                               observaciones,
                               tiempo_estimado_min,
                               subtotal
                           )
                           VALUES (?, ?, ?, 'PENDIENTE', ?, ?, ?)
                           """;

        String sqlDetalle = """
                            INSERT INTO detalle_pedido (
                                id_pedido,
                                id_plato,
                                cantidad,
                                precio_unitario,
                                subtotal,
                                tiempo_estimado_min
                            )
                            VALUES (?, ?, ?, ?, ?, ?)
                            """;

        String sqlDescontarStock = """
                                   UPDATE stock_diario_plato
                                   SET cantidad_disponible = cantidad_disponible - ?
                                   WHERE id_plato = ?
                                     AND fecha = CURDATE()
                                     AND cantidad_disponible >= ?
                                   """;

        String sqlActualizarMesa = """
                                   UPDATE mesas
                                   SET estado = 'OCUPADA'
                                   WHERE id_mesa = ?
                                   """;

        try (Connection conexion = ConexionBD.obtenerConexion()) {

            conexion.setAutoCommit(false);

            try {
                int idPedidoGenerado;

                // 1. Registra la cabecera del pedido.
                try (PreparedStatement psPedido = conexion.prepareStatement(sqlPedido, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    psPedido.setString(1, codigoPedido);
                    psPedido.setInt(2, pedido.getIdMesa());
                    psPedido.setInt(3, idUsuarioMesero);
                    psPedido.setString(4, pedido.getObservaciones());
                    psPedido.setInt(5, pedido.getTiempoEstimado());
                    psPedido.setBigDecimal(6, pedido.getSubtotal());

                    psPedido.executeUpdate();

                    try (ResultSet rs = psPedido.getGeneratedKeys()) {
                        if (rs.next()) {
                            idPedidoGenerado = rs.getInt(1);
                        } else {
                            throw new SQLException("No se pudo obtener el ID del pedido generado.");
                        }
                    }
                }

                // 2. Registra cada plato del pedido y descuenta stock.
                for (PedidoItemDTO item : pedido.getItems()) {

                    BigDecimal subtotalItem = item.getSubtotal();
                    int tiempoItem = item.getTiempoPreparacion() * item.getCantidad();

                    try (PreparedStatement psDetalle = conexion.prepareStatement(sqlDetalle)) {
                        psDetalle.setInt(1, idPedidoGenerado);
                        psDetalle.setInt(2, item.getIdPlato());
                        psDetalle.setInt(3, item.getCantidad());
                        psDetalle.setBigDecimal(4, item.getPrecio());
                        psDetalle.setBigDecimal(5, subtotalItem);
                        psDetalle.setInt(6, tiempoItem);
                        psDetalle.executeUpdate();
                    }

                    try (PreparedStatement psStock = conexion.prepareStatement(sqlDescontarStock)) {
                        psStock.setInt(1, item.getCantidad());
                        psStock.setInt(2, item.getIdPlato());
                        psStock.setInt(3, item.getCantidad());

                        int filasStock = psStock.executeUpdate();

                        if (filasStock == 0) {
                            throw new SQLException("No hay stock suficiente para el plato: " + item.getNombrePlato());
                        }
                    }
                }

                // 3. Cambia la mesa a OCUPADA.
                try (PreparedStatement psMesa = conexion.prepareStatement(sqlActualizarMesa)) {
                    psMesa.setInt(1, pedido.getIdMesa());
                    psMesa.executeUpdate();
                }

                conexion.commit();
                return codigoPedido;

            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            } finally {
                conexion.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar el pedido en la base de datos.", e);
        }
    }

    /**
     * Genera un código visual para el pedido.
     *
     * Ejemplo:
     * P-001, P-002, P-003.
     */
    private String generarCodigoPedido() {
        String sql = "SELECT COALESCE(MAX(id_pedido), 0) + 1 AS siguiente FROM pedidos";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                int siguiente = rs.getInt("siguiente");
                return String.format("P-%03d", siguiente);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al generar código de pedido.", e);
        }

        return "P-001";
    }

    /**
     * Lista los pedidos que deben verse en cocina.
     *
     * Estados considerados:
     * - PENDIENTE
     * - EN_PREPARACION
     * - LISTO
     */
    public List<PedidoCocinaDTO> listarPedidosParaCocina() {
        Map<Integer, PedidoCocinaDTO> pedidosMap = new LinkedHashMap<>();

        String sql = """
                     SELECT
                         p.id_pedido,
                         p.codigo_pedido,
                         p.id_mesa,
                         CONCAT('Mesa ', m.numero_mesa) AS nombre_mesa,
                         p.observaciones,
                         p.tiempo_estimado_min,
                         p.subtotal AS subtotal_pedido,
                         p.estado,
                         d.id_plato,
                         pl.nombre_plato,
                         d.precio_unitario,
                         pl.tiempo_preparacion_min,
                         d.cantidad
                     FROM pedidos p
                     INNER JOIN mesas m ON p.id_mesa = m.id_mesa
                     INNER JOIN detalle_pedido d ON p.id_pedido = d.id_pedido
                     INNER JOIN platos pl ON d.id_plato = pl.id_plato
                     WHERE p.estado IN ('PENDIENTE', 'EN_PREPARACION', 'LISTO')
                     ORDER BY p.fecha_creacion ASC, p.id_pedido ASC
                     """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int idPedido = rs.getInt("id_pedido");

                PedidoCocinaDTO pedido = pedidosMap.get(idPedido);

                if (pedido == null) {
                    pedido = new PedidoCocinaDTO(
                            idPedido,
                            rs.getString("codigo_pedido"),
                            rs.getInt("id_mesa"),
                            rs.getString("nombre_mesa"),
                            new ArrayList<>(),
                            rs.getString("observaciones"),
                            rs.getInt("tiempo_estimado_min"),
                            rs.getBigDecimal("subtotal_pedido"),
                            rs.getString("estado")
                    );

                    pedidosMap.put(idPedido, pedido);
                }

                PedidoItemDTO item = new PedidoItemDTO(
                        rs.getInt("id_plato"),
                        rs.getString("nombre_plato"),
                        rs.getBigDecimal("precio_unitario"),
                        rs.getInt("tiempo_preparacion_min"),
                        rs.getInt("cantidad"),
                        0
                );

                pedido.getItems().add(item);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar pedidos para cocina.", e);
        }

        return new ArrayList<>(pedidosMap.values());
    }

    /**
     * Cuenta los pedidos activos.
     *
     * Se usará luego para el dashboard.
     */
    public int contarPedidosActivos() {
        String sql = """
                     SELECT COUNT(*) AS total
                     FROM pedidos
                     WHERE estado IN ('PENDIENTE', 'EN_PREPARACION', 'LISTO')
                     """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al contar pedidos activos.", e);
        }

        return 0;
    }
        /**
     * Cambia un pedido de PENDIENTE a EN_PREPARACION.
     *
     * Esta acción representa que cocina ya empezó a preparar el pedido.
     *
     * @param idPedido ID del pedido.
     */
    public void iniciarPreparacion(int idPedido) {
        String sql = """
                     UPDATE pedidos
                     SET estado = 'EN_PREPARACION'
                     WHERE id_pedido = ?
                       AND estado = 'PENDIENTE'
                     """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idPedido);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new RuntimeException("El pedido no puede iniciar preparación porque no está en estado PENDIENTE.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al iniciar preparación del pedido.", e);
        }
    }

    /**
     * Cambia un pedido a estado LISTO.
     *
     * Esta acción representa que cocina terminó de preparar el pedido.
     * La mesa NO se libera aquí. Se liberará cuando Caja cobre el pedido.
     *
     * @param idPedido ID del pedido.
     */
    public void marcarPedidoListo(int idPedido) {
        String sql = """
                     UPDATE pedidos
                     SET estado = 'LISTO'
                     WHERE id_pedido = ?
                       AND estado IN ('PENDIENTE', 'EN_PREPARACION')
                     """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idPedido);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new RuntimeException("El pedido no puede marcarse como listo en su estado actual.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al marcar pedido como listo.", e);
        }
    }
}