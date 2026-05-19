package Agencia_DTO;

public class CategoriaDTO {
    private int idCategoria;   
    private String nombreCat; 

    public CategoriaDTO(int idCategoria, String nombreCat) {
        this.idCategoria = idCategoria;
        this.nombreCat = nombreCat;
    }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }
    public String getNombreCat() { return nombreCat; }
    public void setNombreCat(String nombreCat) { this.nombreCat = nombreCat; }
}
