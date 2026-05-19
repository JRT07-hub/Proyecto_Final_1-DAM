package Agencia_DTO;

import Agencia_Excepciones.AgenciaException;

public class DestinoDTO implements Validar{
	private String codDestino,nombreDestino,pais,ciudad,descripcion;
	private double precioBase;
	private int duracion;
	private boolean disponibilidad;
	private CategoriaDTO categoria;
	
	public DestinoDTO(String codDestino, String nombreDestino, String pais, String ciudad, String descripcion,double precioBase, int duracion, boolean disponibilidad, CategoriaDTO categoria) throws AgenciaException{
		this.codDestino = codDestino;
		this.nombreDestino = nombreDestino;
		this.pais = pais;
		this.ciudad = ciudad;
		this.descripcion = descripcion;
		this.precioBase = precioBase;
		this.duracion = duracion;
		this.disponibilidad = disponibilidad;
		this.categoria = categoria;
		validar();
	}
	
	public String getCodDestino() {return codDestino;}
	public void setCodDestino(String codDestino) {this.codDestino = codDestino;}
	public String getNombreDestino() {return nombreDestino;}
	public void setNombreDestino(String nombreCompleto) {this.nombreDestino = nombreCompleto;}
	public String getPais() {return pais;}
	public void setPais(String pais) {this.pais = pais;}
	public String getCiudad() {return ciudad;}
	public void setCiudad(String ciudad) {this.ciudad = ciudad;}
	public String getDescripcion() {return descripcion;}
	public void setDescripcion(String descripcion) {this.descripcion = descripcion;}
	public double getPrecioBase() {return precioBase;}
	public void setPrecioBase(double precioBase) {this.precioBase = precioBase;}
	public int getDuracion() {return duracion;}
	public void setDuracion(int duracion) {this.duracion = duracion;}
	public boolean isDisponibilidad() {return disponibilidad;}
	public void setDisponibilidad(boolean disponibilidad) {this.disponibilidad = disponibilidad;}
	public CategoriaDTO getCategoria() {return categoria;}
	public void setCategoria(CategoriaDTO categoria) {this.categoria = categoria;}
	
	public void aplicarDescuentoTemporada(double porcentaje) throws AgenciaException {
	    if (porcentaje < 0 || porcentaje > 100) {
	        throw new AgenciaException("El porcentaje de descuento debe estar entre 0 y 100.");
	    }
	    this.precioBase -= this.precioBase * (porcentaje / 100);
	}

	@Override
	public boolean validar() throws AgenciaException {
        if (codDestino == null || codDestino.trim().length() != 5) {
            throw new AgenciaException("El código de destino es obligatorio y debe tener exactamente 5 caracteres.");
        }
        if (nombreDestino == null || nombreDestino.trim().isEmpty()) {
            throw new AgenciaException("El nombre del destino no puede estar vacío.");
        }
        if (precioBase <= 0) {
            throw new AgenciaException("El precio base debe ser un valor positivo mayor que cero.");
        }
        if (duracion <= 0) {
            throw new AgenciaException("La duración del viaje debe ser de al menos 1 día.");
        }
        if (categoria == null) {
            throw new AgenciaException("El destino debe tener una categoría válida asignada.");
        }
        return true;
    }
	
	
	
	
}
