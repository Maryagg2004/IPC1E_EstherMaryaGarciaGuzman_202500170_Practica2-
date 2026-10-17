# Manual Técnico - Quetzal Space Defender

## 1. Resumen

Simulador de combate espacial de desplazamiento horizontal (side-scroller),
hecho en Java con interfaz gráfica Swing. El jugador elige un piloto (que
determina el modelo de nave y por lo tanto la dificultad), enfrenta oleadas de
enemigos, asteroides y power-ups generadas dinámicamente, y al perder puede
consultar su historial de partidas ordenado, visualizarlo con una gráfica
(JFreeChart) y exportarlo como un reporte HTML.

## 2. Librerías utilizadas

| Librería                                                              | Uso                                                                                      |
| --------------------------------------------------------------------- | ---------------------------------------------------------------------------------------- |
| `javax.swing.*`, `java.awt.*`                                         | Toda la interfaz gráfica (ventanas, paneles, botones, dibujo del campo de batalla)       |
| `java.lang.Thread` / `Runnable`                                       | Movimiento independiente de cada entidad, disparo del jugador, generador de enemigos     |
| `java.util.Vector`                                                    | Única estructura dinámica permitida; almacena pilotos e historial de partidas en memoria |
| `org.jfree.chart.*` (JFreeChart 1.5.4, en `lib/jfreechart-1.5.4.jar`) | Gráfica de barras del Top de Puntajes, en pantalla y exportada a PNG                     |
| `java.io.*` (`File`, `FileWriter`, `PrintWriter`)                     | Generación del reporte HTML (único lugar del proyecto que escribe en disco)              |
| `javax.imageio.ImageIO`                                               | Exportar la gráfica de JFreeChart como imagen PNG embebida en el reporte                 |

No se usa `ArrayList`, `LinkedList`, `List`, `HashMap` ni ninguna otra
colección del framework de colecciones de Java, ni archivos/bases de datos
para guardar el estado del juego (pilotos e historial viven solo en memoria,
en los `Vector` de `Repositorio`).

## 3. Estructura del proyecto

```
src/quetzal/
├── Main.java                 Punto de entrada (arranca VentanaPrincipal)
├── modelo/                   Datos puros: sin Swing, sin hilos
│   ├── EntidadJuego.java       Base de enemigos/asteroides/power-ups/proyectiles
│   ├── Enemigo.java, Asteroide.java, PowerUp.java, Proyectil.java
│   ├── TipoPowerUp.java         enum NUCLEO_ENERGIA, CAPSULA_SUMINISTRO
│   ├── Nave.java                Base de la nave del jugador (sin hilo propio)
│   ├── Explorador.java, CazaEstelar.java, Acorazado.java   (perfiles de dificultad)
│   ├── Dificultad.java, Piloto.java, PartidaHistorial.java
├── hilos/                     Runnables: SOLO mueven o generan, nada de UI
│   ├── HiloMovimiento.java      mueve una entidad hasta que se desactiva
│   ├── HiloDisparoJugador.java  respeta la cadencia de disparo de la nave
│   └── GeneradorEnemigos.java   crea enemigos/asteroides/power-ups al azar
├── juego/                     El motor: arrays compartidos, reglas, colisiones
│   ├── MotorJuego.java
│   ├── GestorColisiones.java
│   └── FabricaNaves.java
├── ui/                        Swing: CardLayout con 5 pantallas
│   ├── VentanaPrincipal.java, PanelMenu.java, PanelCrearPiloto.java
│   ├── PanelJuego.java, PanelTopPuntajes.java, PanelResultados.java
├── persistencia/
│   └── Repositorio.java       Vector<Piloto>, Vector<PartidaHistorial>
├── reportes/
│   ├── GeneradorGrafica.java    construye y exporta el JFreeChart
│   └── GeneradorReporteHTML.java escribe reportes/reporte.html con java.io
└── util/
    ├── Constantes.java         toda la configuración numérica del juego
    └── Ordenador.java          ordenamiento manual (selección) sobre arreglos
```

