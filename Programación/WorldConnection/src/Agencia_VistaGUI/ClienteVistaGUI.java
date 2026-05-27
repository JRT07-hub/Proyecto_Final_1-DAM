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
import java.util.List;

import Agencia_DAO.ClienteDAO;
import Agencia_DTO.ClienteDTO;
import Agencia_Excepciones.AgenciaException;
import Agencia_Main.MainGUI;

public class ClienteVistaGUI {

    // =================================================================
    // 2. VISTA: SUBMENÚ DE CLIENTES
    // =================================================================
    public static VBox crearMenuClientes(BorderPane layoutPrincipal, ClienteDAO clienteDao, MainGUI mainApp) {
        VBox menu = new VBox(10);
        menu.setAlignment(Pos.CENTER);

        Label titulo = new Label("Gestión de Clientes");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        titulo.setStyle("-fx-text-fill: #2C3E50; -fx-padding: 0 0 15 0;");

        Button btnInsertar = mainApp.crearBotonMenu("1. Registrar nuevo cliente");
        Button btnBuscar = mainApp.crearBotonMenu("2. Buscar cliente por DNI");
        Button btnModificar = mainApp.crearBotonMenu("3. Modificar datos de un cliente");
        Button btnEliminar = mainApp.crearBotonMenu("4. Eliminar un cliente");
        Button btnListar = mainApp.crearBotonMenu("5. Mostrar todos los clientes");
        Button btnDescuento = mainApp.crearBotonMenu("6. Aplicar descuento VIP");
        Button btnPasaporte = mainApp.crearBotonMenu("7. Consultar clientes con pasaporte");
        Button btnVolver = mainApp.crearBotonVolver("8. Volver al menú principal");

        // ACCIONES DE CLIENTES NAVEGANDO PASANDO LAS REFERENCIAS
        btnInsertar.setOnAction(e -> layoutPrincipal.setCenter(crearFormularioNuevoCliente(layoutPrincipal, clienteDao, mainApp)));
        btnBuscar.setOnAction(e -> layoutPrincipal.setCenter(crearVistaBuscarCliente(layoutPrincipal, clienteDao, mainApp)));
        btnModificar.setOnAction(e -> layoutPrincipal.setCenter(crearVistaModificarCliente(layoutPrincipal, clienteDao, mainApp)));
        btnEliminar.setOnAction(e -> layoutPrincipal.setCenter(crearVistaEliminarCliente(layoutPrincipal, clienteDao, mainApp)));
        btnListar.setOnAction(e -> layoutPrincipal.setCenter(crearVistaListarClientes(layoutPrincipal, clienteDao, mainApp)));
        btnDescuento.setOnAction(e -> layoutPrincipal.setCenter(crearVistaProcedimientoDescuento(layoutPrincipal, clienteDao, mainApp)));
        btnPasaporte.setOnAction(e -> layoutPrincipal.setCenter(crearVistaClientesConPasaporte(layoutPrincipal, clienteDao, mainApp)));
        btnVolver.setOnAction(e -> layoutPrincipal.setCenter(mainApp.crearMenuPrincipal()));

        menu.getChildren().addAll(titulo, btnInsertar, btnBuscar, btnModificar, btnEliminar, btnListar, btnDescuento, btnPasaporte, btnVolver);
        return menu;
    }

