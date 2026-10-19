package quetzal.modelo;

/**
 * Tipos de recompensa que puede recoger el jugador. Los puntos que otorga
 * cada tipo viven aquí porque son una propiedad propia del tipo de power-up.
 */
public enum TipoPowerUp {

    NUCLEO_ENERGIA(150),
    CAPSULA_SUMINISTRO(10);

    private final int puntos;

    TipoPowerUp(int puntos) {
        this.puntos = puntos;
    }

    public int getPuntos() {
        return puntos;
    }
}
