CREATE DATABASE IF NOT EXISTS agencia_viajes;

USE agencia_viajes;

-- Cliente
CREATE TABLE cliente (
    DNI CHAR(9) NOT NULL,
    NombreCompleto VARCHAR(100) NOT NULL,
    Correo VARCHAR(100) NOT NULL UNIQUE,
    Direccion VARCHAR(255) NOT NULL,
    Telefono VARCHAR(15) NOT NULL,
    Pasaporte VARCHAR(20),
    PRIMARY KEY (DNI)
);

-- Empleado
CREATE TABLE empleado (
    ID_Empleado INT NOT NULL AUTO_INCREMENT,
    NombreCompleto VARCHAR(100) NOT NULL,
    Cargo VARCHAR(50) NOT NULL,
    Especialidad VARCHAR(50) NOT NULL,
    Turno VARCHAR(20),
    Experiencia INT NOT NULL DEFAULT 0,
    PRIMARY KEY (ID_Empleado)
);

-- Categoría
CREATE TABLE categoria (
    ID_Categoria INT NOT NULL AUTO_INCREMENT,
    NombreCat VARCHAR(50) NOT NULL,
    PRIMARY KEY (ID_Categoria)
);

-- Destino
CREATE TABLE destino (
    Cod_Destino CHAR(5) NOT NULL,
    NombreDestino VARCHAR(100) NOT NULL,
    Pais VARCHAR(50) NOT NULL,
    Ciudad VARCHAR(50) NOT NULL,
    Descripcion TEXT,
    PrecioBase DECIMAL(10,2) NOT NULL,
    Duracion INT NOT NULL,
    Disponibilidad BOOLEAN NOT NULL DEFAULT TRUE,
    ID_Categoria INT NOT NULL,
    PRIMARY KEY (Cod_Destino),
    FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria) 
        ON DELETE RESTRICT ON UPDATE CASCADE
);

-- Reserva
CREATE TABLE reserva (
    ID_Reserva INT NOT NULL AUTO_INCREMENT,
    FechaReserva DATE NOT NULL,
    FechaSalida DATE NOT NULL,
    FechaRegreso DATE NOT NULL,
    NumViajeros INT NOT NULL,
    ImporteTotal DECIMAL(10,2) NOT NULL,
    Estado VARCHAR(20) NOT NULL,
    DNI_Cliente CHAR(9) NOT NULL,
    ID_Empleado INT NOT NULL,
    Cod_Destino CHAR(5) NOT NULL,
    PRIMARY KEY (ID_Reserva),
    FOREIGN KEY (DNI_Cliente) REFERENCES cliente(DNI)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (ID_Empleado) REFERENCES empleado(ID_Empleado)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (Cod_Destino) REFERENCES destino(Cod_Destino)
        ON DELETE RESTRICT ON UPDATE CASCADE
);