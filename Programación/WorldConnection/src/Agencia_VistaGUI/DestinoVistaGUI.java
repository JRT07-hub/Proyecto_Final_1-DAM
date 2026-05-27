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

import Agencia_DAO.DestinoDAO;	
import Agencia_DAO.CategoriaDAO;
import Agencia_DTO.DestinoDTO;
import Agencia_DTO.CategoriaDTO;
import Agencia_Excepciones.AgenciaException;
import Agencia_Main.MainGUI;

public class DestinoVistaGUI {

    // Almacenes estáticos para que todos los métodos compartan los DAOs
    private static DestinoDAO destinoDao;
    private static CategoriaDAO categoriaDao;

    // =================================================================
    // 1. VISTA: SUBMENÚ DE DESTINOS
    // =================================================================
    public static VBox crearMenuDestinos(BorderPane layoutPrincipal, DestinoDAO destDaoRecibido, CategoriaDAO catDaoRecibido, MainGUI mainApp) {
        // Guardamos las referencias para que estén accesibles globalmente en esta clase
        destinoDao = destDaoRecibido;
        categoriaDao = catDaoRecibido;

        VBox menu = new VBox(12);
        menu.setAlignment(Pos.CENTER);

        Label titulo = new Label("Módulo Gestión de Destinos");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        titulo.setStyle("-fx-text-fill: #2C3E50; -fx-padding: 0 0 15 0;");

        Button btnInsertar = mainApp.crearBotonMenu("1. Registrar nuevo destino (Alta)");
        Button btnListar = mainApp.crearBotonMenu("2. Ver catálogo de destinos");
        Button btnBuscar = mainApp.crearBotonMenu("3. Buscar destino por Código (ID)");
        Button btnModificar = mainApp.crearBotonMenu("4. Modificar datos de un destino");
        Button btnEliminar = mainApp.crearBotonMenu("5. Eliminar destino del catálogo");
        Button btnVolver = mainApp.crearBotonVolver("6. Volver al menú principal");

        // Enrutamiento gráfico interactivo
        btnInsertar.setOnAction(e -> layoutPrincipal.setCenter(crearFormularioNuevoDestino(layoutPrincipal, mainApp)));
        btnListar.setOnAction(e -> layoutPrincipal.setCenter(crearVistaListarDestinos(layoutPrincipal, mainApp)));
        btnBuscar.setOnAction(e -> layoutPrincipal.setCenter(crearVistaBuscarDestino(layoutPrincipal, mainApp)));
        btnModificar.setOnAction(e -> layoutPrincipal.setCenter(crearVistaModificarDestino(layoutPrincipal, mainApp)));
        btnEliminar.setOnAction(e -> layoutPrincipal.setCenter(crearVistaEliminarDestino(layoutPrincipal, mainApp)));
        btnVolver.setOnAction(e -> layoutPrincipal.setCenter(mainApp.crearMenuPrincipal()));

        menu.getChildren().addAll(titulo, btnInsertar, btnListar, btnBuscar, btnModificar, btnEliminar, btnVolver);
        return menu;
    }

