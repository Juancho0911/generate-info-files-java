import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        try {
            // Cargar vendedores y productos
            Map<Long, String> vendedores = cargarVendedores("data/salesmen.txt");
            Map<Integer, Producto> productos = cargarProductos("data/products.txt");

            // Mapas para acumular resultados
            Map<Long, Integer> ventasPorVendedor = new HashMap<>();
            Map<Integer, Integer> ventasPorProducto = new HashMap<>();

            // Procesar todos los archivos de ventas
            File carpeta = new File("data/");
            for (File archivo : carpeta.listFiles()) {
                if (archivo.getName().startsWith("sales_")) {
                    procesarVentas(archivo, ventasPorVendedor, ventasPorProducto);
                }
            }

            // Generar reportes preliminares
            generarReporteVendedores(vendedores, ventasPorVendedor, productos);
            generarReporteProductos(productos, ventasPorProducto);

            System.out.println("Reportes generados correctamente.");
        } catch (Exception e) {
            System.err.println("Error al procesar archivos: " + e.getMessage());
        }
    }

    // ---------------- MÉTODOS AUXILIARES ----------------

    // Clase interna para productos
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

    // Cargar vendedores desde salesmen.txt
    private static Map<Long, String> cargarVendedores(String ruta) throws IOException {
        Map<Long, String> vendedores = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(";");
                long id = Long.parseLong(partes[1].trim());
                String nombreCompleto = partes[2].trim() + " " + partes[3].trim();
                vendedores.put(id, nombreCompleto);
            }
        }
        return vendedores;
    }

    // Cargar productos desde products.txt
    private static Map<Integer, Producto> cargarProductos(String ruta) throws IOException {
        Map<Integer, Producto> productos = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(";");
                int id = Integer.parseInt(partes[0].trim());
                String nombre = partes[1].trim();
                int precio = Integer.parseInt(partes[2].trim());
                productos.put(id, new Producto(id, nombre, precio));
            }
        }
        return productos;
    }

    // Procesar archivo de ventas de un vendedor
    private static void procesarVentas(File archivo, Map<Long, Integer> ventasPorVendedor,
                                       Map<Integer, Integer> ventasPorProducto) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea = br.readLine(); // primera línea: CC;id
            long idVendedor = Long.parseLong(linea.split(";")[1].trim());

            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(";");
                if (partes.length >= 2) {
                    int idProducto = Integer.parseInt(partes[0].trim());
                    int cantidad = Integer.parseInt(partes[1].trim());

                    ventasPorProducto.put(idProducto,
                        ventasPorProducto.getOrDefault(idProducto, 0) + cantidad);

                    ventasPorVendedor.put(idVendedor,
                        ventasPorVendedor.getOrDefault(idVendedor, 0) + cantidad);
                }
            }
        }
    }

    // Generar reporte de vendedores
    private static void generarReporteVendedores(Map<Long, String> vendedores,
                                                 Map<Long, Integer> ventasPorVendedor,
                                                 Map<Integer, Producto> productos) throws IOException {
        try (FileWriter writer = new FileWriter("data/report_vendedores.csv")) {
            for (Map.Entry<Long, Integer> entry : ventasPorVendedor.entrySet()) {
                long id = entry.getKey();
                int cantidad = entry.getValue();
                String nombre = vendedores.get(id);
                writer.write(nombre + ";" + cantidad + "\n");
            }
        }
    }

    // Generar reporte de productos
    private static void generarReporteProductos(Map<Integer, Producto> productos,
                                                Map<Integer, Integer> ventasPorProducto) throws IOException {
        try (FileWriter writer = new FileWriter("data/report_productos.csv")) {
            for (Map.Entry<Integer, Integer> entry : ventasPorProducto.entrySet()) {
                int id = entry.getKey();
                int cantidad = entry.getValue();
                Producto p = productos.get(id);
                writer.write(p.nombre + ";" + p.precio + ";" + cantidad + "\n");
            }
        }
    }
}
