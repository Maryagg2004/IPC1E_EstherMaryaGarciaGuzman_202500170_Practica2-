package quetzal.modelo;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import quetzal.util.Constantes;
/**
 * Base de todo objeto del campo de batalla que se desplaza solo en el eje X
 * (enemigos, asteroides, power-ups y proyectiles). Cada instancia se mueve
 * dentro de su propio hilo (ver quetzal.hilos.HiloMovimiento), por eso los
 * campos que ese hilo escribe y que el hilo de dibujo (EDT) lee se marcan
 * como volatile: así el EDT siempre ve la última posición, sin necesidad de
 * un bloqueo por cada entidad.
 */
public abstract class EntidadJuego {

    protected volatile int x;
    protected volatile int y;
    protected final int ancho;
    protected final int alto;
    protected final int velocidadPx;
    protected final int direccion; // +1 se mueve a la derecha, -1 a la izquierda
    protected volatile boolean activo = true;

    protected EntidadJuego(int x, int y, int ancho, int alto, int velocidadPx, int direccion) {
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
        this.velocidadPx = velocidadPx;
        this.direccion = direccion;
    }

    /** Avanza un paso en el eje X; se desactiva sola al salir de la pantalla. */
    public void mover() {
        x += velocidadPx * direccion;
        if (x + ancho < 0 || x > Constantes.ANCHO_PANTALLA) {
            activo = false;
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

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    /** Dibuja la entidad en su posición actual. Cada subclase define su forma. */
    public abstract void dibujar(Graphics2D g);
}
