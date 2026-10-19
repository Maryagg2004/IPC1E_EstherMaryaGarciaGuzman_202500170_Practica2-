# Manual de Usuario - Quetzal Space Defender

## 1. Requisitos

- Java JDK 11 o superior.
- El archivo `lib/jfreechart-1.5.4.jar` incluido en este repositorio (no
  requiere instalación, solo agregarlo al classpath/librerías del proyecto).

## 2. Cómo abrir el proyecto en NetBeans

1. Abre NetBeans → **File → New Project… → Java with Existing Sources**.
2. En "Name and Location", elige un nombre de proyecto y la carpeta
   `proyecto2` como ubicación.
3. En "Source Package Folders", agrega la carpeta `src/`.
4. Termina el asistente (Finish).
5. Agrega JFreeChart como librería: clic derecho sobre el proyecto →
   **Properties → Libraries → Compile → Add JAR/Folder…** y selecciona
   `lib/jfreechart-1.5.4.jar`.
6. Clic derecho sobre `Main.java` (paquete `quetzal`) → **Run File**.

## 3. Cómo compilar y ejecutar por línea de comandos

Desde la carpeta `proyecto2/`:

```bash
# Compilar
javac -d build -cp "lib/jfreechart-1.5.4.jar" $(find src -name "*.java")

# Ejecutar
java -cp "build:lib/jfreechart-1.5.4.jar" quetzal.Main
```

En Windows (PowerShell/cmd), usa `;` en vez de `:` como separador del
classpath.

## 4. Menú principal

![Menú principal](capturas/01_menu_principal.png)

- **Jugar**: muestra la lista de pilotos ya creados para elegir con cuál
  volar. Si todavía no hay ninguno, pide crear uno primero.
- **Crear Piloto**: registra un nuevo piloto.
- **Top de Puntajes**: muestra el historial de partidas ordenado, con su
  gráfica, y permite exportar el reporte.
- **Salir**: cierra la aplicación.

## 5. Crear un piloto

![Crear piloto](capturas/02_crear_piloto.png)

Escribe un nombre y elige una de las tres dificultades. La dificultad define
el modelo de nave con el que volarás siempre que elijas a ese piloto:

| Dificultad | Nave         | Estilo de juego                                                                |
| ---------- | ------------ | ------------------------------------------------------------------------------ |
| Fácil      | Explorador   | Se mueve rápido; dispara cada 2 segundos                                       |
| Normal     | Caza Estelar | Velocidad y cadencia de disparo equilibradas (1 segundo)                       |
| Difícil    | Acorazado    | Se mueve lento (más difícil de esquivar); dispara en ráfagas cada 0.3 segundos |

## 6. Controles durante la partida

| Tecla                                   | Acción                                                       |
| --------------------------------------- | ------------------------------------------------------------ |
| ↑ / W                                   | Mover la nave hacia arriba                                   |
| ↓ / S                                   | Mover la nave hacia abajo                                    |
| ← / A                                   | Mover la nave a la izquierda (dentro de su franja permitida) |
| → / D                                   | Mover la nave a la derecha (dentro de su franja permitida)   |
| Barra espaciadora (mantener presionada) | Disparar, según la cadencia de la nave                       |

![Juego en curso](capturas/04_juego_en_curso.png)

**Objetos del campo de batalla:**

- 🔺 Rojo: nave enemiga. Chocar con ella termina la partida.
- ⚪ Gris: asteroide. Al chocar, bloquea tu nave 2 segundos (no te mata),
  mostrando el aviso "¡RALENTIZADO!" en pantalla.
- 🟢 Verde con "C": Cápsula de Suministro, +10 puntos.
- 🔵 Cian con "N": Núcleo de Energía, +150 puntos y destruye a todos los
  enemigos visibles en ese momento.

![Nave ralentizada por un asteroide](capturas/05_ralentizado_por_asteroide.png)

**Nota:** mientras tu nave está bloqueada por un asteroide (el aro amarillo a
su alrededor), seguir tocando enemigos u otros asteroides durante esos 2
segundos no termina la partida; es un período de gracia intencional.

## 7. Fin de la partida

Chocar con un enemigo (fuera del bloqueo por asteroide) termina la partida de
inmediato. Se guarda tu puntaje en el historial y se muestra la pantalla de
resultados:

![Pantalla de resultados](capturas/06_resultados.png)

Desde ahí puedes jugar de nuevo con el mismo piloto, ver el Top de Puntajes o
volver al menú principal.

## 8. Top de Puntajes y exportación de reportes

![Top de puntajes](capturas/03_top_puntajes.png)

La tabla y la gráfica muestran las mejores partidas registradas en esta
sesión (de mayor a menor puntaje). El botón **Exportar Reporte**:

1. Genera `reportes/grafica_top.png` (la gráfica como imagen).
2. Genera `reportes/reporte.html` (tabla + gráfica).
3. Muestra la ruta del archivo generado.

Para guardarlo como PDF: abre `reportes/reporte.html` con tu navegador (doble
clic sobre el archivo) y usa **Imprimir → Guardar como PDF**.

> El historial de partidas y los pilotos se guardan solo en memoria mientras
> la aplicación está abierta (así lo exige el enunciado); al cerrarla, se
> pierden. Es el comportamiento esperado, no un error.
