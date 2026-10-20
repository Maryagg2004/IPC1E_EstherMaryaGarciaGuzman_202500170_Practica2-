package quetzal.hilos;

import quetzal.modelo.EntidadJuego;

/**
 * Hilo genérico que mueve UNA entidad del campo de batalla (un enemigo, un
 * asteroide, un power-up o un proyectil) hasta que esta se desactiva sola
 * (sale de la pantalla o choca con algo). Es la clase que cumple el
 * requisito de "cada proyectil y cada enemigo en pantalla debe ser un hilo
 * independiente que actualice su posición (x, y)".
 */
public class HiloMovimiento implements Runnable {

    private final EntidadJuego entidad;
    private final long intervaloMs;

    public HiloMovimiento(EntidadJuego entidad, long intervaloMs) {
        this.entidad = entidad;
        this.intervaloMs = intervaloMs;
    }

    @Override
    public void run() {
        while (entidad.isActivo()) {
            entidad.mover();
            try {
                Thread.sleep(intervaloMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
