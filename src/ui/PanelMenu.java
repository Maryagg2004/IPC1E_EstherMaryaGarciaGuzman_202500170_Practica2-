package quetzal.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import quetzal.modelo.Piloto;
import quetzal.persistencia.Repositorio;

/** Menú principal: Jugar, Crear Piloto, Top de Puntajes y Salir. */
public class PanelMenu extends JPanel {

    public PanelMenu(VentanaPrincipal ventana) {
        setLayout(new GridBagLayout());
        setBackground(Color.BLACK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;

        JLabel titulo = new JLabel("Quetzal Space Defender");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        titulo.setForeground(Color.CYAN);
        gbc.gridy = 0;
        add(titulo, gbc);

        JButton btnJugar = new JButton("Jugar");
        btnJugar.addActionListener(e -> elegirPilotoYJugar(ventana));
        gbc.gridy = 1;
        add(btnJugar, gbc);

        JButton btnCrearPiloto = new JButton("Crear Piloto");
        btnCrearPiloto.addActionListener(e -> ventana.mostrarPantalla(VentanaPrincipal.CREAR_PILOTO));
        gbc.gridy = 2;
        add(btnCrearPiloto, gbc);

        JButton btnTopPuntajes = new JButton("Top de Puntajes");
        btnTopPuntajes.addActionListener(e -> ventana.mostrarPantalla(VentanaPrincipal.TOP_PUNTAJES));
        gbc.gridy = 3;
        add(btnTopPuntajes, gbc);

        JButton btnSalir = new JButton("Salir");
        btnSalir.addActionListener(e -> System.exit(0));
        gbc.gridy = 4;
        add(btnSalir, gbc);
    }

    private void elegirPilotoYJugar(VentanaPrincipal ventana) {
        Vector<Piloto> pilotos = Repositorio.obtenerPilotos();
        if (pilotos.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Primero crea un piloto en \"Crear Piloto\".",
                    "Sin pilotos registrados", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Piloto[] opciones = new Piloto[pilotos.size()];
        pilotos.copyInto(opciones);

        Piloto seleccionado = (Piloto) JOptionPane.showInputDialog(
                this,
                "Selecciona el piloto con el que vas a volar:",
                "Elegir piloto",
                JOptionPane.PLAIN_MESSAGE,
                null,
                opciones,
                opciones[0]);

        if (seleccionado != null) {
            ventana.iniciarPartida(seleccionado);
        }
    }
}
