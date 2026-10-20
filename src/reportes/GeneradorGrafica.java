package quetzal.reportes;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import quetzal.modelo.PartidaHistorial;

/**
 * Construye la gráfica de barras del Top de Puntajes con JFreeChart. La
 * misma gráfica se reutiliza para mostrarla en pantalla (dentro de un
 * {@code ChartPanel}) y para exportarla a PNG en el reporte.
 */
public final class GeneradorGrafica {

    private GeneradorGrafica() {
    }

    public static JFreeChart crearGraficaTopPuntajes(PartidaHistorial[] top) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < top.length; i++) {
            PartidaHistorial partida = top[i];
            String categoria = (i + 1) + ". " + partida.getPiloto().getNombre();
            dataset.addValue(partida.getPuntaje(), "Puntaje", categoria);
        }

        return ChartFactory.createBarChart(
                "Top de Puntajes - Quetzal Space Defender",
                "Piloto",
                "Puntaje",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);
    }

    public static void exportarComoImagen(JFreeChart chart, File destino, int ancho, int alto)
            throws IOException {
        BufferedImage imagen = chart.createBufferedImage(ancho, alto);
        ImageIO.write(imagen, "png", destino);
    }
}
