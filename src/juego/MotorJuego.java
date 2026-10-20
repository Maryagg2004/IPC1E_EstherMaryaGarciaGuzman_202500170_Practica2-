package quetzal.juego;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Random;
import quetzal.hilos.HiloMovimiento;
import quetzal.modelo.Asteroide;
import quetzal.modelo.Enemigo;
import quetzal.modelo.EntidadJuego;
import quetzal.modelo.Nave;
import quetzal.modelo.PowerUp;
import quetzal.modelo.Proyectil;
import quetzal.modelo.TipoPowerUp;
import quetzal.util.Constantes;

/**
 * Motor de una partida: guarda los arreglos de entidades vivas, el puntaje y
 * la nave del jugador, y centraliza las reglas de colisión.
 *
 * <p>El movimiento de enemigos, asteroides, power-ups y proyectiles ocurre en
 * sus propios hilos ({@link HiloMovimiento}), que ya están corriendo en
 * paralelo. {@link #tick()} NO los mueve: solo mueve a la nave del jugador
 * (que no tiene hilo propio), revisa colisiones y aplica las reglas del
 * juego. {@code tick()} lo llama un {@code javax.swing.Timer}, es decir,
 * siempre se ejecuta en el Event Dispatch Thread (EDT).
 *
 * <p>Los métodos que insertan entidades nuevas en los arreglos se llaman
 * desde el hilo {@code GeneradorEnemigos} o {@code HiloDisparoJugador}, por
 * lo que se marcan {@code synchronized} junto con {@code tick()}: es la
 * única sección crítica real de todo el juego (un hilo inserta, el EDT
 * recorre), y un único candado (el propio objeto {@code MotorJuego}) basta
 * para evitarla.
 */
public class MotorJuego {

    private final Nave naveJugador;
    private final Random random = new Random();

    private final Enemigo[] enemigos = new Enemigo[Constantes.MAX_ENEMIGOS];
    private final Asteroide[] asteroides = new Asteroide[Constantes.MAX_ASTEROIDES];
    private final Proyectil[] proyectiles = new Proyectil[Constantes.MAX_PROYECTILES];
    private final PowerUp[] powerUps = new PowerUp[Constantes.MAX_POWERUPS];

    private volatile int puntaje = 0;
    private volatile boolean jugadorVivo = true;
    private volatile boolean disparando = false;

    private volatile boolean teclaArriba;
    private volatile boolean teclaAbajo;
    private volatile boolean teclaIzquierda;
    private volatile boolean teclaDerecha;

    public MotorJuego(Nave naveJugador) {
        this.naveJugador = naveJugador;
    }

    // ---- Control desde el teclado (PanelJuego) ----------------------------

    public void setTeclaArriba(boolean v) {
        teclaArriba = v;
    }

    public void setTeclaAbajo(boolean v) {
        teclaAbajo = v;
    }

    public void setTeclaIzquierda(boolean v) {
        teclaIzquierda = v;
    }

    public void setTeclaDerecha(boolean v) {
        teclaDerecha = v;
    }

    public void setDisparando(boolean v) {
        disparando = v;
    }

    public boolean isDisparando() {
        return disparando;
    }

    public Nave getNaveJugador() {
        return naveJugador;
    }

    public boolean isJugadorVivo() {
        return jugadorVivo;
    }

    public int getPuntaje() {
        return puntaje;
    }

    // ---- Bucle principal ---------------------------------------------------

    /** Se llama ~30 veces por segundo desde el Timer de Swing (EDT). */
    public synchronized void tick() {
        if (!jugadorVivo) {
            return;
        }
        moverJugador();
        detectarColisionesProyectiles();
        detectarColisionesJugador();
    }

    private void moverJugador() {
        int velocidad = naveJugador.getVelocidadPx();
        int dx = 0;
        int dy = 0;
        if (teclaArriba) {
            dy -= velocidad;
        }
        if (teclaAbajo) {
            dy += velocidad;
        }
        if (teclaIzquierda) {
            dx -= velocidad;
        }
        if (teclaDerecha) {
            dx += velocidad;
        }
        if (dx != 0 || dy != 0) {
            naveJugador.mover(dx, dy);
        }
    }

    private void detectarColisionesProyectiles() {
        for (Proyectil p : proyectiles) {
            if (p == null || !p.isActivo()) {
                continue;
            }
            for (Enemigo enemigo : enemigos) {
                if (enemigo == null || !enemigo.isActivo()) {
                    continue;
                }
                if (GestorColisiones.colisionan(p.getBounds(), enemigo.getBounds())) {
                    p.setActivo(false);
                    enemigo.setActivo(false);
                    puntaje += enemigo.getPuntosAlDestruir();
                }
            }
        }
    }

