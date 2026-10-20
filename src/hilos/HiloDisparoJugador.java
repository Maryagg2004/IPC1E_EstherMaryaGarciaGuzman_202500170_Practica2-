package quetzal.hilos;

import quetzal.juego.MotorJuego;

/**
 * Hilo de vida larga (uno por partida) que controla la cadencia de disparo
 * del jugador: cada {@code getIntervaloDisparoMs()} de la nave actual,
 * dispara un proyectil si el jugador mantiene presionada la barra espaciadora.
 */
public class HiloDisparoJugador implements Runnable {

    private final MotorJuego motor;
    private volatile boolean activo = true;

    public HiloDisparoJugador(MotorJuego motor) {
        this.motor = motor;
    }

    public void detener() {
        activo = false;
    }

    @Override
    public void run() {
        while (activo) {
            if (motor.isDisparando()) {
                motor.dispararProyectilJugador();
            }
            try {
                Thread.sleep(motor.getNaveJugador().getIntervaloDisparoMs());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