    // =================================================================
    // 3. VISTA: FORMULARIO AÑADIR CLIENTE (INSERT)
    // =================================================================
    private static VBox crearFormularioNuevoCliente(BorderPane layoutPrincipal, ClienteDAO clienteDao, MainGUI mainApp) {
        VBox contenedor = new VBox(20);
        contenedor.setAlignment(Pos.CENTER);
        
        Label titulo = new Label("Registrar Nuevo Cliente");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titulo.setStyle("-fx-text-fill: #2C3E50;");

        GridPane formulario = new GridPane();
        formulario.setAlignment(Pos.CENTER);
        formulario.setHgap(10); formulario.setVgap(10);

        TextField txtNombre = mainApp.crearCampoTexto(formulario, "Nombre Completo:", "Ej: Juan Pérez", 0);
        TextField txtDni = mainApp.crearCampoTexto(formulario, "DNI:", "8 números y 1 letra", 1);
        TextField txtCorreo = mainApp.crearCampoTexto(formulario, "Correo electrónico:", "ejemplo@correo.com", 2);
        TextField txtTelefono = mainApp.crearCampoTexto(formulario, "Teléfono:", "9 dígitos", 3);
        TextField txtDir = mainApp.crearCampoTexto(formulario, "Dirección:", "Calle, Número...", 4);
        TextField txtPasaporte = mainApp.crearCampoTexto(formulario, "Pasaporte (Opcional):", "ENTER si no tiene", 5);
        
        Button btnGuardar = new Button("Guardar en MySQL");
        btnGuardar.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;");
        Button btnCancelar = mainApp.crearBotonVolver("Cancelar");

        HBox cajaBotones = new HBox(15, btnGuardar, btnCancelar);
        cajaBotones.setAlignment(Pos.CENTER);

        btnCancelar.setOnAction(e -> layoutPrincipal.setCenter(crearMenuClientes(layoutPrincipal, clienteDao, mainApp)));
        btnGuardar.setOnAction(e -> {
            try {
                String pasaporteVal = txtPasaporte.getText().trim().isEmpty() ? null : txtPasaporte.getText().trim();
                
                ClienteDTO nuevo = new ClienteDTO(
                    txtNombre.getText(), txtDni.getText(), txtCorreo.getText(), 
                    txtTelefono.getText(), txtDir.getText(), pasaporteVal
                );
                
                nuevo.validar();
                clienteDao.insertar(nuevo); 
                mainApp.mostrarAlerta("¡Éxito!", "Cliente registrado correctamente en MySQL.");
                layoutPrincipal.setCenter(crearMenuClientes(layoutPrincipal, clienteDao, mainApp));
                
            } catch (AgenciaException ex) {
                mainApp.mostrarAlertaError("Fallo al insertar", ex.getMessage());
            } catch (Exception ex) {
                mainApp.mostrarAlertaError("Error", "Revisa los datos introducidos.\n" + ex.getMessage());
            }
        });

        contenedor.getChildren().addAll(titulo, formulario, cajaBotones);
        return contenedor;
    }

    // =================================================================
    // 3.1 VISTA: BUSCAR CLIENTE POR DNI (SELECT BY ID)
    // =================================================================
    private static VBox crearVistaBuscarCliente(BorderPane layoutPrincipal, ClienteDAO clienteDao, MainGUI mainApp) {
        VBox contenedor = new VBox(20);
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setPadding(new Insets(20));

        Label titulo = new Label("Buscar Cliente por DNI");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titulo.setStyle("-fx-text-fill: #2C3E50;");

        HBox busquedaBox = new HBox(10);
        busquedaBox.setAlignment(Pos.CENTER);
        
        Label lblDni = new Label("Introduce el DNI:");
        lblDni.setFont(Font.font("Segoe UI", 16));
        TextField txtDni = new TextField();
        txtDni.setPromptText("Ej: 12345678A");
        txtDni.setStyle("-fx-font-size: 14px; -fx-padding: 6;");
        
        Button btnBuscar = new Button("Buscar");
        btnBuscar.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 7 15; -fx-background-radius: 5; -fx-cursor: hand;");
        busquedaBox.getChildren().addAll(lblDni, txtDni, btnBuscar);

        VBox resultadoBox = new VBox(10);
        resultadoBox.setAlignment(Pos.CENTER_LEFT);
        resultadoBox.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #BDC3C7; -fx-border-radius: 5; -fx-padding: 15;");
        resultadoBox.setMaxWidth(400);
        resultadoBox.setVisible(false); 

        Label lblResultadoTitulo = new Label("[REGISTRO ENCONTRADO]");
        lblResultadoTitulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblResultadoTitulo.setStyle("-fx-text-fill: #27AE60; -fx-padding: 0 0 5 0;");
        
        Label lblNombreRes = new Label();
        Label lblEmailRes = new Label();
        Label lblTlfRes = new Label();
        Label lblDirRes = new Label();
        Label lblPasaporteRes = new Label();

        resultadoBox.getChildren().addAll(lblResultadoTitulo, lblNombreRes, lblEmailRes, lblTlfRes, lblDirRes, lblPasaporteRes);

        btnBuscar.setOnAction(e -> {
            String dni = txtDni.getText().trim();
            if (dni.isEmpty()) {
                mainApp.mostrarAlertaError("Campo vacío", "Por favor, introduce un DNI para buscar.");
                return;
            }
            try {
                ClienteDTO c = clienteDao.buscarPorId(dni);
                if (c != null) {
                    lblNombreRes.setText("Nombre:    " + c.getNombreCompleto());
                    lblEmailRes.setText("Email:     " + c.getCorreo());
                    lblTlfRes.setText("Teléfono:  " + c.getTelefono());
                    lblDirRes.setText("Dirección: " + c.getDireccion());
                    lblPasaporteRes.setText("Pasaporte: " + (c.getPasaporte() != null ? c.getPasaporte() : "Ninguno"));
                    resultadoBox.setVisible(true); 
                } else {
                    resultadoBox.setVisible(false);
                    mainApp.mostrarAlerta("Aviso", "No existe ningún cliente en la BD con el DNI: " + dni);
                }
            } catch (AgenciaException ex) {
                mainApp.mostrarAlertaError("Error en la búsqueda", ex.getMessage());
            }
        });

        Button btnVolver = mainApp.crearBotonVolver("Volver al Menú de Clientes");
        btnVolver.setOnAction(e -> layoutPrincipal.setCenter(crearMenuClientes(layoutPrincipal, clienteDao, mainApp)));

        contenedor.getChildren().addAll(titulo, busquedaBox, resultadoBox, btnVolver);
        return contenedor;
    }

