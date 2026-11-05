package com.example.sudoku.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main window of the Sudoku 6x6 game.
 * It loads the {@code game-view.fxml} file and shows it in a fixed size window.
 *
 * @author Anthony Alejandro Mora
 * @version 1.0
 */
public class View extends Stage {

    /** Width of the window in pixels. */
    private static final int WIDTH = 520;

    /** Height of the window in pixels. */
    private static final int HEIGHT = 640;

    /**
     * Creates and shows the game window.
     *
     * @throws IOException if the FXML file cannot be loaded
     */
    public View() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/game-view.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        setTitle("Sudoku 6x6");
        setScene(scene);
        setResizable(false);
        show();
    }
}
