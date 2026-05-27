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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;

import Agencia_DAO.ReservaDAO;
import Agencia_DAO.ClienteDAO;
import Agencia_DAO.EmpleadoDAO;
import Agencia_DAO.DestinoDAO;
import Agencia_DTO.ReservaDTO;
import Agencia_DTO.ClienteDTO;
import Agencia_DTO.EmpleadoDTO;
import Agencia_DTO.DestinoDTO;
import Agencia_Excepciones.AgenciaException;
import Agencia_Main.MainGUI;

public class ReservaVistaGUI {

    // Almacenes estáticos para los DAOs inyectados
    private static ReservaDAO reservaDao;
    private static ClienteDAO clienteDao;
    private static EmpleadoDAO empleadoDao;
    private static DestinoDAO destinoDao;

    // Formateador de fechas idéntico al de tu consola
    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // =================================================================
    // 1. VISTA: SUBMENÚ DE RESERVAS
    // =================================================================
    public static VBox crearMenuReservas(BorderPane layoutPrincipal, ReservaDAO resDao, ClienteDAO cliDao, EmpleadoDAO empDao, DestinoDAO destDao, MainGUI mainApp) {
        reservaDao = resDao;
        clienteDao = cliDao;
        empleadoDao = empDao;
        destinoDao = destDao;

        VBox menu = new VBox(12);
        menu.setAlignment(Pos.CENTER);

        Label titulo = new Label("Módulo Gestión de Reservas");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        titulo.setStyle("-fx-text-fill: #2C3E50; -fx-padding: 0 0 15 0;");

        Button btnInsertar = mainApp.crearBotonMenu("1. Registrar nueva reserva (Alta Completa)");
        Button btnListar = mainApp.crearBotonMenu("2. Ver histórico de todas las reservas");
        Button btnBuscar = mainApp.crearBotonMenu("3. Buscar reserva por ID");
        Button btnModificar = mainApp.crearBotonMenu("4. Modificar reserva (Estado/Viajeros)");
        Button btnEliminar = mainApp.crearBotonMenu("5. Eliminar / Cancelar contrato");
        Button btnVolver = mainApp.crearBotonVolver("6. Volver al menú principal");

        // Enrutamiento interactivo
        btnInsertar.setOnAction(e -> layoutPrincipal.setCenter(crearFormularioNuevaReserva(layoutPrincipal, mainApp)));
        btnListar.setOnAction(e -> layoutPrincipal.setCenter(crearVistaListarReservas(layoutPrincipal, mainApp)));
        btnBuscar.setOnAction(e -> layoutPrincipal.setCenter(crearVistaBuscarReserva(layoutPrincipal, mainApp)));
        btnModificar.setOnAction(e -> layoutPrincipal.setCenter(crearVistaModificarReserva(layoutPrincipal, mainApp)));
        btnEliminar.setOnAction(e -> layoutPrincipal.setCenter(crearVistaEliminarReserva(layoutPrincipal, mainApp)));
        btnVolver.setOnAction(e -> layoutPrincipal.setCenter(mainApp.crearMenuPrincipal()));

        menu.getChildren().addAll(titulo, btnInsertar, btnListar, btnBuscar, btnModificar, btnEliminar, btnVolver);
        return menu;
    }

