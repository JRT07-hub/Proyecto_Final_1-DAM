package Agencia_DTO;

import Agencia_Excepciones.AgenciaException;

public class ClienteDTO extends Persona {
	 private String dni,correo,direccion,pasaporte,telefono;
	 
	 public ClienteDTO(String nombreComp, String dni, String correo, String telefono, String direccion) throws AgenciaException {
			super(nombreComp);
			this.dni = dni;
			this.correo = correo;
			this.telefono = telefono;
			this.direccion = direccion;
			validar();
		 }
	 
	 public ClienteDTO(String nombreComp, String dni, String correo, String telefono, String direccion, String pasaporte) throws AgenciaException {
		super(nombreComp);
		this.dni = dni;
		this.correo = correo;
		this.telefono = telefono;
		this.direccion = direccion;
		this.pasaporte = pasaporte;
		validar();
	 }

	 public String getDni() {return dni;}
	 public void setDni(String dni) {this.dni = dni;}
	 public String getCorreo() {return correo;}
	 public void setCorreo(String correo) {this.correo = correo;}
	 public String getDireccion() {return direccion;}
	 public void setDireccion(String direccion) {this.direccion = direccion;}
	 public String getTelefono() {return telefono;}
	 public void setTelefono(String telefono) {this.telefono = telefono;}
	 public String getPasaporte() {return pasaporte;}
	 public void setPasaporte(String pasaporte) {this.pasaporte = pasaporte;}

	 @Override
	 public boolean validar() throws AgenciaException {
		    if (this.nombreCompleto == null || this.nombreCompleto.trim().isEmpty()) {
		        throw new AgenciaException("El nombre no puede estar vacío.");}

		    if (this.dni == null || !this.dni.matches("^[0-9]{8}[A-Z]$")) {
		        throw new AgenciaException("El DNI debe tener 8 números y una letra mayúscula.");}

		    if (this.correo == null || !this.correo.matches("^[\\w.]+@[\\w.]+\\.[a-zA-Z]{2,}$")) {
		        throw new AgenciaException("El formato del correo es inválido.");}

		    if (this.telefono == null || !this.telefono.matches("\\d{9}")) {  // \d significa "dígito" (0-9) y {9} significa "exactamente 9 veces"
		    	throw new AgenciaException("El teléfono debe estar compuesto por 9 números.");}
		    
		    if (this.pasaporte != null && !this.pasaporte.trim().isEmpty()) {
		        if (!this.pasaporte.matches("^[A-Z0-9]{9}$")) {
		            throw new AgenciaException("El formato del pasaporte es incorrecto.");}
		    }
		    
		    return true;
	 }
	 
}
