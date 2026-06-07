package pe.edu.utp.sigg.util;
/**
 * @autho FRANCK
 */
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase utilitaria para obtener conexiones hacia MySQL.
 *
 * Esta clase centraliza la conexión a la base de datos.
 * Así evitamos repetir usuario, contraseña y URL en cada DAO.
 */
public class ConexionBD {

    // Archivo de configuración ubicado en src/main/resources.
    private static final String CONFIG_FILE = "database.properties";

    /**
     * Constructor privado para evitar crear objetos de esta clase.
     * La conexión se obtiene usando el método estático obtenerConexion().
     */
    private ConexionBD() {
    }

    /**
     * Obtiene una conexión activa hacia la base de datos MySQL.
     *
     * Lee los datos desde database.properties:
     * - db.url
     * - db.user
     * - db.password
     *
     * @return Connection activa hacia MySQL.
     * @throws SQLException si ocurre un error al conectar.
     */
    /**
        * Obtiene una conexión activa hacia la base de datos MySQL.
        *
        * Este método carga explícitamente el driver de MySQL y luego
        * abre la conexión usando los datos del archivo database.properties.
        *
        * @return Connection activa hacia MySQL.
        * @throws SQLException si ocurre un error al conectar.
        */
       public static Connection obtenerConexion() throws SQLException {
           Properties propiedades = cargarPropiedades();

           String url = propiedades.getProperty("db.url");
           String usuario = propiedades.getProperty("db.user");
           String password = propiedades.getProperty("db.password");

           try {
               // Carga explícita del driver JDBC de MySQL.
               // Esto evita el error: No suitable driver found.
               Class.forName("com.mysql.cj.jdbc.Driver");
           } catch (ClassNotFoundException e) {
               throw new SQLException("No se encontró el driver JDBC de MySQL. Revisa el pom.xml.", e);
           }

           return DriverManager.getConnection(url, usuario, password);
       }

    /**
     * Carga el archivo database.properties desde resources.
     *
     * @return Properties con los datos de conexión.
     */
    private static Properties cargarPropiedades() {
        Properties propiedades = new Properties();

        try (InputStream input = ConexionBD.class
                .getClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {

            if (input == null) {
                throw new IllegalStateException("No se encontró el archivo " + CONFIG_FILE);
            }

            propiedades.load(input);

        } catch (IOException e) {
            throw new IllegalStateException("Error al cargar la configuración de base de datos.", e);
        }

        return propiedades;
    }
}