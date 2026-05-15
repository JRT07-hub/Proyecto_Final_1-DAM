-- FUNCIONALIDAD 1: Alerta de Pasaportes Faltantes
-- Propósito: Detectar clientes que viajan al extranjero en < 15 días sin pasaporte.


SELECT 
    c.NombreCompleto, 
    c.Telefono, 
    d.NombreDestino, 
    r.FechaSalida
FROM cliente c
JOIN reserva r ON c.DNI = r.DNI_Cliente
JOIN destino d ON r.Cod_Destino = d.Cod_Destino
JOIN categoria cat ON d.ID_Categoria = cat.ID_Categoria
WHERE c.Pasaporte IS NULL
  AND r.FechaSalida <= DATE_ADD(CURDATE(), INTERVAL 15 DAY);


-- FUNCIONALIDAD 2: Cálculo de Comisiones por Empleado
-- Propósito: Calcular el 2% de comisión sobre las ventas del mes actual.


SELECT 
    e.NombreCompleto,
    SUM(r.ImporteTotal) AS Ventas_Totales,
    SUM(r.ImporteTotal) * 0.02 AS Comision_Euros
FROM empleado e
JOIN reserva r ON e.ID_Empleado = r.ID_Empleado
WHERE MONTH(r.FechaReserva) = MONTH(CURDATE())
GROUP BY e.ID_Empleado,e.NombreCompleto;


-- FUNCIONALIDAD 3: Limpieza de Catálogo (Destinos Fantasma)
-- Propósito: Ayudar a la agencia a decidir si debe eliminar esos destinos de la oferta o lanzar una promoción especial para incentivarlos.


SELECT 
    d.NombreDestino,
    d.Pais,
    d.PrecioBase
FROM destino d
LEFT JOIN reserva r ON d.Cod_Destino = r.Cod_Destino
WHERE r.ID_Reserva IS NULL;


-- FUNCIONALIDAD 4: Programa de Fidelización "Cliente Platino"
-- Propósito: Detectar a los clientes más rentables (más de 2 viajes y > 2.000€) para enviarles descuentos exclusivos.


SELECT 
    c.NombreCompleto, 
    COUNT(r.ID_Reserva) AS Numero_Viajes,
    SUM(r.ImporteTotal) AS Gasto_Total
FROM cliente c
JOIN reserva r ON c.DNI = r.DNI_Cliente
GROUP BY c.NombreCompleto
HAVING COUNT(r.ID_Reserva) >= 2
   AND SUM(r.ImporteTotal) > 2000;


-- FUNCIONALIDAD 5: Análisis de Rendimiento por Categoría
-- Propósito: Conocer qué tipo de turismo deja más ingresos para ajustar los precios base de la agencia.


SELECT 
    cat.NombreCat,
    SUM(r.ImporteTotal) AS Ingresos_Totales,
    AVG(r.ImporteTotal) AS Gasto_Medio_Por_Viaje,
    COUNT(r.ID_Reserva) AS Numero_Ventas
FROM reserva r
JOIN destino d ON r.Cod_Destino = d.Cod_Destino
JOIN categoria cat ON d.ID_Categoria = cat.ID_Categoria
GROUP BY cat.NombreCat;


-- FUNCIONALIDAD 6: Control de Solapamiento de Viajes
-- Propósito: Evitar que un mismo cliente reserve dos viajes que ocurran en las mismas fechas.


SELECT 
    c.NombreCompleto,
    r1.ID_Reserva AS Reserva_A,
    r1.FechaSalida AS Salida_A,
    r1.FechaRegreso AS Regreso_A,
    r2.ID_Reserva AS Reserva_B,
    r2.FechaSalida AS Salida_B
FROM reserva r1
JOIN reserva r2 ON r1.DNI_Cliente = r2.DNI_Cliente AND r1.ID_Reserva <> r2.ID_Reserva
JOIN cliente c ON r1.DNI_Cliente = c.DNI
WHERE r2.FechaSalida BETWEEN r1.FechaSalida AND r1.FechaRegreso;


-- FUNCIONALIDAD 7: Previsión de Ocupación Mensual
-- Propósito: Planificar la carga de trabajo de los empleados según el volumen de viajeros total previsto por mes.


SELECT 
    MONTH(FechaSalida) AS Mes, 
    SUM(NumViajeros) AS Total_Viajeros
FROM reserva
GROUP BY MONTH(FechaSalida);


-- FUNCIONALIDAD 8: Auditoría de Estado de Reservas
-- Propósito: Permitir llamadas de urgencia para confirmar pagos de viajes que salen en menos de 48h y siguen 'Pendientes'.


SELECT
    ID_Reserva,
    DNI_Cliente,
    FechaSalida,
    ImporteTotal,
    Estado
FROM reserva
WHERE Estado = 'Pendiente'
AND DATEDIFF(FechaSalida, CURDATE()) <=2
AND DATEDIFF(FechaSalida, CURDATE()) >=0;