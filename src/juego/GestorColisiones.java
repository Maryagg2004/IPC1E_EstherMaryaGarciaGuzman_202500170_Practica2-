package quetzal.juego;

import java.awt.Rectangle;

/** Detección de colisiones: rectángulos simples (AABB), sin pixel-perfect. */
public final class GestorColisiones {

    private GestorColisiones() {
    }

    public static boolean colisionan(Rectangle a, Rectangle b) {
        return a.intersects(b);
    }
}
