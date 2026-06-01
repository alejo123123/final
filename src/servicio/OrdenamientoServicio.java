package servicio;

import modelo.Predio;
import java.util.List;


public class OrdenamientoServicio {

   
    public void ordenar(List<Predio> predios, int columna) {
        quicksortHoare(predios, 0, predios.size() - 1, columna);
    }

    

    private void quicksortHoare(List<Predio> lista, int inicio, int fin, int columna) {
        if (inicio >= fin) return;

        int indicePivote = particionHoare(lista, inicio, fin, columna);

        quicksortHoare(lista, inicio,          indicePivote,     columna);
        quicksortHoare(lista, indicePivote + 1, fin,             columna);
    }

    
    private int particionHoare(List<Predio> lista, int inicio, int fin, int columna) {
       
        int medio = inicio + (fin - inicio) / 2;
        medianaDeTres(lista, inicio, medio, fin, columna);
        String valorPivote = lista.get(inicio).getValorColumna(columna);

        int i = inicio - 1;
        int j = fin + 1;

        while (true) {
            do { i++; } while (comparar(lista.get(i).getValorColumna(columna), valorPivote) < 0);
            do { j--; } while (comparar(lista.get(j).getValorColumna(columna), valorPivote) > 0);

            if (i >= j) return j;

            intercambiar(lista, i, j);
        }
    }

   
    private void medianaDeTres(List<Predio> lista, int a, int b, int c, int columna) {
        String va = lista.get(a).getValorColumna(columna);
        String vb = lista.get(b).getValorColumna(columna);
        String vc = lista.get(c).getValorColumna(columna);

        if (comparar(va, vb) > 0) { intercambiar(lista, a, b); va = lista.get(a).getValorColumna(columna); }
        if (comparar(va, vc) > 0) { intercambiar(lista, a, c); }
        if (comparar(lista.get(b).getValorColumna(columna), vc) > 0) intercambiar(lista, b, c);
    }

    

    
    private int comparar(String a, String b) {
        return a.compareToIgnoreCase(b);
    }

    private void intercambiar(List<Predio> lista, int i, int j) {
        Predio temp = lista.get(i);
        lista.set(i, lista.get(j));
        lista.set(j, temp);
    }
}
