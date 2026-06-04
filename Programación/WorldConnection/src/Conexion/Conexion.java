package Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
* Utilidad de infraestructura para la gestión de conexiones con la Base de Datos.
* * <p>Aplica un diseño centralizado para cargar el controlador JDBC de MySQL y 
* establecer canales de comunicación activos con el esquema local del servidor.</p>
*/
public class Conexion {
	/** Dirección URL estándar de conexión JDBC que apunta al servidor local MySQL y a la BD 'agencia_viajes'. */
    private static final String URL = "jdbc:mysql://localhost:3306/agencia_viajes";
    /** Nombre de usuario por defecto para la autenticación en el servidor MySQL. */
    private static final String USER = "root";
    /** Contraseña asociada al usuario de la base de datos (por defecto vacía en entornos locales). */
    private static final String PASSWORD = ""; 
    /**
     * Intenta abrir y retornar una conexión física activa con la base de datos MySQL.
     * * <p>El método utiliza el {@link DriverManager} para negociar el acceso. Si la conexión 
     * falla por credenciales incorrectas o porque el servidor está apagado, captura el error
     * silenciosamente en la consola de error del sistema (stderr) y retorna {@code null}.</p>
     * * @return Un objeto {@link Connection} listo para ejecutar sentencias SQL, o {@code null} si la conexión no pudo establecerse.
     */
    public static Connection getConexion() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
        return con;
    }
}
