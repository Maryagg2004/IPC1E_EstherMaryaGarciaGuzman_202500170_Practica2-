package quetzal.modelo;

import java.awt.Color;
import quetzal.util.Constantes;

/** Dificultad Fácil: alta velocidad, disparo lento (recarga cada 2 seg). */
public class Explorador extends Nave {

    @Override
    public int getVelocidadPx() {
        return Constantes.VELOCIDAD_EXPLORADOR_PX;
    }

    @Override
    public long getIntervaloDisparoMs() {
        return Constantes.INTERVALO_DISPARO_EXPLORADOR_MS;
    }

    @Override
    public String getNombreModelo() {
        return "Explorador";
    }

    @Override
    protected Color getColor() {
        return Color.CYAN;
    }
}
