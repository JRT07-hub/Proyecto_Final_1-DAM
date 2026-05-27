package Agencia_Vista;

import Agencia_DAO.EmpleadoDAO;
import Agencia_DTO.EmpleadoDTO;
import Agencia_DTO.Cargo;
import Agencia_DTO.Turno;
import Agencia_Excepciones.AgenciaException;

import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class EmpleadoVista {
    private static EmpleadoDAO empleadoDao = new EmpleadoDAO();
    private static Scanner teclado = new Scanner(System.in);

    public static void ejecutar() {
        int op = 0;
        do {
            System.out.println("\n=============================================");
            System.out.println("           MENÚ GESTIÓN DE EMPLEADOS            ");
            System.out.println("=============================================");
            System.out.println("1. Registrar nuevo empleado (Alta)");
            System.out.println("2. Ver lista completa de empleados");
            System.out.println("3. Buscar empleado por ID");
            System.out.println("4. Modificar datos de un empleado");
            System.out.println("5. Dar de baja / Eliminar empleado");
            System.out.println("6. CALCULAR COMISIÓN ACUMULADA (Métrica)");
            System.out.println("7. Volver al menú principal");
            System.out.println("=============================================");
            System.out.print("Seleccione opción: ");
            try {
                op = Integer.parseInt(teclado.nextLine());
                switch (op) {
                    case 1:
                        System.out.println("\n--- ALTA DE EMPLEADO ---");
                        System.out.print("ID Empleado (Numérico): "); int id = Integer.parseInt(teclado.nextLine());
                        System.out.print("Nombre Completo: "); String nom = teclado.nextLine();
                        System.out.print("Cargo (AGENTE_JUNIOR, AGENTE_SENIOR, GERENTE...): "); 
                        String cargoIn = teclado.nextLine().trim().replace(" ", "_").toUpperCase();
                        System.out.print("Especialidad: "); String esp = teclado.nextLine();
                        System.out.print("Turno (MAÑANA o TARDE): "); 
                        String turnoIn = teclado.nextLine().trim().toUpperCase();
                        System.out.print("Años Experiencia: "); int exp = Integer.parseInt(teclado.nextLine());

                        EmpleadoDTO nuevo = new EmpleadoDTO(nom, id, Cargo.valueOf(cargoIn), esp, Turno.valueOf(turnoIn), exp);
                        empleadoDao.insertar(nuevo);
                        System.out.println("[ÉXITO] Guardado correctamente en la BD.");
                        break;

                    case 2:
                        System.out.println("\n--- LISTADO GENERAL DE EMPLEADOS ---");
                        List<EmpleadoDTO> lista = empleadoDao.listarTodos();
                        if(lista.isEmpty()) System.out.println("No hay empleados registrados.");
                        else {
                        	Iterator<EmpleadoDTO> it = lista.iterator();
                        while (it.hasNext()) {
                            EmpleadoDTO e = it.next();
                            System.out.println("• ID: " + e.getID_Empleado() + " | " + e.getNombreCompleto() + " [" + e.getCargo() + "] - Exp: " + e.getAnios_experiencia() + " años");
                        }
                        }
                            break;

                    case 3:
                        System.out.println("\n--- BUSCAR EMPLEADO ---");
                        System.out.print("Introduce ID: "); int idB = Integer.parseInt(teclado.nextLine());
                        EmpleadoDTO empB = empleadoDao.buscarPorId(idB);
                        if(empB != null) {
                            System.out.println("-> Nombre: " + empB.getNombreCompleto() + " | Cargo: " + empB.getCargo() + " | Turno: " + empB.getTurno());
                        } else System.out.println("[AVISO] Empleado no encontrado.");
                        break;

                    case 4:
                        System.out.println("\n--- MODIFICAR EMPLEADO ---");
                        System.out.print("Introduce el ID del empleado a cambiar: "); int idM = Integer.parseInt(teclado.nextLine());
                        EmpleadoDTO empM = empleadoDao.buscarPorId(idM);
                        if(empM == null) {
                            System.out.println("[ERROR] El empleado no existe.");
                            break;
                        }
                        System.out.print("Nuevo Nombre (" + empM.getNombreCompleto() + "): "); String nNom = teclado.nextLine();
                        System.out.print("Nuevo Cargo (" + empM.getCargo() + "): "); String nCar = teclado.nextLine().trim().replace(" ", "_").toUpperCase();
                        System.out.print("Nueva Especialidad (" + empM.getEspecialidad() + "): "); String nEsp = teclado.nextLine();
                        System.out.print("Nuevo Turno (" + empM.getTurno() + "): "); String nTur = teclado.nextLine().trim().toUpperCase();
                        System.out.print("Nuevos Años Experiencia (" + empM.getAnios_experiencia() + "): "); int nExp = Integer.parseInt(teclado.nextLine());

                        EmpleadoDTO modificado = new EmpleadoDTO(nNom, idM, Cargo.valueOf(nCar), nEsp, Turno.valueOf(nTur), nExp);
                        empleadoDao.modificar(modificado);
                        System.out.println("[ÉXITO] Datos actualizados en MySQL.");
                        break;

                    case 5:
                        System.out.println("\n--- ELIMINAR EMPLEADO ---");
                        System.out.print("Introduce ID del empleado a borrar: "); int idE = Integer.parseInt(teclado.nextLine());
                        System.out.print("¿Estás seguro de que deseas eliminar este registro? (SI/NO): ");
                        if(teclado.nextLine().equalsIgnoreCase("SI")) {
                            empleadoDao.eliminar(idE);
                            System.out.println("[ÉXITO] Registro borrado de la base de datos.");
                        }
                        break;

                    case 6:
                        System.out.println("\n--- MÉTRICA ANALÍTICA DE RENDIMIENTO ---");
                        System.out.print("ID del Empleado: "); int idC = Integer.parseInt(teclado.nextLine());
                        // Llamada al método complejo que calcula el 2% usando agregaciones SUM de SQL
                        double comision = empleadoDao.calcularComisionEmpleado(idC);
                        System.out.println("💰 Comisión total acumulada por este asesor: " + comision + "€");
                        break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("[ERROR FORMATO] Cargo o Turno no válidos. Revisa las constantes.");
            } catch (AgenciaException e) {
                System.out.println("[ERROR BD] Operación cancelada: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("[ERROR INDETERMINADO] " + e.getMessage());
            }
        } while (op != 7);
    }
}