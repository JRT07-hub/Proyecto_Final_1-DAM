-- FUNCIÓN 1: Porcentaje de Éxito en Ventas por Empleado
-- Propósito: Devuelve un texto con el ratio de reservas pagadas frente al total gestionado por un empleado.
DROP FUNCTION IF EXISTS fn_EficienciaEmpleado;
DELIMITER $$
CREATE FUNCTION fn_EficienciaEmpleado(p_IdEmp INT) 
RETURNS VARCHAR(50)
DETERMINISTIC
BEGIN
    DECLARE v_Total INT;
    DECLARE v_Pagadas INT;
    DECLARE v_Ratio DECIMAL(5,2);

    SELECT COUNT(*) INTO v_Total FROM reserva WHERE ID_Empleado = p_IdEmp;
    IF v_Total = 0 THEN RETURN 'Sin actividad'; END IF;

    SELECT COUNT(*) INTO v_Pagadas FROM reserva WHERE ID_Empleado = p_IdEmp AND Estado = 'Pagada';
    SET v_Ratio = (v_Pagadas / v_Total) * 100;

    RETURN CONCAT(v_Ratio, '% de efectividad');
END $$
DELIMITER ;


-- Llamada al función

SELECT NombreCompleto, fn_EficienciaEmpleado(1) AS Eficiencia FROM empleado WHERE ID_Empleado = 1;

-- FUNCIÓN 2: Gasto Promedio por Categoría Turística
-- Propósito: Calcula el promedio de ingresos de una categoría específica uniendo tablas de reserva, destino y categoría.


DROP FUNCTION IF EXISTS fn_GastoMedioCategoria;
DELIMITER $$
CREATE FUNCTION fn_GastoMedioCategoria(p_NombreCat VARCHAR(50)) 
RETURNS DECIMAL(10,2)
DETERMINISTIC
BEGIN
    DECLARE v_Promedio DECIMAL(10,2);

    SELECT AVG(r.ImporteTotal) INTO v_Promedio
    FROM reserva r
    JOIN destino d ON r.Cod_Destino = d.Cod_Destino
    JOIN categoria c ON d.ID_Categoria = c.ID_Categoria
    WHERE c.NombreCat = p_NombreCat;

    RETURN IFNULL(v_Promedio, 0.00);
END $$
DELIMITER ;


-- Llamada al función

SELECT fn_GastoMedioCategoria('Aventura') AS Promedio_Euros;

-- FUNCIÓN 3: Contador de Días de Viaje por Cliente
-- Propósito: Suma la duración real (en días) de todos los viajes finalizados/pagados de un cliente.


DROP FUNCTION IF EXISTS fn_DiasTotalesViajados;
DELIMITER $$
CREATE FUNCTION fn_DiasTotalesViajados(p_DNI CHAR(9)) 
RETURNS INT
DETERMINISTIC
BEGIN
    DECLARE v_Dias INT;

    SELECT SUM(DATEDIFF(FechaRegreso, FechaSalida)) INTO v_Dias
    FROM reserva
    WHERE DNI_Cliente = p_DNI AND Estado = 'Pagada';

    RETURN IFNULL(v_Dias, 0);
END $$
DELIMITER ;


-- Llamada al función

SELECT fn_DiasTotalesViajados('33333333C') AS Dias_Acumulados;

-- FUNCIÓN 4: Destino con Mayor Volumen de Ingresos de un Empleado
-- Propósito: Identifica el nombre del destino que más facturación ha reportado a un empleado mediante un JOIN.


DROP FUNCTION IF EXISTS fn_DestinoEstrellaEmpleado;
DELIMITER $$
CREATE FUNCTION fn_DestinoEstrellaEmpleado(p_IdEmp INT) 
RETURNS VARCHAR(100)
DETERMINISTIC
BEGIN
    DECLARE v_Nombre VARCHAR(100);

    SELECT d.NombreDestino INTO v_Nombre
    FROM destino d
    JOIN reserva r ON d.Cod_Destino = r.Cod_Destino
    WHERE r.ID_Empleado = p_IdEmp
    GROUP BY d.NombreDestino
    ORDER BY SUM(r.ImporteTotal) DESC
    LIMIT 1;

    RETURN IFNULL(v_Nombre, 'Ninguno');
END $$
DELIMITER ;


-- Llamada al función

SELECT fn_DestinoEstrellaEmpleado(1) AS Producto_Estrella;