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

import Agencia_DAO.CategoriaDAO;
import Agencia_DTO.CategoriaDTO;
import Agencia_Excepciones.AgenciaException;
import Agencia_Main.MainGUI;

public class CategoriaVistaGUI {

    private static CategoriaDAO categoriaDao;

    // =================================================================
    // 1. VISTA: SUBMENÚ DE CATEGORÍAS
    // =================================================================
    public static VBox crearMenuCategorias(BorderPane layoutPrincipal, CategoriaDAO catDaoRecibido, MainGUI mainApp) {
        categoriaDao = catDaoRecibido;

        VBox menu = new VBox(12);
        menu.setAlignment(Pos.CENTER);

        Label titulo = new Label("Módulo Gestión de Categorías");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        titulo.setStyle("-fx-text-fill: #2C3E50; -fx-padding: 0 0 15 0;");

        Button btnInsertar = mainApp.crearBotonMenu("1. Registrar nueva categoría (Alta)");
        Button btnListar = mainApp.crearBotonMenu("2. Ver lista de categorías");
        Button btnBuscar = mainApp.crearBotonMenu("3. Buscar categoría por ID");
        Button btnModificar = mainApp.crearBotonMenu("4. Modificar nombre de categoría");
        Button btnEliminar = mainApp.crearBotonMenu("5. Eliminar categoría (Baja)");
        Button btnVolver = mainApp.crearBotonVolver("6. Volver al menú principal");

        // Ruteo de pantallas en el BorderPane principal
        btnInsertar.setOnAction(e -> layoutPrincipal.setCenter(crearFormularioNuevaCategoria(layoutPrincipal, mainApp)));
        btnListar.setOnAction(e -> layoutPrincipal.setCenter(crearVistaListarCategorias(layoutPrincipal, mainApp)));
        btnBuscar.setOnAction(e -> layoutPrincipal.setCenter(crearVistaBuscarCategoria(layoutPrincipal, mainApp)));
        btnModificar.setOnAction(e -> layoutPrincipal.setCenter(crearVistaModificarCategoria(layoutPrincipal, mainApp)));
        btnEliminar.setOnAction(e -> layoutPrincipal.setCenter(crearVistaEliminarCategoria(layoutPrincipal, mainApp)));
        btnVolver.setOnAction(e -> layoutPrincipal.setCenter(mainApp.crearMenuPrincipal()));

        menu.getChildren().addAll(titulo, btnInsertar, btnListar, btnBuscar, btnModificar, btnEliminar, btnVolver);
        return menu;
    }

    // =================================================================
    // 2. VISTA: REGISTRAR / ALTA DE CATEGORÍA
    // =================================================================
    private static VBox crearFormularioNuevaCategoria(BorderPane layoutPrincipal, MainGUI mainApp) {
        VBox contenedor = new VBox(20);
        contenedor.setAlignment(Pos.CENTER);
        
        Label titulo = new Label("Alta de Nueva Categoría");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        GridPane formulario = new GridPane();
        formulario.setAlignment(Pos.CENTER);
        formulario.setHgap(10); formulario.setVgap(10);

        TextField txtId = mainApp.crearCampoTexto(formulario, "ID Categoría (Numérico):", "Ej: 5", 0);
        TextField txtNom = mainApp.crearCampoTexto(formulario, "Nombre de la Categoría:", "Ej: Aventura, Relax, Cultural", 1);
        
        Button btnGuardar = new Button("Guardar en MySQL");
        btnGuardar.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;");
        Button btnCancelar = mainApp.crearBotonVolver("Cancelar");

        HBox cajaBotones = new HBox(15, btnGuardar, btnCancelar);
        cajaBotones.setAlignment(Pos.CENTER);

        btnCancelar.setOnAction(e -> layoutPrincipal.setCenter(crearMenuCategorias(layoutPrincipal, categoriaDao, mainApp)));
        btnGuardar.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtId.getText().trim());
                String nom = txtNom.getText().trim();

                if (nom.isEmpty()) {
                    mainApp.mostrarAlertaError("Validación", "El nombre de la categoría no puede estar vacío.");
                    return;
                }

                CategoriaDTO nueva = new CategoriaDTO(id, nom);
                categoriaDao.insertar(nueva);
                
