package quetzal.modelo;

/** Dificultad elegida por el piloto; determina el modelo de nave que usará. */
public enum Dificultad {
    FACIL("Fácil - Explorador"),
    NORMAL("Normal - Caza Estelar"),
    DIFICIL("Difícil - Acorazado");

    private final String etiqueta;

    Dificultad(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
