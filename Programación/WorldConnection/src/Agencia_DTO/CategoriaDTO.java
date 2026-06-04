package Agencia_DTO;
/**
 * DTO que representa las Categorías de clasificación de los destinos turísticos (Ej: Playa, Aventura, Cultural).
 * Es una estructura simple de datos pura.
 */
public class CategoriaDTO {
    private int idCategoria;   
    private String nombreCat; 
    /** Constructor completo de la categoría. */
    public CategoriaDTO(int idCategoria, String nombreCat) {
        this.idCategoria = idCategoria;
        this.nombreCat = nombreCat;
    }
    // GETTERS Y SETTERS
    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }
    public String getNombreCat() { return nombreCat; }
    public void setNombreCat(String nombreCat) { this.nombreCat = nombreCat; }
}
