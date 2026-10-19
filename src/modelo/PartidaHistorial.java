package quetzal.modelo;

/** Registro de una partida terminada: quién la jugó, qué puntaje hizo y cuándo. */
public class PartidaHistorial {

    private final Piloto piloto;
    private final int puntaje;
    private final String fecha;

    public PartidaHistorial(Piloto piloto, int puntaje, String fecha) {
        this.piloto = piloto;
        this.puntaje = puntaje;
        this.fecha = fecha;
    }

    public Piloto getPiloto() {
        return piloto;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public String getFecha() {
        return fecha;
    }
}
