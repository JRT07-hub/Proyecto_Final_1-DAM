package Agencia_DTO;

import Agencia_Excepciones.AgenciaException;
/**
 * DTO que representa a un Empleado dentro de la plantilla de la agencia.
 * 
 * <p>Esta clase <b>extiende de {@link Persona}</b>, por lo que hereda su nombre completo
 * y la implementación para poder ser comparado y ordenado alfabéticamente.</p>
 * 
 * Además, implementa una regla dinámica de negocio que ajusta automáticamente 
 * el cargo del empleado basándose en sus años de experiencia acumulados.
 */
public class EmpleadoDTO extends Persona{
	private int ID_Empleado;
	private Cargo cargo;
	private String especialidad; 
    private Turno turno; 
    private int anios_experiencia;
    /** Constructor completo para lectura desde Base de Datos (incluye ID autogenerado). */
	public EmpleadoDTO(String nombreComp, int iD_Empleado, Cargo cargo, String Especialidad, Turno turno,int anios_experiencia)throws AgenciaException {
		super(nombreComp);
		ID_Empleado = iD_Empleado;
		this.cargo = cargo;
		especialidad = Especialidad;
		this.turno = turno;
		this.anios_experiencia = anios_experiencia;
		actualizarCargoSegunExperiencia();
		validar();
	}
	/** Constructor sin ID (Ideal para operaciones de creación/inserción antes de persistir). */
	public EmpleadoDTO(String nombreComp,Cargo cargo, String Especialidad, Turno turno,int anios_experiencia)throws AgenciaException {
		super(nombreComp);
		this.cargo = cargo;
		especialidad = Especialidad;
		this.turno = turno;
		this.anios_experiencia = anios_experiencia;
		actualizarCargoSegunExperiencia();
		validar();
	}
	// GETTERS Y SETTERS
	public int getID_Empleado() {return ID_Empleado;}
	public void setID_Empleado(int iD_Empleado) {ID_Empleado = iD_Empleado;}
	public Cargo getCargo() {return cargo;}
	public void setCargo(Cargo cargo) {this.cargo = cargo;}
	public String getEspecialidad() {return especialidad;}
	public void setEspecialidad(String especialidad) {this.especialidad = especialidad;}
	public Turno getTurno() {return turno;}
	public void setTurno(Turno turno) {this.turno = turno;}
	public int getAnios_experiencia() {return anios_experiencia;}
	public void setAnios_experiencia(int anios_experiencia) {this.anios_experiencia = anios_experiencia;}
	
	/**
     * Lógica Automática de Negocio: Evalúa los años de experiencia para asignar el rango del agente.
     * Los cargos especiales (GERENTE, PRODUCT_MANAGER, GUIA_TURISTICO) están protegidos y no cambian.
     * Escala de Ascensos:
     * <ul>
     * <li>Menos de 2 años: AGENTE_JUNIOR</li>
     * <li>De 2 a 7 años: AGENTE_SENIOR</li>
     * <li>8 años o más: COORDINADOR</li>
     * </ul>
     */
	public void actualizarCargoSegunExperiencia() {
		//Cargos especiales no se modifican,los únicos que "ascienden" son los agentes que se modifican según su años de experiencia
		if (this.cargo == Cargo.GERENTE || this.cargo == Cargo.PRODUCT_MANAGER || this.cargo == Cargo.GUIA_TURISTICO) {
		        return;
		    }
		
	    if (this.anios_experiencia < 2) {
	        this.cargo = Cargo.AGENTE_JUNIOR;
	    } else if (this.anios_experiencia < 8) {
	        this.cargo = Cargo.AGENTE_SENIOR;
	    } else {
	        this.cargo = Cargo.COORDINADOR;
	    }
	}
	/**
     * Valida las restricciones del empleado.
     * Reglas aplicadas: El turno, el cargo y la especialidad son obligatorios; la experiencia no puede ser negativa.
     * * @throws AgenciaException Si falta algún parámetro mandatorio o la experiencia es inválida.
     */
	@Override
	public boolean validar() throws AgenciaException {
		if (this.turno == null) {
		    throw new AgenciaException("El turno es obligatorio y debe ser Mañana o Tarde.");}
		
		if (this.cargo == null) {
	        throw new AgenciaException("Error: El cargo es obligatorio. Debe seleccionar una de las categorías permitidas.");}
		
		if (this.especialidad == null || this.especialidad.trim().isEmpty()) {
            throw new AgenciaException("La especialidad no puede estar vacía.");}
		
		if (this.anios_experiencia < 0) {
            throw new AgenciaException("Los años de experiencia deben ser un valor positivo.");}
		
		return true;
	} 
	
    
    

}
