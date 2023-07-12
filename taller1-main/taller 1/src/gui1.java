import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class gui1 extends JFrame {

    private JPanel panel1;
    private JButton bAgregar;
    private JButton bModificar;
    private JButton bEliminar;
    private JTable tablaDatos;
    private JMenuBar menuBar;
    private JMenu Menu;
    private JMenuItem salir;
    private JMenuItem bArchivo;
    private File archivoSeleccionado;

    public gui1() {
        super("Tabla de Información:");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setContentPane(panel1);
        menuBar = new JMenuBar();
        Menu = new JMenu("Menu");
        salir = new JMenuItem("Salir");
        bArchivo = new JMenuItem("Buscar Archivo");

        menuBar.add(Menu);
        Menu.add(bArchivo);
        Menu.add(salir);

        setJMenuBar(menuBar);

        salir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                System.exit(0);
            }
        });

        bArchivo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                FileNameExtensionFilter filtro = new FileNameExtensionFilter("Archivos de Texto", "txt", "xlsx");
                fc.setFileFilter(filtro);
                fc.setCurrentDirectory(new File("."));
                int rsp = fc.showOpenDialog(null);
                if (rsp == JFileChooser.APPROVE_OPTION) {
                    archivoSeleccionado = fc.getSelectedFile();
                    leerArchivo(archivoSeleccionado);
                }
            }
        });

        bAgregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarNuevoValor();
            }
        });

        bEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarValorSeleccionado();
            }
        });

        bModificar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modificarValorSeleccionado();
            }
        });

        tablaDatos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaDatos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaDatos.getSelectedRow() != -1) {
                bEliminar.setEnabled(true);
                bModificar.setEnabled(true);
            } else {
                bEliminar.setEnabled(false);
                bModificar.setEnabled(false);
            }
        });
    }

    private void leerArchivo(File archivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea = br.readLine();
            if (linea != null && !linea.isEmpty()) {
                String[] titulos = linea.split(",");
                DefaultTableModel modelo = new DefaultTableModel(titulos, 0);

                while ((linea = br.readLine()) != null) {
                    String[] datos = linea.split(",");
                    modelo.addRow(datos);
                }

                tablaDatos.setModel(modelo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void agregarNuevoValor() {
        if (archivoSeleccionado == null) {
            JOptionPane.showMessageDialog(null, "No se ha seleccionado un archivo.");
            return;
        }

        String fecha = JOptionPane.showInputDialog(null, "Ingrese la fecha (dd/mm/aa):");
        String hora = JOptionPane.showInputDialog(null, "Ingrese la hora (hh:mm:ss):");
        String humedad = JOptionPane.showInputDialog(null, "Ingrese la humedad (%):");
        String luz = JOptionPane.showInputDialog(null, "Ingrese la luz:");

        String nuevoValor = fecha + "," + hora + "," + humedad + "%," + luz;

        DefaultTableModel modelo = (DefaultTableModel) tablaDatos.getModel();
        modelo.addRow(nuevoValor.split(","));

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoSeleccionado, true))) {
            bw.newLine();
            bw.write(nuevoValor);
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void eliminarValorSeleccionado() {
        if (archivoSeleccionado == null) {
            return;
        }

        int selectedRow = tablaDatos.getSelectedRow();
        if (selectedRow != -1) {
            DefaultTableModel modelo = (DefaultTableModel) tablaDatos.getModel();
            modelo.removeRow(selectedRow);

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoSeleccionado))) {
                for (int row = 0; row < modelo.getRowCount(); row++) {
                    StringBuilder fila = new StringBuilder();
                    for (int col = 0; col < modelo.getColumnCount(); col++) {
                        Object value = modelo.getValueAt(row, col);
                        if (value != null) {
                            fila.append(value);
                        }
                        if (col < modelo.getColumnCount() - 1) {
                            fila.append(",");
                        }
                    }
                    bw.write(fila.toString());
                    bw.newLine();
                }
                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Selecciona una fila para eliminar.");
        }
    }



    private void modificarValorSeleccionado() {
        if (archivoSeleccionado == null) {
            return;
        }

        int filaS = tablaDatos.getSelectedRow();
        if (filaS != -1) {
            DefaultTableModel modelo = (DefaultTableModel) tablaDatos.getModel();
            int modeloFila = tablaDatos.convertRowIndexToModel(filaS);

            String fecha = JOptionPane.showInputDialog(null, "Ingrese la fecha (dd/mm/aa):", modelo.getValueAt(modeloFila, 0));
            String hora = JOptionPane.showInputDialog(null, "Ingrese la hora (hh:mm:ss):", modelo.getValueAt(modeloFila, 1));
            String humedad = JOptionPane.showInputDialog(null, "Ingrese la humedad (%):", modelo.getValueAt(modeloFila, 2));
            String luz = JOptionPane.showInputDialog(null, "Ingrese la luz:", modelo.getValueAt(modeloFila, 3));

            String nuevoValor = fecha + "," + hora + "," + humedad + "%," + luz;

            modelo.setValueAt(fecha, modeloFila, 0);
            modelo.setValueAt(hora, modeloFila, 1);
            modelo.setValueAt(humedad, modeloFila, 2);
            modelo.setValueAt(luz, modeloFila, 3);

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoSeleccionado))) {
                for (int row = 0; row < modelo.getRowCount(); row++) {
                    StringBuilder fila = new StringBuilder();
                    boolean rowHasData = false;
                    for (int col = 0; col < modelo.getColumnCount(); col++) {
                        Object value = modelo.getValueAt(row, col);
                        if (value != null) {
                            fila.append(value);
                            rowHasData = true;
                        }
                        if (col < modelo.getColumnCount() - 1) {
                            fila.append(",");
                        }
                    }
                    if (rowHasData) {
                        bw.write(fila.toString());
                        bw.newLine();
                    }
                }
                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Seleccione la fila que desea modificar.");
        }
    }
}

