package Agencia_DTO;

public abstract class Persona implements Validar, Comparable<Persona>{
	protected String nombreCompleto;

	public Persona(String nombreComp) {
		nombreCompleto = nombreComp;
	}

	public String getNombreCompleto() {
		return nombreCompleto;
	}

	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}
	@Override
    public int compareTo(Persona otra) {
        return this.nombreCompleto.compareToIgnoreCase(otra.nombreCompleto);
    }
	
	
}
