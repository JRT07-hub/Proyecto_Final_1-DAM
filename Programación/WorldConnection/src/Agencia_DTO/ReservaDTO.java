package Agencia_DTO;

import java.time.LocalDate;

import Agencia_Excepciones.AgenciaException;
/**
 * DTO Central del sistema que modela una Reserva de viaje.
 * Agrupa de forma relacional al {@link ClienteDTO}, el {@link EmpleadoDTO} gestor y el {@link DestinoDTO} de viaje.
 */
public class ReservaDTO implements Validar {
    private int idReserva,numViajeros;
    private double importeTotal;
    private LocalDate fechaReserva,fechaSalida,fechaRegreso;
    private String estado;
    private ClienteDTO cliente;
    private EmpleadoDTO empleado;
    private DestinoDTO destino;
    
    /**
     * Constructor para Nuevas Reservas. 
     * Asigna automáticamente la fecha actual del sistema como fecha de creación, 
     * establece el estado inicial en 'Pendiente' y calcula el precio total.
     */
    public ReservaDTO(LocalDate fechaSalida, LocalDate fechaRegreso, int numViajeros,ClienteDTO cliente, EmpleadoDTO empleado, DestinoDTO destino) throws AgenciaException {
        this.fechaReserva = LocalDate.now();
        this.fechaSalida = fechaSalida;
        this.fechaRegreso = fechaRegreso;
        this.numViajeros = numViajeros;
        this.cliente = cliente;
        this.empleado = empleado;
        this.destino = destino;
        this.estado = "Pendiente";
        calcularImporteTotal();
        validar();
    }

    /** Constructor Completo utilizado para mapear registros históricos provenientes de la Base de Datos. */
    public ReservaDTO(int idReserva, LocalDate fechaReserva, LocalDate fechaSalida, LocalDate fechaRegreso,int numViajeros, double importeTotal, String estado, ClienteDTO cliente, EmpleadoDTO empleado, DestinoDTO destino) throws AgenciaException {
        this.idReserva = idReserva;
        this.fechaReserva = fechaReserva;
        this.fechaSalida = fechaSalida;
        this.fechaRegreso = fechaRegreso;
        this.numViajeros = numViajeros;
        this.importeTotal = importeTotal;
        this.estado = estado;
        this.cliente = cliente;
        this.empleado = empleado;
        this.destino = destino;
        validar();
    }
    /**
    * Lógica Automática de Negocio: Calcula el importe base total de la reserva multiplicando 
    * el precio por día del destino por la cantidad de viajeros y la duración total del viaje en días.
    */
    public void calcularImporteTotal() {
        if (this.destino != null) {
            this.importeTotal = this.destino.getPrecioBase() * this.numViajeros;
        }
    }
    /**
     * Valida los parámetros temporales y lógicos de la reserva.
     * Reglas aplicadas:
     * <ul>
     * <li>La fecha de salida debe ser igual o posterior al día actual.</li>
     * <li>La fecha de regreso debe ser estrictamente posterior a la fecha de salida.</li>
     * <li>El número de viajeros debe ser al menos 1 o más.</li>
     * <li>Las entidades Cliente, Empleado y Destino asociadas no pueden ser nulas.</li>
     * </ul>
     */
    @Override
    public boolean validar() throws AgenciaException {
        if (this.cliente == null) {
            throw new AgenciaException("Error: La reserva debe tener un cliente asignado.");
        }
        if (this.empleado == null) {
            throw new AgenciaException("Error: La reserva debe tener un empleado asignado.");
        }
        if (this.destino == null) {
            throw new AgenciaException("Error: La reserva debe tener un destino asignado.");
        }
        if (this.numViajeros <= 0) {
            throw new AgenciaException("Error: El número de viajeros debe ser como mínimo 1.");
        }
        if (this.fechaSalida == null || this.fechaRegreso == null) {
            throw new AgenciaException("Error: Las fechas de salida y regreso son obligatorias.");
        }
        if (this.fechaSalida.isBefore(this.fechaReserva)) {
            throw new AgenciaException("Error: La fecha de salida no puede ser anterior a la fecha de hoy.");
        }
        if (this.fechaSalida.isAfter(this.fechaRegreso)) {
            throw new AgenciaException("Error: La fecha de salida no puede ser posterior a la fecha de regreso.");
        }
        return true;
    }
    // GETTERS Y SETTERS
    public int getIdReserva() { return idReserva; }
    public void setIdReserva(int idReserva) { this.idReserva = idReserva; }
    public LocalDate getFechaReserva() { return fechaReserva; }
    public void setFechaReserva(LocalDate fechaReserva) { this.fechaReserva = fechaReserva; }
    public LocalDate getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(LocalDate fechaSalida) { this.fechaSalida = fechaSalida; }
    public LocalDate getFechaRegreso() { return fechaRegreso; }
    public void setFechaRegreso(LocalDate fechaRegreso) { this.fechaRegreso = fechaRegreso; }
    public int getNumViajeros() { return numViajeros; }
    public void setNumViajeros(int numViajeros) { this.numViajeros = numViajeros; }
    public double getImporteTotal() { return importeTotal; }
    public void setImporteTotal(double importeTotal) { this.importeTotal = importeTotal; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public ClienteDTO getCliente() { return cliente; }
    public void setCliente(ClienteDTO cliente) { this.cliente = cliente; }
    public EmpleadoDTO getEmpleado() { return empleado; }
    public void setEmpleado(EmpleadoDTO empleado) { this.empleado = empleado; }
    public DestinoDTO getDestino() { return destino; }
    public void setDestino(DestinoDTO destino) { this.destino = destino; }

    @Override
    public String toString() {
        return "Reserva Nro: " + idReserva + " | Cliente: " + cliente.getNombreCompleto() + 
               " | Destino: " + destino.getNombreDestino() + " | Total: " + importeTotal + "€ [" + estado + "]";
    }
}