    /**
     * Regla de derrota: chocar con un enemigo siempre mata, a menos que la
     * nave ya esté bajo el efecto de ralentización (periodo de gracia).
     * Chocar con un asteroide activa ese efecto por 2 segundos en vez de
     * matar. Mientras el efecto está activo, más choques con asteroides u
     * otros enemigos solo los desactivan a ellos, sin volver a matar ni
     * reiniciar el temporizador de ralentización.
     */
    private void detectarColisionesJugador() {
        Rectangle boundsJugador = naveJugador.getBounds();
        boolean yaRalentizado = naveJugador.estaRalentizado();

        for (Enemigo enemigo : enemigos) {
            if (enemigo == null || !enemigo.isActivo()) {
                continue;
            }
            if (GestorColisiones.colisionan(boundsJugador, enemigo.getBounds())) {
                if (!yaRalentizado) {
                    jugadorVivo = false;
                    return;
                }
                enemigo.setActivo(false);
            }
        }

        for (Asteroide asteroide : asteroides) {
            if (asteroide == null || !asteroide.isActivo()) {
                continue;
            }
            if (GestorColisiones.colisionan(boundsJugador, asteroide.getBounds())) {
                if (!yaRalentizado) {
                    naveJugador.activarRalentizacion(Constantes.RALENTIZACION_MS);
                }
                asteroide.setActivo(false);
            }
        }

        for (PowerUp powerUp : powerUps) {
            if (powerUp == null || !powerUp.isActivo()) {
                continue;
            }
            if (GestorColisiones.colisionan(boundsJugador, powerUp.getBounds())) {
                aplicarPowerUp(powerUp);
                powerUp.setActivo(false);
            }
        }
    }

    private void aplicarPowerUp(PowerUp powerUp) {
        puntaje += powerUp.getTipo().getPuntos();
        if (powerUp.getTipo() == TipoPowerUp.NUCLEO_ENERGIA) {
            for (Enemigo enemigo : enemigos) {
                if (enemigo != null) {
                    enemigo.setActivo(false);
                }
            }
        }
    }

    // ---- Generación de entidades (llamado desde los hilos de fondo) -------

    public synchronized void generarEnemigo() {
        int slot = buscarSlotLibre(enemigos);
        if (slot == -1) {
            return;
        }
        int y = random.nextInt(Constantes.ALTO_PANTALLA - Constantes.ENEMIGO_ALTO);
        Enemigo enemigo = new Enemigo(Constantes.ANCHO_PANTALLA, y);
        enemigos[slot] = enemigo;
        lanzarHiloMovimiento(enemigo);
    }

    public synchronized void generarAsteroide() {
        int slot = buscarSlotLibre(asteroides);
        if (slot == -1) {
            return;
        }
        int y = random.nextInt(Constantes.ALTO_PANTALLA - Constantes.ASTEROIDE_DIAMETRO);
        Asteroide asteroide = new Asteroide(Constantes.ANCHO_PANTALLA, y);
        asteroides[slot] = asteroide;
        lanzarHiloMovimiento(asteroide);
    }

    public synchronized void generarPowerUp(TipoPowerUp tipo) {
        int slot = buscarSlotLibre(powerUps);
        if (slot == -1) {
            return;
        }
        int y = random.nextInt(Constantes.ALTO_PANTALLA - Constantes.POWERUP_DIAMETRO);
        PowerUp powerUp = new PowerUp(Constantes.ANCHO_PANTALLA, y, tipo);
        powerUps[slot] = powerUp;
        lanzarHiloMovimiento(powerUp);
    }

    public synchronized void dispararProyectilJugador() {
        if (!jugadorVivo) {
            return;
        }
        int slot = buscarSlotLibre(proyectiles);
        if (slot == -1) {
            return;
        }
        int x = naveJugador.getX() + naveJugador.getAncho();
        int y = naveJugador.getY() + naveJugador.getAlto() / 2;
        Proyectil proyectil = new Proyectil(x, y);
        proyectiles[slot] = proyectil;
        lanzarHiloMovimiento(proyectil);
    }

    private void lanzarHiloMovimiento(EntidadJuego entidad) {
        new Thread(new HiloMovimiento(entidad, Constantes.INTERVALO_MOVIMIENTO_MS)).start();
    }

    /** Un slot está libre si nunca se usó o si su entidad ya no está activa. */
    private int buscarSlotLibre(EntidadJuego[] lista) {
        for (int i = 0; i < lista.length; i++) {
            if (lista[i] == null || !lista[i].isActivo()) {
                return i;
            }
        }
        return -1;
    }

    /** Marca todo como inactivo para que cada HiloMovimiento termine solo. */
    public synchronized void detenerTodasLasEntidades() {
        desactivarTodas(enemigos);
        desactivarTodas(asteroides);
        desactivarTodas(proyectiles);
        desactivarTodas(powerUps);
    }

    private void desactivarTodas(EntidadJuego[] lista) {
        for (EntidadJuego e : lista) {
            if (e != null) {
                e.setActivo(false);
            }
        }
    }

    // ---- Dibujo (llamado desde PanelJuego.paintComponent, en el EDT) ------

    public void dibujar(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.drawString("Puntaje: " + puntaje, 10, 20);
        g.drawString("Nave: " + naveJugador.getNombreModelo(), 10, 38);
        if (naveJugador.estaRalentizado()) {
            g.setColor(Color.YELLOW);
            g.drawString("¡RALENTIZADO!", Constantes.ANCHO_PANTALLA / 2 - 50, 20);
        }

        naveJugador.dibujar(g);
        dibujarLista(enemigos, g);
        dibujarLista(asteroides, g);
        dibujarLista(proyectiles, g);
        dibujarLista(powerUps, g);
    }

    private void dibujarLista(EntidadJuego[] lista, Graphics2D g) {
        for (EntidadJuego e : lista) {
            if (e != null && e.isActivo()) {
                e.dibujar(g);
            }
        }
    }
}
