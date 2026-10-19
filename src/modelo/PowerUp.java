package quetzal.modelo;

import java.awt.Color;
import java.awt.Graphics2D;
import quetzal.util.Constantes;

/** Núcleo de Energía o Cápsula de Suministro: otorgan puntos al tocarlos. */
public class PowerUp extends EntidadJuego {

    private final TipoPowerUp tipo;

    public PowerUp(int x, int y, TipoPowerUp tipo) {
        super(x, y, Constantes.POWERUP_DIAMETRO, Constantes.POWERUP_DIAMETRO,
                Constantes.VELOCIDAD_POWERUP_PX, -1);
        this.tipo = tipo;
    }

    public TipoPowerUp getTipo() {
        return tipo;
    }

    @Override
    public void dibujar(Graphics2D g) {
        g.setColor(tipo == TipoPowerUp.NUCLEO_ENERGIA ? Color.CYAN : Color.GREEN);
        g.fillOval(x, y, ancho, alto);
        g.setColor(Color.BLACK);
        String letra = tipo == TipoPowerUp.NUCLEO_ENERGIA ? "N" : "C";
        g.drawString(letra, x + ancho / 2 - 4, y + alto / 2 + 5);
    }
}