    // =================================================================
    // 3.2 VISTA: ELIMINAR CLIENTE DE LA BD (DELETE)
    // =================================================================
    private static VBox crearVistaEliminarCliente(BorderPane layoutPrincipal, ClienteDAO clienteDao, MainGUI mainApp) {
        VBox contenedor = new VBox(20);
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setPadding(new Insets(20));

        Label titulo = new Label("Eliminar Cliente de la Base de Datos");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titulo.setStyle("-fx-text-fill: #C0392B;");

        HBox formularioBox = new HBox(10);
        formularioBox.setAlignment(Pos.CENTER);
        
        Label lblDni = new Label("DNI del cliente a dar de baja:");
        lblDni.setFont(Font.font("Segoe UI", 16));
        TextField txtDni = new TextField();
        txtDni.setPromptText("Ej: 12345678A");
        txtDni.setStyle("-fx-font-size: 14px; -fx-padding: 6;");
        
        Button btnEliminar = new Button("Eliminar Registro");
        btnEliminar.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 7 15; -fx-background-radius: 5; -fx-cursor: hand;");
        formularioBox.getChildren().addAll(lblDni, txtDni, btnEliminar);

        btnEliminar.setOnAction(e -> {
            String dni = txtDni.getText().trim();
            if (dni.isEmpty()) {
                mainApp.mostrarAlertaError("Campo vacío", "Por favor, introduce un DNI.");
                return;
            }
            
            Alert alertaConfirmar = new Alert(AlertType.CONFIRMATION);
            alertaConfirmar.setTitle("Confirmar Baja");
            alertaConfirmar.setHeaderText(null);
            alertaConfirmar.setContentText("¿Está completamente seguro de borrar este cliente?");
            
            alertaConfirmar.showAndWait().ifPresent(respuesta -> {
                if (respuesta == javafx.scene.control.ButtonType.OK) {
                    try {
                        clienteDao.eliminar(dni);
                        mainApp.mostrarAlerta("¡Éxito!", "Cliente borrado de la base de datos.");
                        txtDni.clear();
                    } catch (AgenciaException ex) {
                        mainApp.mostrarAlertaError("Fallo al eliminar", ex.getMessage());
                    }
                }
            });
        });

        Button btnVolver = mainApp.crearBotonVolver("Volver al Menú de Clientes");
        btnVolver.setOnAction(e -> layoutPrincipal.setCenter(crearMenuClientes(layoutPrincipal, clienteDao, mainApp)));

        contenedor.getChildren().addAll(titulo, formularioBox, btnVolver);
        return contenedor;
    }