    // =================================================================
    // 2. VISTA: ALTA COMPLETA (CON COMPROBACIÓN CRUZADA DE IDS)
    // =================================================================
    private static VBox crearFormularioNuevaReserva(BorderPane layoutPrincipal, MainGUI mainApp) {
        VBox contenedor = new VBox(20);
        contenedor.setAlignment(Pos.CENTER);
        
        Label titulo = new Label("Registrar Nueva Reserva");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        GridPane formulario = new GridPane();
        formulario.setAlignment(Pos.CENTER);
        formulario.setHgap(10); formulario.setVgap(10);

        TextField txtDni = mainApp.crearCampoTexto(formulario, "DNI Cliente:", "Ej: 12345678A", 0);
        TextField txtIdEmp = mainApp.crearCampoTexto(formulario, "ID Empleado Asesor:", "Ej: 101", 1);
        TextField txtCodDest = mainApp.crearCampoTexto(formulario, "Código Destino:", "Ej: PAR01", 2);
        TextField txtIdRes = mainApp.crearCampoTexto(formulario, "ID único Reserva:", "Ej: 5001", 3);
        TextField txtFSal = mainApp.crearCampoTexto(formulario, "Fecha Salida (dd/mm/aaaa):", "Ej: 15/08/2026", 4);
        TextField txtFReg = mainApp.crearCampoTexto(formulario, "Fecha Regreso (dd/mm/aaaa):", "Ej: 22/08/2026", 5);
        TextField txtViajeros = mainApp.crearCampoTexto(formulario, "Número de Viajeros:", "Ej: 2", 6);
        
        Button btnGuardar = new Button("Emitir Contrato");
        btnGuardar.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;");
        Button btnCancelar = mainApp.crearBotonVolver("Cancelar");

        HBox cajaBotones = new HBox(15, btnGuardar, btnCancelar);
        cajaBotones.setAlignment(Pos.CENTER);

        btnCancelar.setOnAction(e -> layoutPrincipal.setCenter(crearMenuReservas(layoutPrincipal, reservaDao, clienteDao, empleadoDao, destinoDao, mainApp)));
        btnGuardar.setOnAction(e -> {
            try {
                // 1. Validaciones de Integridad Relacional
                String dni = txtDni.getText().trim();
                ClienteDTO cli = clienteDao.buscarPorId(dni);
                if (cli == null) { mainApp.mostrarAlertaError("Validación", "El cliente con DNI " + dni + " no existe."); return; }

                int idEmp = Integer.parseInt(txtIdEmp.getText().trim());
                EmpleadoDTO emp = empleadoDao.buscarPorId(idEmp);
                if (emp == null) { mainApp.mostrarAlertaError("Validación", "El empleado con ID " + idEmp + " no existe."); return; }

                String cod = txtCodDest.getText().trim().toUpperCase();
                DestinoDTO dest = destinoDao.buscarPorId(cod);
                if (dest == null) { mainApp.mostrarAlertaError("Validación", "El destino " + cod + " no existe en el catálogo."); return; }

                // 2. Parseo de datos propios de la reserva
                int idR = Integer.parseInt(txtIdRes.getText().trim());
                LocalDate fSal = LocalDate.parse(txtFSal.getText().trim(), fmt);
                LocalDate fReg = LocalDate.parse(txtFReg.getText().trim(), fmt);
                int viajeros = Integer.parseInt(txtViajeros.getText().trim());

                // Lógica de Negocio automatizada
                double total = dest.getPrecioBase() * viajeros;
                
                ReservaDTO nueva = new ReservaDTO(idR, LocalDate.now(), fSal, fReg, viajeros, total, "Confirmada", cli, emp, dest);
                reservaDao.insertar(nueva);
                
                mainApp.mostrarAlerta("Reserva Registrada", "Contrato guardado con éxito.\nImporte Total: " + total + "€");
                layoutPrincipal.setCenter(crearMenuReservas(layoutPrincipal, reservaDao, clienteDao, empleadoDao, destinoDao, mainApp));
                
            } catch (NumberFormatException ex) {
                mainApp.mostrarAlertaError("Error de Formato", "Los campos ID, Viajeros deben ser valores numéricos enteros.");
            } catch (java.time.format.DateTimeParseException ex) {
                mainApp.mostrarAlertaError("Error de Fecha", "Asegúrate de escribir las fechas exactamente en formato dd/mm/aaaa.");
            } catch (AgenciaException ex) {
                mainApp.mostrarAlertaError("Error BD", ex.getMessage());
            } catch (Exception ex) {
                mainApp.mostrarAlertaError("Error", ex.getMessage());
            }
        });

        contenedor.getChildren().addAll(titulo, formulario, cajaBotones);
        return contenedor;
    }

    // =================================================================
    // 3. VISTA: HISTÓRICO UTILIZANDO ITERATOR
    // =================================================================
    private static VBox crearVistaListarReservas(BorderPane layoutPrincipal, MainGUI mainApp) {
        VBox contenedor = new VBox(15);
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setPadding(new Insets(20));

        Label titulo = new Label("Histórico General de Reservas");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        TextArea areaTexto = new TextArea();
        areaTexto.setEditable(false);
        areaTexto.setFont(Font.font("Monospaced", 13));

        try {
            List<ReservaDTO> lista = reservaDao.listarTodos();
            if (lista.isEmpty()) {
                areaTexto.setText("No constan registros en la base de datos.");
            } else {
                StringBuilder sb = new StringBuilder();
                Iterator<ReservaDTO> it = lista.iterator();
                while (it.hasNext()) {
                    ReservaDTO r = it.next();
                    sb.append("• ID: ").append(r.getIdReserva())
                      .append(" | Cliente: ").append(r.getCliente().getNombreCompleto())
                      .append(" | Destino: ").append(r.getDestino().getNombreDestino())
                      .append(" | Total: ").append(r.getImporteTotal()).append("€ [")
                      .append(r.getEstado()).append("]\n");
                }
                areaTexto.setText(sb.toString());
            }
        } catch (AgenciaException e) {
            areaTexto.setText("[ERROR BD] " + e.getMessage());
        }

        Button btnVolver = mainApp.crearBotonVolver("Volver");
        btnVolver.setOnAction(e -> layoutPrincipal.setCenter(crearMenuReservas(layoutPrincipal, reservaDao, clienteDao, empleadoDao, destinoDao, mainApp)));

        contenedor.getChildren().addAll(titulo, areaTexto, btnVolver);
        return contenedor;
    }

