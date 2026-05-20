package Agencia_DAO;

import Conexion.Conexion;
import Agencia_DTO.CategoriaDTO;
import Agencia_Excepciones.AgenciaException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO implements Idao<CategoriaDTO, Integer> {

    @Override
    public void insertar(CategoriaDTO categoria) throws AgenciaException {
        String sql = "INSERT INTO categoria (ID_Categoria, NombreCat) VALUES (?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, categoria.getIdCategoria());
            ps.setString(2, categoria.getNombreCat());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AgenciaException("Error de BD al insertar categoría: " + e.getMessage());
        }
    }

    @Override
    public void modificar(CategoriaDTO categoria) throws AgenciaException {
        String sql = "UPDATE categoria SET NombreCat = ? WHERE ID_Categoria = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, categoria.getNombreCat());
            ps.setInt(2, categoria.getIdCategoria());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AgenciaException("Error de BD al modificar categoría: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(Integer id) throws AgenciaException {
        String sql = "DELETE FROM categoria WHERE ID_Categoria = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AgenciaException("Error al eliminar categoría (Asegúrate de que no tenga destinos asociados): " + e.getMessage());
        }
    }

    @Override
    public CategoriaDTO buscarPorId(Integer id) throws AgenciaException {
        String sql = "SELECT * FROM categoria WHERE ID_Categoria = ?";
        CategoriaDTO categoria = null;
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    categoria = new CategoriaDTO(rs.getInt("ID_Categoria"), rs.getString("NombreCat"));
                }
            }
        } catch (SQLException e) {
            throw new AgenciaException("Error de BD al buscar categoría: " + e.getMessage());
        }
        return categoria;
    }

    @Override
    public List<CategoriaDTO> listarTodos() throws AgenciaException {
        String sql = "SELECT * FROM categoria";
        List<CategoriaDTO> lista = new ArrayList<>();
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new CategoriaDTO(rs.getInt("ID_Categoria"), rs.getString("NombreCat")));
            }
        } catch (SQLException e) {
            throw new AgenciaException("Error de BD al listar categorías: " + e.getMessage());
        }
        return lista;
    }
}