**Regla de dependencia entre capas** (para defenderla oralmente en una frase):
`modelo` no sabe que existe Swing; `ui` nunca mueve objetos directamente, solo
lee el modelo para pintar; `hilos` solo tocan los campos de una entidad;
`juego` es el único que coordina arrays, colisiones y el temporizador.

## 4. Lógica de hilos

Solo 3 clases implementan `Runnable`:

- **`HiloMovimiento`**: genérico, uno por cada entidad viva (cada proyectil del
  jugador y cada enemigo/asteroide/power-up en pantalla). En su `run()`, hace
  `while (entidad.isActivo()) { entidad.mover(); Thread.sleep(intervalo); }`.
  Cumple literalmente el requisito del enunciado: *"cada proyectil disparado
  por el jugador y cada enemigo en pantalla debe ser un hilo independiente que
  actualice su posición (x, y)"*. Se extendió el mismo patrón a asteroides y
  power-ups por consistencia (todo lo que se desplaza solo usa el mismo hilo
  genérico).
- **`HiloDisparoJugador`**: un hilo de vida larga por partida. Cada
  `nave.getIntervaloDisparoMs()` (2000/1000/300 ms según el modelo de nave),
  dispara un proyectil si el jugador mantiene presionada la barra espaciadora.
- **`GeneradorEnemigos`**: un hilo de vida larga por partida. Duerme un
  intervalo aleatorio (800-1500 ms) y crea un enemigo, asteroide o power-up en
  el borde derecho de la pantalla (distribución aproximada: 50% enemigos, 30%
  asteroides, 10% cápsulas, 10% núcleos de energía), lanzando además el
  `HiloMovimiento` de esa nueva entidad.

La nave del jugador **no** tiene hilo propio: se mueve directamente desde el
Event Dispatch Thread (EDT) en cada `tick()`, a partir de las teclas
presionadas, porque el enunciado solo exige hilo para proyectiles y enemigos.

**Cómo terminan los hilos:** cada entidad se autodesactiva (`activo = false`)
al salir de la pantalla o al colisionar (lo decide `MotorJuego`); el `while`
del `HiloMovimiento` revisa esa bandera y termina solo, sin necesidad de
`interrupt()`. Al terminar la partida, `MotorJuego.detenerTodasLasEntidades()`
marca `activo = false` en todas las entidades vivas (para que sus hilos de
movimiento salgan de inmediato), y `PanelJuego.finalizarPartida()` llama
`interrupt()` a los hilos de disparo y de generación, que en ese momento están
dormidos en `Thread.sleep(...)` y de otro modo seguirían esperando.

### Sincronización

Nivel introductorio, sin `java.util.concurrent`:

- Los campos `x`, `y` y `activo` de `EntidadJuego` (y los campos de estado de
  `Nave`) son `volatile`. Un hilo escribe la posición, el EDT la lee para
  dibujar; `volatile` garantiza que el EDT siempre vea el último valor escrito,
  sin el costo de un `synchronized` en cada frame.
- Los métodos de `MotorJuego` que **insertan** una entidad nueva en los
  arreglos (`generarEnemigo`, `generarAsteroide`, `generarPowerUp`,
  `dispararProyectilJugador`) y el método `tick()` que **recorre** esos mismos
  arreglos para dibujar y detectar colisiones están marcados `synchronized`
  sobre el propio objeto `MotorJuego`. Es la única sección crítica real del
  proyecto (un hilo de fondo inserta, el EDT recorre al mismo tiempo), y un
  único candado implícito (el objeto `MotorJuego`) basta para evitar que un
  `tick()` lea el arreglo a la mitad de una inserción.

### Bucle de juego

`PanelJuego` arranca un `javax.swing.Timer` (cada 30 ms) en vez de un hilo
propio para el bucle principal, porque Swing exige que todo lo que toque
componentes gráficos ocurra en el EDT, y el `Timer` de Swing ya dispara sus
eventos ahí mismo. Cada disparo del timer llama a `MotorJuego.tick()`, que:

1. Mueve la nave del jugador según las teclas presionadas (si no está
   ralentizada).
