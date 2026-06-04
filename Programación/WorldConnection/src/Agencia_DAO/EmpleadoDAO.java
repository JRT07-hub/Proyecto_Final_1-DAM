package Agencia_DAO;

import Conexion.Conexion;
import Agencia_DTO.EmpleadoDTO;
import Agencia_DTO.Cargo;
import Agencia_DTO.Turno;
import Agencia_Excepciones.AgenciaException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del DAO para la gestión de Empleados.
 * Maneja la conversión de objetos de enumeración de Java (Enum) a texto en la base de datos y viceversa.
 */
public class EmpleadoDAO implements Idao<EmpleadoDTO, Integer> {

	/**
     * {@inheritDoc}
     * Almacena los Enums {@link Cargo} y {@link Turno} usando su propiedad {@code .name()} como texto plano en MySQL.
     */
    @Override
    public void insertar(EmpleadoDTO empleado) throws AgenciaException {
        String sql = "INSERT INTO empleado (NombreCompleto, Cargo, Especialidad, Turno, Experiencia) VALUES ( ?, ?, ?, ?, ?)";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, empleado.getNombreCompleto());
            // Guardamos los ENUMs en MySQL como texto usando .name() (Ej: "AGENTE_SENIOR")
            ps.setString(2, empleado.getCargo().name());
            ps.setString(3, empleado.getEspecialidad());
            ps.setString(4, empleado.getTurno().name());
            ps.setInt(5, empleado.getAnios_experiencia());
            
            ps.executeUpdate();
            
        } catch (SQLException e) {
            throw new AgenciaException("Error de base de datos al registrar el empleado: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void modificar(EmpleadoDTO empleado) throws AgenciaException {
        String sql = "UPDATE empleado SET NombreCompleto = ?, Cargo = ?, Especialidad = ?, Turno = ?, Experiencia = ? WHERE ID_Empleado = ?";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, empleado.getNombreCompleto());
            ps.setString(2, empleado.getCargo().name());
            ps.setString(3, empleado.getEspecialidad());
            ps.setString(4, empleado.getTurno().name());
            ps.setInt(5, empleado.getAnios_experiencia());
            ps.setInt(6, empleado.getID_Empleado()); // Filtramos por la clave primaria
            
            ps.executeUpdate();
            
        } catch (SQLException e) {
            throw new AgenciaException("Error de base de datos al actualizar el empleado: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     * @throws AgenciaException Si el empleado tiene reservas históricas o asignadas bajo su cargo.
     */
    @Override
    public void eliminar(Integer idEmpleado) throws AgenciaException {
        String sql = "DELETE FROM empleado WHERE ID_Empleado = ?";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idEmpleado);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            throw new AgenciaException("Error al eliminar empleado (Verifica que no tenga reservas registradas a su cargo): " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     * Recupera el empleado limpiando los espacios y formateando los textos de la BD para mapearlos 
     * de forma segura a los ENUMs correspondientes.
     */
    @Override
    public EmpleadoDTO buscarPorId(Integer idEmpleado) throws AgenciaException {
        String sql = "SELECT * FROM empleado WHERE ID_Empleado = ?";
        EmpleadoDTO empleado = null;
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idEmpleado);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String cargoBD = rs.getString("Cargo");
                    String cargoLimpio = cargoBD.trim().replace(" ", "_").toUpperCase();
                    Cargo cargoEnum = Cargo.valueOf(cargoLimpio);
                    
                    String turnoBD = rs.getString("Turno");
                    Turno turnoEnum = Turno.valueOf(turnoBD.trim().toUpperCase());
                    
                    empleado = new EmpleadoDTO(
                        rs.getString("NombreCompleto"),
                        rs.getInt("ID_Empleado"),
                        cargoEnum,
                        rs.getString("Especialidad"),
                        turnoEnum,
                        rs.getInt("Experiencia")
                    );
                }
            }
        } catch (SQLException e) {
            throw new AgenciaException("Error de base de datos al buscar empleado: " + e.getMessage());
        }
        
        return empleado;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EmpleadoDTO> listarTodos() throws AgenciaException {
        String sql = "SELECT * FROM empleado";
        List<EmpleadoDTO> lista = new ArrayList<>();
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                String cargoBD = rs.getString("Cargo");
                String cargoLimpio = cargoBD.trim().replace(" ", "_").toUpperCase();
                Cargo cargoEnum = Cargo.valueOf(cargoLimpio);
                
                String turnoBD = rs.getString("Turno");
                Turno turnoEnum = Turno.valueOf(turnoBD.trim().toUpperCase());
                
                EmpleadoDTO emp = new EmpleadoDTO(
                    rs.getString("NombreCompleto"),
                    rs.getInt("ID_Empleado"),
                    cargoEnum,
                    rs.getString("Especialidad"),
                    turnoEnum,
                    rs.getInt("Experiencia")
                );
                lista.add(emp);
            }
        } catch (SQLException e) {
            throw new AgenciaException("Error de base de datos al listar empleados: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Requisito Analítico: Calcula la comisión total acumulada de un empleado basándose 
     * en el 2% de las reservas totales que ha gestionado exitosamente.
     * Utiliza la función de agregación {@code SUM} en MySQL filtrando únicamente por estado 'Confirmada'.
     *
     * @param idEmpleado El identificador único del empleado.
     * @return El monto total en decimales (double) de las comisiones acumuladas.
     * @throws AgenciaException Si ocurre un error en la base de datos al calcular la suma de importes.
     */
    public double calcularComisionEmpleado(int idEmpleado) throws AgenciaException {
        String sql = "SELECT SUM(ImporteTotal) * 0.02 AS Comision FROM reserva WHERE ID_Empleado = ? AND Estado = 'Confirmada'";
        double comision = 0.0;
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idEmpleado);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    comision = rs.getDouble("Comision");
                }
            }
        } catch (SQLException e) {
            throw new AgenciaException("Error al calcular la comisión del empleado en la BD: " + e.getMessage());
        }
        return comision;
    }
}