                mainApp.mostrarAlerta("¡Éxito!", "Categoría guardada en MySQL correctamente.");
                layoutPrincipal.setCenter(crearMenuCategorias(layoutPrincipal, categoriaDao, mainApp));
                
            } catch (NumberFormatException ex) {
                mainApp.mostrarAlertaError("Error de Formato", "El ID de la categoría debe ser un número entero.");
            } catch (AgenciaException ex) {
                mainApp.mostrarAlertaError("Error BD", "Rechazado por MySQL: " + ex.getMessage());
            } catch (Exception ex) {
                mainApp.mostrarAlertaError("Error", ex.getMessage());
            }
        });

        contenedor.getChildren().addAll(titulo, formulario, cajaBotones);
        return contenedor;
    }

    // =================================================================
    // 3. VISTA: LISTADO GENERAL UTILIZANDO ITERATOR
    // =================================================================
    private static VBox crearVistaListarCategorias(BorderPane layoutPrincipal, MainGUI mainApp) {
        VBox contenedor = new VBox(15);
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setPadding(new Insets(20));

        Label titulo = new Label("Listado Completo de Categorías");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        TextArea areaTexto = new TextArea();
        areaTexto.setEditable(false);
        areaTexto.setFont(Font.font("Monospaced", 13));

        try {
            List<CategoriaDTO> lista = categoriaDao.listarTodos();
            if (lista.isEmpty()) {
                areaTexto.setText("No hay categorías en la base de datos.");
            } else {
                StringBuilder sb = new StringBuilder();
                Iterator<CategoriaDTO> it = lista.iterator();
                while (it.hasNext()) {
                    CategoriaDTO c = it.next();
                    sb.append("• ID: ").append(c.getIdCategoria()).append(" -> ").append(c.getNombreCat()).append("\n");
                }
                areaTexto.setText(sb.toString());
            }
        } catch (AgenciaException e) {
            areaTexto.setText("[ERROR BD] " + e.getMessage());
        }

        Button btnVolver = mainApp.crearBotonVolver("Volver");
        btnVolver.setOnAction(e -> layoutPrincipal.setCenter(crearMenuCategorias(layoutPrincipal, categoriaDao, mainApp)));

        contenedor.getChildren().addAll(titulo, areaTexto, btnVolver);
        return contenedor;
    }

    // =================================================================
    // 4. VISTA: BUSCAR CATEGORÍA POR ID
    // =================================================================
    private static VBox crearVistaBuscarCategoria(BorderPane layoutPrincipal, MainGUI mainApp) {
        VBox contenedor = new VBox(20);
        contenedor.setAlignment(Pos.CENTER);

        Label titulo = new Label("Buscar Categoría por ID");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        HBox cajaBusqueda = new HBox(10);
        cajaBusqueda.setAlignment(Pos.CENTER);
        Label lblId = new Label("Introduce ID:");
        TextField txtIdB = new TextField();
        Button btnBuscar = new Button("Buscar");
        btnBuscar.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-weight: bold;");
        cajaBusqueda.getChildren().addAll(lblId, txtIdB, btnBuscar);

        VBox resultadoBox = new VBox(8);
        resultadoBox.setAlignment(Pos.CENTER);
        resultadoBox.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #BDC3C7; -fx-padding: 15; -fx-border-radius: 5;");
        resultadoBox.setMaxWidth(400);
        resultadoBox.setVisible(false);

        Label lblResultado = new Label();
        lblResultado.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        resultadoBox.getChildren().add(lblResultado);

        btnBuscar.setOnAction(e -> {
            try {
                int idB = Integer.parseInt(txtIdB.getText().trim());
                CategoriaDTO catB = categoriaDao.buscarPorId(idB);
                if (catB != null) {
                    lblResultado.setText("-> Categoría Encontrada: " + catB.getNombreCat());
                    resultadoBox.setVisible(true);
                } else {
                    resultadoBox.setVisible(false);
                    mainApp.mostrarAlerta("Aviso", "Categoría no encontrada.");
                }
            } catch (NumberFormatException ex) {
                mainApp.mostrarAlertaError("Formato", "El ID debe ser un número entero.");
            } catch (AgenciaException ex) {
                mainApp.mostrarAlertaError("Error BD", ex.getMessage());
            }
        });

        Button btnVolver = mainApp.crearBotonVolver("Volver");
        btnVolver.setOnAction(e -> layoutPrincipal.setCenter(crearMenuCategorias(layoutPrincipal, categoriaDao, mainApp)));

        contenedor.getChildren().addAll(titulo, cajaBusqueda, resultadoBox, btnVolver);
        return contenedor;
    }

    // =================================================================
    // 5. VISTA: MODIFICAR NOMBRE DE CATEGORÍA
    // =================================================================
    private static VBox crearVistaModificarCategoria(BorderPane layoutPrincipal, MainGUI mainApp) {
        VBox contenedor = new VBox(15);
        contenedor.setAlignment(Pos.CENTER);

        Label titulo = new Label("Modificar Categoría");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        HBox busquedaBox = new HBox(10);
        busquedaBox.setAlignment(Pos.CENTER);
        Label lblId = new Label("ID de la categoría:");
        TextField txtIdM = new TextField();
        Button btnCargar = new Button("Cargar Ficha");
        btnCargar.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");
        busquedaBox.getChildren().addAll(lblId, txtIdM, btnCargar);

        GridPane formulario = new GridPane();
        formulario.setAlignment(Pos.CENTER);
        formulario.setHgap(10); formulario.setVgap(10);
        formulario.setVisible(false);

        TextField txtNom = mainApp.crearCampoTexto(formulario, "Nuevo Nombre:", "", 0);

        Button btnGuardar = new Button("Actualizar Nombre");
        btnGuardar.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        HBox cajaBotonForm = new HBox(btnGuardar);
        cajaBotonForm.setAlignment(Pos.CENTER);
        cajaBotonForm.setVisible(false);

        btnCargar.setOnAction(e -> {
            try {
                int idM = Integer.parseInt(txtIdM.getText().trim());
                CategoriaDTO catM = categoriaDao.buscarPorId(idM);
                if (catM != null) {
                    txtNom.setText(catM.getNombreCat());
                    formulario.setVisible(true);
                    cajaBotonForm.setVisible(true);
                    txtIdM.setEditable(false);
                } else {
                    formulario.setVisible(false);
                    cajaBotonForm.setVisible(false);
                    mainApp.mostrarAlertaError("Error", "No existe la categoría solicitada.");
                }
            } catch (Exception ex) { mainApp.mostrarAlertaError("Error", "Introduce un ID numérico válido."); }
        });

        btnGuardar.setOnAction(e -> {
            try {
                int idOriginal = Integer.parseInt(txtIdM.getText().trim());
                String nNom = txtNom.getText().trim();

                if (nNom.isEmpty()) {
                    mainApp.mostrarAlertaError("Error", "El nombre no puede dejarse en blanco.");
                    return;
                }

                CategoriaDTO modificada = new CategoriaDTO(idOriginal, nNom);
                categoriaDao.modificar(modificada);
                
                mainApp.mostrarAlerta("¡Éxito!", "Categoría actualizada correctamente.");
                layoutPrincipal.setCenter(crearMenuCategorias(layoutPrincipal, categoriaDao, mainApp));
            } catch (Exception ex) {
                mainApp.mostrarAlertaError("Fallo", ex.getMessage());
            }
        });

        Button btnVolver = mainApp.crearBotonVolver("Cancelar");
        btnVolver.setOnAction(e -> layoutPrincipal.setCenter(crearMenuCategorias(layoutPrincipal, categoriaDao, mainApp)));

        contenedor.getChildren().addAll(titulo, busquedaBox, formulario, cajaBotonForm, btnVolver);
        return contenedor;
    }

    // =================================================================
    // 6. VISTA: ELIMINAR / BAJA (CON ALERTA DE CLAVE FORÁNEA)
    // =================================================================
    private static VBox crearVistaEliminarCategoria(BorderPane layoutPrincipal, MainGUI mainApp) {
        VBox contenedor = new VBox(20);
        contenedor.setAlignment(Pos.CENTER);

        Label titulo = new Label("Eliminar Categoría del Sistema");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titulo.setStyle("-fx-text-fill: #C0392B;");

        HBox cajaForm = new HBox(10);
        cajaForm.setAlignment(Pos.CENTER);
        Label lblId = new Label("ID a borrar:");
        TextField txtIdE = new TextField();
        Button btnEliminar = new Button("Borrar de la BD");
        btnEliminar.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-weight: bold;");
        cajaForm.getChildren().addAll(lblId, txtIdE, btnEliminar);

        btnEliminar.setOnAction(e -> {
            try {
                int idE = Integer.parseInt(txtIdE.getText().trim());
                
                Alert conf = new Alert(AlertType.CONFIRMATION);
                conf.setTitle("Confirmar Baja");
                conf.setHeaderText("⚠ Advertencia de Integridad Referencial");
                conf.setContentText("Si hay destinos asignados a esta categoría, MySQL denegará la operación automáticamente.\n\n¿Deseas continuar?");
                
                conf.showAndWait().ifPresent(res -> {
                    if (res == javafx.scene.control.ButtonType.OK) {
                        try {
                            categoriaDao.eliminar(idE);
                            mainApp.mostrarAlerta("Éxito", "Registro eliminado de la BD de manera definitiva.");
                            txtIdE.clear();
                        } catch (AgenciaException ex) { 
                            mainApp.mostrarAlertaError("Error de Restricción FK", "Operación rechazada por MySQL: " + ex.getMessage()); 
                        }
                    }
                });
            } catch (NumberFormatException ex) { 
                mainApp.mostrarAlertaError("Error", "Introduce un ID numérico entero."); 
            }
        });

        Button btnVolver = mainApp.crearBotonVolver("Volver");
        btnVolver.setOnAction(e -> layoutPrincipal.setCenter(crearMenuCategorias(layoutPrincipal, categoriaDao, mainApp)));

        contenedor.getChildren().addAll(titulo, cajaForm, btnVolver);
        return contenedor;
    }
}