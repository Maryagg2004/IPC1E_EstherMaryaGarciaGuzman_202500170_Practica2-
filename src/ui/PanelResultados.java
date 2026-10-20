package quetzal.ui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import quetzal.modelo.Piloto;

/** Pantalla de resultados tras una derrota: puntaje final y opciones. */
public class PanelResultados extends JPanel {

    private final JLabel etiquetaResultado = new JLabel();
    private Piloto piloto;

    public PanelResultados(VentanaPrincipal ventana) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;

        JLabel titulo = new JLabel("¡Partida Finalizada!");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        gbc.gridy = 0;
        add(titulo, gbc);

        etiquetaResultado.setFont(new Font("SansSerif", Font.PLAIN, 18));
        gbc.gridy = 1;
        add(etiquetaResultado, gbc);

        JButton btnReintentar = new JButton("Jugar de nuevo");
        btnReintentar.addActionListener(e -> ventana.iniciarPartida(piloto));
        gbc.gridy = 2;
        add(btnReintentar, gbc);

        JButton btnTop = new JButton("Ver Top de Puntajes");
        btnTop.addActionListener(e -> ventana.mostrarPantalla(VentanaPrincipal.TOP_PUNTAJES));
        gbc.gridy = 3;
        add(btnTop, gbc);

        JButton btnMenu = new JButton("Menú Principal");
        btnMenu.addActionListener(e -> ventana.mostrarPantalla(VentanaPrincipal.MENU));
        gbc.gridy = 4;
        add(btnMenu, gbc);
    }

    public void setDatos(Piloto piloto, int puntaje) {
        this.piloto = piloto;
        etiquetaResultado.setText(
                "Piloto: " + piloto.getNombre() + "  |  Puntaje final: " + puntaje);
    }
}
