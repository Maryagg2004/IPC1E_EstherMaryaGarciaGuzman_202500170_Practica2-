package quetzal.modelo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import quetzal.util.Constantes;

public abstract class Nave {

    protected volatile int x;
    protected volatile int y;
    protected final int ancho = Constantes.NAVE_ANCHO;
    protected final int alto = Constantes.NAVE_ALTO;

    // Instante (epoch millis) en el que termina el bloqueo por ralentización.
    // volatile porque lo escribe el hilo del EDT (tick) y lo lee también el
    // propio EDT al mover y pintar; se deja volatile por si en el futuro se
    // consulta desde otro hilo.
    private volatile long finRalentizacionMillis = 0L;

    protected Nave() {
        this.x = Constantes.NAVE_X_MIN;
        this.y = Constantes.ALTO_PANTALLA / 2 - alto / 2;
    }

    /** true si la nave sigue bloqueada por haber chocado con un asteroide. */
    public boolean estaRalentizado() {
        return System.currentTimeMillis() < finRalentizacionMillis;
    }

    public void activarRalentizacion(long milisegundos) {
        finRalentizacionMillis = System.currentTimeMillis() + milisegundos;
    }

    /**
     * Intenta desplazar la nave. Mientras esté ralentizada, el movimiento se
     * ignora por completo (bloqueo total), que es la lectura más simple de
     * "la bloquea por 2 segundos" del enunciado.
     */
    public void mover(int dx, int dy) {
        if (estaRalentizado()) {
            return;
        }
        int nuevoX = x + dx;
        int nuevoY = y + dy;
        if (nuevoX >= Constantes.NAVE_X_MIN && nuevoX <= Constantes.NAVE_X_MAX) {
            x = nuevoX;
        }
        if (nuevoY >= 0 && nuevoY <= Constantes.ALTO_PANTALLA - alto) {
            y = nuevoY;
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, ancho, alto);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getAncho() {
        return ancho;
    }

    public int getAlto() {
        return alto;
    }

    public void dibujar(Graphics2D g) {
        g.setColor(getColor());
        // Triángulo apuntando a la derecha: la nave del jugador dispara hacia allá.
        int[] xs = {x, x, x + ancho};
        int[] ys = {y, y + alto, y + alto / 2};
        g.fillPolygon(xs, ys, 3);
        if (estaRalentizado()) {
            g.setColor(Color.YELLOW);
            g.drawOval(x - 4, y - 4, ancho + 8, alto + 8);
        }
    }

    /** Píxeles que avanza la nave por cada pulsación de tecla procesada. */
    public abstract int getVelocidadPx();

    /** Cada cuántos milisegundos puede disparar un nuevo proyectil. */
    public abstract long getIntervaloDisparoMs();

    public abstract String getNombreModelo();

    protected abstract Color getColor();
}
