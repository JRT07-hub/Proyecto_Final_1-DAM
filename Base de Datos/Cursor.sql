DELIMITER $$

CREATE PROCEDURE sp_premiar_empleados_top()
BEGIN
    DECLARE v_id_empleado INT;
    DECLARE v_total_ventas DECIMAL(10,2);
    DECLARE fin_bucle INT DEFAULT FALSE;
    DECLARE cursor_empleados CURSOR FOR
        SELECT e.ID_Empleado, IFNULL(SUM(r.ImporteTotal), 0)
        FROM empleado e
        LEFT JOIN reserva r ON e.ID_Empleado = r.ID_Empleado AND r.Estado = 'Confirmada'
        GROUP BY e.ID_Empleado;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET fin_bucle = TRUE;
    OPEN cursor_empleados;
    bucle_lectura: LOOP
        FETCH cursor_empleados INTO v_id_empleado, v_total_ventas;
        IF fin_bucle THEN
            LEAVE bucle_lectura;
        END IF;
        IF v_total_ventas > 5000.00 THEN
            UPDATE empleado
            SET Experiencia = Experiencia + 1
            WHERE ID_Empleado = v_id_empleado;
        END IF;

    END LOOP bucle_lectura;
    CLOSE cursor_empleados;
END$$

DELIMITER ;