    // =================================================================
    // 4. VISTA: BUSCAR RESERVA
    // =================================================================
    private static VBox crearVistaBuscarReserva(BorderPane layoutPrincipal, MainGUI mainApp) {
        VBox contenedor = new VBox(20);
        contenedor.setAlignment(Pos.CENTER);

        Label titulo = new Label("Consulta de Contratos de Reservas");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        HBox cajaBusqueda = new HBox(10);
        cajaBusqueda.setAlignment(Pos.CENTER);
        Label lblId = new Label("ID Reserva:");
        TextField txtIdB = new TextField();
        Button btnBuscar = new Button("Buscar");
        btnBuscar.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-weight: bold;");
        cajaBusqueda.getChildren().addAll(lblId, txtIdB, btnBuscar);

        VBox resultadoBox = new VBox(8);
        resultadoBox.setAlignment(Pos.CENTER_LEFT);
        resultadoBox.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #BDC3C7; -fx-padding: 15; -fx-border-radius: 5;");
        resultadoBox.setMaxWidth(480);
        resultadoBox.setVisible(false);

        Label lblL1 = new Label(); Label lblL2 = new Label(); Label lblL3 = new Label();
        resultadoBox.getChildren().addAll(lblL1, lblL2, lblL3);

        btnBuscar.setOnAction(e -> {
            try {
                int idB = Integer.parseInt(txtIdB.getText().trim());
                ReservaDTO rB = reservaDao.buscarPorId(idB);
                if (rB != null) {
                    lblL1.setText("-> Reserva Nº: " + rB.getIdReserva() + " | Estado: " + rB.getEstado());
                    lblL2.setText("   Pasajero Titular: " + rB.getCliente().getNombreCompleto());
                    lblL3.setText("   Destino Contratado: " + rB.getDestino().getNombreDestino() + " (" + rB.getDestino().getPais() + ")");
                    resultadoBox.setVisible(true);
                } else {
                    resultadoBox.setVisible(false);
                    mainApp.mostrarAlerta("Aviso", "Reserva no encontrada.");
                }
            } catch (NumberFormatException ex) {
                mainApp.mostrarAlertaError("Formato", "El ID debe ser numérico entero.");
            } catch (AgenciaException ex) {
                mainApp.mostrarAlertaError("Error BD", ex.getMessage());
            }
        });

        Button btnVolver = mainApp.crearBotonVolver("Volver");
        btnVolver.setOnAction(e -> layoutPrincipal.setCenter(crearMenuReservas(layoutPrincipal, reservaDao, clienteDao, empleadoDao, destinoDao, mainApp)));

        contenedor.getChildren().addAll(titulo, cajaBusqueda, resultadoBox, btnVolver);
        return contenedor;
    }

