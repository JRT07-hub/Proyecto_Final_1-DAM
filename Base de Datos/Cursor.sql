DELIMITER $$

CREATE PROCEDURE sp_premiar_empleados_top()
BEGIN
    -- Variables para almacenar los datos de la fila actual del cursor
    DECLARE v_id_empleado INT;
    DECLARE v_total_ventas DECIMAL(10,2);

    -- Variable de control para el fin del bucle
    DECLARE fin_bucle INT DEFAULT FALSE;

    -- 1. Declaración del CURSOR (Selecciona los empleados y lo que han vendido)
    DECLARE cursor_empleados CURSOR FOR
        SELECT e.ID_Empleado, IFNULL(SUM(r.ImporteTotal), 0)
        FROM empleado e
        LEFT JOIN reserva r ON e.ID_Empleado = r.ID_Empleado AND r.Estado = 'Confirmada'
        GROUP BY e.ID_Empleado;

    -- 2. Manejador de error para cuando el cursor se quede sin filas
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET fin_bucle = TRUE;

    -- 3. Abrir el cursor
    OPEN cursor_empleados;

    -- 4. Bucle de lectura
    bucle_lectura: LOOP
        -- Extraemos los datos de la fila actual en nuestras variables
        FETCH cursor_empleados INTO v_id_empleado, v_total_ventas;

        -- Si ya no hay más filas, salimos del bucle
        IF fin_bucle THEN
            LEAVE bucle_lectura;
        END IF;

        -- Lógica de negocio: Si el empleado ha vendido más de 5000, le damos +1 de experiencia
        IF v_total_ventas > 5000.00 THEN
            UPDATE empleado
            SET Experiencia = Experiencia + 1
            WHERE ID_Empleado = v_id_empleado;
        END IF;

    END LOOP bucle_lectura;

    -- 5. Cerrar el cursor
    CLOSE cursor_empleados;
END$$

DELIMITER ;
