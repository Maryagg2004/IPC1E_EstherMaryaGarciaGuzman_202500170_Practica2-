package quetzal.juego;

import quetzal.modelo.Acorazado;
import quetzal.modelo.CazaEstelar;
import quetzal.modelo.Dificultad;
import quetzal.modelo.Explorador;
import quetzal.modelo.Nave;

/** Crea la nave correspondiente a la dificultad elegida por el piloto. */
public final class FabricaNaves {

    private FabricaNaves() {
    }

    public static Nave crear(Dificultad dificultad) {
        switch (dificultad) {
            case FACIL:
                return new Explorador();
            case NORMAL:
                return new CazaEstelar();
            case DIFICIL:
                return new Acorazado();
            default:
                throw new IllegalArgumentException("Dificultad no soportada: " + dificultad);
        }
    }
}
