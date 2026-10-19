package quetzal.util;
import quetzal.modelo.PartidaHistorial;

/**
 * Ordenamiento manual sobre arreglos (a propósito, sin Collections.sort ni
 * Comparator): este curso prohíbe las colecciones dinámicas del framework,
 * y el ordenamiento por selección es el más sencillo de explicar en voz alta.
 * organiza las partidas de mayor a menor puntaje.
 */
public final class Ordenador {

    private Ordenador() {
    }

    /** Ordena el arreglo de mayor a menor puntaje, en el propio arreglo. */
    public static void ordenarPorPuntajeDescendente(PartidaHistorial[] partidas) {
        for (int i = 0; i < partidas.length - 1; i++) {
            int indiceMayor = i;
            for (int j = i + 1; j < partidas.length; j++) {
                if (partidas[j].getPuntaje() > partidas[indiceMayor].getPuntaje()) {
                    indiceMayor = j;
                }
            }
            if (indiceMayor != i) {
                PartidaHistorial temp = partidas[i];
                partidas[i] = partidas[indiceMayor];
                partidas[indiceMayor] = temp;
            }
        }
    }
}
