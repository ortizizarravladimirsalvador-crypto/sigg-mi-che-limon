package pe.edu.utp.sigg.model;
/**
 *
 * @author FRANCK
 */
/**
 * Clase modelo que representa una mesa física del restaurante.
 *
 * En arquitectura MVC:
 * - Esta clase pertenece al Modelo.
 * - Sirve para transportar información de una mesa entre Service, Servlet y JSP.
 */
public class Mesa {

    // Identificador interno de la mesa.
    private int idMesa;

    // Número visible de la mesa dentro del restaurante.
    private int numeroMesa;

    // Cantidad máxima de personas que soporta la mesa.
    private int capacidad;

    // Estado actual: LIBRE, OCUPADA o RESERVADA.
    private String estado;

    /**
     * Constructor vacío requerido para crear objetos sin datos iniciales.
     */
    public Mesa() {
    }

    /**
     * Constructor completo para crear mesas con todos sus datos.
     */
    public Mesa(int idMesa, int numeroMesa, int capacidad, String estado) {
        this.idMesa = idMesa;
        this.numeroMesa = numeroMesa;
        this.capacidad = capacidad;
        this.estado = estado;
    }

    public int getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(int idMesa) {
        this.idMesa = idMesa;
    }

    public int getNumeroMesa() {
        return numeroMesa;
    }

    public void setNumeroMesa(int numeroMesa) {
        this.numeroMesa = numeroMesa;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}