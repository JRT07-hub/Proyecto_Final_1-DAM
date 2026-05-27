package Agencia_Main;

import javafx.application.Application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

// CONEXIÓN CON TUS CLASES DE BASE DE DATOS
import Agencia_DAO.ClienteDAO;
import Agencia_DAO.EmpleadoDAO;
import Agencia_DAO.CategoriaDAO;
import Agencia_DAO.DestinoDAO;
import Agencia_DAO.ReservaDAO;
import Agencia_VistaGUI.DestinoVistaGUI;
import Agencia_VistaGUI.ClienteVistaGUI;
import Agencia_VistaGUI.EmpleadoVistaGUI;
import Agencia_VistaGUI.CategoriaVistaGUI;
import Agencia_VistaGUI.ReservaVistaGUI;

public class MainGUI extends Application {

    // VARIABLES GLOBALES DE LA CLASE (Aquí se soluciona el error de tu captura)
    private BorderPane layoutPrincipal;
    private ClienteDAO clienteDao = new ClienteDAO();
    private EmpleadoDAO empleadoDao = new EmpleadoDAO();
    private CategoriaDAO categoriaDao = new CategoriaDAO();
    private DestinoDAO destinoDao = new DestinoDAO();
    private ReservaDAO reservaDao = new ReservaDAO();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage ventana) {
        ventana.setTitle("Sistema de Gestión Interactivo");

        layoutPrincipal = new BorderPane();
        layoutPrincipal.setStyle("-fx-background-color: #F8F9FA;");

        // Iniciamos mostrando el menú principal
        layoutPrincipal.setCenter(crearMenuPrincipal());

        Scene escena = new Scene(layoutPrincipal, 750, 550);
        ventana.setScene(escena);
        ventana.show();
    }

    // =================================================================
    // 1. VISTA: MENÚ PRINCIPAL
    // =================================================================
    public VBox crearMenuPrincipal() {
        VBox menu = new VBox(15);
        menu.setAlignment(Pos.CENTER);
        menu.setPadding(new Insets(20));

        Label titulo = new Label("SISTEMA DE GESTIÓN");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        titulo.setStyle("-fx-text-fill: #2C3E50; -fx-padding: 0 0 20 0;");

        Button btnClientes = crearBotonMenu("1. Gestión de Clientes");
        Button btnEmpleados = crearBotonMenu("2. Gestión de Empleados");
        Button btnCategorias = crearBotonMenu("3. Gestión de Categorías");
        Button btnDestinos = crearBotonMenu("4. Gestión de Destinos");
        Button btnReservas = crearBotonMenu("5. Gestión de Reservas");
        Button btnSalir = crearBotonMenu("6. Apagar Aplicación");
        
        btnSalir.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5; -fx-cursor: hand;");

        // ACCIONES DE LOS BOTONES PRINCIPALES
        btnClientes.setOnAction(e -> layoutPrincipal.setCenter(
            ClienteVistaGUI.crearMenuClientes(layoutPrincipal, clienteDao, this)
        ));
        btnEmpleados.setOnAction(e -> layoutPrincipal.setCenter(
                EmpleadoVistaGUI.crearMenuEmpleados(layoutPrincipal, empleadoDao, this)
        ));
        btnCategorias.setOnAction(e -> layoutPrincipal.setCenter(
            CategoriaVistaGUI.crearMenuCategorias(layoutPrincipal, categoriaDao, this)
        ));
        btnDestinos.setOnAction(e -> layoutPrincipal.setCenter(
        	    DestinoVistaGUI.crearMenuDestinos(layoutPrincipal, destinoDao, categoriaDao, this)
        ));
        btnReservas.setOnAction(e -> layoutPrincipal.setCenter(
        	    ReservaVistaGUI.crearMenuReservas(layoutPrincipal, reservaDao, clienteDao, empleadoDao, destinoDao, this)
        	));
        
        btnSalir.setOnAction(e -> System.exit(0));

        menu.getChildren().addAll(titulo, btnClientes, btnEmpleados, btnCategorias, btnDestinos, btnReservas, btnSalir);
        return menu;
    }
    
    // =================================================================
    // MÉTODOS AUXILIARES (Estilos y herramientas visuales)
    // =================================================================
    public Button crearBotonMenu(String texto) {
        Button boton = new Button(texto);
        boton.setPrefWidth(280); 
        String estiloNormal = "-fx-background-color: #34495E; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;";
        String estiloHover = "-fx-background-color: #2C3E50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;";
        
        boton.setStyle(estiloNormal);
        boton.setOnMouseEntered(e -> boton.setStyle(estiloHover));
        boton.setOnMouseExited(e -> boton.setStyle(estiloNormal));
        return boton;
    }

    public Button crearBotonVolver(String texto) {
        Button boton = new Button(texto);
        String estiloNormal = "-fx-background-color: transparent; -fx-text-fill: #7F8C8D; -fx-font-size: 14px; -fx-border-color: #7F8C8D; -fx-border-radius: 5; -fx-padding: 8 20; -fx-cursor: hand;";
        String estiloHover = "-fx-background-color: #7F8C8D; -fx-text-fill: white; -fx-font-size: 14px; -fx-border-color: #7F8C8D; -fx-border-radius: 5; -fx-padding: 8 20; -fx-cursor: hand;";
        
        boton.setStyle(estiloNormal);
        boton.setOnMouseEntered(e -> boton.setStyle(estiloHover));
        boton.setOnMouseExited(e -> boton.setStyle(estiloNormal));
        return boton;
    }

    public TextField crearCampoTexto(GridPane form, String etiqueta, String prompt, int fila) {
        Label lbl = new Label(etiqueta);
        lbl.setFont(Font.font("Segoe UI", 15));
        TextField txt = new TextField();
        txt.setPromptText(prompt);
        txt.setStyle("-fx-font-size: 14px; -fx-padding: 6; -fx-background-radius: 5; -fx-border-color: #BDC3C7;");
        txt.setPrefWidth(250);
        form.add(lbl, 0, fila);
        form.add(txt, 1, fila);
        return txt;
    }

    public void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    public void mostrarAlertaError(String titulo, String mensaje) {
        Alert alerta = new Alert(AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}