# Sistema de Gestión para Agencia de Viajes "WorldConnection"

Este repositorio contiene la implementación de la capa de persistencia (Base de Datos en MySQL) y la documentación técnica del sistema de gestión para la agencia de viajes **WorldConnection**, especializada en la organización de viajes nacionales e internacionales.

El proyecto ha sido desarrollado bajo un entorno formativo siguiendo una arquitectura de software robusta, aplicando el patrón de diseño **DAO (Data Access Object)** en la capa de la aplicación (Java) y un control avanzado de integridad, transacciones y lógica de negocio programada directamente en el servidor de Base de Datos mediante **Procedimientos Almacenados, Funciones, Triggers y Cursores**.

---

## 🛠️ Estructura del Proyecto de Base de Datos

El diseño se compone de 5 entidades principales interconectadas mediante relaciones relacionales `[1:N]`:
*   **Cliente:** Información personal, de contacto e historial de reservas. Obligatoriedad de pasaporte para destinos internacionales.
*   **Empleado:** Gestión de la plantilla de la agencia, puestos (`Cargo`), horarios (`Turno`), experiencia y especialidades.
*   **Categoría:** Tabla maestra que clasifica la naturaleza de los viajes (Playa, Aventura, Cultural, etc.).
*   **Destino:** Catálogo de la oferta turística incluyendo ciudades, países, descripciones, precios base y disponibilidad de plazas.
*   **Reserva:** Núcleo transaccional que unifica a los clientes, los empleados gestores y los destinos contratados, calculando importes de forma automática y controlando los estados de la reserva.

---

## 💾 Scripts y Componentes de la Base de Datos

En la carpeta `Base de Datos/` encontrarás los siguientes módulos SQL independientes y listos para producción:

### 1. Modelo Entidad-Relación y Relacional
*   `Base de Datos/PK Base de Datos/ModeloER.png`: Diagrama visual Entidad-Relación que modela la lógica del problema.
*   `Base de Datos/ModeloRelacional.txt`: Definición textual de las claves primarias (`PK`), claves foráneas (`FK`) y restricciones de integridad.

### 2. Estructura y Tablas Principales
*   `Base de Datos/BBDD_agencias_viajes.sql`: Contiene la creación de la base de datos `agencia_viajes` y la definición DDL de todas las tablas (`cliente`, `empleado`, `categoria`, `destino`, `reserva`) con sus correspondientes restricciones de clave y borrados/actualizaciones en cascada (`ON DELETE / ON UPDATE`).
*   `Base de Datos/Script_agencia_viajes.sql`: Script unificado para el despliegue rápido de la estructura base del sistema.

### 3. Programación y Lógica de Negocio Almacenada
*   `Base de Datos/Procedimientos.sql`: Operaciones transaccionales y de mantenimiento seguro:
    *   `sp_RegistrarReservaSegura`: Inserción controlada de reservas con verificación interna del destino mediante un `JOIN`, cálculo automatizado del importe total e implementación de bloques de reversión (`ROLLBACK`) ante fallos.
    *   `sp_BajaEmpleadoSegura`: Borrado lógico/físico de un empleado reasignando automáticamente toda su cartera de reservas activas al compañero disponible con mayor experiencia dentro de su misma especialidad.
    *   `sp_AplicarDescuentoFidelidad`: Identificación masiva mediante agregación (`SUM` e `INGRESOS > 5000€`) de clientes VIP para aplicar descuentos porcentuales a sus reservas pendientes.
    *   `sp_LimpiarDestinosObsoletos`: Mantenimiento preventivo que elimina de forma segura aquellos destinos "fantasma" que nunca han registrado ventas.
