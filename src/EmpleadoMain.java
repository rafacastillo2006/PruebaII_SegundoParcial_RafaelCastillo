import java.util.Scanner;

public class EmpleadoMain {

    public static void main(String[] args) {
        EmpleadoManager manager = new EmpleadoManager();
        Scanner sc = new Scanner(System.in);
        int opcion = 0;

        do {
            System.out.println("\n\nMENU\n========");
            System.out.println("1. Agregar empleado");
            System.out.println("2. Listar empleados (No Despedidos)");
            System.out.println("3. Agregar Venta");
            System.out.println("4. Pagar a Empleado");
            System.out.println("5. Despedir Empleado");
            System.out.println("6. Imprimir Ficha Empleado");
            System.out.println("7. Salir");
            System.out.print("Escoja una opción: ");

            try {
                opcion = sc.nextInt();
                sc.nextLine();

                switch (opcion) {
                    case 1:
                        System.out.print("Nombre del empleado: ");
                        String nombre = sc.nextLine();
                        System.out.print("Salario base: ");
                        double salario = sc.nextDouble();
                        manager.addEmployee(nombre, salario);
                        System.out.println("Empleado agregado correctamente.");
                        break;

                    case 2:
                        manager.employeeList();
                        break;

                    case 3:
                        System.out.print("Código del empleado: ");
                        int codVenta = sc.nextInt();
                        System.out.print("Monto de la venta: ");
                        double monto = sc.nextDouble();
                        manager.addSaletoEmployee1(codVenta, monto);
                        break;

                    case 4:
                        System.out.print("Código del empleado a pagar: ");
                        int codPago = sc.nextInt();
                        manager.payEmployee(codPago);
                        break;

                    case 5:
                        System.out.print("Código del empleado a despedir: ");
                        int codDespedir = sc.nextInt();
                        if (manager.fireEmployee(codDespedir)) {
                            System.out.println("Empleado despedido correctamente.");
                        } else {
                            System.out.println("No se pudo despedir al empleado.");
                        }
                        break;

                    case 6:
                        System.out.print("Código del empleado a consultar: ");
                        int codImprimir = sc.nextInt();
                        manager.printEmployee(codImprimir);
                        break;

                    case 7:
                        System.out.println("Saliendo del sistema.");
                        break;

                    default:
                        System.out.println("Opción no válida.");
                }
            } catch (Exception e) {
                System.out.println("Error en la entrada de datos: " + e.getMessage());
                sc.nextLine();
            }

        } while (opcion != 7);

        sc.close();
    }
}