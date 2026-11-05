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

public class Controller {

    private static final String STYLE_ERROR = "cell-error";
    private static final String STYLE_FIXED = "cell-fixed";
    private static final String STYLE_HINT = "cell-hint";
    private static final String STYLE_MESSAGE_ERROR = "message-error";

    private final IModel model = new Model();
    private final IAlertBox alertBox = new AlertBox();
    private final Random random = new Random();

    @FXML
    private GridPane boardGridPane;

    @FXML
    private Button newGameButton;

    @FXML
    private Button helpButton;

    @FXML
    private Label messageLabel;

    private TextField[][] cells;
    private boolean[][] locked;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private boolean gameStarted = false;

    public Controller() {
    }

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

    private void selectCell(int row, int col) {
        selectedRow = row;
        selectedCol = col;
        cells[row][col].requestFocus();
        cells[row][col].deselect();
    }

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

    private void updateValidation(int row, int col) {
        int errors = markErrors();

        if (isWrong(row, col)) {
            showMessage("El " + model.getEntry(row, col) + " de la fila " + (row + 1) + ", columna " + (col + 1)
                    + " ya está en su fila, columna o bloque.", true);
        } else if (errors > 0) {
            showMessage("Todavía hay " + errors + (errors == 1 ? " número" : " números")
                    + " que rompen las reglas (en rojo).", true);
        } else {
            showMessage("Sin errores, ¡sigue así!", false);
        }
    }

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

    private boolean isWrong(int row, int col) {
        return !locked[row][col] && model.getEntry(row, col) != 0 && !model.isEntryValid(row, col);
    }

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

    private void showMessage(String text, boolean isError) {
        messageLabel.setText(text);
        setStyleClass(messageLabel, STYLE_MESSAGE_ERROR, isError);
    }

    private static void setStyleClass(Node node, String styleClass, boolean enabled) {
        boolean present = node.getStyleClass().contains(styleClass);
        if (enabled && !present) {
            node.getStyleClass().add(styleClass);
        } else if (!enabled && present) {
            node.getStyleClass().remove(styleClass);
        }
    }

    private static int indexOrZero(Integer index) {
        return index == null ? 0 : index;
    }
}
