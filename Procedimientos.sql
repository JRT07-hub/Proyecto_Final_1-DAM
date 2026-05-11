-- PROCEDIMIENTO 1: Registro de Reserva con Validación de Importe y Existencia
-- Propósito: Inserta una reserva calculando el importe automáticamente mediante un JOIN con la tabla destino y controlando errores de integridad.


DROP PROCEDURE IF EXISTS sp_RegistrarReservaSegura;
DELIMITER $$
CREATE PROCEDURE sp_RegistrarReservaSegura(
    IN p_DNI CHAR(9),
    IN p_IdEmp INT,
    IN p_CodDestino CHAR(5),
    IN p_NumViajeros INT,
    IN p_Salida DATE,
    IN p_Regreso DATE
)
BEGIN
    DECLARE v_PrecioBase DECIMAL(10,2);
    DECLARE v_ImporteTotal DECIMAL(10,2);
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SELECT 'ERROR: Datos inválidos o conflicto de claves (Cliente/Empleado/Destino no existe).' AS Mensaje;
    END;

    START TRANSACTION;
        SELECT d.PrecioBase INTO v_PrecioBase
        FROM destino d
        JOIN categoria c ON d.ID_Categoria = c.ID_Categoria
        WHERE d.Cod_Destino = p_CodDestino;

        IF v_PrecioBase IS NULL THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Error: Destino no encontrado.';
        END IF;

        SET v_ImporteTotal = v_PrecioBase * p_NumViajeros;

        INSERT INTO reserva (FechaReserva, FechaSalida, FechaRegreso, NumViajeros, ImporteTotal, Estado, DNI_Cliente, ID_Empleado, Cod_Destino)
        VALUES (CURDATE(), p_Salida, p_Regreso, p_NumViajeros, v_ImporteTotal, 'Pendiente', p_DNI, p_IdEmp, p_CodDestino);
    COMMIT;
    
    SELECT 'Reserva exitosa' AS Estado, v_ImporteTotal AS Total_Calculado;
END $$
DELIMITER ;

-- Llamada al procedimiento

CALL sp_RegistrarReservaSegura('11111111A', 5, 'JPN01', 3, '2026-06-01', '2026-06-15');

-- PROCEDIMIENTO 2: Baja de Empleado con Reasignación de Cartera Especializada
-- Propósito: Elimina un empleado y utiliza un JOIN para encontrar automáticamente al sustituto con más experiencia en su misma especialidad.


DROP PROCEDURE IF EXISTS sp_BajaEmpleadoSegura;
DELIMITER $$
CREATE PROCEDURE sp_BajaEmpleadoSegura(IN p_IdEmpEliminar INT)
BEGIN
    DECLARE v_IdNuevoResponsable INT;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SELECT 'ERROR: No se pudo reasignar la cartera. Operación abortada.' AS Mensaje;
    END;

    START TRANSACTION;
        SELECT e2.ID_Empleado INTO v_IdNuevoResponsable
        FROM empleado e1
        JOIN empleado e2 ON e1.Especialidad = e2.Especialidad
        WHERE e1.ID_Empleado = p_IdEmpEliminar AND e2.ID_Empleado <> p_IdEmpEliminar
        ORDER BY e2.Experiencia DESC LIMIT 1;

        IF v_IdNuevoResponsable IS NULL THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Error: No hay compañeros de la misma especialidad.';
        END IF;

        UPDATE reserva SET ID_Empleado = v_IdNuevoResponsable WHERE ID_Empleado = p_IdEmpEliminar;
        DELETE FROM empleado WHERE ID_Empleado = p_IdEmpEliminar;
    COMMIT;
    SELECT CONCAT('Empleado eliminado. Reservas pasadas al ID: ', v_IdNuevoResponsable) AS Resultado;
END $$
DELIMITER ;


-- Llamada al procedimiento

CALL sp_BajaEmpleadoSegura(2);

-- PROCEDIMIENTO 3: Aplicar Descuento de Fidelidad a Clientes Platino
-- Propósito: Identifica mediante un JOIN a clientes con gasto histórico > 5000€ y aplica un descuento a sus reservas pendientes.


DROP PROCEDURE IF EXISTS sp_AplicarDescuentoFidelidad;
DELIMITER $$
CREATE PROCEDURE sp_AplicarDescuentoFidelidad(IN p_Porcentaje DECIMAL(5,2))
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION SELECT 'Error en la actualización masiva' AS Mensaje;

    UPDATE reserva r
    JOIN (
        SELECT DNI_Cliente 
        FROM reserva 
        GROUP BY DNI_Cliente 
        HAVING SUM(ImporteTotal) > 5000
    ) AS vip ON r.DNI_Cliente = vip.DNI_Cliente
    SET r.ImporteTotal = r.ImporteTotal * (1 - (p_Porcentaje / 100))
    WHERE r.Estado = 'Pendiente';

    SELECT 'Descuento aplicado correctamente a clientes VIP' AS Resultado;
END $$
DELIMITER ;


-- Llamada al procedimiento
CALL sp_AplicarDescuentoFidelidad(10.00);

-- PROCEDIMIENTO 4: Mantenimiento Preventivo de Destinos sin Ventas
-- Propósito: Elimina destinos que no tienen registros en la tabla reserva (Destinos Fantasma) de forma segura.


DROP PROCEDURE IF EXISTS sp_LimpiarDestinosObsoletos;
DELIMITER $$
CREATE PROCEDURE sp_LimpiarDestinosObsoletos()
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION SELECT 'Error: Integridad referencial violada.' AS Mensaje;

    DELETE d FROM destino d
    LEFT JOIN reserva r ON d.Cod_Destino = r.Cod_Destino
    WHERE r.ID_Reserva IS NULL;

    SELECT 'Mantenimiento finalizado: Destinos obsoletos eliminados.' AS Resultado;
END $$
DELIMITER ;


-- Llamada al procedimiento
CALL sp_LimpiarDestinosObsoletos();