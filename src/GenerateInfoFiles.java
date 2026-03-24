import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Clase principal encargada de generar los archivos de prueba necesarios para
 * el proyecto de procesamiento de ventas.
 * <p>
 * Este programa crea:
 * <ul>
 * <li>Un archivo con la informacion de productos.</li>
 * <li>Un archivo con la informacion de vendedores.</li>
 * <li>Un archivo de ventas por cada vendedor generado.</li>
 * </ul>
 * <p>
 * El programa no solicita informacion al usuario. Toda la informacion se
 * construye de manera pseudoaleatoria al ejecutarse.
 */
public class GenerateInfoFiles {

	/**
	 * Representa un vendedor generado para los archivos de prueba. Se usa para
	 * garantizar coherencia entre el archivo de vendedores y el archivo de ventas
	 * correspondiente.
	 */
	private static class Salesman {
		private final long id;
		private final String name;
		private final String lastName;

		/**
		 * Construye un vendedor con sus datos basicos.
		 *
		 * @param id       identificacion del vendedor
		 * @param name     nombre del vendedor
		 * @param lastName apellido del vendedor
		 */
		private Salesman(long id, String name, String lastName) {
			this.id = id;
			this.name = name;
			this.lastName = lastName;
		}
	}

	/**
	 * Carpeta en la que se guardan todos los archivos generados.
	 */
	private static final String DATA_FOLDER = "data/";

	/**
	 * Ruta del archivo de productos.
	 */
	private static final String PRODUCTS_FILE = DATA_FOLDER + "products.txt";

	/**
	 * Ruta del archivo con informacion de vendedores.
	 */
	private static final String SALESMEN_FILE = DATA_FOLDER + "salesmen.txt";

	/**
	 * Cantidad de productos que se generan por defecto.
	 */
	private static final int DEFAULT_PRODUCTS = 10;

	/**
	 * Cantidad de vendedores que se generan por defecto.
	 */
	private static final int DEFAULT_SALESMEN = 5;

	/**
	 * Cantidad de ventas que se generan por defecto para cada vendedor.
	 */
	private static final int DEFAULT_SALES_PER_SELLER = 10;

	/**
	 * Lista de nombres posibles para los vendedores.
	 */
	private static final String[] NAMES = { "Juan", "Carlos", "Ana", "Luisa", "Pedro", "Maria", "Sofia", "Andres" };

	/**
	 * Lista de apellidos posibles para los vendedores.
	 */
	private static final String[] LAST_NAMES = { "Perez", "Gomez", "Rodriguez", "Lopez", "Martinez", "Garcia" };

	/**
	 * Generador pseudoaleatorio usado en todo el programa.
	 */
	private static final Random RANDOM = new Random();

	/**
	 * Metodo principal que ejecuta la generacion de archivos.
	 *
	 * @param args argumentos de linea de comandos no utilizados
	 */
	public static void main(String[] args) {
		try {
			createDataFolder();
			createProductsFile(DEFAULT_PRODUCTS);

			Salesman[] salesmen = createSalesManInfoFile(DEFAULT_SALESMEN);

			for (Salesman salesman : salesmen) {
				createSalesMenFile(DEFAULT_SALES_PER_SELLER, salesman.name, salesman.id, DEFAULT_PRODUCTS);
			}

			System.out.println("Archivos generados correctamente en la carpeta data.");
		} catch (IOException e) {
			System.err.println("Error al generar archivos: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Crea la carpeta de salida donde se almacenaran los archivos generados.
	 *
	 * @throws IOException si la carpeta no existe y no se puede crear
	 */
	private static void createDataFolder() throws IOException {
		File folder = new File(DATA_FOLDER);
		if (!folder.exists() && !folder.mkdir()) {
			throw new IOException("No fue posible crear la carpeta: " + DATA_FOLDER);
		}
	}

	/**
	 * Genera un archivo de productos con informacion pseudoaleatoria. Cada linea
	 * contiene el identificador del producto, su nombre y su precio unitario.
	 * <p>
	 * Formato por linea:
	 * 
	 * <pre>
	 * IDProducto;NombreProducto;Precio
	 * </pre>
	 *
	 * @param productsCount cantidad de productos a generar
	 * @throws IOException si ocurre un error durante la escritura del archivo
	 */
	public static void createProductsFile(int productsCount) throws IOException {
		try (FileWriter writer = new FileWriter(PRODUCTS_FILE)) {
			for (int i = 1; i <= productsCount; i++) {
				int price = RANDOM.nextInt(50000) + 1000;
				writer.write(i + ";Producto" + i + ";" + price + "\n");
			}
		}
	}

	/**
	 * Genera el archivo con la informacion de los vendedores. Tambien devuelve los
	 * vendedores creados para reutilizar los mismos datos al momento de construir
	 * los archivos de ventas.
	 * <p>
	 * Formato por linea:
	 * 
	 * <pre>
	 * TipoDocumento;NumeroDocumento;Nombres;Apellidos
	 * </pre>
	 *
	 * @param salesmanCount cantidad de vendedores a generar
	 * @return arreglo con los vendedores generados
	 * @throws IOException si ocurre un error durante la escritura del archivo
	 */
	public static Salesman[] createSalesManInfoFile(int salesmanCount) throws IOException {
		Salesman[] salesmen = new Salesman[salesmanCount];

		try (FileWriter writer = new FileWriter(SALESMEN_FILE)) {
			for (int i = 1; i <= salesmanCount; i++) {
				String name = NAMES[RANDOM.nextInt(NAMES.length)];
				String lastName = LAST_NAMES[RANDOM.nextInt(LAST_NAMES.length)];

				Salesman salesman = new Salesman(i, name, lastName);
				salesmen[i - 1] = salesman;

				writer.write("CC;" + salesman.id + ";" + salesman.name + ";" + salesman.lastName + "\n");
			}
		}

		return salesmen;
	}

	/**
	 * Genera un archivo de ventas para un vendedor especifico. La primera linea
	 * contiene la identificacion del vendedor y las siguientes lineas contienen
	 * productos vendidos y cantidades.
	 * <p>
	 * Formato del archivo:
	 * 
	 * <pre>
	 * TipoDocumento;NumeroDocumento
	 * IDProducto;Cantidad;
	 * </pre>
	 *
	 * @param randomSalesCount cantidad de registros de venta a generar
	 * @param name             nombre del vendedor
	 * @param id               identificacion del vendedor
	 * @param productsCount    cantidad total de productos validos
	 * @throws IOException si ocurre un error durante la escritura del archivo
	 */
	public static void createSalesMenFile(int randomSalesCount, String name, long id, int productsCount)
			throws IOException {
		String safeName = name.toLowerCase().replace(" ", "_");
		String fileName = DATA_FOLDER + "sales_" + safeName + "_" + id + ".txt";

		try (FileWriter writer = new FileWriter(fileName)) {
			writer.write("CC;" + id + "\n");

			for (int i = 0; i < randomSalesCount; i++) {
				int productId = RANDOM.nextInt(productsCount) + 1;
				int quantity = RANDOM.nextInt(20) + 1;

				writer.write(productId + ";" + quantity + ";\n");
			}
		}
	}
}