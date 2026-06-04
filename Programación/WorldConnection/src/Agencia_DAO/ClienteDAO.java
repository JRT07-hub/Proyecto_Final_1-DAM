package Agencia_DAO;
import Conexion.Conexion;
import Agencia_DTO.ClienteDTO;
import Agencia_Excepciones.AgenciaException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del DAO para la gestión de Clientes.
 * Mapea las operaciones CRUD y llamadas a procedimientos almacenados para la tabla 'cliente'.
 */
public class ClienteDAO implements Idao<ClienteDTO, String> {

	/**
     * {@inheritDoc}
     * Registra un cliente con sus datos básicos (DNI, Nombre, Correo, Teléfono, Dirección y Pasaporte).
     */
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

    /**
    * {@inheritDoc}
    * Actualiza la información del cliente. Controla de forma segura los valores nulos en el campo Pasaporte.
    */
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

    /**
     * {@inheritDoc}
     * @throws AgenciaException Si el cliente cuenta con reservas activas en el sistema.
     */
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
    
    /**
     * {@inheritDoc}
     * Filtra la búsqueda mediante la clave primaria de tipo String (DNI).
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * Aplica de manera masiva un descuento por fidelidad a los importes de las reservas en la base de datos.
     * Mapea de forma directa el Procedimiento Almacenado de MySQL: {@code sp_AplicarDescuentoFidelidad}.
     *
     * @param porcentaje El porcentaje de descuento a aplicar (ej. 10.0 para un 10%).
     * @throws AgenciaException Si ocurre un error al invocar o ejecutar el procedimiento almacenado.
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