*   `Base de Datos/Funciones.sql`: Funciones deterministas optimizadas para la explotación de datos en la aplicación:
    *   `fn_EficienciaEmpleado`: Calcula el ratio de éxito en ventas (reservas pagadas frente a totales).
    *   `fn_GastoMedioCategoria`: Computa el promedio de facturación de un tipo de categoría uniendo tres tablas.
    *   `fn_DiasTotalesViajados`: Suma la duración real de todos los viajes disfrutados por un cliente (`DATEDIFF`).
    *   `fn_DestinoEstrellaEmpleado`: Retorna el nombre del destino que mayor facturación ha reportado a un trabajador específico.
*   `Base de Datos/Trigger.sql`: 
    *   `tg_calcular_importe_reserva`: Disparador automático de tipo `BEFORE INSERT` en la tabla `reserva` que intercepta la transacción, consulta el precio base del destino seleccionado y calcula el `ImporteTotal` de forma exacta multiplicándolo por el número de viajeros, garantizando la consistencia de los datos económicos sin delegar la responsabilidad en la capa cliente.
*   `Base de Datos/Cursor.sql`:
    *   `sp_premiar_empleados_top`: Procedimiento avanzado que implementa un cursor de lectura (`CURSOR FOR`) para recorrer de forma secuencial la plantilla de empleados, evaluar su volumen total de ventas en estado 'Confirmada' y recompensar con un año de experiencia extra a aquellos que superen el umbral crítico de 5000.00€.
*   `Base de Datos/Funcionalidades_Avanzadas.sql`: Consultas complejas para la toma de decisiones estratégicas, incluyendo:
    *   Alertas de pasaportes faltantes para vuelos internacionales próximos (< 15 días).
    *   Cálculo automático del 2% de comisiones mensuales por empleado.
    *   Análisis cruzado de rendimientos financieros por categoría turística.
    *   Detección proactiva de solapamiento de fechas de viaje para un mismo cliente.
    *   Previsiones de ocupación y volumen de viajeros mensual.
    *   Auditoría de urgencia para reservas 'Pendientes' con salidas en menos de 48 horas.

---

## ☕ Arquitectura de la Aplicación (Java)

El software cliente está desarrollado en Java bajo el patrón de diseño **DAO**, estructurado de la siguiente manera:
*   **Conexión:** Administra el ciclo de vida del enlace con el servidor MySQL mediante JDBC.
*   **DAO (Data Access Object):** Clases que contienen las sentencias SQL preparadas (`PreparedStatement` y `CallableStatement`) aisladas completamente de la lógica de presentación.
*   **DTO (Data Transfer Object):** Objetos de transferencia que encapsulan los datos de las tablas y realizan validaciones de formato internas antes de persistir (métodos `.validar()`).
*   **Excepciones (`AgenciaException`):** Captura personalizada y segura de errores en tiempo de ejecución (fallos SQL, formatos de DNI incorrectos, etc.).
*   **Módulos / Vistas:** Interfaz interactiva para el procesamiento secuencial de operaciones CRUD utilizando estructuras de datos avanzadas como `Iterator`, `HashMap`, `TreeMap` y operaciones de flujo **Java Streams** para la ordenación alfabética y el filtrado en tiempo real.

---

## 🚀 Requisitos e Instalación

Para desplegar y utilizar este sistema de forma local, asegúrese de cumplir con los siguientes requisitos incluidos en el paquete:

1.  **Librerías Externas:**
    *   `Librerias externas/PK Librerias externas/javafx-sdk-26.0.zip`: SDK necesario para la ejecución y renderizado de la interfaz gráfica de usuario.
    *   `Connector J/ 8.0.25`: Driver JDBC oficial de Oracle para habilitar la comunicación entre Java y MySQL.
2.  **Base de Datos:**
    *   Contar con una instancia activa de MySQL Server.
    *   Ejecutar en orden los scripts: `BBDD_agencias_viajes.sql` seguido de los archivos de funciones, procedimientos, triggers y cursores.

---

## 👥 Desarrollador
*   **Autor:** Javier Rincón Toro
*   **Especialidad:** Desarrollo de Aplicaciones Multiplataforma (DAM)
*   **Centro:** Escuelas Salesianas María Auxiliadora, Sevilla (Año 2026)
