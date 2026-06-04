package Agencia_DTO;
import Agencia_Excepciones.AgenciaException;
/**
 * Interfaz que define el contrato de validación para los objetos de transferencia de datos (DTO).
 * Permite asegurar la integridad y consistencia de los datos antes de operar con ellos o persistirlos.
 */
public interface Validar{
	/**
     * Realiza la validación interna de los atributos del objeto.
     * * @return {@code true} si todos los campos cumplen con las restricciones de negocio.
     * @throws AgenciaException Si alguna regla de validación es vulnerada o faltan campos obligatorios.
     */
	boolean validar() throws AgenciaException;
	}
