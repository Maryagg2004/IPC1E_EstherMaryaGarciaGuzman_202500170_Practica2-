package quetzal.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JPanel;
import javax.swing.Timer;
import quetzal.hilos.GeneradorEnemigos;
import quetzal.hilos.HiloDisparoJugador;
import quetzal.juego.FabricaNaves;
import quetzal.juego.MotorJuego;
import quetzal.modelo.Nave;
import quetzal.modelo.PartidaHistorial;
import quetzal.modelo.Piloto;
import quetzal.persistencia.Repositorio;
import quetzal.util.Constantes;

/**
 * Pantalla de juego: dibuja el campo de batalla, captura el teclado y arranca
 * los hilos de la partida (disparo del jugador y generador de enemigos). El
 * refresco visual y la detección de colisiones corren en un
 * {@code javax.swing.Timer}, que ya se ejecuta en el Event Dispatch Thread.
 */
public class PanelJuego extends JPanel implements KeyListener {

    private final VentanaPrincipal ventana;
    private final Piloto piloto;
    private final MotorJuego motor;
    private final Timer timer;

    private HiloDisparoJugador hiloDisparoRunnable;
    private GeneradorEnemigos generadorRunnable;
    private Thread hiloDisparo;
    private Thread hiloGenerador;

    public PanelJuego(VentanaPrincipal ventana, Piloto piloto) {
        this.ventana = ventana;
        this.piloto = piloto;

        Nave nave = FabricaNaves.crear(piloto.getDificultad());
        this.motor = new MotorJuego(nave);

        setPreferredSize(new Dimension(Constantes.ANCHO_PANTALLA, Constantes.ALTO_PANTALLA));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        timer = new Timer(Constantes.INTERVALO_RENDER_MS, e -> onTick());
    }

    /** Arranca los hilos de la partida y el temporizador de dibujo. */
    public void iniciar() {
        hiloDisparoRunnable = new HiloDisparoJugador(motor);
        generadorRunnable = new GeneradorEnemigos(motor);

        hiloDisparo = new Thread(hiloDisparoRunnable, "HiloDisparoJugador");
        hiloGenerador = new Thread(generadorRunnable, "GeneradorEnemigos");
        hiloDisparo.start();
        hiloGenerador.start();

        timer.start();
    }

    private void onTick() {
        motor.tick();
        if (!motor.isJugadorVivo()) {
            finalizarPartida();
            return;
        }
        repaint();
    }

    private void finalizarPartida() {
        timer.stop();
        hiloDisparoRunnable.detener();
        generadorRunnable.detener();
        hiloDisparo.interrupt();
        hiloGenerador.interrupt();
        motor.detenerTodasLasEntidades();

        String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
        Repositorio.registrarPartida(new PartidaHistorial(piloto, motor.getPuntaje(), fecha));

        ventana.mostrarResultados(piloto, motor.getPuntaje());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        motor.dibujar((Graphics2D) g);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                motor.setTeclaArriba(true);
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                motor.setTeclaAbajo(true);
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                motor.setTeclaIzquierda(true);
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                motor.setTeclaDerecha(true);
                break;
            case KeyEvent.VK_SPACE:
                motor.setDisparando(true);
                break;
            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                motor.setTeclaArriba(false);
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                motor.setTeclaAbajo(false);
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                motor.setTeclaIzquierda(false);
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                motor.setTeclaDerecha(false);
                break;
            case KeyEvent.VK_SPACE:
                motor.setDisparando(false);
                break;
            default:
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No se usa: el movimiento y disparo se manejan con pressed/released.
    }
}