2. Revisa colisiones proyectil-enemigo (AABB con `Rectangle.intersects`).
3. Revisa colisiones jugador-enemigo, jugador-asteroide y jugador-power-up, y
   aplica las reglas (sección 5).
4. Si el jugador murió, `PanelJuego` detiene el timer e hilos, registra la
   partida y cambia a la pantalla de Resultados.

## 5. Decisiones de diseño documentadas

El enunciado deja algunos detalles abiertos a interpretación. Estas son las
decisiones que se tomaron, y por qué:

1. **Núcleo de Energía: ¿150 puntos fijos o por enemigo?** Se implementó como
   **150 puntos fijos** al recogerlo (más destruir a todos los enemigos
   visibles en ese instante), por ser la lectura más simple de "otorga 150
   puntos y destruye a todos los enemigos visibles".
2. **Asteroide: ¿ralentiza o bloquea?** Se implementó como **bloqueo total del
   movimiento durante 2 segundos** (`Nave.mover()` ignora las teclas mientras
   `estaRalentizado()` es verdadero), en vez de una reducción parcial de
   velocidad, para no inventar un porcentaje arbitrario no especificado por el
   enunciado.
3. **Alcance del "período de gracia".** El enunciado dice: *"si la nave del
   jugador colisiona con un enemigo o asteroide (fuera del efecto de
   ralentización), la partida finaliza inmediatamente"*. Se interpretó de
   forma literal: **mientras la ralentización está activa, ninguna colisión
   adicional termina la partida** (ni con otro asteroide, ni con un enemigo);
   solo desactiva al objeto con el que se chocó. Fuera de ese período, chocar
   con un enemigo siempre es derrota inmediata, y chocar con un asteroide
   activa la ralentización (sin matar). Esto evita además muertes injustas por
   varias hitboxes superpuestas mientras la nave ya está inmovilizada.
4. **Capacidad fija de los arreglos.** Enemigos, asteroides, proyectiles y
   power-ups viven en arreglos de tamaño fijo (`Constantes.MAX_*`). Si no hay
   slot libre cuando se intenta generar uno nuevo, el spawn simplemente se
   descarta (`MotorJuego.buscarSlotLibre` devuelve `-1`). Es un límite
   intencional para no crear cientos de hilos simultáneos, no un error.
5. **Persistencia solo en memoria.** Pilotos e historial se pierden al cerrar
   la aplicación, porque el enunciado prohíbe archivos/bases de datos externas
   para esos datos. Es el comportamiento esperado.
