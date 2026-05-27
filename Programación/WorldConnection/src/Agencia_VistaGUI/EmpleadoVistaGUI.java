package Agencia_VistaGUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.Iterator;
import java.util.List;

import Agencia_DAO.EmpleadoDAO;
import Agencia_DTO.EmpleadoDTO;
import Agencia_DTO.Cargo;
import Agencia_DTO.Turno;
import Agencia_Excepciones.AgenciaException;
import Agencia_Main.MainGUI;

public class EmpleadoVistaGUI {
    
    // Almacén estático para que todos los métodos de esta clase puedan usar el DAO
    private static EmpleadoDAO empleadoDao;

    // =================================================================
    // 1. VISTA: SUBMENÚ DE EMPLEADOS
    // =================================================================
    public static VBox crearMenuEmpleados(BorderPane layoutPrincipal, EmpleadoDAO daoRecibido, MainGUI mainApp) {
        // Asignamos el DAO recibido a nuestra variable de clase para que sea accesible por todos los métodos
        empleadoDao = daoRecibido;

        VBox menu = new VBox(12);
        menu.setAlignment(Pos.CENTER);

        Label titulo = new Label("Módulo Gestión de Empleados");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        titulo.setStyle("-fx-text-fill: #2C3E50; -fx-padding: 0 0 15 0;");

        Button btnInsertar = mainApp.crearBotonMenu("1. Registrar nuevo empleado (Alta)");
        Button btnListar = mainApp.crearBotonMenu("2. Ver lista completa de empleados");
        Button btnBuscar = mainApp.crearBotonMenu("3. Buscar empleado por ID");
        Button btnModificar = mainApp.crearBotonMenu("4. Modificar datos de un empleado");
        Button btnEliminar = mainApp.crearBotonMenu("5. Dar de baja / Eliminar empleado");
        Button btnComision = mainApp.crearBotonMenu("6. Calcular Comisión Acumulada");
        Button btnVolver = mainApp.crearBotonVolver("7. Volver al menú principal");

        // Enrutamiento gráfico interactivo
        btnInsertar.setOnAction(e -> layoutPrincipal.setCenter(crearFormularioNuevoEmpleado(layoutPrincipal, mainApp)));
        btnListar.setOnAction(e -> layoutPrincipal.setCenter(crearVistaListarEmpleados(layoutPrincipal, mainApp)));
        btnBuscar.setOnAction(e -> layoutPrincipal.setCenter(crearVistaBuscarEmpleado(layoutPrincipal, mainApp)));
        btnModificar.setOnAction(e -> layoutPrincipal.setCenter(crearVistaModificarEmpleado(layoutPrincipal, mainApp)));
        btnEliminar.setOnAction(e -> layoutPrincipal.setCenter(crearVistaEliminarEmpleado(layoutPrincipal, mainApp)));
        btnComision.setOnAction(e -> layoutPrincipal.setCenter(crearVistaCalcularComision(layoutPrincipal, mainApp)));
        btnVolver.setOnAction(e -> layoutPrincipal.setCenter(mainApp.crearMenuPrincipal()));

        menu.getChildren().addAll(titulo, btnInsertar, btnListar, btnBuscar, btnModificar, btnEliminar, btnComision, btnVolver);
        return menu;
    }

