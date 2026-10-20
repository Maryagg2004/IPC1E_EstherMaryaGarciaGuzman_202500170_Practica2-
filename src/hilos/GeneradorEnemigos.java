package quetzal.hilos;

import java.util.Random;
import quetzal.juego.MotorJuego;
import quetzal.modelo.TipoPowerUp;
import quetzal.util.Constantes;

public class GeneradorEnemigos implements Runnable {

    private final MotorJuego motor;
    private final Random random = new Random();
    private volatile boolean activo = true;

    public GeneradorEnemigos(MotorJuego motor) {
        this.motor = motor;
    }

    public void detener() {
        activo = false;
    }

    @Override
    public void run() {
        while (activo) {
            try {
                long espera = Constantes.GENERADOR_ESPERA_MIN_MS
                        + random.nextInt((int) Constantes.GENERADOR_ESPERA_RANGO_MS);
                Thread.sleep(espera);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            int sorteo = random.nextInt(10);
            if (sorteo < 5) {
                motor.generarEnemigo();
            } else if (sorteo < 8) {
                motor.generarAsteroide();
            } else if (sorteo == 8) {
                motor.generarPowerUp(TipoPowerUp.CAPSULA_SUMINISTRO);
            } else {
                motor.generarPowerUp(TipoPowerUp.NUCLEO_ENERGIA);
            }
        }
    }
}
