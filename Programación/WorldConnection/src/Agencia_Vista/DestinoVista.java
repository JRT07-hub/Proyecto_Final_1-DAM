package Agencia_Vista;

import Agencia_DAO.DestinoDAO;
import Agencia_DAO.CategoriaDAO;
import Agencia_DTO.DestinoDTO;
import Agencia_DTO.CategoriaDTO;
import Agencia_Excepciones.AgenciaException;
import java.util.List;
import java.util.Scanner;

public class DestinoVista {
    private static DestinoDAO destinoDao = new DestinoDAO();
    private static CategoriaDAO categoriaDao = new CategoriaDAO(); // Auxiliar para validar integridad
    private static Scanner teclado = new Scanner(System.in);

    public static void ejecutar() {
        int op = 0;
        do {
            System.out.println("\n=============================================");
            System.out.println("           MENÚ GESTIÓN DE DESTINOS             ");
            System.out.println("=============================================");
            System.out.println("1. Registrar nuevo destino (Alta Catálogo)");
            System.out.println("2. Ver catálogo completo de destinos");
            System.out.println("3. Buscar destino por Código (ID)");
            System.out.println("4. Modificar datos de un destino");
            System.out.println("5. Eliminar destino del catálogo");
            System.out.println("6. Volver al menú principal");
            System.out.println("=============================================");
            System.out.print("Seleccione opción: ");
            try {
                op = Integer.parseInt(teclado.nextLine());
                switch (op) {
                    case 1:
                        System.out.println("\n--- REGISTRAR NUEVO DESTINO ---");
                        System.out.print("Código Destino (Max 5 caracteres, Ej: PAR01): "); String cod = teclado.nextLine().toUpperCase();
                        System.out.print("Nombre Comercial: "); String nom = teclado.nextLine();
                        System.out.print("País: "); String pais = teclado.nextLine();
                        System.out.print("Ciudad: "); String ciudad = teclado.nextLine();
                        System.out.print("Descripción del Viaje: "); String desc = teclado.nextLine();
                        System.out.print("Precio Base (€): "); double precio = Double.parseDouble(teclado.nextLine());
                        System.out.print("Duración en Días: "); int dias = Integer.parseInt(teclado.nextLine());
                        System.out.print("¿Está Disponible actualmente? (true/false): "); boolean disp = Boolean.parseBoolean(teclado.nextLine());
                        
                        // VALIDACIÓN DE CLAVE FORÁNEA INTERACTIVA
                        System.out.print("ID de la Categoría que le corresponde: "); int idCat = Integer.parseInt(teclado.nextLine());
                        CategoriaDTO catAsignada = categoriaDao.buscarPorId(idCat);
                        if(catAsignada == null) {
                            System.out.println("[ABORTO] La categoría Nº " + idCat + " no existe en MySQL. ¡Créala primero!");
                            break;
                        }

                        DestinoDTO nuevoDest = new DestinoDTO(cod, nom, pais, ciudad, desc, precio, dias, disp, catAsignada);
                        destinoDao.insertar(nuevoDest);
                        System.out.println("[ÉXITO] Destino indexado y guardado correctamente.");
                        break;

                    case 2:
                        System.out.println("\n--- CATÁLOGO GENERAL DE DESTINOS ---");
                        List<DestinoDTO> lista = destinoDao.listarTodos();
                        if(lista.isEmpty()) System.out.println("El catálogo está vacío.");
                        else {
                            for(DestinoDTO d : lista) {
                                System.out.println("• [" + d.getCodDestino() + "] " + d.getNombreDestino() + " (" + d.getCiudad() + ", " + d.getPais() + ") | Precio Base: " + d.getPrecioBase() + "€ | Categoria: " + d.getCategoria().getNombreCat());
                            }
                        }
                        break;

                    case 3:
                        System.out.println("\n--- BUSCAR DESTINO ---");
                        System.out.print("Introduce Código (Ej: PAR01): "); String codB = teclado.nextLine().toUpperCase();
                        DestinoDTO destB = destinoDao.buscarPorId(codB);
                        if(destB != null) {
                            System.out.println("-> [" + destB.getCodDestino() + "] " + destB.getNombreDestino());
                            System.out.println("   Descripción: " + destB.getDescripcion());
                            System.out.println("   Precio/Días: " + destB.getPrecioBase() + "€ por " + destB.getDuracion() + " días.");
                            System.out.println("   Tipo Categoría: " + destB.getCategoria().getNombreCat());
                        } else System.out.println("[AVISO] Destino no encontrado en el catálogo.");
                        break;

                    case 4:
                        System.out.println("\n--- MODIFICAR DESTINO ---");
                        System.out.print("Introduce el Código del destino a modificar: "); String codM = teclado.nextLine().toUpperCase();
                        DestinoDTO destM = destinoDao.buscarPorId(codM);
                        if(destM == null) { System.out.println("[ERROR] El destino solicitado no existe."); break; }
                        
                        System.out.print("Nuevo Nombre (" + destM.getNombreDestino() + "): "); String nNom = teclado.nextLine();
                        System.out.print("Nuevo País (" + destM.getPais() + "): "); String nPais = teclado.nextLine();
                        System.out.print("Nueva Ciudad (" + destM.getCiudad() + "): "); String nCiu = teclado.nextLine();
                        System.out.print("Nueva Descripción: "); String nDesc = teclado.nextLine();
                        System.out.print("Nuevo Precio Base (" + destM.getPrecioBase() + "): "); double nPrecio = Double.parseDouble(teclado.nextLine());
                        System.out.print("Nueva Duración Días (" + destM.getDuracion() + "): "); int nDias = Integer.parseInt(teclado.nextLine());
                        System.out.print("¿Disponible? (" + destM.isDisponibilidad() + " - true/false): "); boolean nDisp = Boolean.parseBoolean(teclado.nextLine());
                        
                        System.out.print("ID Categoría (Actual: " + destM.getCategoria().getIdCategoria() + "): "); int nIdCat = Integer.parseInt(teclado.nextLine());
                        CategoriaDTO nCat = categoriaDao.buscarPorId(nIdCat);
                        if(nCat == null) { System.out.println("[ABORTO] La nueva categoría no existe."); break; }

                        DestinoDTO modificado = new DestinoDTO(codM, nNom, nPais, nCiu, nDesc, nPrecio, nDias, nDisp, nCat);
                        destinoDao.modificar(modificado);
                        System.out.println("[ÉXITO] Ficha del destino actualizada en MySQL.");
                        break;

                    case 5:
                        System.out.println("\n--- ELIMINAR DESTINO ---");
                        System.out.print("Introduce el Código del destino a borrar: "); String codE = teclado.nextLine().toUpperCase();
                        System.out.print("⚠ Si hay reservas asociadas a este viaje el sistema denegará el borrado. ¿Proceder? (SI/NO): ");
                        if(teclado.nextLine().equalsIgnoreCase("SI")) {
                            destinoDao.eliminar(codE);
                            System.out.println("[ÉXITO] Registro purgado del catálogo.");
                        }
                        break;
                }
            } catch (AgenciaException e) {
                System.out.println("[ERROR BD] Operación rechazada por MySQL: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("[ERROR] Entrada inválida o fuera de rango: " + e.getMessage());
            }
        } while (op != 6);
    }
}