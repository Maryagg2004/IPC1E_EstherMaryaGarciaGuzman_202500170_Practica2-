package quetzal.modelo;

import java.awt.Color;
import java.awt.Graphics2D;
import quetzal.util.Constantes;
/** Disparo del jugador: nace junto a la nave y avanza hacia la derecha. */
public class Proyectil extends EntidadJuego {

    public Proyectil(int x, int y) {
        super(x, y, Constantes.PROYECTIL_ANCHO, Constantes.PROYECTIL_ALTO,
                Constantes.VELOCIDAD_PROYECTIL_PX, +1);
    }

    @Override
    public void dibujar(Graphics2D g) {
        g.setColor(Color.YELLOW);
        g.fillRect(x, y, ancho, alto);
    }
}
