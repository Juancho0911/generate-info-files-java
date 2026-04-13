import java.io.*;
import java.util.*;

public class Main {

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

    public static void main(String[] args) {

        try {

            Map<Long, String> vendedores = cargarVendedores("data/salesmen.txt");
            Map<Integer, Producto> productos = cargarProductos("data/products.txt");

            Map<Long, Integer> ventasPorVendedor = new HashMap<>();
            Map<Integer, Integer> ventasPorProducto = new HashMap<>();

            File carpeta = new File("data/");
            File[] archivos = carpeta.listFiles();

            if (archivos != null) {
                for (File archivo : archivos) {

                    if (archivo.getName().startsWith("sales_")) {
                        procesarVentas(archivo, ventasPorVendedor, ventasPorProducto);
                    }
                }
            }

            generarReporteVendedores(vendedores, ventasPorVendedor);
            generarReporteProductos(productos, ventasPorProducto);

            System.out.println("Reportes generados correctamente.");

        } catch (Exception e) {

            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

    }

    private static Map<Long, String> cargarVendedores(String ruta) throws IOException {

        Map<Long, String> vendedores = new HashMap<>();

        BufferedReader br = new BufferedReader(new FileReader(ruta));
        String linea;

        while ((linea = br.readLine()) != null) {

            String[] partes = linea.split(";");

            long id = Long.parseLong(partes[1]);
            String nombre = partes[2] + " " + partes[3];

            vendedores.put(id, nombre);
        }

        br.close();

        return vendedores;
    }

    private static Map<Integer, Producto> cargarProductos(String ruta) throws IOException {

        Map<Integer, Producto> productos = new HashMap<>();

        BufferedReader br = new BufferedReader(new FileReader(ruta));
        String linea;

        while ((linea = br.readLine()) != null) {

            String[] partes = linea.split(";");

            int id = Integer.parseInt(partes[0]);
            String nombre = partes[1];
            int precio = Integer.parseInt(partes[2]);

            productos.put(id, new Producto(id, nombre, precio));
        }

        br.close();

        return productos;
    }

    private static void procesarVentas(File archivo,
            Map<Long, Integer> ventasPorVendedor,
            Map<Integer, Integer> ventasPorProducto) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(archivo));

        String linea = br.readLine();

        if (linea == null) {
            br.close();
            return;
        }

        long idVendedor = Long.parseLong(linea.split(";")[1]);

        while ((linea = br.readLine()) != null) {

            String[] partes = linea.split(";");

            int idProducto = Integer.parseInt(partes[0]);
            int cantidad = Integer.parseInt(partes[1]);

            ventasPorProducto.put(
                    idProducto,
                    ventasPorProducto.getOrDefault(idProducto, 0) + cantidad);

            ventasPorVendedor.put(
                    idVendedor,
                    ventasPorVendedor.getOrDefault(idVendedor, 0) + cantidad);
        }

        br.close();
    }

    private static void generarReporteVendedores(
            Map<Long, String> vendedores,
            Map<Long, Integer> ventas) throws IOException {

        FileWriter writer = new FileWriter("data/report_vendedores.csv");

        for (Long id : ventas.keySet()) {

            String nombre = vendedores.get(id);
            int cantidad = ventas.get(id);

            writer.write(nombre + ";" + cantidad + "\n");
        }

        writer.close();
    }

    private static void generarReporteProductos(
            Map<Integer, Producto> productos,
            Map<Integer, Integer> ventas) throws IOException {

        FileWriter writer = new FileWriter("data/report_productos.csv");

        for (Integer id : ventas.keySet()) {

            Producto p = productos.get(id);
            int cantidad = ventas.get(id);

            writer.write(p.nombre + ";" + p.precio + ";" + cantidad + "\n");
        }

        writer.close();
    }
}
