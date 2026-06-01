package vista;

import controlador.PredioControlador;
import modelo.Predio;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VistaPrincipal extends JFrame {

    private final PredioControlador controlador;

    private JComboBox<String> comboColumna;
    private JTextField        campoBusqueda;
    private JButton           btnBuscar;
    private JButton           btnCargar;
    private JTextField lblTiempo;
    private JLabel            lblEstado;
    private JLabel            lblTotal;
    private JTable            tabla;
    private DefaultTableModel modeloTabla;

    private static final String[] COLUMNAS_COMBO  = {"NPN", "Municipio", "Dirección", "Ficha"};
    private static final String[] COLUMNAS_TABLA  = {"#", "NPN", "Municipio", "Dirección", "Ficha"};



    public VistaPrincipal(PredioControlador controlador) {
        this.controlador = controlador;
        construirUI();
    }

    public void mostrar() {
        setVisible(true);
    }



    private void construirUI() {
        setTitle("Sistema Catastral – Ordenamiento y Búsqueda");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 680);
        setMinimumSize(new Dimension(900, 500));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        add(crearPanelSuperior(), BorderLayout.NORTH);
        add(crearPanelTabla(),    BorderLayout.CENTER);
        add(crearBarraEstado(),   BorderLayout.SOUTH);
    }



    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8));

        panel.add(crearPanelCarga(),   BorderLayout.NORTH);
        panel.add(crearPanelBusqueda(), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelCarga() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));

        btnCargar = new JButton("📂 Cargar CSV");
        btnCargar.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        btnCargar.setToolTipText("Seleccionar el archivo predios.csv");
        btnCargar.addActionListener(e -> abrirSelectorArchivo());

        lblTotal = new JLabel("Sin archivo cargado");
        lblTotal.setForeground(Color.DARK_GRAY);

        panel.add(btnCargar);
        panel.add(lblTotal);
        return panel;
    }

    private JPanel crearPanelBusqueda() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        panel.setBorder(BorderFactory.createTitledBorder("Búsqueda"));


        JLabel lblColumna = new JLabel("Buscar por:");
        comboColumna = new JComboBox<>(COLUMNAS_COMBO);
        comboColumna.setPreferredSize(new Dimension(130, 28));


        campoBusqueda = new JTextField(25);
        campoBusqueda.setToolTipText("Puede ingresar solo una parte del valor");
        campoBusqueda.addActionListener(e -> ejecutarBusqueda());  

        btnBuscar = new JButton("🔍 Buscar");
        btnBuscar.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        btnBuscar.addActionListener(e -> ejecutarBusqueda());


        JLabel lblTiempoTitulo = new JLabel("  Tiempo:");
        lblTiempo = new JTextField("00:00:000", 9);
        lblTiempo.setEditable(false);
        lblTiempo.setHorizontalAlignment(JTextField.CENTER);
        lblTiempo.setFont(new Font(Font.MONOSPACED, Font.BOLD, 13));
        lblTiempo.setBackground(new Color(220, 255, 220));
        lblTiempo.setToolTipText("Tiempo total de ordenamiento + búsqueda");

        panel.add(lblColumna);
        panel.add(comboColumna);
        panel.add(campoBusqueda);
        panel.add(btnBuscar);
        panel.add(lblTiempoTitulo);
        panel.add(lblTiempo);

        return panel;
    }



    private JScrollPane crearPanelTabla() {
        modeloTabla = new DefaultTableModel(COLUMNAS_TABLA, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        tabla = new JTable(modeloTabla);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(20);
        tabla.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        tabla.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        tabla.setFillsViewportHeight(true);

      
        tabla.getColumnModel().getColumn(0).setPreferredWidth(55);  
        tabla.getColumnModel().getColumn(1).setPreferredWidth(280);  
        tabla.getColumnModel().getColumn(2).setPreferredWidth(120);  
        tabla.getColumnModel().getColumn(3).setPreferredWidth(250); 
        tabla.getColumnModel().getColumn(4).setPreferredWidth(100); 

        return new JScrollPane(tabla);
    }


    private JPanel crearBarraEstado() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        panel.setBorder(BorderFactory.createEtchedBorder());

        lblEstado = new JLabel("Listo. Cargue un archivo CSV para comenzar.");
        lblEstado.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));

        panel.add(lblEstado);
        return panel;
    }


    private void abrirSelectorArchivo() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Seleccionar archivo de predios");
        fc.setFileFilter(new FileNameExtensionFilter("Archivos CSV (*.csv)", "csv"));
        fc.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));

        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String ruta = fc.getSelectedFile().getAbsolutePath();
            lblTotal.setText(fc.getSelectedFile().getName());
            controlador.cargarArchivo(ruta);
        }
    }

    private void ejecutarBusqueda() {
        int    columna  = comboColumna.getSelectedIndex();
        String criterio = campoBusqueda.getText().trim();
        lblTiempo.setText("...");
        controlador.ejecutarBusqueda(columna, criterio);
    }




    public void mostrarResultados(List<Predio> predios) {
        modeloTabla.setRowCount(0);
        int fila = 1;
        for (Predio p : predios) {
            modeloTabla.addRow(new Object[]{
                    fila++,
                    p.getNpn(),
                    p.getMunicipio(),
                    p.getDireccion(),
                    p.getFicha()
            });
        }
    }


    public void limpiarResultados() {
        modeloTabla.setRowCount(0);
    }

  
    public void setTiempo(long milisegundos) {
        long min  = milisegundos / 60_000;
        long seg  = (milisegundos % 60_000) / 1_000;
        long msec = milisegundos % 1_000;
        lblTiempo.setText(String.format("%02d:%02d:%03d", min, seg, msec));
    }


    public void setTotalRegistros(int total) {
        lblTotal.setText(total + " registros cargados");
    }


    public void setEstado(String mensaje) {
        lblEstado.setText(mensaje);
    }

  
    public void setBloqueado(boolean bloqueado) {
        btnBuscar.setEnabled(!bloqueado);
        btnCargar.setEnabled(!bloqueado);
        comboColumna.setEnabled(!bloqueado);
        campoBusqueda.setEnabled(!bloqueado);
        setCursor(bloqueado
                ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
                : Cursor.getDefaultCursor());
    }

   
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
