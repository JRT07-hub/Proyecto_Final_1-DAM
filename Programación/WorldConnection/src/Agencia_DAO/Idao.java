package Agencia_DAO;

import Agencia_Excepciones.AgenciaException;
import java.util.List;

public interface Idao<T, K> {
    void insertar(T dto) throws AgenciaException;
    void modificar(T dto) throws AgenciaException;
    void eliminar(K id) throws AgenciaException;
    T buscarPorId(K id) throws AgenciaException;
    List<T> listarTodos() throws AgenciaException;
}
