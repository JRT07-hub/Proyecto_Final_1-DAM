package Agencia_Vista;

import Agencia_DAO.ClienteDAO;
import Agencia_DTO.ClienteDTO;
import Agencia_Excepciones.AgenciaException;
import java.util.List;
import java.util.Scanner;

public class ClienteVista {
    private static ClienteDAO clienteDao = new ClienteDAO();
    private static Scanner teclado = new Scanner(System.in);

    public static void ejecutar() {
        int opcion = 0;

        do {
            System.out.println("\n=============================================");
            System.out.println("           MENÚ GESTIÓN DE CLIENTES            ");
            System.out.println("=============================================");
            System.out.println("1. Registrar nuevo cliente (INSERT)");
            System.out.println("2. Buscar cliente por DNI (SELECT BY ID)");
            System.out.println("3. Modificar datos de un cliente (UPDATE)");
            System.out.println("4. Eliminar un cliente de la BD (DELETE)");
            System.out.println("5. Mostrar todos los clientes (SELECT ALL)");
            System.out.println("6. Aplicar descuento masivo VIP (PROCEDIMIENTO ALMACENADO)");
            System.out.println("7. Volver al menú principal");
            System.out.println("=============================================");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = Integer.parseInt(teclado.nextLine());

                switch (opcion) {
                    case 1: menuInsertar(); break;
                    case 2: menuBuscarPorId(); break;
                    case 3: menuModificar(); break;
                    case 4: menuEliminar(); break;
                    case 5: menuListarTodos(); break;
                    case 6: menuProcedimientoDescuento(); break;
                    case 7: System.out.println("Saliendo del gestor de clientes..."); break;
                    default: System.out.println("Opción no válida. Inténtelo de nuevo.");
                }
            } catch (NumberFormatException e) {
                System.out.println("\n[ERROR VISTA] Por favor, introduzca un número válido para la opción.");
            }
        } while (opcion != 7);
    }

    // =========================================================================
    // 1. CASO DE PRUEBA: INSERTAR
    // =========================================================================
    private static void menuInsertar() {
        System.out.println("\n--- REGISTRAR NUEVO CLIENTE ---");
        System.out.print("Nombre Completo: ");
        String nombre = teclado.nextLine();
        System.out.print("DNI (8 números y 1 letra): ");
        String dni = teclado.nextLine();
        System.out.print("Correo electrónico: ");
        String correo = teclado.nextLine();
        System.out.print("Teléfono (9 dígitos): ");
        String tlf = teclado.nextLine();
        System.out.print("Dirección de residencia: ");
        String dir = teclado.nextLine();
        System.out.print("Número de Pasaporte (Presione ENTER si no tiene): ");
        String pasaporte = teclado.nextLine();
        if (pasaporte.trim().isEmpty()) pasaporte = null;

        try {
            // El constructor de tu ClienteDTO ejecutará automáticamente su método validar()
            ClienteDTO nuevo = new ClienteDTO(nombre, dni, correo, tlf, dir, pasaporte);
            
            System.out.println("Conectando con la base de datos...");
            clienteDao.insertar(nuevo);
            System.out.println("\n[ÉXITO] ¡Cliente registrado correctamente en MySQL!");
        } catch (AgenciaException e) {
            System.out.println("\n[FALLO] No se pudo insertar: " + e.getMessage());
        }
    }

    // =========================================================================
    // 2. CASO DE PRUEBA: BUSCAR POR ID
    // =========================================================================
    private static void menuBuscarPorId() {
        System.out.println("\n--- BUSCAR CLIENTE POR DNI ---");
        System.out.print("Introduce el DNI a buscar: ");
        String dni = teclado.nextLine();

        try {
            ClienteDTO c = clienteDao.buscarPorId(dni);
            if (c != null) {
                System.out.println("\n[REGISTRO ENCONTRADO]");
                System.out.println("----------------------------------------");
                System.out.println("Nombre:    " + c.getNombreCompleto());
                System.out.println("DNI:       " + c.getDni());
                System.out.println("Email:     " + c.getCorreo());
                System.out.println("Teléfono:  " + c.getTelefono());
                System.out.println("Dirección: " + c.getDireccion());
                System.out.println("Pasaporte: " + (c.getPasaporte() != null ? c.getPasaporte() : "Ninguno"));
                System.out.println("----------------------------------------");
            } else {
                System.out.println("\n[AVISO] No existe ningún cliente en la BD con el DNI: " + dni);
            }
        } catch (AgenciaException e) {
            System.out.println("\n[FALLO] Error en la búsqueda: " + e.getMessage());
        }
    }

    // =========================================================================
    // 3. CASO DE PRUEBA: MODIFICAR
    // =========================================================================
    private static void menuModificar() {
        System.out.println("\n--- MODIFICAR DATOS DE CLIENTE ---");
        System.out.print("Introduce el DNI del cliente que deseas modificar: ");
        String dni = teclado.nextLine();

        try {
            // Primero verificamos si existe
            ClienteDTO c = clienteDao.buscarPorId(dni);
            if (c == null) {
                System.out.println("[AVISO] El cliente no existe en la base de datos.");
                return;
            }

            System.out.println("Modificando los datos de: " + c.getNombreCompleto());
            System.out.print("Nuevo Nombre Completo: ");
            c.setNombreCompleto(teclado.nextLine());
            System.out.print("Nuevo Correo: ");
            c.setCorreo(teclado.nextLine());
            System.out.print("Nuevo Teléfono: ");
            c.setTelefono(teclado.nextLine());
            System.out.print("Nueva Dirección: ");
            c.setDireccion(teclado.nextLine());
            System.out.print("Nuevo Pasaporte (ENTER si no tiene): ");
            String pas = teclado.nextLine();
            c.setPasaporte(pas.trim().isEmpty() ? null : pas);

            // Validamos que cumpla las reglas de negocio en Java antes de enviar a MySQL
            c.validar();

            clienteDao.modificar(c);
            System.out.println("\n[ÉXITO] ¡Datos actualizados correctamente en MySQL!");
        } catch (AgenciaException e) {
            System.out.println("\n[FALLO] No se pudo actualizar: " + e.getMessage());
        }
    }

    // =========================================================================
    // 4. CASO DE PRUEBA: ELIMINAR
    // =========================================================================
    private static void menuEliminar() {
        System.out.println("\n--- ELIMINAR CLIENTE ---");
        System.out.print("Introduce el DNI del cliente a dar de baja: ");
        String dni = teclado.nextLine();

        try {
            System.out.print("¿Está completamente seguro de borrar este cliente? (S/N): ");
            String confirmacion = teclado.nextLine();
            
            if (confirmacion.equalsIgnoreCase("S")) {
                clienteDao.eliminar(dni);
                System.out.println("\n[ÉXITO] Cliente borrado de la base de datos.");
            } else {
                System.out.println("Operación cancelada.");
            }
        } catch (AgenciaException e) {
            System.out.println("\n[FALLO] No se pudo eliminar: " + e.getMessage());
        }
    }

    // =========================================================================
    // 5. CASO DE PRUEBA: LISTAR TODOS
    // =========================================================================
    private static void menuListarTodos() {
        System.out.println("\n--- CATALOGO COMPLETO DE CLIENTES ---");
        try {
            List<ClienteDTO> lista = clienteDao.listarTodos();
            if (lista.isEmpty()) {
                System.out.println("La tabla 'cliente' está vacía en MySQL.");
            } else {
                System.out.println("Clientes totales encontrados: " + lista.size());
                System.out.println("---------------------------------------------------------------------------------");
                System.out.printf("%-20s | %-10s | %-20s | %-9s | %-10s\n", "NOMBRE", "DNI", "CORREO", "TELÉFONO", "PASAPORTE");
                System.out.println("---------------------------------------------------------------------------------");
                for (ClienteDTO c : lista) {
                    System.out.printf("%-20s | %-10s | %-20s | %-9s | %-10s\n", 
                        c.getNombreCompleto(), 
                        c.getDni(), 
                        c.getCorreo(), 
                        c.getTelefono(), 
                        (c.getPasaporte() != null ? c.getPasaporte() : "N/A"));
                }
                System.out.println("---------------------------------------------------------------------------------");
            }
        } catch (AgenciaException e) {
            System.out.println("\n[FALLO] No se pudo recuperar el listado: " + e.getMessage());
        }
    }

    // =========================================================================
    // 6. CASO DE PRUEBA: PROCEDIMIENTO ALMACENADO (CallableStatement)
    // =========================================================================
    private static void menuProcedimientoDescuento() {
        System.out.println("\n--- EJECUTAR SCRIPT PROCEDIMIENTO ALMACENADO ---");
        System.out.print("Introduce el porcentaje de descuento VIP masivo a aplicar en BD (Ej: 15.0): ");
        
        try {
            double porcentaje = Double.parseDouble(teclado.nextLine());
            if (porcentaje <= 0 || porcentaje > 100) {
                System.out.println("[ERROR VISTA] El porcentaje debe estar entre 1 y 100.");
                return;
            }

            System.out.println("Invocando {CALL sp_AplicarDescuentoFidelidad(?)} en MySQL...");
            clienteDao.aplicarDescuentoVIPEnBD(porcentaje);
            System.out.println("\n[ÉXITO PROCEDIMIENTO] El descuento masivo ha sido ejecutado con éxito en el servidor.");
        } catch (NumberFormatException e) {
            System.out.println("[ERROR VISTA] Debe introducir un número decimal válido.");
        } catch (AgenciaException e) {
            System.out.println("\n[FALLO BASE DE DATOS] Error al ejecutar sp_AplicarDescuentoFidelidad: " + e.getMessage());
        }
    }
}