package quetzal.ui;

import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import quetzal.modelo.Piloto;
import quetzal.util.Constantes;

/**
 * Ventana única de la aplicación. Usa {@link CardLayout} para alternar entre
 * las 5 pantallas del juego sin abrir ventanas nuevas.
 */
public class VentanaPrincipal extends JFrame {

    public static final String MENU = "MENU";
    public static final String CREAR_PILOTO = "CREAR_PILOTO";
    public static final String JUEGO = "JUEGO";
    public static final String TOP_PUNTAJES = "TOP_PUNTAJES";
    public static final String RESULTADOS = "RESULTADOS";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contenedor = new JPanel(cardLayout);

    private final PanelTopPuntajes panelTopPuntajes;
    private final PanelResultados panelResultados;
    private PanelJuego panelJuego;

    public VentanaPrincipal() {
        super("Quetzal Space Defender - Side Scroller");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        PanelMenu panelMenu = new PanelMenu(this);
        PanelCrearPiloto panelCrearPiloto = new PanelCrearPiloto(this);
        panelTopPuntajes = new PanelTopPuntajes(this);
        panelResultados = new PanelResultados(this);

        contenedor.add(panelMenu, MENU);
        contenedor.add(panelCrearPiloto, CREAR_PILOTO);
        contenedor.add(panelTopPuntajes, TOP_PUNTAJES);
        contenedor.add(panelResultados, RESULTADOS);

        // Reserva desde el inicio el tamaño que necesita el campo de batalla
        // (aunque el PanelJuego real se crea más tarde, al iniciar la primera
        // partida), para que ninguna pantalla se vea recortada.
        contenedor.setPreferredSize(
                new Dimension(Constantes.ANCHO_PANTALLA, Constantes.ALTO_PANTALLA + 40));

        add(contenedor);
        pack();
        setLocationRelativeTo(null);
    }

    public void mostrarPantalla(String nombre) {
        if (TOP_PUNTAJES.equals(nombre)) {
            panelTopPuntajes.actualizar();
        }
        cardLayout.show(contenedor, nombre);
    }

    /** Crea una partida nueva (un PanelJuego nuevo) para el piloto dado. */
    public void iniciarPartida(Piloto piloto) {
        if (panelJuego != null) {
            contenedor.remove(panelJuego);
        }
        panelJuego = new PanelJuego(this, piloto);
        contenedor.add(panelJuego, JUEGO);
        // El panel de juego es más grande que las demás pantallas; sin este
        // pack() la ventana se queda con el tamaño del menú y recorta el
        // campo de batalla.
        pack();
        setLocationRelativeTo(null);
        mostrarPantalla(JUEGO);
        panelJuego.requestFocusInWindow();
        panelJuego.iniciar();
    }

    public void mostrarResultados(Piloto piloto, int puntaje) {
        panelResultados.setDatos(piloto, puntaje);
        mostrarPantalla(RESULTADOS);
    }
}
