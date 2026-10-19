package quetzal.modelo;

import java.awt.Color;
import java.awt.Graphics2D;
import quetzal.util.Constantes;

/**
 * Obstáculo que no mata al jugador: al tocarlo activa el efecto de
 * ralentización.
 */
public class Asteroide extends EntidadJuego {

    public Asteroide(int x, int y) {
        super(x, y, Constantes.ASTEROIDE_DIAMETRO, Constantes.ASTEROIDE_DIAMETRO,
                Constantes.VELOCIDAD_ASTEROIDE_PX, -1);
    }

    @Override
    public void dibujar(Graphics2D g) {
        g.setColor(Color.GRAY);
        g.fillOval(x, y, ancho, alto);
        g.setColor(Color.DARK_GRAY);
        g.drawOval(x, y, ancho, alto);
    }
}
