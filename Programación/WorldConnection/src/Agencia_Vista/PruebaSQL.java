package Agencia_Vista;

import Agencia_DAO.ClienteDAO;
import Agencia_DTO.ClienteDTO;
import Agencia_Excepciones.AgenciaException;

public class PruebaSQL {
    public static void main(String[] args) {
        ClienteDAO clienteDao = new ClienteDAO();

        try {
            // 1. Creamos un objeto DTO de prueba en memoria RAM
            ClienteDTO nuevoCliente = new ClienteDTO("Carlos Pérez", "11223344B", "carlos@gmail.com", "600112233", "Calle Mayor 10");
            
            System.out.println("Intentando conectar e insertar en MySQL...");
            
            // 2. Ejecutamos el SQL a través del DAO
            clienteDao.insertar(nuevoCliente);
            
            // SI LLEGA A ESTA LÍNEA, SIGNIFICA QUE EL SQL FUNCIONA PERFECTAMENTE
            System.out.println("[ÉXITO] El SQL ha funcionado. Cliente guardado en la Base de Datos.");

            // 3. Probamos a buscarlo para confirmar que la lectura también funciona
            ClienteDTO buscado = clienteDao.buscarPorId("11223344B");
            if (buscado != null) {
                System.out.println("[ÉXITO READ] Recuperado de la BD: " + buscado.getNombreCompleto() + " - Correo: " + buscado.getCorreo());
            }

        } catch (AgenciaException e) {
            // SI LLEGA AQUÍ, EL SQL HA FALLADO Y ESTE MENSAJE TE DIRÁ POR QUÉ
            System.out.println("[ERROR EN TU SQL o BD] " + e.getMessage());
        }
    }
}