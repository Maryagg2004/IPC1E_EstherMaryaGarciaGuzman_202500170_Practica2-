package quetzal.util;

/**
 * Valores fijos de configuración del juego. Centralizarlos aquí evita
 * "números mágicos" repartidos en el resto del código y facilita ajustar
 * el balance del juego desde un solo lugar.
 */
public final class Constantes {

    private Constantes() {
        // Clase utilitaria: no debe instanciarse.
    }

    // Dimensiones del campo de batalla
    public static final int ANCHO_PANTALLA = 900;
    public static final int ALTO_PANTALLA = 600;

    // Franja horizontal permitida para la nave del jugador (lado izquierdo
    // de la pantalla, como corresponde a un side-scroller).
    public static final int NAVE_X_MIN = 20;
    public static final int NAVE_X_MAX = 300;
    public static final int NAVE_ANCHO = 50;
    public static final int NAVE_ALTO = 30;

    // Tamaños de las demás entidades del campo de batalla
    public static final int ENEMIGO_ANCHO = 45;
    public static final int ENEMIGO_ALTO = 30;
    public static final int ASTEROIDE_DIAMETRO = 40;
    public static final int POWERUP_DIAMETRO = 26;
    public static final int PROYECTIL_ANCHO = 14;
    public static final int PROYECTIL_ALTO = 4;

    // Velocidad en píxeles que avanza cada entidad en cada paso de su hilo
    public static final int VELOCIDAD_ENEMIGO_PX = 4;
    public static final int VELOCIDAD_ASTEROIDE_PX = 3;
    public static final int VELOCIDAD_POWERUP_PX = 2;
    public static final int VELOCIDAD_PROYECTIL_PX = 12;

    // Cadencia de los hilos de movimiento (cada cuánto se actualiza x,y)
    public static final long INTERVALO_MOVIMIENTO_MS = 20;

    // Cadencia del Timer de Swing que dibuja y detecta colisiones (EDT)
    public static final int INTERVALO_RENDER_MS = 30;

    // Rango de espera del generador de enemigos/asteroides/power-ups
    public static final long GENERADOR_ESPERA_MIN_MS = 800;
    public static final long GENERADOR_ESPERA_RANGO_MS = 700;

    // Capacidad fija de los arreglos de entidades vivas (límite intencional:
    // si no hay slot libre, el nuevo spawn simplemente se descarta)
    public static final int MAX_ENEMIGOS = 20;
    public static final int MAX_ASTEROIDES = 10;
    public static final int MAX_PROYECTILES = 50;
    public static final int MAX_POWERUPS = 5;

    // Reglas de combate
    public static final long RALENTIZACION_MS = 2000;
    public static final int TOP_N = 10;

    // Perfiles de dificultad / modelo de nave (velocidad de desplazamiento
    // por pulsación y cadencia de disparo en milisegundos)
    public static final int VELOCIDAD_EXPLORADOR_PX = 8;
    public static final long INTERVALO_DISPARO_EXPLORADOR_MS = 2000;

    public static final int VELOCIDAD_CAZA_ESTELAR_PX = 5;
    public static final long INTERVALO_DISPARO_CAZA_ESTELAR_MS = 1000;

    public static final int VELOCIDAD_ACORAZADO_PX = 3;
    public static final long INTERVALO_DISPARO_ACORAZADO_MS = 300;
}
