package Agencia_Excepciones;
/**
 * Excepción personalizada para el dominio de la Agencia de Viajes.
 * * <p>Esta clase centraliza la gestión de errores del sistema, abstrayendo fallos de 
 * bajo nivel (como excepciones SQL {@link java.sql.SQLException} o de formato) 
 * y transformándolas en mensajes de error controlados y comprensibles para la lógica del negocio.</p>
 */
public class AgenciaException extends Exception{
	/** * Identificador único de versión para la serialización de la clase.
     * Garantiza que el emisor y el receptor de un objeto de excepción sean compatibles.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Construye una nueva excepción con un mensaje descriptivo detallado sobre la falla.
     * * @param message Texto descriptivo que especifica la causa raíz del error (ej. "El DNI es inválido").
     */
	public AgenciaException(String message) {
		super(message);
	}
	

}
