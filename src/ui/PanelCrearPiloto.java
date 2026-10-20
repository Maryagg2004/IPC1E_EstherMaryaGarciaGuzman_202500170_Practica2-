package quetzal.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import quetzal.modelo.Dificultad;
import quetzal.modelo.Piloto;
import quetzal.persistencia.Repositorio;

/** Formulario de registro de un piloto: nombre + modelo de nave/dificultad. */
public class PanelCrearPiloto extends JPanel {

    private final JTextField campoNombre = new JTextField(15);
    private final JRadioButton radioFacil = new JRadioButton(
            "Fácil - Explorador (veloz, disparo lento)");
    private final JRadioButton radioNormal = new JRadioButton(
            "Normal - Caza Estelar (equilibrada)", true);
    private final JRadioButton radioDificil = new JRadioButton(
            "Difícil - Acorazado (lenta, ráfagas rápidas)");

    public PanelCrearPiloto(VentanaPrincipal ventana) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        ButtonGroup grupo = new ButtonGroup();
        grupo.add(radioFacil);
        grupo.add(radioNormal);
        grupo.add(radioDificil);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(new JLabel("Crear Piloto"), gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        add(campoNombre, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(radioFacil, gbc);
        gbc.gridy = 3;
        add(radioNormal, gbc);
        gbc.gridy = 4;
        add(radioDificil, gbc);

        JButton btnRegistrar = new JButton("Registrar Piloto");
        btnRegistrar.addActionListener(e -> registrar(ventana));
        gbc.gridy = 5;
        add(btnRegistrar, gbc);

        JButton btnVolver = new JButton("Volver al Menú");
        btnVolver.addActionListener(e -> ventana.mostrarPantalla(VentanaPrincipal.MENU));
        gbc.gridy = 6;
        add(btnVolver, gbc);
    }

    private void registrar(VentanaPrincipal ventana) {
        String nombre = campoNombre.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa un nombre para el piloto.",
                    "Nombre requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Dificultad dificultad = Dificultad.NORMAL;
        if (radioFacil.isSelected()) {
            dificultad = Dificultad.FACIL;
        } else if (radioDificil.isSelected()) {
            dificultad = Dificultad.DIFICIL;
        }

        Repositorio.registrarPiloto(new Piloto(nombre, dificultad));
        JOptionPane.showMessageDialog(this, "Piloto \"" + nombre + "\" registrado.",
                "Piloto creado", JOptionPane.INFORMATION_MESSAGE);

        campoNombre.setText("");
        ventana.mostrarPantalla(VentanaPrincipal.MENU);
    }
}
