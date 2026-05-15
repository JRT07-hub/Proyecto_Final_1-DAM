package Agencia_DTO;

public class EmpleadoDTO extends Persona{
	private int ID_Empleado;
	private Cargo cargo;
	private String especialidad; 
    private Turno turno; 
    private int anios_experiencia;
	public EmpleadoDTO(String nombreComp, int iD_Empleado, Cargo cargo, String Especialidad, Turno turno,int anios_experiencia) {
		super(nombreComp);
		ID_Empleado = iD_Empleado;
		this.cargo = cargo;
		especialidad = Especialidad;
		this.turno = turno;
		this.anios_experiencia = anios_experiencia;
		actualizarCargoSegunExperiencia();
	}
	
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
	
	public void actualizarCargoSegunExperiencia() {
	    if (this.anios_experiencia < 2) {
	        this.cargo = Cargo.AGENTE_JUNIOR;
	    } else if (this.anios_experiencia < 8) {
	        this.cargo = Cargo.AGENTE_SENIOR;
	    } else {
	        this.cargo = Cargo.COORDINADOR;
	    }
	}

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
