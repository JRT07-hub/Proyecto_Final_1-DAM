package Agencia_Vista;

import Agencia_DAO.ClienteDAO;
import Agencia_DTO.ClienteDTO;
import Agencia_Excepciones.AgenciaException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
/**
 * Interfaz de usuario por consola para la administración del módulo de Clientes.
 * <p>Además de encapsular el flujo CRUD básico mediante el {@link ClienteDAO}, 
 * esta clase implementa lógica avanzada en memoria para el filtrado mediante Streams, 
 * mapas asociativos hashed y la reordenación natural de colecciones mediante estructuras arbóreas.</p>
 */
public class ClienteVista {
    private static ClienteDAO clienteDao = new ClienteDAO();
    private static Scanner teclado = new Scanner(System.in);
    /**
     * Despliega el menú de administración de clientes en la terminal.
     * <p>Permite la gestión de perfiles de clientes y expone operaciones especiales como 
     * la aplicación masiva de descuentos VIP (Procedimientos Almacenados) y la consulta analítica 
     * estructurada de clientes internacionales utilizando agrupaciones en memoria (TreeMap).</p>
     */
    public static void ejecutar() {
        int opcion = 0;

        do {
            System.out.println("\n=============================================");
            System.out.println("           MENÚ GESTIÓN DE CLIENTES            ");
            System.out.println("=============================================");
            System.out.println("1. Registrar nuevo cliente ");
            System.out.println("2. Buscar cliente por DNI ");
            System.out.println("3. Modificar datos de un cliente ");
            System.out.println("4. Eliminar un cliente de la BD ");
            System.out.println("5. Mostrar todos los clientes ");
            System.out.println("6. Aplicar descuento masivo VIP ");
            System.out.println("7. Consultar clientes con pasaporte ");
            System.out.println("8. Volver al menú principal");
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
                    case 7: menuClientesConPasaporte(); break;
                    case 8: System.out.println("Saliendo del gestor de clientes..."); break;
                    default: System.out.println("Opción no válida. Inténtelo de nuevo.");
                }
            } catch (NumberFormatException e) {
                System.out.println("\n[ERROR VISTA] Por favor, introduzca un número válido para la opción.");
            }
        } while (opcion != 8);
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
            	lista.sort((c1, c2) -> c1.getNombreCompleto().compareToIgnoreCase(c2.getNombreCompleto()));
            	
                long conPasaporte = lista.stream().filter(c -> c.getPasaporte() != null).count();
                System.out.println("-> Clientes con pasaporte activo (Internacionales): " + conPasaporte);
                
                System.out.println("Clientes totales encontrados: " + lista.size());
                System.out.println("---------------------------------------------------------------------------------");
                System.out.printf("%-20s | %-10s | %-20s | %-9s | %-10s\n", "NOMBRE", "DNI", "CORREO", "TELÉFONO", "PASAPORTE");
                System.out.println("---------------------------------------------------------------------------------");
                Iterator<ClienteDTO> it = lista.iterator();
                while (it.hasNext()) {
                    ClienteDTO c = it.next();
                    
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
        System.out.print("Introduce el porcentaje de descuento VIP masivo a aplicar en BD: ");
        
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
 // =========================================================================
    // 7. DETECTAR Y MAPEAR CLIENTES CON PASAPORTE (Satisface Mapas y Streams)
    // =========================================================================
    private static void menuClientesConPasaporte() {
        System.out.println("\n--- DETECCIÓN DE CLIENTES CON PASAPORTE (PROCESAMIENTO EN MEMORIA) ---");
        try {
            // Recorremos la lista base desde el DAO
            List<ClienteDTO> listaCompleta = clienteDao.listarTodos();

            if (listaCompleta.isEmpty()) {
                System.out.println("No hay clientes en la base de datos.");
                return;
            }

            // [REQUISITO: STREAMS Y PROGRAMACIÓN FUNCIONAL]
            // Filtramos en Java para detectar quién tiene pasaporte activo (!= null)
            // y los empaquetamos directamente dentro de un HashMap indexado por su DNI.
            Map<String, ClienteDTO> mapaClientesHasheados = listaCompleta.stream()
                .filter(c -> c.getPasaporte() != null) // Filtro funcional (solo clientes con pasaporte)
                .collect(Collectors.toMap(
                    ClienteDTO::getDni,             // Clave del mapa (DNI)
                    c -> c,                         // Valor del mapa (El objeto cliente)
                    (existente, nuevo) -> existente,
                    HashMap::new                    // [REQUISITO: HASHMAP CUMPLIDO]
                ));

            if (mapaClientesHasheados.isEmpty()) {
                System.out.println("No se ha detectado ningún cliente con pasaporte en la base de datos.");
                return;
            }

            System.out.println("\n[INFO] Clientes con pasaporte encontrados e indexados en HashMap: " + mapaClientesHasheados.size());

            // [REQUISITO: TREEMAP CUMPLIDO]
            // Para mostrar este subgrupo ordenado alfabéticamente por su NombreCompleto sin alterar el HashMap, 
            // volcamos los elementos procesados en un TreeMap.
            Map<String, ClienteDTO> mapaClientesOrdenado = new TreeMap<>();
            
            for (ClienteDTO cliente : mapaClientesHasheados.values()) {
                mapaClientesOrdenado.put(cliente.getNombreCompleto(), cliente); // Al insertar, TreeMap ordena solo de la A a la Z
            }

            // Recorremos el TreeMap para mostrar la información en pantalla ya estructurada
            System.out.println("\nListado estructurado en memoria (vía TreeMap por Nombre Completo):");
            System.out.println("---------------------------------------------------------------------------------");
            System.out.printf("%-25s | %-10s | %-15s\n", "NOMBRE (CLAVE TREEMAP)", "DNI", "Nº PASAPORTE");
            System.out.println("---------------------------------------------------------------------------------");
            
            for (Map.Entry<String, ClienteDTO> entrada : mapaClientesOrdenado.entrySet()) {
                System.out.printf("%-25s | %-10s | %-15s\n", 
                    entrada.getKey(), 
                    entrada.getValue().getDni(), 
                    entrada.getValue().getPasaporte());
            }
            System.out.println("---------------------------------------------------------------------------------");

        } catch (AgenciaException e) {
            System.out.println("\n[ERROR INTERNO] No se pudieron procesar las estructuras: " + e.getMessage());
        }
    }
}