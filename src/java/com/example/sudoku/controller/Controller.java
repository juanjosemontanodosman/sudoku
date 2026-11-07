package com.example.sudoku.controller;

import com.example.sudoku.model.Board;
import com.example.sudoku.model.IModel;
import com.example.sudoku.model.Model;
import com.example.sudoku.view.AlertBox;
import com.example.sudoku.view.IAlertBox;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Controlador del juego Sudoku 6x6.
 * <p>
 * Conecta la vista ({@code game-view.fxml}) con el {@link IModel} para gestionar
 * la interacción del usuario con el tablero.
 * </p>
 *
 * @author [Roiban Rosales Bastidas]
 */
public class Controller {

    /** Clase CSS para indicar un error en una celda. */
    private static final String STYLE_ERROR = "cell-error";

    /** Clase CSS para los números iniciales fijos. */
    private static final String STYLE_FIXED = "cell-fixed";

    /** Clase CSS para resaltar una pista. */
    private static final String STYLE_HINT = "cell-hint";

    /** Clase CSS para el texto de error en el label de mensajes. */
    private static final String STYLE_MESSAGE_ERROR = "message-error";

    /** Lógica del juego. */
    private final IModel model = new Model();

    /** Gestor de ventanas de alerta. */
    private final IAlertBox alertBox = new AlertBox();

    /** Generador de números aleatorios. */
    private final Random random = new Random();

    /** Tablero gráfico con las celdas. */
    @FXML
    private GridPane boardGridPane;

    /** Botón para iniciar un nuevo juego. */
    @FXML
    private Button newGameButton;

    /** Botón para pedir una ayuda. */
    @FXML
    private Button helpButton;

    /** Etiqueta para mostrar mensajes al usuario. */
    @FXML
    private Label messageLabel;

    /** Matriz bidimensional que almacena las celdas de texto del tablero. */
    private TextField[][] cells;

    /** Matriz que indica qué celdas están bloqueadas (fijas o pistas). */
    private boolean[][] locked;

    /** Fila de la celda seleccionada actualmente (-1 si no hay ninguna). */
    private int selectedRow = -1;

    /** Columna de la celda seleccionada actualmente (-1 si no hay ninguna). */
    private int selectedCol = -1;

    /** Indica si el juego ha comenzado (se ha presionado "Nuevo juego"). */
    private boolean gameStarted = false;

    /**
     * Constructor por defecto del controlador.
     */
    public Controller() {
    }

    /**
     * Inicializa el controlador. Se ejecuta automáticamente al cargar el FXML.
     * Recoge las celdas del GridPane y configura sus eventos.
     */
    @FXML
    private void initialize() {
        cells = new TextField[model.getBoardHeight()][model.getBoardWidth()];
        locked = new boolean[model.getBoardHeight()][model.getBoardWidth()];

        for (Node node : boardGridPane.getChildren()) {
            if (node instanceof TextField cell) {
                int row = indexOrZero(GridPane.getRowIndex(cell));
                int col = indexOrZero(GridPane.getColumnIndex(cell));
                cells[row][col] = cell;
                setUpCell(cell, row, col);
            }
        }

        newGameButton.setOnAction(event -> onNewGame());
        helpButton.setOnAction(event -> onHelp());
    }

    /**
     * Configura los eventos de ratón y teclado de una celda específica.
     *
     * @param cell la celda (TextField) a configurar
     * @param row  la fila donde se encuentra la celda
     * @param col  la columna donde se encuentra la celda
     */
    private void setUpCell(TextField cell, int row, int col) {
        cell.setEditable(false);
        cell.setOnMouseClicked(event -> selectCell(row, col));
        cell.focusedProperty().addListener((observable, wasFocused, isFocused) -> {
            if (isFocused) {
                selectedRow = row;
                selectedCol = col;
            }
        });
        cell.addEventFilter(KeyEvent.KEY_TYPED, event -> onKeyTyped(event, row, col));
        cell.addEventFilter(KeyEvent.KEY_PRESSED, event -> onKeyPressed(event, row, col));
    }

    /**
     * Selecciona una celda dándole el foco.
     *
     * @param row fila de la celda
     * @param col columna de la celda
     */
    private void selectCell(int row, int col) {
        selectedRow = row;
        selectedCol = col;
        cells[row][col].requestFocus();
        cells[row][col].deselect();
    }