    // =================================================================
    // 3.3 VISTA: MODIFICAR DATOS DE UN CLIENTE (UPDATE)
    // =================================================================
    private static VBox crearVistaModificarCliente(BorderPane layoutPrincipal, ClienteDAO clienteDao, MainGUI mainApp) {
        VBox contenedor = new VBox(15);
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setPadding(new Insets(20));

        Label titulo = new Label("Modificar Datos de Cliente");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titulo.setStyle("-fx-text-fill: #2C3E50;");

        HBox busquedaBox = new HBox(10);
        busquedaBox.setAlignment(Pos.CENTER);
        Label lblDniBuscar = new Label("DNI del cliente:");
        lblDniBuscar.setFont(Font.font("Segoe UI", 16));
        TextField txtDniBuscar = new TextField();
        txtDniBuscar.setPromptText("Ej: 12345678A");
        Button btnCargar = new Button("Cargar Datos");
        btnCargar.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        busquedaBox.getChildren().addAll(lblDniBuscar, txtDniBuscar, btnCargar);

        GridPane formulario = new GridPane();
        formulario.setAlignment(Pos.CENTER);
        formulario.setHgap(10); formulario.setVgap(10);
        formulario.setVisible(false); 

        TextField txtNombre = mainApp.crearCampoTexto(formulario, "Nuevo Nombre:", "", 0);
        TextField txtCorreo = mainApp.crearCampoTexto(formulario, "Nuevo Correo:", "", 1);
        TextField txtTelefono = mainApp.crearCampoTexto(formulario, "Nuevo Teléfono:", "", 2);
        TextField txtDir = mainApp.crearCampoTexto(formulario, "Nueva Dirección:", "", 3);
        TextField txtPasaporte = mainApp.crearCampoTexto(formulario, "Nuevo Pasaporte:", "", 4);

        Button btnGuardarCambios = new Button("Actualizar en MySQL");
        btnGuardarCambios.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;");
        
        HBox cajaBotonesForm = new HBox(btnGuardarCambios);
        cajaBotonesForm.setAlignment(Pos.CENTER);
        cajaBotonesForm.setVisible(false);

        btnCargar.setOnAction(e -> {
            String dni = txtDniBuscar.getText().trim();
            if (dni.isEmpty()) {
                mainApp.mostrarAlertaError("Campo vacío", "Introduce un DNI para buscar.");
                return;
            }
            try {
                ClienteDTO c = clienteDao.buscarPorId(dni);
                if (c != null) {
                    txtNombre.setText(c.getNombreCompleto());
                    txtCorreo.setText(c.getCorreo());
                    txtTelefono.setText(c.getTelefono());
                    txtDir.setText(c.getDireccion());
                    txtPasaporte.setText(c.getPasaporte() != null ? c.getPasaporte() : "");
                    
                    formulario.setVisible(true);
                    cajaBotonesForm.setVisible(true);
                    txtDniBuscar.setEditable(false); 
                } else {
                    formulario.setVisible(false);
                    cajaBotonesForm.setVisible(false);
                    mainApp.mostrarAlerta("Aviso", "El cliente no existe en la base de datos.");
                }
            } catch (AgenciaException ex) {
                mainApp.mostrarAlertaError("Error", ex.getMessage());
            }
        });

        btnGuardarCambios.setOnAction(e -> {
            try {
                String pasaporteVal = txtPasaporte.getText().trim().isEmpty() ? null : txtPasaporte.getText().trim();
                
                ClienteDTO cModificado = new ClienteDTO(
                    txtNombre.getText(), 
                    txtDniBuscar.getText().trim(), 
                    txtCorreo.getText(), 
                    txtTelefono.getText(), 
                    txtDir.getText(), 
                    pasaporteVal
                );

                cModificado.validar();
                clienteDao.modificar(cModificado);
                
                mainApp.mostrarAlerta("¡Éxito!", "¡Datos actualizados correctamente en MySQL!");
                layoutPrincipal.setCenter(crearMenuClientes(layoutPrincipal, clienteDao, mainApp)); 
                
            } catch (AgenciaException ex) {
                mainApp.mostrarAlertaError("Fallo al actualizar", ex.getMessage());
            } catch (Exception ex) {
                mainApp.mostrarAlertaError("Error de validación", "Revisa los campos.\n" + ex.getMessage());
            }
        });

        Button btnVolver = mainApp.crearBotonVolver("Cancelar y Volver");
        btnVolver.setOnAction(e -> layoutPrincipal.setCenter(crearMenuClientes(layoutPrincipal, clienteDao, mainApp)));

        contenedor.getChildren().addAll(titulo, busquedaBox, formulario, cajaBotonesForm, btnVolver);
        return contenedor;
    }

