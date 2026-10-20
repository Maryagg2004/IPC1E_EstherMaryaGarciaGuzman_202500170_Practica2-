package quetzal.ui;

import java.awt.BorderLayout;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import quetzal.modelo.PartidaHistorial;
import quetzal.persistencia.Repositorio;
import quetzal.reportes.GeneradorGrafica;
import quetzal.reportes.GeneradorReporteHTML;
import quetzal.util.Constantes;

/** Muestra el historial ordenado (tabla + gráfica JFreeChart) y permite exportarlo. */
public class PanelTopPuntajes extends JPanel {

    private final DefaultTableModel modeloTabla =
            new DefaultTableModel(new Object[]{"#", "Piloto", "Dificultad", "Puntaje", "Fecha"}, 0);
    private final JPanel panelGrafica = new JPanel(new BorderLayout());

    public PanelTopPuntajes(VentanaPrincipal ventana) {
        setLayout(new BorderLayout(10, 10));

        JTable tabla = new JTable(modeloTabla);
        add(new JScrollPane(tabla), BorderLayout.WEST);
        add(panelGrafica, BorderLayout.CENTER);

        JButton btnExportar = new JButton("Exportar Reporte");
        btnExportar.addActionListener(e -> exportarReporte());

        JButton btnVolver = new JButton("Volver al Menú");
        btnVolver.addActionListener(e -> ventana.mostrarPantalla(VentanaPrincipal.MENU));

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnExportar);
        panelBotones.add(btnVolver);
        add(panelBotones, BorderLayout.SOUTH);
    }

    /** Se llama cada vez que se entra a esta pantalla, para mostrar datos frescos. */
    public void actualizar() {
        PartidaHistorial[] top = Repositorio.obtenerTopN(Constantes.TOP_N);

        modeloTabla.setRowCount(0);
        for (int i = 0; i < top.length; i++) {
            PartidaHistorial p = top[i];
            modeloTabla.addRow(new Object[]{
                i + 1, p.getPiloto().getNombre(), p.getPiloto().getDificultad(),
                p.getPuntaje(), p.getFecha()
            });
        }

        JFreeChart chart = GeneradorGrafica.crearGraficaTopPuntajes(top);
        panelGrafica.removeAll();
        panelGrafica.add(new ChartPanel(chart), BorderLayout.CENTER);
        panelGrafica.revalidate();
        panelGrafica.repaint();
    }

    private void exportarReporte() {
        PartidaHistorial[] top = Repositorio.obtenerTopN(Constantes.TOP_N);
        if (top.length == 0) {
            JOptionPane.showMessageDialog(this, "Todavía no hay partidas registradas.",
                    "Sin datos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            JFreeChart chart = GeneradorGrafica.crearGraficaTopPuntajes(top);
            String ruta = GeneradorReporteHTML.generar(top, chart);
            JOptionPane.showMessageDialog(this,
                    "Reporte generado en:\n" + ruta
                    + "\n\nÁbrelo en tu navegador y usa Imprimir > Guardar como PDF.",
                    "Reporte exportado", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al generar el reporte: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
