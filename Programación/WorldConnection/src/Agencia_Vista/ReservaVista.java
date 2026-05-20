package Agencia_Vista;

import Agencia_DAO.ReservaDAO;
import Agencia_DAO.ClienteDAO;
import Agencia_DAO.EmpleadoDAO;
import Agencia_DAO.DestinoDAO;

import Agencia_DTO.ReservaDTO;
import Agencia_DTO.ClienteDTO;
import Agencia_DTO.EmpleadoDTO;
import Agencia_DTO.DestinoDTO;
import Agencia_Excepciones.AgenciaException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class ReservaVista {
    private static ReservaDAO reservaDao = new ReservaDAO();
    private static ClienteDAO clienteDao = new ClienteDAO();
    private static EmpleadoDAO empleadoDao = new EmpleadoDAO();
    private static DestinoDAO destinoDao = new DestinoDAO();

    private static Scanner teclado = new Scanner(System.in);
    private static DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void ejecutar() {
        int op = 0;
        do {
            System.out.println("\n=============================================");
            System.out.println("           MENÚ GESTIÓN DE RESERVAS             ");
            System.out.println("=============================================");
            System.out.println("1. Registrar nueva reserva (Alta Completa)");
            System.out.println("2. Ver histórico de todas las reservas");
            System.out.println("3. Buscar reserva por ID");
            System.out.println("4. Modificar reserva (Cambio de Estado/Fechas)");
            System.out.println("5. Eliminar / Cancelar contrato de reserva");
            System.out.println("6. Volver al menú principal");
            System.out.println("=============================================");
            System.out.print("Seleccione opción: ");
            try {
                op = Integer.parseInt(teclado.nextLine());
                switch (op) {
                    case 1:
                        System.out.println("\n--- NUEVA RESERVA ---");
                        System.out.print("DNI Cliente: "); String dni = teclado.nextLine();
                        ClienteDTO cli = clienteDao.buscarPorId(dni);
                        if(cli == null) { System.out.println("El cliente no existe."); break; }

                        System.out.print("ID Empleado Asesor: "); int idEmp = Integer.parseInt(teclado.nextLine());
                        EmpleadoDTO emp = empleadoDao.buscarPorId(idEmp);
                        if(emp == null) { System.out.println("El empleado no existe."); break; }

                        System.out.print("Código Destino (Ej: PAR01): "); String cod = teclado.nextLine();
                        DestinoDTO dest = destinoDao.buscarPorId(cod);
                        if(dest == null) { System.out.println("El destino no existe."); break; }

                        System.out.print("ID único Reserva: "); int idR = Integer.parseInt(teclado.nextLine());
                        System.out.print("Fecha Salida (dd/mm/aaaa): "); LocalDate fSal = LocalDate.parse(teclado.nextLine(), fmt);
                        System.out.print("Fecha Regreso (dd/mm/aaaa): "); LocalDate fReg = LocalDate.parse(teclado.nextLine(), fmt);
                        System.out.print("Número de Viajeros: "); int viajeros = Integer.parseInt(teclado.nextLine());

                        double total = dest.getPrecioBase() * viajeros; // Lógica automatizada
                        
                        ReservaDTO nueva = new ReservaDTO(idR, LocalDate.now(), fSal, fReg, viajeros, total, "Confirmada", cli, emp, dest);
                        reservaDao.insertar(nueva);
                        System.out.println("[ÉXITO] Reserva guardada. Importe Total calculado: " + total + "€");
                        break;

                    case 2:
                        System.out.println("\n--- HISTÓRICO GENERAL DE RESERVAS ---");
                        List<ReservaDTO> lista = reservaDao.listarTodos();
                        if(lista.isEmpty()) System.out.println("No constan registros en la base de datos.");
                        else {
                            for(ReservaDTO r : lista) {
                                System.out.println("ID: " + r.getIdReserva() + " | Cliente: " + r.getCliente().getNombreCompleto() + " | Destino: " + r.getDestino().getNombreDestino() + " | Total: " + r.getImporteTotal() + "€ [" + r.getEstado() + "]");
                            }
                        }
                        break;

                    case 3:
                        System.out.println("\n--- CONSULTA DE RESERVA ---");
                        System.out.print("ID Reserva: "); int idB = Integer.parseInt(teclado.nextLine());
                        ReservaDTO rB = reservaDao.buscarPorId(idB);
                        if(rB != null) {
                            System.out.println("Reserva Nº: " + rB.getIdReserva() + " | Estado: " + rB.getEstado());
                            System.out.println("Pasajero Titular: " + rB.getCliente().getNombreCompleto());
                            System.out.println("Destino Contratado: " + rB.getDestino().getNombreDestino() + " (" + rB.getDestino().getPais() + ")");
                        } else System.out.println("Reserva no encontrada.");
                        break;

                    case 4:
                        System.out.println("\n--- MODIFICAR ESTADO/DATOS DE RESERVA ---");
                        System.out.print("Introduce el ID de la reserva: "); int idM = Integer.parseInt(teclado.nextLine());
                        ReservaDTO rM = reservaDao.buscarPorId(idM);
                        if(rM == null) { System.out.println("No existe la reserva."); break; }

                        System.out.print("Nuevo Estado (Actual: " + rM.getEstado() + " [Ej: Confirmada, Cancelada, Pendiente]): ");
                        String nEst = teclado.nextLine();
                        System.out.print("Modificar número de viajeros (" + rM.getNumViajeros() + "): ");
                        int nViajeros = Integer.parseInt(teclado.nextLine());

                        // Recalculamos el importe automáticamente basándonos en el cambio de viajeros
                        double nTotal = rM.getDestino().getPrecioBase() * nViajeros;

                        ReservaDTO resModificada = new ReservaDTO(idM, rM.getFechaReserva(), rM.getFechaSalida(), rM.getFechaRegreso(), nViajeros, nTotal, nEst, rM.getCliente(), rM.getEmpleado(), rM.getDestino());
                        reservaDao.modificar(resModificada);
                        System.out.println("[ÉXITO] Contrato de reserva modificado en MySQL.");
                        break;

                    case 5:
                        System.out.println("\n--- ELIMINAR / BORRAR RESERVA ---");
                        System.out.print("ID de la reserva a eliminar: "); int idE = Integer.parseInt(teclado.nextLine());
                        System.out.print("¿Seguro que deseas eliminar la reserva físicamente de la BD? (SI/NO): ");
                        if(teclado.nextLine().equalsIgnoreCase("SI")) {
                            reservaDao.eliminar(idE);
                            System.out.println("[ÉXITO] Registro eliminado de MySQL.");
                        }
                        break;
                }
            } catch (AgenciaException e) {
                System.out.println("[ERROR BD] " + e.getMessage());
            } catch (Exception e) {
                System.out.println("[ERROR] Formato incorrecto: " + e.getMessage());
            }
        } while (op != 6);
    }
}