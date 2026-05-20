package Agencia_DAO;

import Conexion.Conexion;
import Agencia_DTO.ReservaDTO;
import Agencia_DTO.ClienteDTO;
import Agencia_DTO.EmpleadoDTO;
import Agencia_DTO.DestinoDTO;
import Agencia_Excepciones.AgenciaException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO implements Idao<ReservaDTO, Integer> {
    
    // Inyectamos los DAOs auxiliares para resolver la integridad de objetos en las lecturas
    private ClienteDAO clienteDao = new ClienteDAO();
    private EmpleadoDAO empleadoDao = new EmpleadoDAO();
    private DestinoDAO destinoDao = new DestinoDAO(); 

    // =========================================================================
    // 1. OPERACIÓN: INSERTAR RESERVA (C de CRUD)
    // =========================================================================
    @Override
    public void insertar(ReservaDTO reserva) throws AgenciaException {
        String sql = "INSERT INTO reserva (ID_Reserva, FechaReserva, FechaSalida, FechaRegreso, NumViajeros, ImporteTotal, Estado, DNI_Cliente, ID_Empleado, Cod_Destino) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, reserva.getIdReserva());
            ps.setDate(2, Date.valueOf(reserva.getFechaReserva()));
            ps.setDate(3, Date.valueOf(reserva.getFechaSalida()));
            ps.setDate(4, Date.valueOf(reserva.getFechaRegreso()));
            ps.setInt(5, reserva.getNumViajeros());
            ps.setDouble(6, reserva.getImporteTotal()); 
            ps.setString(7, reserva.getEstado());
            
            // Extracción de Claves Foráneas desde los sub-DTOs ricos
            ps.setString(8, reserva.getCliente().getDni());
            ps.setInt(9, reserva.getEmpleado().getID_Empleado());
            ps.setString(10, reserva.getDestino().getCodDestino());
            
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AgenciaException("Error al insertar la reserva en MySQL: " + e.getMessage());
        }
    }

    // =========================================================================
    // 2. OPERACIÓN: MODIFICAR RESERVA (U de CRUD)
    // =========================================================================
    @Override
    public void modificar(ReservaDTO reserva) throws AgenciaException {
        String sql = "UPDATE reserva SET FechaReserva = ?, FechaSalida = ?, FechaRegreso = ?, NumViajeros = ?, ImporteTotal = ?, Estado = ?, DNI_Cliente = ?, ID_Empleado = ?, Cod_Destino = ? WHERE ID_Reserva = ?";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setDate(1, Date.valueOf(reserva.getFechaReserva()));
            ps.setDate(2, Date.valueOf(reserva.getFechaSalida()));
            ps.setDate(3, Date.valueOf(reserva.getFechaRegreso()));
            ps.setInt(4, reserva.getNumViajeros());
            ps.setDouble(5, reserva.getImporteTotal());
            ps.setString(6, reserva.getEstado());
            ps.setString(7, reserva.getCliente().getDni());
            ps.setInt(8, reserva.getEmpleado().getID_Empleado());
            ps.setString(9, reserva.getDestino().getCodDestino());
            ps.setInt(10, reserva.getIdReserva()); // Filtro Clave Primaria
            
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AgenciaException("Error al modificar la reserva en MySQL: " + e.getMessage());
        }
    }

    // =========================================================================
    // 3. OPERACIÓN: ELIMINAR RESERVA (D de CRUD)
    // =========================================================================
    @Override
    public void eliminar(Integer idReserva) throws AgenciaException {
        String sql = "DELETE FROM reserva WHERE ID_Reserva = ?";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idReserva);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AgenciaException("Error al eliminar la reserva en MySQL: " + e.getMessage());
        }
    }

    // =========================================================================
    // 4. OPERACIÓN: BUSCAR RESERVA POR ID (R de CRUD)
    // =========================================================================
    @Override
    public ReservaDTO buscarPorId(Integer idReserva) throws AgenciaException {
        String sql = "SELECT * FROM reserva WHERE ID_Reserva = ?";
        ReservaDTO reserva = null;
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idReserva);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // 1. Recuperamos las claves foráneas de la fila actual
                    String dniCliente = rs.getString("DNI_Cliente");
                    int idEmpleado = rs.getInt("ID_Empleado");
                    String codDestino = rs.getString("Cod_Destino");
                    
                    // 2. Delegamos en los otros DAOs para componer las dependencias de objetos
                    ClienteDTO cliente = clienteDao.buscarPorId(dniCliente);
                    EmpleadoDTO empleado = empleadoDao.buscarPorId(idEmpleado);
                    DestinoDTO destino = destinoDao.buscarPorId(codDestino);
                    
                    // 3. Reconstruimos el DTO mapeado con sus objetos vinculados
                    reserva = new ReservaDTO(
                        rs.getInt("ID_Reserva"),
                        rs.getDate("FechaReserva").toLocalDate(),
                        rs.getDate("FechaSalida").toLocalDate(),
                        rs.getDate("FechaRegreso").toLocalDate(),
                        rs.getInt("NumViajeros"),
                        rs.getDouble("ImporteTotal"),
                        rs.getString("Estado"),
                        cliente,
                        empleado,
                        destino
                    );
                }
            }
        } catch (SQLException e) {
            throw new AgenciaException("Error al buscar la reserva por ID: " + e.getMessage());
        }
        return reserva;
    }

    // =========================================================================
    // 5. OPERACIÓN: LISTAR TODAS LAS RESERVAS (R de CRUD) - ¡TERMINADO!
    // =========================================================================
    @Override
    public List<ReservaDTO> listarTodos() throws AgenciaException {
        String sql = "SELECT * FROM reserva";
        List<ReservaDTO> lista = new ArrayList<>();
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                // 1. Extraemos los identificadores relacionales
                String dniCliente = rs.getString("DNI_Cliente");
                int idEmpleado = rs.getInt("ID_Empleado");
                String codDestino = rs.getString("Cod_Destino");
                
                // 2. Cargamos los objetos completos usando la persistencia correspondiente
                ClienteDTO cliente = clienteDao.buscarPorId(dniCliente);
                EmpleadoDTO empleado = empleadoDao.buscarPorId(idEmpleado);
                DestinoDTO destino = destinoDao.buscarPorId(codDestino);
                
                // 3. Ensamblamos el DTO de la reserva
                ReservaDTO reserva = new ReservaDTO(
                    rs.getInt("ID_Reserva"),
                    rs.getDate("FechaReserva").toLocalDate(),
                    rs.getDate("FechaSalida").toLocalDate(),
                    rs.getDate("FechaRegreso").toLocalDate(),
                    rs.getInt("NumViajeros"),
                    rs.getDouble("ImporteTotal"),
                    rs.getString("Estado"),
                    cliente,
                    empleado,
                    destino
                );
                lista.add(reserva);
            }
        } catch (SQLException e) {
            throw new AgenciaException("Error de base de datos al listar el histórico de reservas: " + e.getMessage());
        }
        return lista;
    }
}