    // =================================================================
    // 3.4 VISTA: PROCEDIMIENTO ALMACENADO (CALLABLE STATEMENT)
    // =================================================================
    private static VBox crearVistaProcedimientoDescuento(BorderPane layoutPrincipal, ClienteDAO clienteDao, MainGUI mainApp) {
        VBox contenedor = new VBox(20);
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setPadding(new Insets(20));

        Label titulo = new Label("Aplicar Descuento Masivo VIP");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titulo.setStyle("-fx-text-fill: #2C3E50;");

        Label descripcion = new Label("Esta acción invocará un procedimiento almacenado en MySQL\npara aplicar un porcentaje de descuento fidelidad en la BD.");
        descripcion.setFont(Font.font("Segoe UI", 14));
        descripcion.setStyle("-fx-text-alignment: center; -fx-text-fill: #7F8C8D;");

        HBox formularioBox = new HBox(10);
        formularioBox.setAlignment(Pos.CENTER);
        
        Label lblPorcentaje = new Label("Porcentaje (1-100):");
        lblPorcentaje.setFont(Font.font("Segoe UI", 16));
        TextField txtPorcentaje = new TextField();
        txtPorcentaje.setPromptText("Ej: 15.5");
        txtPorcentaje.setPrefWidth(100);
        txtPorcentaje.setStyle("-fx-font-size: 14px; -fx-padding: 6;");
        
        Button btnEjecutar = new Button("Ejecutar en Servidor");
        btnEjecutar.setStyle("-fx-background-color: #9B59B6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 7 15; -fx-background-radius: 5; -fx-cursor: hand;");
        formularioBox.getChildren().addAll(lblPorcentaje, txtPorcentaje, btnEjecutar);

        btnEjecutar.setOnAction(e -> {
            try {
                double porcentaje = Double.parseDouble(txtPorcentaje.getText().trim());
                if (porcentaje <= 0 || porcentaje > 100) {
                    mainApp.mostrarAlertaError("Porcentaje no válido", "El porcentaje debe estar entre 1 y 100.");
                    return;
                }

                clienteDao.aplicarDescuentoVIPEnBD(porcentaje);
                mainApp.mostrarAlerta("¡Éxito Procedimiento!", "El descuento masivo ha sido ejecutado en MySQL.");
                txtPorcentaje.clear();
                
            } catch (NumberFormatException ex) {
                mainApp.mostrarAlertaError("Error de formato", "Debe introducir un número decimal válido.");
            } catch (AgenciaException ex) {
                mainApp.mostrarAlertaError("Fallo en Base de Datos", ex.getMessage());
            }
        });

        Button btnVolver = mainApp.crearBotonVolver("Volver al Menú de Clientes");
        btnVolver.setOnAction(e -> layoutPrincipal.setCenter(crearMenuClientes(layoutPrincipal, clienteDao, mainApp)));

        contenedor.getChildren().addAll(titulo, descripcion, formularioBox, btnVolver);
        return contenedor;
    }

