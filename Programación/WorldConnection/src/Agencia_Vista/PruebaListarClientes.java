package Agencia_Vista;

import Agencia_DAO.ClienteDAO;
import Agencia_DTO.ClienteDTO;
import Agencia_Excepciones.*;
import java.util.List;

public class PruebaListarClientes {
    public static void main(String[] args) {
        // 1. Instanciamos el DAO de clientes
        ClienteDAO clienteDao = new ClienteDAO();

        try {
            System.out.println("Conectando a MySQL y recuperando todos los clientes...");
            
            // 2. Llamamos al método que lee toda la tabla de la base de datos
            List<ClienteDTO> todosLosClientes = clienteDao.listarTodos();
            
            // 3. Comprobamos si la tabla está vacía en MySQL
            if (todosLosClientes.isEmpty()) {
                System.out.println("No hay ningún cliente registrado en la base de datos.");
            } else {
                System.out.println("\n=== LISTADO DE CLIENTES EN LA BASE DE DATOS ===");
                System.out.println("Total encontrados: " + todosLosClientes.size());
                System.out.println("------------------------------------------------");
                
                // 4. Recorremos la lista para mostrar los datos de cada uno
                for (ClienteDTO c : todosLosClientes) {
                    System.out.println("• Nombre: " + c.getNombreCompleto());
                    System.out.println("  DNI:    " + c.getDni());
                    System.out.println("  Email:  " + c.getCorreo());
                    System.out.println("  Tlf:    " + c.getTelefono());
                    System.out.println("  Dir:    " + c.getDireccion());
                    System.out.println("  Pasaporte: " + (c.getPasaporte() != null ? c.getPasaporte() : "No tiene"));
                    System.out.println("------------------------------------------------");
                }
            }

        } catch (AgenciaException e) {
            // Si el MySQL está apagado o la tabla no existe, saltará aquí
            System.out.println("[ERROR AL LEER DE MYSQL] " + e.getMessage());
        }
    }
}
