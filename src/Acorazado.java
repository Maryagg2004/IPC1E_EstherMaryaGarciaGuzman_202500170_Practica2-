package quetzal.modelo;

import java.awt.Color;
import quetzal.util.Constantes;

/** Dificultad Difícil: velocidad baja (nave pesada), ráfagas cada 0.3 seg. */
public class Acorazado extends Nave {

    @Override
    public int getVelocidadPx() {
        return Constantes.VELOCIDAD_ACORAZADO_PX;
    }

    @Override
    public long getIntervaloDisparoMs() {
        return Constantes.INTERVALO_DISPARO_ACORAZADO_MS;
    }

    @Override
    public String getNombreModelo() {
        return "Acorazado";
    }

    @Override
    protected Color getColor() {
        return Color.MAGENTA;
    }
}