6. **Un hilo real por entidad, sin pool.** Se usa `new Thread(...).start()`
   directamente para cada proyectil/enemigo/asteroide/power-up, en vez de un
   `ExecutorService`. Es la lectura literal de la rúbrica ("cada proyectil...
   debe ser un hilo independiente") y el nivel apropiado para un curso
   introductorio; los arreglos de capacidad fija acotan cuántos hilos pueden
   existir a la vez.
7. **`ImageIO` junto a `java.io.*`.** Técnicamente vive en `javax.imageio`, no
   en `java.io`, pero el propio enunciado la recomienda explícitamente junto
   con `FileWriter`/`PrintWriter` para exportar la gráfica como imagen.

## 6. Ordenamiento del historial (sin colecciones del framework)

`Repositorio.obtenerTopN(n)`:

1. Vuelca el `Vector<PartidaHistorial>` a un arreglo con `historial.copyInto(arreglo)`
   (método propio de `Vector`, no es `Collections`).
2. Ordena el arreglo de mayor a menor puntaje con **ordenamiento por
   selección** (`Ordenador.ordenarPorPuntajeDescendente`): en cada vuelta busca
   el puntaje más alto restante y lo intercambia a su posición. Se eligió este
   algoritmo (en vez de `Collections.sort` o `Arrays.sort` con `Comparator`)
   porque es el más sencillo de explicar paso a paso en una defensa oral.
3. Devuelve una copia con los primeros `n` elementos.

## 7. Reportes (JFreeChart + java.io)

`GeneradorGrafica.crearGraficaTopPuntajes(top)` arma un `JFreeChart` de barras
(`ChartFactory.createBarChart`) con un `DefaultCategoryDataset` donde cada
categoría es "`#. nombrePiloto`" y el valor es el puntaje. Esa misma gráfica
se usa para mostrarla en pantalla (`ChartPanel` dentro de `PanelTopPuntajes`) y
para exportarla a PNG (`chart.createBufferedImage(...)` + `ImageIO.write(...)`).

`GeneradorReporteHTML.generar(top, chart)`:

1. Crea la carpeta `reportes/` si no existe (`File.mkdirs()`).
2. Exporta la gráfica a `reportes/grafica_top.png`.
3. Escribe `reportes/reporte.html` con `PrintWriter`/`FileWriter`: una tabla
   con los datos del top y un `<img src="grafica_top.png">`.
4. Devuelve la ruta absoluta del HTML para mostrarla en un `JOptionPane`.

El HTML resultante se abre en cualquier navegador; desde ahí, "Imprimir" →
"Guardar como PDF" produce el PDF, tal como recomienda el enunciado.

## 8. Preguntas teóricas (hilos y POO aplicados al proyecto)

**¿Por qué cada proyectil y cada enemigo es un hilo independiente, y no un
solo bucle que mueva a todos?**
Porque el enunciado lo exige explícitamente, y porque conceptualmente cada
entidad tiene su propio "comportamiento autónomo" (avanzar hasta salir de la
pantalla o ser destruida) que no depende del resto. En términos de hilos, esto
demuestra `Runnable` + `Thread` de forma literal: una unidad de trabajo
(`HiloMovimiento`) reutilizada para distintos tipos de objeto.

**¿Qué pasaría si `x`, `y` y `activo` no fueran `volatile`?**
El hilo de movimiento podría escribir un nuevo valor que el EDT (hilo de
dibujo/colisión) no vea de inmediato por quedar en una caché de CPU o
registro del otro hilo, mostrando posiciones "viejas" de forma intermitente
(parpadeos, colisiones detectadas tarde). `volatile` obliga a leer/escribir
siempre desde memoria principal, sin necesitar un `synchronized` costoso solo
para una lectura/escritura simple.

**¿Por qué los métodos de inserción y `tick()` de `MotorJuego` son
`synchronized` y no el resto de la clase?**
Porque es la única sección donde un hilo de fondo (`GeneradorEnemigos` o
`HiloDisparoJugador`) modifica un arreglo compartido mientras el EDT lo está
recorriendo en el mismo instante. Sincronizar solo esos métodos (en vez de
cada getter) evita bloqueos innecesarios y dejar claro, con el propio nombre
del método, dónde está la sección crítica.

**¿Dónde se aplica herencia y polimorfismo en el proyecto?**
`Nave` es abstracta con tres subclases (`Explorador`, `CazaEstelar`,
`Acorazado`) que solo sobreescriben `getVelocidadPx()`,
`getIntervaloDisparoMs()`, `getNombreModelo()` y el color; `FabricaNaves`
decide cuál instanciar según la `Dificultad` del piloto, y el resto del código
(`MotorJuego`, `HiloDisparoJugador`) programa contra el tipo abstracto `Nave`
sin preguntar nunca "¿de qué subtipo es?". Lo mismo ocurre con
`EntidadJuego` y sus 4 subclases (`Enemigo`, `Asteroide`, `PowerUp`,
`Proyectil`): todas comparten el mismo `HiloMovimiento` y el mismo bucle de
dibujo en `MotorJuego.dibujarLista(...)`, pero cada una define su propio
`dibujar(Graphics2D)`.

**¿Por qué el bucle principal usa `javax.swing.Timer` y no un `Thread`
propio?**
Porque el bucle necesita leer el estado de las entidades y llamar a
`repaint()`, y Swing exige que toda esa interacción con componentes gráficos
ocurra en el Event Dispatch Thread. `javax.swing.Timer` ya entrega sus eventos
en el EDT automáticamente; usar un `Thread` propio habría obligado a envolver
cada actualización en `SwingUtilities.invokeLater(...)`, añadiendo complejidad
sin ningún beneficio real para este proyecto.
