package quetzal.reportes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.jfree.chart.JFreeChart;
import quetzal.modelo.PartidaHistorial;

/**
 * Genera el reporte del Top de Puntajes como un archivo HTML
 */
public final class GeneradorReporteHTML {

    private static final String CARPETA_REPORTES = "reportes";
    private static final String NOMBRE_IMAGEN = "grafica_top.png";
    private static final String NOMBRE_HTML = "reporte.html";

    private GeneradorReporteHTML() {
    }

    /** Genera la gráfica + el HTML y devuelve la ruta absoluta del HTML. */
    public static String generar(PartidaHistorial[] top, JFreeChart chart) throws IOException {
        File carpeta = new File(CARPETA_REPORTES);
        if (!carpeta.exists() && !carpeta.mkdirs()) {
            throw new IOException("No se pudo crear la carpeta: " + carpeta.getAbsolutePath());
        }

        File imagen = new File(carpeta, NOMBRE_IMAGEN);
        GeneradorGrafica.exportarComoImagen(chart, imagen, 700, 400);

        File html = new File(carpeta, NOMBRE_HTML);
        try (PrintWriter escritor = new PrintWriter(new FileWriter(html))) {
            escritor.println("<!DOCTYPE html>");
            escritor.println("<html lang=\"es\">");
            escritor.println("<head>");
            escritor.println("<meta charset=\"UTF-8\">");
            escritor.println("<title>Reporte Quetzal Space Defender</title>");
            escritor.println("<style>");
            escritor.println("body { font-family: Arial, sans-serif; margin: 30px; }");
            escritor.println("h1 { color: #1a3b5d; }");
            escritor.println("table { border-collapse: collapse; width: 100%; margin-top: 20px; }");
            escritor.println("th, td { border: 1px solid #999; padding: 8px; text-align: left; }");
            escritor.println("th { background-color: #1a3b5d; color: white; }");
            escritor.println("tr:nth-child(even) { background-color: #f2f2f2; }");
            escritor.println("img { margin-top: 20px; max-width: 100%; }");
            escritor.println("</style>");
            escritor.println("</head>");
            escritor.println("<body>");
            escritor.println("<h1>Quetzal Space Defender - Top de Puntajes</h1>");

            escritor.println("<table>");
            escritor.println("<tr><th>#</th><th>Piloto</th><th>Dificultad</th>"
                    + "<th>Puntaje</th><th>Fecha</th></tr>");
            for (int i = 0; i < top.length; i++) {
                PartidaHistorial partida = top[i];
                escritor.printf("<tr><td>%d</td><td>%s</td><td>%s</td><td>%d</td><td>%s</td></tr>%n",
                        i + 1,
                        partida.getPiloto().getNombre(),
                        partida.getPiloto().getDificultad(),
                        partida.getPuntaje(),
                        partida.getFecha());
            }
            escritor.println("</table>");

            escritor.println("<img src=\"" + NOMBRE_IMAGEN + "\" alt=\"Gráfica de puntajes\">");
            escritor.println("</body>");
            escritor.println("</html>");
        }

        return html.getAbsolutePath();
    }
}