    // =================================================================
    // 2. VISTA: ALTA DE EMPLEADO (INSERT)
    // =================================================================
    private static VBox crearFormularioNuevoEmpleado(BorderPane layoutPrincipal, MainGUI mainApp) {
        VBox contenedor = new VBox(20);
        contenedor.setAlignment(Pos.CENTER);
        
        Label titulo = new Label("Alta de Nuevo Empleado");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        GridPane formulario = new GridPane();
        formulario.setAlignment(Pos.CENTER);
        formulario.setHgap(10); formulario.setVgap(10);

        TextField txtId = mainApp.crearCampoTexto(formulario, "ID Empleado (Numérico):", "Ej: 101", 0);
        TextField txtNom = mainApp.crearCampoTexto(formulario, "Nombre Completo:", "Ej: Carlos Gómez", 1);
        TextField txtCargo = mainApp.crearCampoTexto(formulario, "Cargo:", "AGENTE_JUNIOR, AGENTE_SENIOR, GERENTE...", 2);
        TextField txtEsp = mainApp.crearCampoTexto(formulario, "Especialidad:", "Ej: Cruceros / Vuelos", 3);
        TextField txtTurno = mainApp.crearCampoTexto(formulario, "Turno:", "MAÑANA o TARDE", 4);
        TextField txtExp = mainApp.crearCampoTexto(formulario, "Años Experiencia:", "Ej: 5", 5);
        
        Button btnGuardar = new Button("Registrar en MySQL");
        btnGuardar.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;");
        Button btnCancelar = mainApp.crearBotonVolver("Cancelar");

        HBox cajaBotones = new HBox(15, btnGuardar, btnCancelar);
        cajaBotones.setAlignment(Pos.CENTER);

        // Corregido: Ahora pasamos los 3 parámetros requeridos
        btnCancelar.setOnAction(e -> layoutPrincipal.setCenter(crearMenuEmpleados(layoutPrincipal, empleadoDao, mainApp)));
        btnGuardar.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtId.getText().trim());
                String nom = txtNom.getText().trim();
                String cargoIn = txtCargo.getText().trim().replace(" ", "_").toUpperCase();
                String esp = txtEsp.getText().trim();
                String turnoIn = txtTurno.getText().trim().toUpperCase();
                int exp = Integer.parseInt(txtExp.getText().trim());

                EmpleadoDTO nuevo = new EmpleadoDTO(nom, id, Cargo.valueOf(cargoIn), esp, Turno.valueOf(turnoIn), exp);
                empleadoDao.insertar(nuevo);
                
