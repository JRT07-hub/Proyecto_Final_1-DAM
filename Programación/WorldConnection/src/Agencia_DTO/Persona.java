package Agencia_DTO;

/**
 * Clase abstracta que representa la base conceptual de cualquier individuo en el sistema.
 * Implementa la interfaz {@link Validar} para la consistencia de datos y {@link Comparable} 
 * para permitir la ordenación natural por nombre.
 */
public abstract class Persona implements Validar, Comparable<Persona>{
	/** El nombre completo de la persona. */
	protected String nombreCompleto;
	/**
     * Constructor base para inicializar una persona con su nombre completo.
     * * @param nombreComp Nombre completo del individuo.
     */
	public Persona(String nombreComp) {
		nombreCompleto = nombreComp;
	}
	/** @return El nombre completo. */
	public String getNombreCompleto() {
		return nombreCompleto;
	}
	/** @param nombreCompleto El nuevo nombre completo a asignar. */
	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}
	/**
     * Compara esta persona con otra basándose en el nombre completo, 
     * ignorando las diferencias entre mayúsculas y minúsculas (Orden alfabético).
     * * @param otra El otro objeto Persona con el que se va a comparar.
     * @return Un valor entero negativo, cero, o un entero positivo si este nombre
     * es alfabéticamente menor, igual o mayor que el de la otra persona.
     */
	@Override
    public int compareTo(Persona otra) {
        return this.nombreCompleto.compareToIgnoreCase(otra.nombreCompleto);
    }
	
	
}