    /**
     * Maneja la escritura por teclado, permitiendo solo números válidos del 1 al 6.
     *
     * @param event el evento de teclado
     * @param row   fila de la celda
     * @param col   columna de la celda
     */
    private void onKeyTyped(KeyEvent event, int row, int col) {
        event.consume();
        String typed = event.getCharacter();
        if (typed.length() != 1 || Character.isISOControl(typed.charAt(0))
                || event.isControlDown() || event.isAltDown() || event.isMetaDown()) {
            return;
        }
        if (!gameStarted) {
            showMessage("Presiona \"Nuevo juego\" para empezar", false);
            return;
        }

        char character = typed.charAt(0);
        int max = model.getBoardWidth();
        if (character < '1' || character > '0' + max) {
            showMessage("Solo se aceptan números del 1 al " + max + ".", true);
            return;
        }
        placeNumber(row, col, character - '0');
    }

    /**
     * Maneja teclas especiales como Backspace y Delete para borrar números.
     *
     * @param event el evento de teclado
     * @param row   fila de la celda
     * @param col   columna de la celda
     */
    private void onKeyPressed(KeyEvent event, int row, int col) {
        if (event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.DELETE) {
            event.consume();
            if (!gameStarted) {
                showMessage("Presiona \"Nuevo juego\" para empezar", false);
                return;
            }
            clearNumber(row, col);
        }
    }

    /**
     * Coloca un número en una celda si no está bloqueada.
     *
     * @param row   fila de la celda
     * @param col   columna de la celda
     * @param value el número a colocar
     */
    private void placeNumber(int row, int col, int value) {
        if (locked[row][col]) {
            showMessage("Esa casilla es una pista y no se puede cambiar.", true);
            return;
        }
        try {
            model.setEntry(row, col, value);
        } catch (IllegalArgumentException e) {
            showMessage(e.getMessage(), true);
            return;
        }
        cells[row][col].setText(String.valueOf(value));
        updateValidation(row, col);
    }

    /**
     * Borra el número de una celda si no está bloqueada.
     *
     * @param row fila de la celda
     * @param col columna de la celda
     */
    private void clearNumber(int row, int col) {
        if (locked[row][col]) {
            showMessage("Esa casilla es una pista y no se puede borrar.", true);
            return;
        }
        if (model.getEntry(row, col) == 0) {
            return;
        }
        try {
            model.setEntry(row, col, 0);
        } catch (IllegalArgumentException e) {
            showMessage(e.getMessage(), true);
            return;
        }
        cells[row][col].setText("");
        updateValidation(row, col);
    }

    /**
     * Inicia un nuevo juego tras pedir confirmación al usuario.
     */
    private void onNewGame() {
        String header = gameStarted ? "¿Iniciar un nuevo juego?" : "¿Empezar a jugar?";
        String content = gameStarted ? "Se perderá el progreso del juego actual." : "Se generará un tablero nuevo.";
        if (!alertBox.showConfirmation("Nuevo juego", header, content)) {
            return;
        }

        model.resetGame();
        gameStarted = true;
        selectedRow = -1;
        selectedCol = -1;
        paintBoard();
        showMessage("¡Nuevo juego! Selecciona una celda y escribe un número del 1 al "
                + model.getBoardWidth() + ".", false);
    }

    /**
     * Pinta el tablero generado por el modelo en la interfaz gráfica.
     */
    private void paintBoard() {
        Board board = model.getBoard();
        Board initialNumbers = model.getHintsBoard();

        for (int row = 0; row < cells.length; row++) {
            for (int col = 0; col < cells[row].length; col++) {
                int value = board.getEntry(row, col);
                boolean isInitial = initialNumbers.getEntry(row, col) != 0;

                cells[row][col].setText(value == 0 ? "" : String.valueOf(value));
                locked[row][col] = isInitial;
                setStyleClass(cells[row][col], STYLE_FIXED, isInitial);
                setStyleClass(cells[row][col], STYLE_HINT, false);
                setStyleClass(cells[row][col], STYLE_ERROR, false);
            }
        }

        setStyleClass(messageLabel, STYLE_MESSAGE_ERROR, false);
    }

    /**
     * Actualiza la validación visual y los mensajes de error tras un cambio en el tablero.
     *
     * @param row fila de la celda modificada
     * @param col columna de la celda modificada
     */
    private void updateValidation(int row, int col) {
        int errors = markErrors();

        if (isWrong(row, col)) {
            showMessage("El " + model.getEntry(row, col) + " de la fila " + (row + 1) + ", columna " + (col + 1)
                    + " ya está en su fila, columna o bloque.", true);
        } else if (errors > 0) {
            showMessage("Todavía hay " + errors + (errors == 1 ? " número" : " números")
                    + " que rompen las reglas (en rojo).", true);
        } else {
            if (model.isBoardComplete()){
                alertBox.showInformation("Felicidades", "Felicidades! completaste el juego",
                        "Presiona nuevo juego para volver a empezar");
            } else {
                showMessage("Sin errores, ¡sigue así!", false);
            }
        }
    }