    // =================================================================
    // 5. VISTA: MODIFICAR CON RECALCULO AUTOMÁTICO DE IMPORTE
    // =================================================================
    private static VBox crearVistaModificarReserva(BorderPane layoutPrincipal, MainGUI mainApp) {
        VBox contenedor = new VBox(15);
        contenedor.setAlignment(Pos.CENTER);

        Label titulo = new Label("Modificar Estado / Datos de Reserva");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        HBox busquedaBox = new HBox(10);
        busquedaBox.setAlignment(Pos.CENTER);
        Label lblId = new Label("ID de la Reserva:");
        TextField txtIdM = new TextField();
        Button btnCargar = new Button("Cargar Contrato");
        btnCargar.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");
        busquedaBox.getChildren().addAll(lblId, txtIdM, btnCargar);

        GridPane formulario = new GridPane();
        formulario.setAlignment(Pos.CENTER);
        formulario.setHgap(10); formulario.setVgap(10);
        formulario.setVisible(false);

        TextField txtEstado = mainApp.crearCampoTexto(formulario, "Nuevo Estado:", "", 0);
        TextField txtViajeros = mainApp.crearCampoTexto(formulario, "Modificar Número Viajeros:", "", 1);

        Button btnGuardar = new Button("Actualizar Contrato");
        btnGuardar.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        HBox cajaBotonForm = new HBox(btnGuardar);
        cajaBotonForm.setAlignment(Pos.CENTER);
        cajaBotonForm.setVisible(false);

        // Guardamos el objeto cargado temporalmente para no perder sus datos originales
        final ReservaDTO[] rMContainer = new ReservaDTO[1];

        btnCargar.setOnAction(e -> {
            try {
                int idM = Integer.parseInt(txtIdM.getText().trim());
                rMContainer[0] = reservaDao.buscarPorId(idM);
                if (rMContainer[0] != null) {
                    txtEstado.setText(rMContainer[0].getEstado());
                    txtViajeros.setText(String.valueOf(rMContainer[0].getNumViajeros()));
                    
                    formulario.setVisible(true);
                    cajaBotonForm.setVisible(true);
                    txtIdM.setEditable(false);
                } else {
                    formulario.setVisible(false);
                    cajaBotonForm.setVisible(false);
                    mainApp.mostrarAlertaError("Error", "No existe esa reserva.");
                }
            } catch (Exception ex) { mainApp.mostrarAlertaError("Error", "ID inválido."); }
        });

        btnGuardar.setOnAction(e -> {
            try {
                int idOriginal = Integer.parseInt(txtIdM.getText().trim());
                String nEst = txtEstado.getText().trim();
                int nViajeros = Integer.parseInt(txtViajeros.getText().trim());

                // Recálculo automático del total basado en el precio base del destino original
                double nTotal = rMContainer[0].getDestino().getPrecioBase() * nViajeros;

                ReservaDTO resModificada = new ReservaDTO(idOriginal, rMContainer[0].getFechaReserva(), rMContainer[0].getFechaSalida(), rMContainer[0].getFechaRegreso(), nViajeros, nTotal, nEst, rMContainer[0].getCliente(), rMContainer[0].getEmpleado(), rMContainer[0].getDestino());
                reservaDao.modificar(resModificada);
                
                mainApp.mostrarAlerta("¡Éxito!", "Contrato modificado en MySQL. Nuevo total: " + nTotal + "€");
                layoutPrincipal.setCenter(crearMenuReservas(layoutPrincipal, reservaDao, clienteDao, empleadoDao, destinoDao, mainApp));
            } catch (Exception ex) {
                mainApp.mostrarAlertaError("Fallo", ex.getMessage());
            }
        });

        Button btnVolver = mainApp.crearBotonVolver("Cancelar");
        btnVolver.setOnAction(e -> layoutPrincipal.setCenter(crearMenuReservas(layoutPrincipal, reservaDao, clienteDao, empleadoDao, destinoDao, mainApp)));

        contenedor.getChildren().addAll(titulo, busquedaBox, formulario, cajaBotonForm, btnVolver);
        return contenedor;
    }

    // =================================================================
    // 6. VISTA: ELIMINAR / CANCELAR CONTRATO
    // =================================================================
    private static VBox crearVistaEliminarReserva(BorderPane layoutPrincipal, MainGUI mainApp) {
        VBox contenedor = new VBox(20);
        contenedor.setAlignment(Pos.CENTER);

        Label titulo = new Label("Eliminar / Cancelar Registro de Reserva");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titulo.setStyle("-fx-text-fill: #C0392B;");

        HBox cajaForm = new HBox(10);
        cajaForm.setAlignment(Pos.CENTER);
        Label lblId = new Label("ID Reserva:");
        TextField txtIdE = new TextField();
        Button btnEliminar = new Button("Eliminar permanentemente");
        btnEliminar.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-weight: bold;");
        cajaForm.getChildren().addAll(lblId, txtIdE, btnEliminar);

        btnEliminar.setOnAction(e -> {
            try {
                int idE = Integer.parseInt(txtIdE.getText().trim());
                
                Alert conf = new Alert(AlertType.CONFIRMATION);
                conf.setTitle("Confirmación de Borrado");
                conf.setHeaderText("¿Seguro que deseas eliminar la reserva físicamente de la BD?");
                conf.setContentText("Esta acción es irreversible y purgará los datos del contrato de MySQL.");
                
                conf.showAndWait().ifPresent(res -> {
                    if (res == javafx.scene.control.ButtonType.OK) {
                        try {
                            reservaDao.eliminar(idE);
                            mainApp.mostrarAlerta("Purgado", "Registro eliminado de MySQL.");
                            txtIdE.clear();
                        } catch (AgenciaException ex) { mainApp.mostrarAlertaError("Error BD", ex.getMessage()); }
                    }
                });
            } catch (NumberFormatException ex) { mainApp.mostrarAlertaError("Error", "Introduce un ID numérico."); }
        });

        Button btnVolver = mainApp.crearBotonVolver("Volver");
        btnVolver.setOnAction(e -> layoutPrincipal.setCenter(crearMenuReservas(layoutPrincipal, reservaDao, clienteDao, empleadoDao, destinoDao, mainApp)));

        contenedor.getChildren().addAll(titulo, cajaForm, btnVolver);
        return contenedor;
    }
}