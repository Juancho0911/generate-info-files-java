import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Clase principal encargada de generar archivos de prueba
 * para el proyecto de procesamiento de ventas.
 * 
 * Genera:
 * - Archivo de productos
 * - Archivo de vendedores
 * - Archivos de ventas por vendedor
 * 
 * No requiere entrada del usuario.
 */
public class GenerateInfoFiles {

    // 📌 Constantes
    private static final String DATA_FOLDER = "data/";
    private static final int DEFAULT_PRODUCTS = 10;
    private static final int DEFAULT_SALESMEN = 5;
    private static final int DEFAULT_SALES_PER_SELLER = 10;

    private static final String[] NAMES = {
        "Juan", "Carlos", "Ana", "Luisa", "Pedro", "Maria", "Sofia", "Andres"
    };

    private static final String[] LASTNAMES = {
        "Perez", "Gomez", "Rodriguez", "Lopez", "Martinez", "Garcia"
    };

    private static final Random RANDOM = new Random();

    /**
     * Método principal que ejecuta la generación de archivos.
     */
    public static void main(String[] args) {
        try {
            createDataFolder();

            createProductsFile(DEFAULT_PRODUCTS);
            createSalesManInfoFile(DEFAULT_SALESMEN);

            for (int i = 1; i <= DEFAULT_SALESMEN; i++) {
                createSalesMenFile(DEFAULT_SALES_PER_SELLER, i);
            }

            System.out.println("✅ Archivos generados correctamente en carpeta /data");

        } catch (Exception e) {
            System.err.println("❌ Error al generar archivos: " + e.getMessage());
        }
    }

    /**
     * Crea la carpeta donde se almacenarán los archivos.
     */
    private static void createDataFolder() {
        File folder = new File(DATA_FOLDER);
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    /**
     * Genera un archivo con productos pseudoaleatorios.
     * 
     * Formato:
     * IDProducto;NombreProducto;Precio
     * 
     * @param productsCount cantidad de productos a generar
     * @throws IOException si ocurre un error al escribir el archivo
     */
    public static void createProductsFile(int productsCount) throws IOException {
        FileWriter writer = new FileWriter(DATA_FOLDER + "products.txt");

        for (int i = 1; i <= productsCount; i++) {
            int price = RANDOM.nextInt(50000) + 1000;

            writer.write(i + ";Producto" + i + ";" + price + "\n");
        }

        writer.close();
    }

    /**
     * Genera un archivo con información de vendedores.
     * 
     * Formato:
     * TipoDocumento;NumeroDocumento;Nombres;Apellidos
     * 
     * @param salesmanCount cantidad de vendedores
     * @throws IOException si ocurre un error al escribir el archivo
     */
    public static void createSalesManInfoFile(int salesmanCount) throws IOException {
        FileWriter writer = new FileWriter(DATA_FOLDER + "salesmen.txt");

        for (int i = 1; i <= salesmanCount; i++) {
            String name = NAMES[RANDOM.nextInt(NAMES.length)];
            String lastname = LASTNAMES[RANDOM.nextInt(LASTNAMES.length)];

            writer.write("CC;" + i + ";" + name + ";" + lastname + "\n");
        }

        writer.close();
    }

    /**
     * Genera un archivo de ventas para un vendedor específico.
     * 
     * Formato:
     * TipoDocumento;NumeroDocumento
     * IDProducto;Cantidad;
     * 
     * @param salesCount cantidad de registros de ventas
     * @param sellerId identificador del vendedor
     * @throws IOException si ocurre un error al escribir el archivo
     */
    public static void createSalesMenFile(int salesCount, long sellerId) throws IOException {
        FileWriter writer = new FileWriter(DATA_FOLDER + "sales_" + sellerId + ".txt");

        // Cabecera del vendedor
        writer.write("CC;" + sellerId + "\n");

        for (int i = 0; i < salesCount; i++) {
            int productId = RANDOM.nextInt(DEFAULT_PRODUCTS) + 1;
            int quantity = RANDOM.nextInt(20) + 1;

            writer.write(productId + ";" + quantity + ";\n");
        }

        writer.close();
    }
}