    // =================================================================
    // 3.5 VISTA: CONSULTAR PASAPORTES (STREAMS Y MAPAS EN MEMORIA)
    // =================================================================
    private static VBox crearVistaClientesConPasaporte(BorderPane layoutPrincipal, ClienteDAO clienteDao, MainGUI mainApp) {
        VBox contenedor = new VBox(15);
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setPadding(new Insets(20));

        Label titulo = new Label("Detección de Clientes con Pasaporte");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titulo.setStyle("-fx-text-fill: #2C3E50;");

        TextArea areaTexto = new TextArea();
        areaTexto.setEditable(false);
        areaTexto.setFont(Font.font("Monospaced", 13));

        try {
            List<ClienteDTO> listaCompleta = clienteDao.listarTodos();

            if (listaCompleta.isEmpty()) {
                areaTexto.setText("No hay clientes en la base de datos.");
            } else {
                java.util.Map<String, ClienteDTO> mapaClientesHasheados = listaCompleta.stream()
                    .filter(c -> c.getPasaporte() != null)
                    .collect(java.util.stream.Collectors.toMap(
                        ClienteDTO::getDni, c -> c, (existente, nuevo) -> existente, java.util.HashMap::new
                    ));

                if (mapaClientesHasheados.isEmpty()) {
                    areaTexto.setText("No se ha detectado ningún cliente con pasaporte activo.");
                } else {
                    java.util.Map<String, ClienteDTO> mapaClientesOrdenado = new java.util.TreeMap<>();
                    for (ClienteDTO cliente : mapaClientesHasheados.values()) {
                        mapaClientesOrdenado.put(cliente.getNombreCompleto(), cliente);
                    }

                    StringBuilder sb = new StringBuilder();
                    sb.append("Listado estructurado en memoria (vía TreeMap por Nombre Completo):\n\n");
                    for (java.util.Map.Entry<String, ClienteDTO> entrada : mapaClientesOrdenado.entrySet()) {
                        sb.append(String.format("- %-30s | DNI: %-12s | Pasaporte: %s\n", 
                            entrada.getKey(), entrada.getValue().getDni(), entrada.getValue().getPasaporte()));
                    }
                    areaTexto.setText(sb.toString());
                }
            }
        } catch (AgenciaException e) {
            areaTexto.setText("[ERROR INTERNO] " + e.getMessage());
        }

        Button btnVolver = mainApp.crearBotonVolver("Volver al Menú de Clientes");
        btnVolver.setOnAction(e -> layoutPrincipal.setCenter(crearMenuClientes(layoutPrincipal, clienteDao, mainApp)));

        contenedor.getChildren().addAll(titulo, areaTexto, btnVolver);
        return contenedor;
    }

    // =================================================================
    // 4. VISTA: LISTAR TODOS LOS CLIENTES (SELECT ALL)
    // =================================================================
    private static VBox crearVistaListarClientes(BorderPane layoutPrincipal, ClienteDAO clienteDao, MainGUI mainApp) {
        VBox contenedor = new VBox(15);
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setPadding(new Insets(20));

        Label titulo = new Label("Catálogo Completo de Clientes");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titulo.setStyle("-fx-text-fill: #2C3E50;");

        TextArea areaTexto = new TextArea();
        areaTexto.setEditable(false);
        areaTexto.setFont(Font.font("Monospaced", 13)); 
        
        try {
            List<ClienteDTO> lista = clienteDao.listarTodos(); 
            if (lista.isEmpty()) {
                areaTexto.setText("La tabla 'cliente' está vacía en MySQL.");
            } else {
                lista.sort((c1, c2) -> c1.getNombreCompleto().compareToIgnoreCase(c2.getNombreCompleto()));
                
                StringBuilder sb = new StringBuilder();
                sb.append("Clientes totales encontrados: ").append(lista.size()).append("\n");
                sb.append("---------------------------------------------------------------------------------\n");
                
                for (ClienteDTO c : lista) {
                    sb.append(String.format("%-20s | %-10s | %-20s | %-9s | %-10s\n", 
                        c.getNombreCompleto(), c.getDni(), c.getCorreo(), c.getTelefono(), 
                        (c.getPasaporte() != null ? c.getPasaporte() : "N/A")));
                }
                areaTexto.setText(sb.toString());
            }
        } catch (AgenciaException e) {
            areaTexto.setText("[FALLO] No se pudo recuperar el listado: " + e.getMessage());
        }

        Button btnVolver = mainApp.crearBotonVolver("Volver");
        btnVolver.setOnAction(e -> layoutPrincipal.setCenter(crearMenuClientes(layoutPrincipal, clienteDao, mainApp)));

        contenedor.getChildren().addAll(titulo, areaTexto, btnVolver);
        return contenedor;
    }
}