package Agencia_Vista;

import Agencia_DAO.CategoriaDAO;
import Agencia_DTO.CategoriaDTO;
import Agencia_Excepciones.AgenciaException;

import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class CategoriaVista {
    private static CategoriaDAO categoriaDao = new CategoriaDAO();
    private static Scanner teclado = new Scanner(System.in);

    public static void ejecutar() {
        int op = 0;
        do {
            System.out.println("\n=============================================");
            System.out.println("          MENÚ GESTIÓN DE CATEGORÍAS            ");
            System.out.println("=============================================");
            System.out.println("1. Registrar nueva categoría (Alta)");
            System.out.println("2. Ver lista de categorías");
            System.out.println("3. Buscar categoría por ID");
            System.out.println("4. Modificar nombre de categoría");
            System.out.println("5. Eliminar categoría (Baja)");
            System.out.println("6. Volver al menú principal");
            System.out.println("=============================================");
            System.out.print("Seleccione opción: ");
            try {
                op = Integer.parseInt(teclado.nextLine());
                switch (op) {
                    case 1:
                        System.out.println("\n--- ALTA DE CATEGORÍA ---");
                        System.out.print("ID Categoría (Numérico): "); int id = Integer.parseInt(teclado.nextLine());
                        System.out.print("Nombre de la Categoría (Ej: Aventura, Relax): "); String nom = teclado.nextLine();
                        
                        CategoriaDTO nueva = new CategoriaDTO(id, nom);
                        categoriaDao.insertar(nueva);
                        System.out.println("[ÉXITO] Categoría guardada en MySQL.");
                        break;

                    case 2:
                        System.out.println("\n--- LISTADO DE CATEGORÍAS ---");
                        List<CategoriaDTO> lista = categoriaDao.listarTodos();
                        if(lista.isEmpty()) System.out.println("No hay categorías en la base de datos.");
                        else { 
                        	Iterator<CategoriaDTO> it = lista.iterator();
                        	while (it.hasNext()) {
                            CategoriaDTO c = it.next();
                            System.out.println("• ID: " + c.getIdCategoria() + " -> " + c.getNombreCat());}}
                        break;

                    case 3:
                        System.out.println("\n--- BUSCAR CATEGORÍA ---");
                        System.out.print("Introduce ID: "); int idB = Integer.parseInt(teclado.nextLine());
                        CategoriaDTO catB = categoriaDao.buscarPorId(idB);
                        if(catB != null) {
                            System.out.println("-> Categoría Encontrada: " + catB.getNombreCat());
                        } else System.out.println("[AVISO] Categoría no encontrada.");
                        break;

                    case 4:
                        System.out.println("\n--- MODIFICAR CATEGORÍA ---");
                        System.out.print("Introduce el ID de la categoría a cambiar: "); int idM = Integer.parseInt(teclado.nextLine());
                        CategoriaDTO catM = categoriaDao.buscarPorId(idM);
                        if(catM == null) { System.out.println("[ERROR] No existe la categoría."); break; }
                        
                        System.out.print("Nuevo Nombre (Actual: " + catM.getNombreCat() + "): "); String nNom = teclado.nextLine();
                        
                        CategoriaDTO modificada = new CategoriaDTO(idM, nNom);
                        categoriaDao.modificar(modificada);
                        System.out.println("[ÉXITO] Categoría actualizada.");
                        break;

                    case 5:
                        System.out.println("\n--- ELIMINAR CATEGORÍA ---");
                        System.out.print("Introduce ID de la categoría a borrar: "); int idE = Integer.parseInt(teclado.nextLine());
                        System.out.print("⚠️ ¿Seguro que deseas eliminarla? Si hay destinos asociados fallará por integridad (SI/NO): ");
                        if(teclado.nextLine().equalsIgnoreCase("SI")) {
                            categoriaDao.eliminar(idE);
                            System.out.println("[ÉXITO] Registro eliminado de la BD.");
                        }
                        break;
                }
            } catch (AgenciaException e) {
                System.out.println("[ERROR BD] " + e.getMessage());
            } catch (Exception e) {
                System.out.println("[ERROR] Formato incorrecto o dato inválido: " + e.getMessage());
            }
        } while (op != 6);
    }
}