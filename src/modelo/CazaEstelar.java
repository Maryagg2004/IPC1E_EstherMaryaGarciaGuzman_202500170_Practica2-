package quetzal.modelo;

import java.awt.Color;
import quetzal.util.Constantes;

/** Dificultad Normal: velocidad media, disparo medio (cadencia de 1 seg). */
public class CazaEstelar extends Nave {

    @Override
    public int getVelocidadPx() {
        return Constantes.VELOCIDAD_CAZA_ESTELAR_PX;
    }

    @Override
    public long getIntervaloDisparoMs() {
        return Constantes.INTERVALO_DISPARO_CAZA_ESTELAR_MS;
    }

    @Override
    public String getNombreModelo() {
        return "Caza Estelar";
    }

    @Override
    protected Color getColor() {
        return Color.ORANGE;
    }
}
