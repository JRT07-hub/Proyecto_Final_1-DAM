
import java.util.Scanner;

import Agencia_Vista.*;

public class Main {
    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);
        int opcion = 0;

        do {
            System.out.println("\n=============================================");
            System.out.println("     SISTEMA DE GESTIÓN INTERACTIVO (DAO)    ");
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