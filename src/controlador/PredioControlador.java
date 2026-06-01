package controlador;

import modelo.Predio;
import servicio.BusquedaServicio;
import servicio.CargadorCsvServicio;
import servicio.OrdenamientoServicio;
import vista.VistaPrincipal;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PredioControlador {

   
    private final CargadorCsvServicio  cargador     = new CargadorCsvServicio();
    private final OrdenamientoServicio ordenamiento = new OrdenamientoServicio();
    private final BusquedaServicio     busqueda     = new BusquedaServicio();

    private VistaPrincipal vista;

   
    private List<Predio> predios          = new ArrayList<>();
    private int          columnaOrdenada  = -1;  
    private SwingWorker<?, ?> trabajoActual = null;

  

    public void iniciar() {
        vista = new VistaPrincipal(this);
        vista.mostrar();
    }


    public void cargarArchivo(String ruta) {
        vista.setEstado("Cargando archivo...");
        vista.setBloqueado(true);

        SwingWorker<List<Predio>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Predio> doInBackground() throws IOException {
                return cargador.cargar(ruta);
            }

            @Override
            protected void done() {
                try {
                    predios = get();
                    columnaOrdenada = -1;
                    vista.setTotalRegistros(predios.size());
                    vista.setEstado("Archivo cargado: " + predios.size() + " predios.");
                } catch (Exception e) {
                    vista.mostrarError("No se pudo cargar el archivo:\n" + e.getMessage());
                    vista.setEstado("Error al cargar.");
                } finally {
                    vista.setBloqueado(false);
                }
            }
        };
        worker.execute();
    }

  
  
    public void ejecutarBusqueda(int columna, String criterio) {
        if (predios.isEmpty()) {
            vista.mostrarError("Primero debe cargar un archivo CSV.");
            return;
        }
        if (criterio.isBlank()) {
            vista.mostrarError("Ingrese un criterio de búsqueda.");
            return;
        }

        if (trabajoActual != null && !trabajoActual.isDone()) {
            trabajoActual.cancel(true);
        }

        vista.setBloqueado(true);
        vista.limpiarResultados();
        final long inicio = System.nanoTime();

        trabajoActual = new SwingWorker<List<Predio>, String>() {

            @Override
            protected List<Predio> doInBackground() {
                
                if (columnaOrdenada != columna) {
                    publish("Ordenando por columna " + nombreColumna(columna) + "...");
                    ordenamiento.ordenar(predios, columna);
                    columnaOrdenada = columna;
                } else {
                    publish("Lista ya ordenada por " + nombreColumna(columna) + ". Omitiendo ordenamiento.");
                }

              
                publish("Buscando \"" + criterio + "\"...");
                return busqueda.buscar(predios, columna, criterio);
            }

            @Override
            protected void process(java.util.List<String> mensajes) {
               
                mensajes.forEach(vista::setEstado);
            }

            @Override
            protected void done() {
                if (isCancelled()) {
                    vista.setEstado("Búsqueda cancelada.");
                    vista.setBloqueado(false);
                    return;
                }
                try {
                    List<Predio> resultados = get();
                    long fin     = System.nanoTime();
                    long miliseg = (fin - inicio) / 1_000_000;

                    vista.mostrarResultados(resultados);
                    vista.setTiempo(miliseg);
                    vista.setEstado("Búsqueda completada. " + resultados.size() + " resultado(s).");
                } catch (Exception e) {
                    vista.mostrarError("Error durante la búsqueda:\n" + e.getMessage());
                    vista.setEstado("Error en la búsqueda.");
                } finally {
                    vista.setBloqueado(false);
                }
            }
        };

        trabajoActual.execute();
    }

   

    private String nombreColumna(int columna) {
        return switch (columna) {
            case 0  -> "NPN";
            case 1  -> "Municipio";
            case 2  -> "Dirección";
            case 3  -> "Ficha";
            default -> "Desconocida";
        };
    }
}