    /**
     * Marca visualmente las celdas que contienen errores.
     *
     * @return el número total de errores encontrados
     */
    private int markErrors() {
        int errors = 0;
        for (int row = 0; row < cells.length; row++) {
            for (int col = 0; col < cells[row].length; col++) {
                boolean wrong = isWrong(row, col);
                setStyleClass(cells[row][col], STYLE_ERROR, wrong);
                if (wrong) {
                    errors++;
                }
            }
        }
        return errors;
    }

    /**
     * Comprueba si el número en una celda rompe las reglas del Sudoku.
     *
     * @param row fila de la celda
     * @param col columna de la celda
     * @return true si el número es inválido, false en caso contrario
     */
    private boolean isWrong(int row, int col) {
        return model.getEntry(row, col) != 0 && !model.isEntryValid(row, col);
    }

    /**
     * Proporciona una pista al usuario y la coloca en el tablero.
     */
    private void onHelp() {
        if (!gameStarted) {
            showMessage("Presiona \"Nuevo juego\" para empezar", true);
            return;
        }

        int row = selectedRow;
        int col = selectedCol;
        if (row < 0) {
            int[] emptyCell = pickEmptyCell();
            if (emptyCell == null) {
                showMessage("No hay casillas vacías para dar una ayuda.", false);
                return;
            }
            row = emptyCell[0];
            col = emptyCell[1];
        }
        if (locked[row][col]) {
            showMessage("Esa casilla ya tiene un número fijo. Selecciona otra casilla.", true);
            return;
        }
        if (model.getEntry(row, col) != 0) {
            showMessage("La casilla seleccionada ya tiene un número. Selecciona una casilla vacía.", true);
            return;
        }

        try {
            int hint = model.getHint(row, col);
            if (hint < 1 || hint > model.getBoardWidth()) {
                showMessage("El modelo devolvió una sugerencia no válida.", true);
                return;
            }
            model.setEntry(row, col, hint);
            cells[row][col].setText(String.valueOf(hint));
            locked[row][col] = true;
            setStyleClass(cells[row][col], STYLE_HINT, true);
            markErrors();
            showMessage("Ayuda: el " + hint + " va en la fila " + (row + 1) + ", columna " + (col + 1) + ".", false);
            selectCell(row, col);
        } catch (UnsupportedOperationException e) {
            alertBox.showInformation("Ayuda", "Sin ayudas disponibles",
                    "Ya usaste todas las ayudas de este juego.");
        } catch (IllegalArgumentException e) {
            showMessage(e.getMessage(), true);
        }
    }

    /**
     * Busca y selecciona una celda vacía al azar.
     *
     * @return un arreglo con la fila y columna de la celda, o null si no hay vacías
     */
    private int[] pickEmptyCell() {
        List<int[]> emptyCells = new ArrayList<>();
        for (int row = 0; row < cells.length; row++) {
            for (int col = 0; col < cells[row].length; col++) {
                if (!locked[row][col] && model.getEntry(row, col) == 0) {
                    emptyCells.add(new int[]{row, col});
                }
            }
        }
        return emptyCells.isEmpty() ? null : emptyCells.get(random.nextInt(emptyCells.size()));
    }

    /**
     * Muestra un mensaje en la etiqueta de la interfaz.
     *
     * @param text    el texto a mostrar
     * @param isError indica si el mensaje es de error (para aplicar estilo)
     */
    private void showMessage(String text, boolean isError) {
        messageLabel.setText(text);
        setStyleClass(messageLabel, STYLE_MESSAGE_ERROR, isError);
    }

    /**
     * Agrega o elimina una clase CSS de un nodo.
     *
     * @param node       el nodo al que se le aplicará el cambio
     * @param styleClass la clase CSS
     * @param enabled    true para agregar, false para eliminar
     */
    private static void setStyleClass(Node node, String styleClass, boolean enabled) {
        boolean present = node.getStyleClass().contains(styleClass);
        if (enabled && !present) {
            node.getStyleClass().add(styleClass);
        } else if (!enabled && present) {
            node.getStyleClass().remove(styleClass);
        }
    }

    /**
     * Obtiene el índice de una celda en el GridPane, manejando valores nulos.
     *
     * @param index el índice proporcionado por el GridPane
     * @return el índice, o 0 si es nulo
     */
    private static int indexOrZero(Integer index) {
        return index == null ? 0 : index;
    }
}
