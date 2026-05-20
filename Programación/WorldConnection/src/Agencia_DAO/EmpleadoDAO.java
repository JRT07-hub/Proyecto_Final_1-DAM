package Agencia_DAO;

import Conexion.Conexion;
import Agencia_DTO.EmpleadoDTO;
import Agencia_DTO.Cargo;
import Agencia_DTO.Turno;
import Agencia_Excepciones.AgenciaException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDAO implements Idao<EmpleadoDTO, Integer> {

    // =========================================================================
    // 1. OPERACIÓN: INSERTAR EMPLEADO (C de CRUD)
    // =========================================================================
    @Override
    public void insertar(EmpleadoDTO empleado) throws AgenciaException {
        String sql = "INSERT INTO empleado (ID_Empleado, NombreCompleto, Cargo, Especialidad, Turno, AniosExperiencia) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, empleado.getID_Empleado());
            ps.setString(2, empleado.getNombreCompleto());
            // Guardamos los ENUMs en MySQL como texto usando .name() (Ej: "AGENTE_SENIOR")
            ps.setString(3, empleado.getCargo().name());
            ps.setString(4, empleado.getEspecialidad());
            ps.setString(5, empleado.getTurno().name());
            ps.setInt(6, empleado.getAnios_experiencia());
            
            ps.executeUpdate();
            
        } catch (SQLException e) {
            throw new AgenciaException("Error de base de datos al registrar el empleado: " + e.getMessage());
        }
    }

    // =========================================================================
    // 2. OPERACIÓN: MODIFICAR EMPLEADO (U de CRUD)
    // =========================================================================
    @Override
    public void modificar(EmpleadoDTO empleado) throws AgenciaException {
        String sql = "UPDATE empleado SET NombreCompleto = ?, Cargo = ?, Especialidad = ?, Turno = ?, AniosExperiencia = ? WHERE ID_Empleado = ?";
        
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

    // =========================================================================
    // 3. OPERACIÓN: ELIMINAR EMPLEADO (D de CRUD)
    // =========================================================================
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

    // =========================================================================
    // 4. OPERACIÓN: BUSCAR POR ID (R de CRUD) - CORREGIDO Y BLINDADO
    // =========================================================================
    @Override
    public EmpleadoDTO buscarPorId(Integer idEmpleado) throws AgenciaException {
        String sql = "SELECT * FROM empleado WHERE ID_Empleado = ?";
        EmpleadoDTO empleado = null;
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idEmpleado);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // BLINDAJE: Limpiamos el texto que viene de MySQL antes de convertirlo a Enum
                    String cargoBD = rs.getString("Cargo");
                    String cargoLimpio = cargoBD.trim().replace(" ", "_").toUpperCase();
                    Cargo cargoEnum = Cargo.valueOf(cargoLimpio);
                    
                    String turnoBD = rs.getString("Turno");
                    Turno turnoEnum = Turno.valueOf(turnoBD.trim().toUpperCase());
                    
                    // Reconstruimos el DTO llamando a tu constructor robusto
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

    // =========================================================================
    // 5. OPERACIÓN: LISTAR TODOS LOS EMPLEADOS (R de CRUD) - CORREGIDO Y BLINDADO
    // =========================================================================
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

    // =========================================================================
    // EXTRA: ENLAZAR CON TUS REQUISITOS / SCRIPTS AVANZADOS DE LA AGENCIA
    // =========================================================================
    /**
     * Calcula la comisión total acumulada de un empleado en base al 2% de sus reservas gestionadas.
     * Mapea un requisito analítico directo utilizando funciones de agregación (SUM) en MySQL.
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