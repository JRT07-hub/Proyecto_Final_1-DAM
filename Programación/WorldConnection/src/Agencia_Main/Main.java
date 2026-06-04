package Agencia_Main;

import java.util.Scanner;
import Agencia_Vista.*;
/**
 * Punto de entrada principal (Bootstrapper) del Sistema de Gestión de la Agencia de Viajes.
 * <p>Esta clase inicializa el flujo de la aplicación mediante el método {@code main} y orquesta 
 * el despacho síncrono del control hacia los diferentes submenús de la capa de presentación 
 * ({@link ClienteVista}, {@link EmpleadoVista}, {@link CategoriaVista}, {@link DestinoVista} y {@link ReservaVista}).</p>
 * @author Javier Rincón Toro
 * @version 1.0
 */
public class Main {
	/**
     * Hilo principal de ejecución (Main Thread) de la aplicación.
     * <p>Implementa un bucle de control continuo (do-while) soportado por una estructura condicional 
     * {@code switch} para evaluar las peticiones del usuario por consola. Cuenta con mecanismos de 
     * control de excepciones locales para evitar la caída abrupta del programa ante fallos de 
     * parseo numérico en la consola.</p>
     *
     * @param args Argumentos de la línea de comandos pasados al iniciar la máquina virtual de Java (no utilizados).
     */
    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);
        int opcion = 0;

        do {
            System.out.println("\n=============================================");
            System.out.println("         SISTEMA DE GESTIÓN INTERACTIVO        ");
            System.out.println("=============================================");
            System.out.println("1. Entrar a la Vista de Clientes");
            System.out.println("2. Entrar a la Vista de Empleados");
            System.out.println("3. Entrar a la Vista de Categorías");
            System.out.println("4. Entrar a la Vista de Destinos");
            System.out.println("5. Entrar a la Vista de Reservas");
            System.out.println("6. Apagar Aplicación");
            System.out.println("=============================================");
            System.out.print("Seleccione un módulo de vista: ");

            try {
                opcion = Integer.parseInt(teclado.nextLine());
                switch (opcion) {
                    case 1: ClienteVista.ejecutar(); break;
                    case 2: EmpleadoVista.ejecutar(); break;
                    case 3: CategoriaVista.ejecutar(); break;
                    case 4: DestinoVista.ejecutar(); break;
                    case 5: ReservaVista.ejecutar(); break;
                    case 6: System.out.println("Cerrando software... Conexiones MySQL cerradas."); break;
                    default: System.out.println("Opción errónea.");
                }
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Introduce un número del 1 al 6.");
            }
        } while (opcion != 6);
        
        teclado.close();
    }
}