    // =================================================================
    // 2. VISTA: ALTA DE DESTINO (INSERT CON VALIDACIÓN FK)
    // =================================================================
    private static VBox crearFormularioNuevoDestino(BorderPane layoutPrincipal, MainGUI mainApp) {
        VBox contenedor = new VBox(20);
        contenedor.setAlignment(Pos.CENTER);
        
        Label titulo = new Label("Registrar Nuevo Destino");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        GridPane formulario = new GridPane();
        formulario.setAlignment(Pos.CENTER);
        formulario.setHgap(10); formulario.setVgap(10);

        TextField txtCod = mainApp.crearCampoTexto(formulario, "Código Destino (Max 5 letras):", "Ej: PAR01", 0);
        TextField txtNom = mainApp.crearCampoTexto(formulario, "Nombre Comercial:", "Ej: Fin de año en París", 1);
        TextField txtPais = mainApp.crearCampoTexto(formulario, "País:", "Ej: Francia", 2);
        TextField txtCiu = mainApp.crearCampoTexto(formulario, "Ciudad:", "Ej: París", 3);
        TextField txtDesc = mainApp.crearCampoTexto(formulario, "Descripción:", "Breve resumen del viaje", 4);
        TextField txtPrecio = mainApp.crearCampoTexto(formulario, "Precio Base (€):", "Ej: 599.90", 5);
        TextField txtDias = mainApp.crearCampoTexto(formulario, "Duración en Días:", "Ej: 5", 6);
        TextField txtDisp = mainApp.crearCampoTexto(formulario, "¿Disponible? (true/false):", "true", 7);
        TextField txtIdCat = mainApp.crearCampoTexto(formulario, "ID de la Categoría:", "Ej: 1", 8);
        
        Button btnGuardar = new Button("Indexar en MySQL");
        btnGuardar.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;");
        Button btnCancelar = mainApp.crearBotonVolver("Cancelar");

        HBox cajaBotones = new HBox(15, btnGuardar, btnCancelar);
        cajaBotones.setAlignment(Pos.CENTER);

        btnCancelar.setOnAction(e -> layoutPrincipal.setCenter(crearMenuDestinos(layoutPrincipal, destinoDao, categoriaDao, mainApp)));
        btnGuardar.setOnAction(e -> {
            try {
                String cod = txtCod.getText().trim().toUpperCase();
                String nom = txtNom.getText().trim();
                String pais = txtPais.getText().trim();
                String ciudad = txtCiu.getText().trim();
                String desc = txtDesc.getText().trim();
                double precio = Double.parseDouble(txtPrecio.getText().trim());
                int dias = Integer.parseInt(txtDias.getText().trim());
                boolean disp = Boolean.parseBoolean(txtDisp.getText().trim().toLowerCase());
                int idCat = Integer.parseInt(txtIdCat.getText().trim());

                // VALIDACIÓN DE INTEGRIDAD REFERENCIAL DIRECTA
                CategoriaDTO catAsignada = categoriaDao.buscarPorId(idCat);
                if (catAsignada == null) {
                    mainApp.mostrarAlertaError("Aborto de Operación", "La categoría Nº " + idCat + " no existe en MySQL. ¡Créala primero!");
                    return;
                }

                DestinoDTO nuevoDest = new DestinoDTO(cod, nom, pais, ciudad, desc, precio, dias, disp, catAsignada);
                destinoDao.insertar(nuevoDest);
                
                mainApp.mostrarAlerta("¡Éxito!", "Destino indexado y guardado correctamente.");
                layoutPrincipal.setCenter(crearMenuDestinos(layoutPrincipal, destinoDao, categoriaDao, mainApp));
                
            } catch (NumberFormatException ex) {
                mainApp.mostrarAlertaError("Error de Formato", "Revisa los campos numéricos (Precio, Días e ID Categoría).");
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
    // 3. VISTA: CATÁLOGO GENERAL UTILIZANDO ITERATOR
    // =================================================================
    private static VBox crearVistaListarDestinos(BorderPane layoutPrincipal, MainGUI mainApp) {
        VBox contenedor = new VBox(15);
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setPadding(new Insets(20));

        Label titulo = new Label("Catálogo General de Destinos");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        TextArea areaTexto = new TextArea();
        areaTexto.setEditable(false);
        areaTexto.setFont(Font.font("Monospaced", 13));

        try {
            List<DestinoDTO> lista = destinoDao.listarTodos();
            if (lista.isEmpty()) {
                areaTexto.setText("El catálogo está vacío.");
            } else {
                StringBuilder sb = new StringBuilder();
                Iterator<DestinoDTO> it = lista.iterator();
                while (it.hasNext()) {
                    DestinoDTO d = it.next();
                    sb.append("• [").append(d.getCodDestino()).append("] ")
                      .append(d.getNombreDestino()).append(" (").append(d.getCiudad()).append(", ").append(d.getPais()).append(")\n")
                      .append("  Precio Base: ").append(d.getPrecioBase()).append("€ | Categoría: ")
                      .append(d.getCategoria().getNombreCat()).append("\n\n");
                }
                areaTexto.setText(sb.toString());
            }
        } catch (AgenciaException e) {
            areaTexto.setText("[ERROR BD] " + e.getMessage());
        }

        Button btnVolver = mainApp.crearBotonVolver("Volver");
        btnVolver.setOnAction(e -> layoutPrincipal.setCenter(crearMenuDestinos(layoutPrincipal, destinoDao, categoriaDao, mainApp)));

        contenedor.getChildren().addAll(titulo, areaTexto, btnVolver);
        return contenedor;
    }

    // =================================================================
    // 4. VISTA: BUSCAR DESTINO POR CÓDIGO
    // =================================================================
    private static VBox crearVistaBuscarDestino(BorderPane layoutPrincipal, MainGUI mainApp) {
        VBox contenedor = new VBox(20);
        contenedor.setAlignment(Pos.CENTER);

        Label titulo = new Label("Buscar Destino por Código");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        HBox cajaBusqueda = new HBox(10);
        cajaBusqueda.setAlignment(Pos.CENTER);
        Label lblCod = new Label("Introduce Código:");
        TextField txtCodB = new TextField();
        txtCodB.setPromptText("Ej: PAR01");
        Button btnBuscar = new Button("Buscar");
        btnBuscar.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-weight: bold;");
        cajaBusqueda.getChildren().addAll(lblCod, txtCodB, btnBuscar);

        VBox resultadoBox = new VBox(8);
        resultadoBox.setAlignment(Pos.CENTER_LEFT);
        resultadoBox.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #BDC3C7; -fx-padding: 15; -fx-border-radius: 5;");
        resultadoBox.setMaxWidth(500);
        resultadoBox.setVisible(false);

        Label lblLinea1 = new Label();
        Label lblLinea2 = new Label();
        Label lblLinea3 = new Label();
        Label lblLinea4 = new Label();
        resultadoBox.getChildren().addAll(lblLinea1, lblLinea2, lblLinea3, lblLinea4);

        btnBuscar.setOnAction(e -> {
            try {
                String codB = txtCodB.getText().trim().toUpperCase();
                DestinoDTO destB = destinoDao.buscarPorId(codB);
                if (destB != null) {
                    lblLinea1.setText("-> [" + destB.getCodDestino() + "] " + destB.getNombreDestino());
                    lblLinea2.setText("   Descripción: " + destB.getDescripcion()); 
                    lblLinea3.setText("   Precio/Días: " + destB.getPrecioBase() + "€ por " + destB.getDuracion() + " días.");
                    lblLinea4.setText("   Tipo Categoría: " + destB.getCategoria().getNombreCat());
                    resultadoBox.setVisible(true);
                } else {
                    resultadoBox.setVisible(false);
                    mainApp.mostrarAlerta("Aviso", "Destino no encontrado en el catálogo.");
                }
            } catch (AgenciaException ex) {
                mainApp.mostrarAlertaError("Error BD", ex.getMessage());
            }
        });

        Button btnVolver = mainApp.crearBotonVolver("Volver");
        btnVolver.setOnAction(e -> layoutPrincipal.setCenter(crearMenuDestinos(layoutPrincipal, destinoDao, categoriaDao, mainApp)));

        contenedor.getChildren().addAll(titulo, cajaBusqueda, resultadoBox, btnVolver);
        return contenedor;
    }

    // =================================================================
    // 5. VISTA: MODIFICAR DESTINO (CON COMPROBACIÓN DE NUEVA FK)
    // =================================================================
    private static VBox crearVistaModificarDestino(BorderPane layoutPrincipal, MainGUI mainApp) {
        VBox contenedor = new VBox(15);
        contenedor.setAlignment(Pos.CENTER);

        Label titulo = new Label("Modificar Ficha de Destino");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        HBox busquedaBox = new HBox(10);
        busquedaBox.setAlignment(Pos.CENTER);
        Label lblCod = new Label("Código del Viaje:");
        TextField txtCodM = new TextField();
        Button btnCargar = new Button("Cargar Datos");
        btnCargar.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");
        busquedaBox.getChildren().addAll(lblCod, txtCodM, btnCargar);

        GridPane formulario = new GridPane();
        formulario.setAlignment(Pos.CENTER);
        formulario.setHgap(10); formulario.setVgap(10);
        formulario.setVisible(false);

        TextField txtNom = mainApp.crearCampoTexto(formulario, "Nuevo Nombre:", "", 0);
        TextField txtPais = mainApp.crearCampoTexto(formulario, "Nuevo País:", "", 1);
        TextField txtCiu = mainApp.crearCampoTexto(formulario, "Nueva Ciudad:", "", 2);
        TextField txtDesc = mainApp.crearCampoTexto(formulario, "Nueva Descripción:", "", 3);
        TextField txtPrecio = mainApp.crearCampoTexto(formulario, "Nuevo Precio:", "", 4);
        TextField txtDias = mainApp.crearCampoTexto(formulario, "Nueva Duración:", "", 5);
        TextField txtDisp = mainApp.crearCampoTexto(formulario, "¿Disponible? (true/false):", "", 6);
        TextField txtIdCat = mainApp.crearCampoTexto(formulario, "ID Categoría:", "", 7);

        Button btnGuardar = new Button("Actualizar en MySQL");
        btnGuardar.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        HBox cajaBotonForm = new HBox(btnGuardar);
        cajaBotonForm.setAlignment(Pos.CENTER);
        cajaBotonForm.setVisible(false);

        btnCargar.setOnAction(e -> {
            try {
                String codM = txtCodM.getText().trim().toUpperCase();
                DestinoDTO destM = destinoDao.buscarPorId(codM);
                if (destM != null) {
                    txtNom.setText(destM.getNombreDestino());
                    txtPais.setText(destM.getPais());
                    txtCiu.setText(destM.getCiudad());
                    txtDesc.setText(destM.getDescripcion());
                    txtPrecio.setText(String.valueOf(destM.getPrecioBase()));
                    txtDias.setText(String.valueOf(destM.getDuracion()));
                    txtDisp.setText(String.valueOf(destM.isDisponibilidad()));
                    txtIdCat.setText(String.valueOf(destM.getCategoria().getIdCategoria()));
                    
                    formulario.setVisible(true);
                    cajaBotonForm.setVisible(true);
                    txtCodM.setEditable(false);
                } else {
                    formulario.setVisible(false);
                    cajaBotonForm.setVisible(false);
                    mainApp.mostrarAlertaError("Error", "El destino solicitado no existe.");
                }
            } catch (Exception ex) { mainApp.mostrarAlertaError("Error", "Verifica el código introducido."); }
        });

        btnGuardar.setOnAction(e -> {
            try {
                String codigoOriginal = txtCodM.getText().trim().toUpperCase();
                String nNom = txtNom.getText().trim();
                String nPais = txtPais.getText().trim();
                String nCiu = txtCiu.getText().trim();
                String nDesc = txtDesc.getText().trim();
                double nPrecio = Double.parseDouble(txtPrecio.getText().trim());
                int nDias = Integer.parseInt(txtDias.getText().trim());
                boolean nDisp = Boolean.parseBoolean(txtDisp.getText().trim().toLowerCase());
                int nIdCat = Integer.parseInt(txtIdCat.getText().trim());

                // Validación de la nueva FK
                CategoriaDTO nCat = categoriaDao.buscarPorId(nIdCat);
                if (nCat == null) {
                    mainApp.mostrarAlertaError("Error", "La nueva categoría no existe.");
                    return;
                }

                DestinoDTO modificado = new DestinoDTO(codigoOriginal, nNom, nPais, nCiu, nDesc, nPrecio, nDias, nDisp, nCat);
                destinoDao.modificar(modificado);
                
                mainApp.mostrarAlerta("¡Éxito!", "Ficha del destino actualizada en MySQL.");
                layoutPrincipal.setCenter(crearMenuDestinos(layoutPrincipal, destinoDao, categoriaDao, mainApp));
            } catch (Exception ex) {
                mainApp.mostrarAlertaError("Fallo", "Revisa los formatos: " + ex.getMessage());
            }
        });

        Button btnVolver = mainApp.crearBotonVolver("Cancelar");
        btnVolver.setOnAction(e -> layoutPrincipal.setCenter(crearMenuDestinos(layoutPrincipal, destinoDao, categoriaDao, mainApp)));

        contenedor.getChildren().addAll(titulo, busquedaBox, formulario, cajaBotonForm, btnVolver);
        return contenedor;
    }

    // =================================================================
    // 6. VISTA: ELIMINAR REGISTRO CON ALERTA DE RESTRICCIÓN FK
    // =================================================================
    private static VBox crearVistaEliminarDestino(BorderPane layoutPrincipal, MainGUI mainApp) {
        VBox contenedor = new VBox(20);
        contenedor.setAlignment(Pos.CENTER);

        Label titulo = new Label("Eliminar Destino del Catálogo");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titulo.setStyle("-fx-text-fill: #C0392B;");

        HBox cajaForm = new HBox(10);
        cajaForm.setAlignment(Pos.CENTER);
        Label lblCod = new Label("Código a borrar:");
        TextField txtCodE = new TextField();
        Button btnEliminar = new Button("Purgar Catálogo");
        btnEliminar.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-weight: bold;");
        cajaForm.getChildren().addAll(lblCod, txtCodE, btnEliminar);

        btnEliminar.setOnAction(e -> {
            String codE = txtCodE.getText().trim().toUpperCase();
            if (codE.isEmpty()) {
                mainApp.mostrarAlertaError("Error", "Introduce un código válido.");
                return;
            }
            
            Alert conf = new Alert(AlertType.CONFIRMATION);
            conf.setTitle("Confirmar Eliminación");
            conf.setHeaderText("⚠ Advertencia de Integridad");
            conf.setContentText("Si hay reservas asociadas a este viaje, el sistema denegará el borrado.\n\n¿Deseas proceder con la purga del registro?");
            
            conf.showAndWait().ifPresent(res -> {
                if (res == javafx.scene.control.ButtonType.OK) {
                    try {
                        destinoDao.eliminar(codE);
                        mainApp.mostrarAlerta("Éxito", "Registro purgado del catálogo.");
                        txtCodE.clear();
                    } catch (AgenciaException ex) { 
                        mainApp.mostrarAlertaError("Error BD / Restricción", "Operación rechazada por MySQL: " + ex.getMessage()); 
                    }
                }
            });
        });

        Button btnVolver = mainApp.crearBotonVolver("Volver");
        btnVolver.setOnAction(e -> layoutPrincipal.setCenter(crearMenuDestinos(layoutPrincipal, destinoDao, categoriaDao, mainApp)));

        contenedor.getChildren().addAll(titulo, cajaForm, btnVolver);
        return contenedor;
    }
}