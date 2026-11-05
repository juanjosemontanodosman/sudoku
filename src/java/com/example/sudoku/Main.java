package com.example.sudoku;

import com.example.sudoku.view.View;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Entry point of the Sudoku 6x6 game.
 *
 * @author Anthony Alejandro Mora
 * @version 1.0
 */
public class Main extends Application {

    /**
     * Creates the application. It is called by the JavaFX runtime.
     */
    public Main() {
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Opens the game window.
     *
     * @param primaryStage stage provided by JavaFX (not used, the game uses its own {@link View})
     * @throws IOException if the game view cannot be loaded
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        new View();
    }
}