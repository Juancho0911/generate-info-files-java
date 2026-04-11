import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hola, funciona!");

        try {
            // Cargar vendedores y productos
            Map<Long, String> vendedores = cargarVendedores("data/salesmen.txt");
            Map<Integer, Producto> productos = cargarProductos("data/products.txt");

            // Mapas para acumular resultados
            Map<Long, Integer> ventasPorVendedor = new HashMap<>();
            Map<Integer, Integer> ventasPorProducto = new HashMap<>();

            // Procesar archivos de ventas
            File carpeta = new File("data/");
            File[] archivos = carpeta.listFiles();

            if (archivos != null) {
                for (File archivo : archivos) {
                    if (archivo.getName().startsWith("sales_")) {
                        procesarVentas(archivo, ventasPorVendedor, ventasPorProducto);
                    }
                }
            } else {
                System.err.println("La carpeta 'data/' no existe o está vacía.");
                return;
            }

            // Generar reportes
            generarReporteVendedores(vendedores, ventasPorVendedor);
            generarReporteProductos(productos, ventasPorProducto);

            System.out.println("Reportes generados correctamente.");

        } catch (Exception e) {
            System.err.println("Error al procesar archivos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ---------------- CLASE PRODUCTO ----------------
    static class Producto {
        int id;
        String nombre;
        int precio;

        Producto(int id, String nombre, int precio) {
            this.id = id;
            this.nombre = nombre;
            this.precio = precio;
        }
    }

    // ---------------- CARGAR VENDEDORES ----------------
    private static Map<Long, String> cargarVendedores(String ruta) throws IOException {
        Map<Long, String> vendedores = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;

            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(";");

                if (partes.length >= 4) {
                    long id = Long.parseLong(partes[1].trim());
                    String nombreCompleto = partes[2].trim() + " " + partes[3].trim();
                    vendedores.put(id, nombreCompleto);
                }
            }
        }

        return vendedores;
    }

    // ---------------- CARGAR PRODUCTOS ----------------
    private static Map<Integer, Producto> cargarProductos(String ruta) throws IOException {
        Map<Integer, Producto> productos = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;

            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(";");

                if (partes.length >= 3) {
                    int id = Integer.parseInt(partes[0].trim());
                    String nombre = partes[1].trim();
                    int precio = Integer.parseInt(partes[2].trim());

                    productos.put(id, new Producto(id, nombre, precio));
                }
            }
        }

        return productos;
    }

    // ---------------- PROCESAR VENTAS ----------------
    private static void procesarVentas(File archivo,
                                       Map<Long, Integer> ventasPorVendedor,
                                       Map<Integer, Integer> ventasPorProducto) throws IOException {

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea = br.readLine();

            if (linea == null) return;

            long idVendedor = Long.parseLong(linea.split(";")[1].trim());

            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(";");

                if (partes.length >= 2) {
                    int idProducto = Integer.parseInt(partes[0].trim());
                    int cantidad = Integer.parseInt(partes[1].trim());

                    // Acumular ventas por producto
                    ventasPorProducto.put(idProducto,
                            ventasPorProducto.getOrDefault(idProducto, 0) + cantidad);

                    // Acumular ventas por vendedor
                    ventasPorVendedor.put(idVendedor,
                            ventasPorVendedor.getOrDefault(idVendedor, 0) + cantidad);
                }
            }
        }
    }

    // ---------------- REPORTE VENDEDORES ----------------
    private static void generarReporteVendedores(Map<Long, String> vendedores,
                                                 Map<Long, Integer> ventasPorVendedor) throws IOException {

        try (FileWriter writer = new FileWriter("data/report_vendedores.csv")) {
            for (Map.Entry<Long, Integer> entry : ventasPorVendedor.entrySet()) {

                long id = entry.getKey();
                int cantidad = entry.getValue();

                String nombre = vendedores.get(id);

                if (nombre != null) {
                    writer.write(nombre + ";" + cantidad + "\n");
                }
            }
        }
    }

    // ---------------- REPORTE PRODUCTOS ----------------
    private static void generarReporteProductos(Map<Integer, Producto> productos,
                                                Map<Integer, Integer> ventasPorProducto) throws IOException {

        try (FileWriter writer = new FileWriter("data/report_productos.csv")) {
            for (Map.Entry<Integer, Integer> entry : ventasPorProducto.entrySet()) {

                int id = entry.getKey();
                int cantidad = entry.getValue();

                Producto p = productos.get(id);

                if (p != null) {
                    writer.write(p.nombre + ";" + p.precio + ";" + cantidad + "\n");
                }
            }
        }
    }
}
