
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.Date;

public class EmpleadoManager {

    private RandomAccessFile rcods, remps;

    public EmpleadoManager() {
        try {
            File root = new File("company");
            root.mkdir();
            rcods = new RandomAccessFile("company/codigos.emp", "rw");
            remps = new RandomAccessFile("company/empleados.emp", "rw");
            initCode();
        } catch (IOException e) {

        }

    }

    private void initCode() throws IOException {
        if (rcods.length() == 0) {
            rcods.writeInt(1);
        }
    }

    private int getCode() throws IOException {
        rcods.seek(0);
        int code = rcods.readInt();
        rcods.seek(0);
        rcods.writeInt(code + 1);
        return code;
    }

    /*
    Int code
    String name
    double salary
    long hDate
    long fDate
    */
    public void addEmployee(String name, double salary) throws IOException {
        remps.seek(remps.length());
        int code = getCode();
        remps.writeInt(code);
        remps.writeUTF(name);
        remps.writeDouble(salary);
        remps.writeLong(Calendar.getInstance().getTimeInMillis());
        remps.writeLong(0);
        //archivos individuales

    }

    private String employeeFolder(int code) {
        return "company/empleado" + code;
    }

    private RandomAccessFile salesFileFor(int code) throws IOException {
        String dirPadre = employeeFolder(code);
        File dir = new File(dirPadre);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        int yearActual = Calendar.getInstance().get(Calendar.YEAR);
        String path = dirPadre + "/ventas" + yearActual + ".emp";
        return new RandomAccessFile(path, "rw");
    }

    /*
    Formato ventasYear.emp
    double ventaMes
    boolean pago
    */

    private void createYearSalesFileFor(int code) throws IOException {
        RandomAccessFile ryear = salesFileFor(code);

        if (ryear.length() == 0) {
            for (int mes = 0; mes < 12; mes++) {
                ryear.writeDouble(0);
                ryear.writeBoolean(false);
            }
        }
    }

    private void createEmployeeFolder(int code) throws IOException {
        File dir = new File(employeeFolder(code));
        dir.mkdir();
        createYearSalesFileFor(code);
    }

    /*
    Codigo - Nombre - Salario - Contratación
    */
    public void employeeList() {
        try {
            remps.seek(0);
            System.out.println("Lista de Empleados");

            while (remps.getFilePointer() < remps.length()) {
                int code = remps.readInt();
                String name = remps.readUTF();
                double salary = remps.readDouble();
                Date hDate = new Date(remps.readLong());
                if (remps.readLong() == 0)
                    System.out.println(code + " - " + name + " - Lps. " + salary + " - Contratación: " + hDate);

            }
        } catch (IOException e) {
            System.out.println("Error al recorrer la lista de empleados: " + e.getMessage());
        }
    }

    public boolean isEmployeeActive(int code) throws IOException {
        remps.seek(0);
        while (remps.getFilePointer() < remps.length()) {
            int codeEmp = remps.readInt();
            long nextPos = remps.getFilePointer();
            remps.readUTF();
            remps.readDouble();
            remps.readLong();
            long fDate = remps.readLong();

            if (codeEmp == code && fDate == 0) {
                remps.seek(nextPos);
                return true;
            }
        }
        return false;
    }

    public void addSaletoEmployee(int code, double amount) throws IOException {
        if (!isEmployeeActive(code)) {
            System.out.println("El Empleado no está activo en el sistema.");
            return;
        }
        int mesActual = Calendar.getInstance().get(Calendar.MONTH);
        RandomAccessFile rYear = salesFileFor(code);

        long pos = mesActual * 9;
        rYear.seek(pos);
        double ventaActual = rYear.readDouble();
        rYear.seek(pos);
        rYear.writeDouble(ventaActual + amount);
        rYear.close();

    }

    public RandomAccessFile billsFileFor(int code) throws IOException {
        String dirPadre = employeeFolder(code);
        File dir = new File(dirPadre);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String path = dirPadre + "/recibos.emp";
        return new RandomAccessFile(path, "rw");
    }

    public boolean isEmployeePayed(int code) throws IOException {
        RandomAccessFile rYear = salesFileFor(code);
        int mesActual = Calendar.getInstance().get(Calendar.MONTH);

        long pos = mesActual * 9;
        rYear.seek(pos + 8);

        boolean pagado = rYear.readBoolean();
        rYear.close();
        return pagado;
    }

    public void payEmployee(int code) throws IOException {
        if(!isEmployeeActive(code) || isEmployeePayed(code)) {
            System.out.println("No se pudo realizar el pago.");
            return;
        }
        String nombre = remps.readUTF();
        double salarioBase = remps.readDouble();

        Calendar calendar = Calendar.getInstance();
        int yearActual = calendar.get(Calendar.YEAR);
        int mesActual = calendar.get(Calendar.MONTH);

        RandomAccessFile rVentas = salesFileFor(code);
        long posicionMes = mesActual * 9;
        rVentas.seek(posicionMes);

        double totalVentas = rVentas.readDouble();

        double sueldo = salarioBase + (totalVentas*0.10);
        double deduccion = sueldo * 0.35;
        double totalPagar = sueldo - deduccion;

        RandomAccessFile bills = billsFileFor(code);
        bills.seek(bills.length());

        bills.writeLong(calendar.getTimeInMillis());
        bills.writeDouble(sueldo);
        bills.writeDouble(deduccion);
        bills.writeInt(yearActual);
        bills.writeInt(mesActual + 1);
        bills.close();

        rVentas.seek(posicionMes + 8);
        rVentas.writeBoolean(true);
        rVentas.close();

        System.out.println("Empleado: " + nombre + " | Pago recibido: Lps. " + totalPagar);
    }

    public void printEmployee(int code) throws IOException {
        if (!isEmployeeActive(code)) {
            System.out.println("El empleado no está activo en el sistema.");
            return;
        }
        String name = remps.readUTF();
        double salario = remps.readDouble();
        Date hireDate = new Date(remps.readLong());

        System.out.println("Codigo: " + code);
        System.out.println("Nombre: " + name);
        System.out.println("Salario: " + salario);
        System.out.println("Fecha de contratación: " + hireDate);

    RandomAccessFile rVentas = salesFileFor(code);
    double totalAnual = 0;

        System.out.println("\nVentas del año actual:");
        for (int mes = 0; mes < 12; mes++) {
            rVentas.seek(mes * 9);
            double venta = rVentas.readDouble();
            totalAnual += venta;
            System.out.println("Mes " + (mes + 1) + " : " + venta);
        }
        rVentas.close();
        System.out.println("Total de ventas del año: " + totalAnual);

        RandomAccessFile rBills = billsFileFor(code);
        int totalRecibos = (int) (rBills.length() / 32);
        rBills.close();

        System.out.println("Total de pagos realizados: " + totalRecibos);
    }

    public boolean fireEmployee(int code) throws IOException {
        if (isEmployeeActive(code)) {
            remps.readUTF();
            remps.readDouble();
            remps.readLong();
            long posFDate = remps.getFilePointer();

            remps.seek(posFDate);
            remps.writeLong(Calendar.getInstance().getTimeInMillis());
            return true;
        }
        return false;
    }

}
