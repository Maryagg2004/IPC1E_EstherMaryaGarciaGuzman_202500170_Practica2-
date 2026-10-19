package quetzal.modelo;
/** Piloto registrado por el jugador: un nombre y la dificultad que eligió. */
public class Piloto {

    private final String nombre;
    private final Dificultad dificultad;

    public Piloto(String nombre, Dificultad dificultad) {
        this.nombre = nombre;
        this.dificultad = dificultad;
    }

    public String getNombre() {
        return nombre;
    }

    public Dificultad getDificultad() {
        return dificultad;
    }

    @Override
    public String toString() {
        return nombre + " (" + dificultad + ")";
    }
}
