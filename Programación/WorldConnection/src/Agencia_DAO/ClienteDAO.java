package Agencia_DAO;
import Conexion.Conexion;
import Agencia_DTO.ClienteDTO;
import Agencia_Excepciones.AgenciaException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO implements Idao<ClienteDTO, String> {

    // =========================================================================
    // 1. OPERACIÓN: INSERTAR CLIENTE (C de CRUD)
    // =========================================================================
    @Override
    public void insertar(ClienteDTO cliente) throws AgenciaException {
    	
        String sql = "INSERT INTO cliente (DNI, NombreCompleto, Correo, Telefono, Direccion, Pasaporte) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, cliente.getDni());
            ps.setString(2, cliente.getNombreCompleto());
            ps.setString(3, cliente.getCorreo());
            ps.setString(4, cliente.getTelefono());
            ps.setString(5, cliente.getDireccion());
            ps.setString(6, cliente.getPasaporte());
            
            ps.executeUpdate();
            
        } catch (SQLException e) {
            throw new AgenciaException("Error de base de datos al registrar el cliente: " + e.getMessage());
        }
    }

    // =========================================================================
    // 2. OPERACIÓN: MODIFICAR CLIENTE (U de CRUD)
    // =========================================================================
    @Override
    public void modificar(ClienteDTO cliente) throws AgenciaException {
        String sql = "UPDATE cliente SET NombreCompleto = ?, Correo = ?, Telefono = ?, Direccion = ?, Pasaporte = ? WHERE DNI = ?";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, cliente.getNombreCompleto());
            ps.setString(2, cliente.getCorreo());
            ps.setString(3, cliente.getTelefono());
            ps.setString(4, cliente.getDireccion());
            ps.setString(6, cliente.getDni());
            
            if (cliente.getPasaporte() != null) {
                ps.setString(5, cliente.getPasaporte());
            } else {
                ps.setNull(5, Types.VARCHAR);
            }
            
            ps.executeUpdate();
            
        } catch (SQLException e) {
            throw new AgenciaException("Error de base de datos al actualizar el cliente: " + e.getMessage());
        }
    }

    // =========================================================================
    // 3. OPERACIÓN: ELIMINAR CLIENTE (D de CRUD)
    // =========================================================================
    @Override
    public void eliminar(String dni) throws AgenciaException {
        String sql = "DELETE FROM cliente WHERE DNI = ?";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, dni);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            throw new AgenciaException("Error al eliminar cliente (Comprueba que no tenga reservas activas asociadas): " + e.getMessage());
        }
    }

 // =========================================================================
    // 4. OPERACIÓN: BUSCAR POR ID / DNI (R de CRUD)
    // =========================================================================
    @Override
    public ClienteDTO buscarPorId(String dni) throws AgenciaException {
        String sql = "SELECT * FROM cliente WHERE DNI = ?";
        ClienteDTO cliente = null;
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, dni);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {

                    cliente = new ClienteDTO(
                        rs.getString("NombreCompleto"),
                        rs.getString("DNI"),
                        rs.getString("Correo"),
                        rs.getString("Telefono"),
                        rs.getString("Direccion"),
                        rs.getString("Pasaporte")
                    );
                }
            }
        } catch (SQLException e) {
            throw new AgenciaException("Error de base de datos al buscar cliente: " + e.getMessage());
        }
        
        return cliente;
    }

    // =========================================================================
    // 5. OPERACIÓN: LISTAR TODOS LOS CLIENTES (R de CRUD)
    // =========================================================================
    @Override
    public List<ClienteDTO> listarTodos() throws AgenciaException {
        String sql = "SELECT * FROM cliente";
        List<ClienteDTO> lista = new ArrayList<>();
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                ClienteDTO cliente = new ClienteDTO(
                    rs.getString("NombreCompleto"),
                    rs.getString("DNI"),
                    rs.getString("Correo"),
                    rs.getString("Telefono"),
                    rs.getString("Direccion"),
                    rs.getString("Pasaporte")
                );
                lista.add(cliente);
            }
        } catch (SQLException e) {
            throw new AgenciaException("Error de base de datos al listar clientes: " + e.getMessage());
        }
        return lista;
    }

    // =========================================================================
    // EXTRA: ENLAZAR CON TUS SCRIPTS PROCEDIMIENTOS ALMACENADOS DE MYSQL
    // =========================================================================
    /**
     * Mapea PROCEDIMIENTO 3: sp_AplicarDescuentoFidelidad.
     * Modifica el importe masivo de las reservas de los clientes VIP directamente en la BD.
     */
    public void aplicarDescuentoVIPEnBD(double porcentaje) throws AgenciaException {
        String sql = "{CALL sp_AplicarDescuentoFidelidad(?)}";
        
        try (Connection con = Conexion.getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setDouble(1, porcentaje);
            cs.execute();
            
        } catch (SQLException e) {
            throw new AgenciaException("Error al ejecutar el descuento masivo de fidelidad: " + e.getMessage());
        }
    }
}
