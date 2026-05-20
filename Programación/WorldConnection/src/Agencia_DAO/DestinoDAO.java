package Agencia_DAO;
import Conexion.Conexion;
import Agencia_DTO.DestinoDTO;
import Agencia_DTO.CategoriaDTO;
import Agencia_Excepciones.AgenciaException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DestinoDAO implements Idao<DestinoDTO, String> {
    
    // Instanciamos el DAO de categorías para resolver la relación/asociación relacional
    private CategoriaDAO categoriaDao = new CategoriaDAO();

    @Override
    public void insertar(DestinoDTO destino) throws AgenciaException {
        String sql = "INSERT INTO destino (Cod_Destino, NombreDestino, Pais, Ciudad, Descripcion, PrecioBase, Duracion, Disponibilidad, ID_Categoria) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, destino.getCodDestino());
            ps.setString(2, destino.getNombreDestino());
            ps.setString(3, destino.getPais());
            ps.setString(4, destino.getCiudad());
            ps.setString(5, destino.getDescripcion());
            ps.setDouble(6, destino.getPrecioBase());
            ps.setInt(7, destino.getDuracion());
            ps.setBoolean(8, destino.isDisponibilidad());
            // Extraemos el ID numérico del objeto Categoria interno
            ps.setInt(9, destino.getCategoria().getIdCategoria());
            
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AgenciaException("Error de BD al registrar el destino: " + e.getMessage());
        }
    }

    @Override
    public void modificar(DestinoDTO destino) throws AgenciaException {
        String sql = "UPDATE destino SET NombreDestino = ?, Pais = ?, Ciudad = ?, Descripcion = ?, PrecioBase = ?, Duracion = ?, Disponibilidad = ?, ID_Categoria = ? WHERE Cod_Destino = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, destino.getNombreDestino());
            ps.setString(2, destino.getPais());
            ps.setString(3, destino.getCiudad());
            ps.setString(4, destino.getDescripcion());
            ps.setDouble(5, destino.getPrecioBase());
            ps.setInt(6, destino.getDuracion());
            ps.setBoolean(7, destino.isDisponibilidad());
            ps.setInt(8, destino.getCategoria().getIdCategoria());
            ps.setString(9, destino.getCodDestino()); // Filtro WHERE
            
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AgenciaException("Error de BD al actualizar el destino: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(String codDestino) throws AgenciaException {
        String sql = "DELETE FROM destino WHERE Cod_Destino = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codDestino);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AgenciaException("Error al eliminar destino (Verifica que no esté enlazado en ninguna reserva): " + e.getMessage());
        }
    }

    @Override
    public DestinoDTO buscarPorId(String codDestino) throws AgenciaException {
        String sql = "SELECT * FROM destino WHERE Cod_Destino = ?";
        DestinoDTO destino = null;
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, codDestino);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // 1. Recuperamos la clave foránea numérica
                    int idCat = rs.getInt("ID_Categoria");
                    // 2. Buscamos el objeto de la categoría completo usando su respectivo DAO
                    CategoriaDTO categoria = categoriaDao.buscarPorId(idCat);
                    
                    // 3. Construimos el DTO inyectándole su categoría resuelta
                    destino = new DestinoDTO(
                        rs.getString("Cod_Destino"),
                        rs.getString("NombreDestino"),
                        rs.getString("Pais"),
                        rs.getString("Ciudad"),
                        rs.getString("Descripcion"),
                        rs.getDouble("PrecioBase"),
                        rs.getInt("Duracion"),
                        rs.getBoolean("Disponibilidad"),
                        categoria
                    );
                }
            }
        } catch (SQLException e) {
            throw new AgenciaException("Error de BD al buscar destino: " + e.getMessage());
        }
        return destino;
    }

    @Override
    public List<DestinoDTO> listarTodos() throws AgenciaException {
        String sql = "SELECT * FROM destino";
        List<DestinoDTO> lista = new ArrayList<>();
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                int idCat = rs.getInt("ID_Categoria");
                CategoriaDTO categoria = categoriaDao.buscarPorId(idCat);
                
                DestinoDTO destino = new DestinoDTO(
                    rs.getString("Cod_Destino"),
                    rs.getString("NombreDestino"),
                    rs.getString("Pais"),
                    rs.getString("Ciudad"),
                    rs.getString("Descripcion"),
                    rs.getDouble("PrecioBase"),
                    rs.getInt("Duracion"),
                    rs.getBoolean("Disponibilidad"),
                    categoria
                );
                lista.add(destino);
            }
        } catch (SQLException e) {
            throw new AgenciaException("Error de BD al listar destinos: " + e.getMessage());
        }
        return lista;
    }
}