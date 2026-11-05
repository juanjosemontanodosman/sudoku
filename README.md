# Sudoku 6x6

Mini proyecto 2 de Fundamentos de Programación Orientada a Eventos (750014C) - Universidad del Valle.

Juego de Sudoku en una cuadrícula de 6x6: cada fila, columna y bloque de 2x3 debe tener los números del 1 al 6 sin repetirse. Al iniciar un juego nuevo, el tablero empieza con 2 números en cada bloque. El jugador escribe los números con el teclado, el juego le marca en rojo los que rompen las reglas y tiene un botón de ayuda que sugiere un número válido (con un límite de usos).

## Integrantes
- Juan José Montaño Dosman - 2538124
- Anthony Alejandro Mora - 2517317
- Roiban Alirio Rosales Bastidas - 2538325

## Cómo ejecutarlo
1. Abrir el proyecto en IntelliJ IDEA (se carga como proyecto Maven por el `pom.xml`).
2. Esperar a que Maven descargue JavaFX.
3. Ejecutar la clase `Main` (`src/java/com/example/sudoku/Main.java`).

También se puede ejecutar con `mvn clean javafx:run`.

## Estructura (MVC)
- `model`: lógica del juego (tablero, validaciones y ayuda).
- `view`: ventana (`View`), alertas (`IAlertBox`, `AlertBox`), `game-view.fxml` y `styles.css`.
- `controller`: eventos de mouse y teclado, y conexión entre la vista y el modelo.

## Tecnologías
- Java 17
- JavaFX 21
- Scene Builder
- Maven
- Git y GitHub