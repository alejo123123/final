package servicio;

import modelo.Predio;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class CargadorCsvServicio {

    
    
    public List<Predio> cargar(String rutaArchivo) throws IOException {
        List<Predio> predios = new ArrayList<>(55_000);

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(rutaArchivo), StandardCharsets.UTF_8))) {

            String encabezado = br.readLine();
            if (encabezado == null) return predios;

            
            char separador = encabezado.contains(";") ? ';' : ',';
            String regex   = String.valueOf(separador);

            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;

                String[] partes = linea.split(regex, -1);
                if (partes.length < 4) continue;

                predios.add(new Predio(
                        partes[0],
                        partes[1],
                        partes[2],
                        partes[3]
                ));
            }
        }
        return predios;
    }
}
