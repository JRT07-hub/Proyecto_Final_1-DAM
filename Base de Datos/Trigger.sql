DELIMITER $$

CREATE TRIGGER tg_calcular_importe_reserva
BEFORE INSERT ON reserva
FOR EACH ROW
BEGIN
    DECLARE v_precio_base DECIMAL(10,2);

    -- 1. Obtenemos el precio base del destino seleccionado en la reserva
    SELECT PrecioBase INTO v_precio_base
    FROM destino
    WHERE Cod_Destino = NEW.Cod_Destino;

    -- 2. Calculamos el importe total automáticamente
    SET NEW.ImporteTotal = v_precio_base * NEW.NumViajeros;
END$$

DELIMITER ;
