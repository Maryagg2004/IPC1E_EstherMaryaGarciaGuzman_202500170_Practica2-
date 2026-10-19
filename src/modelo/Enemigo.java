package quetzal.modelo;

import java.awt.Color;
import java.awt.Graphics2D;
import quetzal.util.Constantes;

/** Nave enemiga que aparece en el borde derecho y avanza hacia la izquierda. */
public class Enemigo extends EntidadJuego {

    private static final int PUNTOS_AL_DESTRUIR = 25;

    public Enemigo(int x, int y) {
        super(x, y, Constantes.ENEMIGO_ANCHO, Constantes.ENEMIGO_ALTO,
                Constantes.VELOCIDAD_ENEMIGO_PX, -1);
    }

    public int getPuntosAlDestruir() {
        return PUNTOS_AL_DESTRUIR;
    }

    @Override
    public void dibujar(Graphics2D g) {
        g.setColor(Color.RED);
        // Triángulo apuntando a la izquierda (hacia donde avanza el enemigo).
        int[] xs = {x + ancho, x + ancho, x};
        int[] ys = {y, y + alto, y + alto / 2};
        g.fillPolygon(xs, ys, 3);
    }
}
