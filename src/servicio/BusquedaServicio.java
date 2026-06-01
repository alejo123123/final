package servicio;

import modelo.Predio;
import java.util.ArrayList;
import java.util.List;


public class BusquedaServicio {

    
    public List<Predio> buscar(List<Predio> predios, int columna, String criterio) {
        List<Predio> resultados = new ArrayList<>();
        if (predios.isEmpty() || criterio == null || criterio.isBlank()) return resultados;

        String criterioNorm = criterio.trim().toLowerCase();

        
        int posicion = busquedaBinaria(predios, columna, criterioNorm);

        if (posicion < 0) return resultados;   

        
        int izq = posicion;
        while (izq > 0 && contiene(predios.get(izq - 1).getValorColumna(columna), criterioNorm)) {
            izq--;
        }

        
        int der = posicion;
        while (der < predios.size() - 1 && contiene(predios.get(der + 1).getValorColumna(columna), criterioNorm)) {
            der++;
        }

       
        for (int i = izq; i <= der; i++) {
            resultados.add(predios.get(i));
        }

        return resultados;
    }

    

   
    private int busquedaBinaria(List<Predio> predios, int columna, String criterioNorm) {
        int inicio = 0;
        int fin    = predios.size() - 1;

        while (inicio <= fin) {
            int medio       = inicio + (fin - inicio) / 2;
            String valorMed = predios.get(medio).getValorColumna(columna).toLowerCase();

            if (contiene(valorMed, criterioNorm)) {
                return medio;  
            }

            // Decidir hacia qué mitad ir
            int cmp = valorMed.compareTo(criterioNorm);

            if (cmp < 0) {
               
                inicio = medio + 1;
            } else {
               
                if (valorMed.startsWith(criterioNorm)) return medio;
                fin = medio - 1;
            }
        }

        
        return busquedaLinealAcotada(predios, columna, criterioNorm, inicio);
    }

    
    private int busquedaLinealAcotada(List<Predio> predios, int columna,
                                      String criterioNorm, int inicio) {
        int n = predios.size();
        int desde = Math.max(0, inicio - 500);
        int hasta = Math.min(n - 1, inicio + 500);

        for (int i = desde; i <= hasta; i++) {
            if (contiene(predios.get(i).getValorColumna(columna).toLowerCase(), criterioNorm)) {
                return i;
            }
        }
        return -1;
    }

    
    private boolean contiene(String valor, String criterioNorm) {
        return valor.toLowerCase().contains(criterioNorm);
    }
}
