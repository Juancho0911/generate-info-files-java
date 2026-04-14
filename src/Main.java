import java.io.*;
import java.util.*;

/**
 * Clase que lee los archivos de ventas y genera dos reportes CSV.
 */
public class Main {

    // -- CLASE PRODUCTO --

    /** Guarda la informacion basica de un producto. */
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

    /** Metodo principal, carga los archivos y genera los reportes. */
    public static void main(String[] args) {
        System.out.println("finalización exitosa!");

        try {
            // Cargar vendedores y productos
            Map<Long, String> vendedores = cargarVendedores("data/salesmen.txt");
            Map<Integer, Producto> productos = cargarProductos("data/products.txt");

            // Mapas para acumular resultados
            Map<Long, Long> ventasPorVendedor = new HashMap<>();
            Map<Integer, Integer> ventasPorProducto = new HashMap<>();

            // Procesar archivos de ventas
            File carpeta = new File("data/");
            File[] archivos = carpeta.listFiles();

            if (archivos != null) {
                for (File archivo : archivos) {
                    if (archivo.getName().startsWith("sales_")) {
                        procesarVentas(archivo, ventasPorVendedor, ventasPorProducto, productos);
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

    // ------- CARGAR VENDEDORES ----

    /** Lee el archivo de vendedores y retorna un mapa con su id y nombre. */
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

    // ---- CARGAR PRODUCTOS -----

    /** Lee el archivo de productos y retorna un mapa con su id y datos. */
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

    // ---- PROCESAR VENTAS -----

    /** Lee un archivo de ventas y acumula el dinero por vendedor y cantidad por producto. */
    private static void procesarVentas(File archivo,
                                       Map<Long, Long> ventasPorVendedor,
                                       Map<Integer, Integer> ventasPorProducto,
                                       Map<Integer, Producto> productos) throws IOException {

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea = br.readLine();

            if (linea == null) return;

            long idVendedor = Long.parseLong(linea.split(";")[1].trim());

            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(";");

                if (partes.length >= 2) {
                    int idProducto = Integer.parseInt(partes[0].trim());
                    int cantidad = Integer.parseInt(partes[1].trim());
                    Producto p = productos.get(idProducto);

                    // si el producto no existe en el catalogo lo saltamos
                    if (p == null) {
                        System.err.println("Producto no encontrado: " + idProducto);
                        continue;
                    }

                    // si la cantidad es rara tambien la saltamos
                    if (cantidad <= 0) {
                        System.err.println("Cantidad invalida en " + archivo.getName() + ": " + cantidad);
                        continue;
                    }

                    // Acumular ventas por producto
                    ventasPorProducto.put(idProducto,
                            ventasPorProducto.getOrDefault(idProducto, 0) + cantidad);

                    // Acumular dinero por vendedor (cantidad x precio)
                    ventasPorVendedor.put(idVendedor,
                            ventasPorVendedor.getOrDefault(idVendedor, 0L) + (long) cantidad * p.precio);
                }
            }
        }
    }

    // ------ REPORTE VENDEDORES -----

    /** Generra el reporte de vendedores ordenado por dineero de mayor a menor. */
    private static void generarReporteVendedores(Map<Long, String> vendedores,
                                                 Map<Long, Long> ventasPorVendedor) throws IOException {

        List<Map.Entry<Long, Long>> lista = new ArrayList<>(ventasPorVendedor.entrySet());
        lista.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));

        try (FileWriter writer = new FileWriter("data/report_vendedores.csv")) {
            for (Map.Entry<Long, Long> entry : lista) {
                String nombre = vendedores.get(entry.getKey());

                if (nombre != null) {
                    writer.write(nombre + ";" + entry.getValue() + "\n");
                }
            }
        }
    }

    // --- REPORTE PRODUCTOS -------

    /** Genera el reporte de productos ordenado por cantidad de mayor a menore. */
    private static void generarReporteProductos(Map<Integer, Producto> productos,
                                                Map<Integer, Integer> ventasPorProducto) throws IOException {

        List<Map.Entry<Integer, Integer>> lista = new ArrayList<>(ventasPorProducto.entrySet());
        lista.sort((a, b) -> b.getValue() - a.getValue());

        try (FileWriter writer = new FileWriter("data/report_productos.csv")) {
            for (Map.Entry<Integer, Integer> entry : lista) {
                Producto p = productos.get(entry.getKey());

                if (p != null) {
                    writer.write(p.nombre + ";" + p.precio + ";" + entry.getValue() + "\n");
                }
            }
        }
    }
    
        
}
