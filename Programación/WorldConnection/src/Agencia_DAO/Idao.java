package Agencia_DAO;

import Agencia_Excepciones.AgenciaException;
import java.util.List;

/**
 * Interfaz genérica que define las operaciones CRUD (Create, Read, Update, Delete)
 * fundamentales para el patrón Data Access Object (DAO).
 *
 * @param <T> El tipo de Data Transfer Object (DTO) que gestionará el DAO.
 * @param <K> El tipo de dato de la clave primaria (ID) utilizada para las búsquedas y eliminaciones.
 */
public interface Idao<T, K> {
    
    /**
     * Inserta un nuevo registro de tipo T en la base de datos.
     *
     * @param dto El objeto con los datos a persistir.
     * @throws AgenciaException Si ocurre un error de acceso, restricción o conexión en la base de datos.
     */
    void insertar(T dto) throws AgenciaException;

    /**
     * Modifica los datos de un registro existente en la base de datos.
     *
     * @param dto El objeto con los datos actualizados. El identificador interno debe coincidir con el registro a modificar.
     * @throws AgenciaException Si ocurre un error durante la actualización en la base de datos.
     */
    void modificar(T dto) throws AgenciaException;

    /**
     * Elimina un registro de la base de datos a partir de su clave primaria.
     *
     * @param id La clave primaria del registro que se desea eliminar.
     * @throws AgenciaException Si el registro tiene dependencias activas o si falla la sentencia SQL.
     */
    void eliminar(K id) throws AgenciaException;

    /**
     * Busca y recupera un registro de la base de datos utilizando su clave primaria.
     *
     * @param id La clave primaria del registro a buscar.
     * @return El objeto DTO correspondiente si se encuentra; {@code null} en caso contrario.
     * @throws AgenciaException Si se produce un error en la ejecución de la consulta SQL.
     */
    T buscarPorId(K id) throws AgenciaException;

    /**
     * Recupera todos los registros existentes de la tabla correspondiente en la base de datos.
     *
     * @return Una lista {@link List} que contiene todos los objetos DTO mapeados; la lista estará vacía si no hay registros.
     * @throws AgenciaException Si se produce un error al leer los datos de la base de datos.
     */
    List<T> listarTodos() throws AgenciaException;
}