package quetzal.persistencia;

import java.util.Vector;
import quetzal.modelo.PartidaHistorial;
import quetzal.modelo.Piloto;
import quetzal.util.Ordenador;

/**
 * Guarda pilotos e historial de partidas únicamente en memoria.
 */
public final class Repositorio {

    private static final Vector<Piloto> pilotos = new Vector<>();
    private static final Vector<PartidaHistorial> historial = new Vector<>();

    private Repositorio() {
    }

    public static void registrarPiloto(Piloto piloto) {
        pilotos.add(piloto);
    }

    public static Vector<Piloto> obtenerPilotos() {
        return pilotos;
    }

    public static void registrarPartida(PartidaHistorial partida) {
        historial.add(partida);
    }

    public static Vector<PartidaHistorial> obtenerHistorial() {
        return historial;
    }

    /** Devuelve hasta  partidas con mejor puntaje, de mayor a menor. */
    public static PartidaHistorial[] obtenerTopN(int n) {
        PartidaHistorial[] todas = new PartidaHistorial[historial.size()];
        historial.copyInto(todas);
        Ordenador.ordenarPorPuntajeDescendente(todas);

        int cantidad = Math.min(n, todas.length);
        PartidaHistorial[] top = new PartidaHistorial[cantidad];
        System.arraycopy(todas, 0, top, 0, cantidad);
        return top;
    }
}