                mainApp.mostrarAlerta("¡Éxito!", "Empleado guardado correctamente en la base de datos.");
                layoutPrincipal.setCenter(crearMenuEmpleados(layoutPrincipal, empleadoDao, mainApp));
                
            } catch (NumberFormatException ex) {
                mainApp.mostrarAlertaError("Error de Formato", "El ID y los Años de Experiencia deben ser números enteros.");
            } catch (IllegalArgumentException ex) {
                mainApp.mostrarAlertaError("Error de Constantes", "Cargo o Turno no válidos. Revisa las constantes exactas de tus Enums.");
            } catch (AgenciaException ex) {
                mainApp.mostrarAlertaError("Error BD", "Operación cancelada: " + ex.getMessage());
            } catch (Exception ex) {
                mainApp.mostrarAlertaError("Error Indeterminado", ex.getMessage());
            }
        });

        contenedor.getChildren().addAll(titulo, formulario, cajaBotones);
        return contenedor;
    }

    // =================================================================
    // 3. VISTA: LISTADO GENERAL UTILIZANDO ITERATOR
    // =================================================================
    private static VBox crearVistaListarEmpleados(BorderPane layoutPrincipal, MainGUI mainApp) {
        VBox contenedor = new VBox(15);
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setPadding(new Insets(20));

        Label titulo = new Label("Listado General de Empleados");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        TextArea areaTexto = new TextArea();
        areaTexto.setEditable(false);
        areaTexto.setFont(Font.font("Monospaced", 13));

        try {
            List<EmpleadoDTO> lista = empleadoDao.listarTodos();
            if (lista.isEmpty()) {
                areaTexto.setText("No hay empleados registrados.");
            } else {
                StringBuilder sb = new StringBuilder();
                Iterator<EmpleadoDTO> it = lista.iterator();
                while (it.hasNext()) {
                    EmpleadoDTO e = it.next();
                    sb.append("• ID: ").append(e.getID_Empleado())
                      .append(" | ").append(e.getNombreCompleto())
                      .append(" [").append(e.getCargo()).append("]")
                      .append(" - Exp: ").append(e.getAnios_experiencia()).append(" años\n");
                }
                areaTexto.setText(sb.toString());
            }
        } catch (AgenciaException e) {
            areaTexto.setText("[ERROR BD] " + e.getMessage());
        }

        Button btnVolver = mainApp.crearBotonVolver("Volver");
        btnVolver.setOnAction(e -> layoutPrincipal.setCenter(crearMenuEmpleados(layoutPrincipal, empleadoDao, mainApp)));

        contenedor.getChildren().addAll(titulo, areaTexto, btnVolver);
        return contenedor;
    }

    // =================================================================
    // 4. VISTA: BUSCAR EMPLEADO POR ID
    // =================================================================
    private static VBox crearVistaBuscarEmpleado(BorderPane layoutPrincipal, MainGUI mainApp) {
        VBox contenedor = new VBox(20);
        contenedor.setAlignment(Pos.CENTER);

        Label titulo = new Label("Buscar Empleado por ID");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        HBox cajaBusqueda = new HBox(10);
        cajaBusqueda.setAlignment(Pos.CENTER);
        Label lblId = new Label("ID Empleado:");
        TextField txtIdB = new TextField();
        Button btnBuscar = new Button("Buscar");
        btnBuscar.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-weight: bold;");
        cajaBusqueda.getChildren().addAll(lblId, txtIdB, btnBuscar);

        VBox resultadoBox = new VBox(8);
        resultadoBox.setAlignment(Pos.CENTER_LEFT);
        resultadoBox.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #BDC3C7; -fx-padding: 15; -fx-border-radius: 5;");
        resultadoBox.setMaxWidth(450);
        resultadoBox.setVisible(false);

        Label lblNombre = new Label();
        Label lblCargo = new Label();
        Label lblTurno = new Label();
        resultadoBox.getChildren().addAll(lblNombre, lblCargo, lblTurno);

        btnBuscar.setOnAction(e -> {
            try {
                int idB = Integer.parseInt(txtIdB.getText().trim());
                EmpleadoDTO empB = empleadoDao.buscarPorId(idB);
                if (empB != null) {
                    lblNombre.setText("-> Nombre: " + empB.getNombreCompleto());
                    lblCargo.setText("-> Cargo:  " + empB.getCargo());
                    lblTurno.setText("-> Turno:  " + empB.getTurno());
                    resultadoBox.setVisible(true);
                } else {
                    resultadoBox.setVisible(false);
                    mainApp.mostrarAlerta("Aviso", "Empleado no encontrado.");
                }
            } catch (NumberFormatException ex) {
                mainApp.mostrarAlertaError("Formato incorrecto", "El ID debe ser un número entero.");
            } catch (AgenciaException ex) {
                mainApp.mostrarAlertaError("Error BD", ex.getMessage());
            }
        });

        Button btnVolver = mainApp.crearBotonVolver("Volver");
        btnVolver.setOnAction(e -> layoutPrincipal.setCenter(crearMenuEmpleados(layoutPrincipal, empleadoDao, mainApp)));

        contenedor.getChildren().addAll(titulo, cajaBusqueda, resultadoBox, btnVolver);
        return contenedor;
    }

    // =================================================================
    // 5. VISTA: MODIFICAR DATOS (UPDATE PRE-CARGADO)
    // =================================================================
    private static VBox crearVistaModificarEmpleado(BorderPane layoutPrincipal, MainGUI mainApp) {
        VBox contenedor = new VBox(15);
        contenedor.setAlignment(Pos.CENTER);

        Label titulo = new Label("Modificar Datos de Empleado");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        HBox busquedaBox = new HBox(10);
        busquedaBox.setAlignment(Pos.CENTER);
        Label lblId = new Label("Introduce ID:");
        TextField txtIdM = new TextField();
        Button btnCargar = new Button("Cargar Ficha");
        btnCargar.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");
        busquedaBox.getChildren().addAll(lblId, txtIdM, btnCargar);

        GridPane formulario = new GridPane();
        formulario.setAlignment(Pos.CENTER);
        formulario.setHgap(10); formulario.setVgap(10);
        formulario.setVisible(false);

        TextField txtNom = mainApp.crearCampoTexto(formulario, "Nuevo Nombre:", "", 0);
        TextField txtCargo = mainApp.crearCampoTexto(formulario, "Nuevo Cargo:", "", 1);
        TextField txtEsp = mainApp.crearCampoTexto(formulario, "Nueva Especialidad:", "", 2);
        TextField txtTurno = mainApp.crearCampoTexto(formulario, "Nuevo Turno:", "", 3);
        TextField txtExp = mainApp.crearCampoTexto(formulario, "Nuevos Años Exp:", "", 4);

        Button btnGuardar = new Button("Actualizar en MySQL");
        btnGuardar.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        HBox cajaBotonForm = new HBox(btnGuardar);
        cajaBotonForm.setAlignment(Pos.CENTER);
        cajaBotonForm.setVisible(false);

        btnCargar.setOnAction(e -> {
            try {
                int idM = Integer.parseInt(txtIdM.getText().trim());
                EmpleadoDTO empM = empleadoDao.buscarPorId(idM);
                if (empM != null) {
                    txtNom.setText(empM.getNombreCompleto());
                    txtCargo.setText(empM.getCargo().toString());
                    txtEsp.setText(empM.getEspecialidad());
                    txtTurno.setText(empM.getTurno().toString());
                    txtExp.setText(String.valueOf(empM.getAnios_experiencia()));
                    
                    formulario.setVisible(true);
                    cajaBotonForm.setVisible(true);
                    txtIdM.setEditable(false);
                } else {
                    formulario.setVisible(false);
                    cajaBotonForm.setVisible(false);
                    mainApp.mostrarAlertaError("Error", "El empleado no existe.");
                }
            } catch (Exception ex) { mainApp.mostrarAlertaError("Error", "Verifica el ID introducido."); }
        });

        btnGuardar.setOnAction(e -> {
            try {
                int idOriginal = Integer.parseInt(txtIdM.getText().trim());
                String nNom = txtNom.getText().trim();
                String nCar = txtCargo.getText().trim().replace(" ", "_").toUpperCase();
                String nEsp = txtEsp.getText().trim();
                String nTur = txtTurno.getText().trim().toUpperCase();
                int nExp = Integer.parseInt(txtExp.getText().trim());

                EmpleadoDTO modificado = new EmpleadoDTO(nNom, idOriginal, Cargo.valueOf(nCar), nEsp, Turno.valueOf(nTur), nExp);
                empleadoDao.modificar(modificado);
                
                mainApp.mostrarAlerta("¡Éxito!", "Datos actualizados correctamente en MySQL.");
                layoutPrincipal.setCenter(crearMenuEmpleados(layoutPrincipal, empleadoDao, mainApp));
            } catch (IllegalArgumentException ex) {
                mainApp.mostrarAlertaError("Formato Erróneo", "Constantes de Cargo o Turno inválidas.");
            } catch (Exception ex) {
                mainApp.mostrarAlertaError("Fallo", ex.getMessage());
            }
        });

        Button btnVolver = mainApp.crearBotonVolver("Cancelar");
        btnVolver.setOnAction(e -> layoutPrincipal.setCenter(crearMenuEmpleados(layoutPrincipal, empleadoDao, mainApp)));

        contenedor.getChildren().addAll(titulo, busquedaBox, formulario, cajaBotonForm, btnVolver);
        return contenedor;
    }

    // =================================================================
    // 6. VISTA: DAR DE BAJA / ELIMINAR REGISTRO
    // =================================================================
    private static VBox crearVistaEliminarEmpleado(BorderPane layoutPrincipal, MainGUI mainApp) {
        VBox contenedor = new VBox(20);
        contenedor.setAlignment(Pos.CENTER);

        Label titulo = new Label("Eliminar Empleado de la Agencia");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titulo.setStyle("-fx-text-fill: #C0392B;");

        HBox cajaForm = new HBox(10);
        cajaForm.setAlignment(Pos.CENTER);
        Label lblId = new Label("ID a eliminar:");
        TextField txtIdE = new TextField();
        Button btnEliminar = new Button("Dar de Baja");
        btnEliminar.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-weight: bold;");
        cajaForm.getChildren().addAll(lblId, txtIdE, btnEliminar);

        btnEliminar.setOnAction(e -> {
            try {
                int idE = Integer.parseInt(txtIdE.getText().trim());
                
                Alert conf = new Alert(AlertType.CONFIRMATION);
                conf.setTitle("Confirmar Borrado");
                conf.setHeaderText(null);
                conf.setContentText("¿Estás seguro de que deseas eliminar permanentemente este registro?");
                
                conf.showAndWait().ifPresent(res -> {
                    if (res == javafx.scene.control.ButtonType.OK) {
                        try {
                            empleadoDao.eliminar(idE);
                            mainApp.mostrarAlerta("Baja Tramitada", "Registro borrado de la base de datos.");
                            txtIdE.clear();
                        } catch (AgenciaException ex) { mainApp.mostrarAlertaError("Error BD", ex.getMessage()); }
                    }
                });
            } catch (NumberFormatException ex) { mainApp.mostrarAlertaError("Error", "Introduce un ID numérico válido."); }
        });

        Button btnVolver = mainApp.crearBotonVolver("Volver");
        btnVolver.setOnAction(e -> layoutPrincipal.setCenter(crearMenuEmpleados(layoutPrincipal, empleadoDao, mainApp)));

        contenedor.getChildren().addAll(titulo, cajaForm, btnVolver);
        return contenedor;
    }

    // =================================================================
    // 7. VISTA: MÉTRICA ANALÍTICA - CALCULAR COMISIÓN ACUMULADA
    // =================================================================
    private static VBox crearVistaCalcularComision(BorderPane layoutPrincipal, MainGUI mainApp) {
        VBox contenedor = new VBox(20);
        contenedor.setAlignment(Pos.CENTER);

        Label titulo = new Label("Métrica Analítica de Rendimiento");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titulo.setStyle("-fx-text-fill: #2C3E50;");

        Label desc = new Label("Realiza un cálculo complejo agregando funciones SUM directas de SQL\npara obtener la comisión del 2% del total facturado por este asesor.");
        desc.setStyle("-fx-text-fill: #7F8C8D; -fx-text-alignment: center;");

        HBox cajaAccion = new HBox(10);
        cajaAccion.setAlignment(Pos.CENTER);
        Label lblId = new Label("ID del Empleado:");
        TextField txtIdC = new TextField();
        Button btnCalcular = new Button("Calcular Métrica");
        btnCalcular.setStyle("-fx-background-color: #9B59B6; -fx-text-fill: white; -fx-font-weight: bold;");
        cajaAccion.getChildren().addAll(lblId, txtIdC, btnCalcular);

        Label lblResultado = new Label();
        lblResultado.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        lblResultado.setStyle("-fx-text-fill: #27AE60;");

        btnCalcular.setOnAction(e -> {
            try {
                int idC = Integer.parseInt(txtIdC.getText().trim());
                double comision = empleadoDao.calcularComisionEmpleado(idC);
                lblResultado.setText("💰 Comisión acumulada: " + comision + "€");
            } catch (NumberFormatException ex) {
                mainApp.mostrarAlertaError("Error", "Introduce un ID numérico.");
            } catch (AgenciaException ex) {
                mainApp.mostrarAlertaError("Error base de datos", ex.getMessage());
            }
        });

        Button btnVolver = mainApp.crearBotonVolver("Volver");
        btnVolver.setOnAction(e -> layoutPrincipal.setCenter(crearMenuEmpleados(layoutPrincipal, empleadoDao, mainApp)));

        contenedor.getChildren().addAll(titulo, desc, cajaAccion, lblResultado, btnVolver);
        return contenedor;
    }
}