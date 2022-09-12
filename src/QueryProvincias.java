import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class QueryProvincias implements Escritor{
	

	public static void main(String[] args) {
		
		StringBuilder output = new StringBuilder("Informacion geopolitica de España: Provincias y sus capitales\\n");
		
		try {
			// Registro
			System.out.println("\tRegistro");
			Scanner sc = new Scanner(System.in);
			System.out.print("Nombre: ");
			String username = sc.nextLine();
			Usuario user = new Usuario(username);
			sc.close();
			System.out.println("Identificado correctamente como " + username +", ID: "+user.getId());

			// Api call and data obtain
			System.out.println("Obteniendo información geopolítica, por favor espere...\n");
			URL url = new URL("https://public.opendatasoft.com/api/records/1.0/search/?dataset=provincias-espanolas&q=&lang=ES&rows=0&sort=provincia&facet=provincia");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.connect();
			
			int res = conn.getResponseCode();
			if (res != 200) { throw new RuntimeException("HttpResponse: " + res);} 
			else {
				StringBuilder sb = new StringBuilder();
				Scanner apiReader = new Scanner(url.openStream());
				while (apiReader.hasNext()) { sb.append(apiReader.nextLine() + "\n");}
				apiReader.close();

				JSONObject respuesta = new JSONObject(sb.toString()) ;
				JSONArray records = respuesta.getJSONArray("facet_groups").getJSONObject(0).getJSONArray("facets");
				ArrayList<Capital> capitales = new ArrayList<Capital>();
				for (int i = 0; i < records.length(); i++) {
					JSONObject provincia = records.getJSONObject(i);
					capitales.add(new Capital(provincia.getString("name"), provincia.getString("path")));
					output.append("\\nProvincia: "+provincia.getString("name") + "\\t\\t-\\t\\tCapital: " + provincia.getString("path"));
				}
				output.append("\\n\\nInformacion obtenida para el usuario " + user.getNombre() + " con ID " + user.getId() + " el " + user.getFechaLogin());
				// Generación de archivos
				System.out.println(output.toString().replace("\\n", "\n").replace("\\t", "\t"));
				new QueryProvincias().escribe(output.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void escribe(String texto) {
		
		try {
			Files.write(Paths.get("Provincias.txt"), Arrays.asList(texto.replace("\\t", "\t").replace("\\n", "\n")), StandardCharsets.UTF_8);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		ArrayList<String> jenkinsLines = new ArrayList<String>();
		jenkinsLines.add(""
				+ "pipeline {\r\n"
				+ "   agent any\r\n"
				+ "   stages {\r\n"
				+ "      stage('PrintGeopolitica') {\r\n"
				+ "         steps {\r\n"
				+ "			  script{\r\n"
				+ "				println \""+texto+"\"\r\n"
				+ "			  }\r\n"
				+ "         }\r\n"
				+ "      }\r\n"
				+ "   }\r\n"
				+ "}\r\n"
				+ "");
		try {
			Path jenkins = Paths.get("Jenkinsfile");
			Files.write(jenkins, jenkinsLines, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
