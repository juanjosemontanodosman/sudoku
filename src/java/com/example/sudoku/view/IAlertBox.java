package com.example.sudoku.view;

/**
 * Contract for the alert windows of the game.
 * <p>
 * The controller uses this interface to ask the player for confirmation
 * or to show information, without depending on how the alerts are built.
 * </p>
 *
 * @author Anthony Alejandro Mora
 * @version 1.0
 */
public interface IAlertBox {

    /**
     * Shows a confirmation alert with "Aceptar" and "Cancelar" buttons.
     *
     * @param title   title of the window
     * @param header  header text
     * @param message content of the alert
     * @return {@code true} if the player pressed "Aceptar", {@code false} otherwise
     */
    boolean showConfirmation(String title, String header, String message);

    /**
     * Shows an information alert.
     *
     * @param title   title of the window
     * @param header  header text
     * @param message content of the alert
     */
    void showInformation(String title, String header, String message);

    /**
     * Shows a warning alert, for example when a number breaks the Sudoku rules.
     *
     * @param title   title of the window
     * @param header  header text
     * @param message content of the alert
     */
    void showWarning(String title, String header, String message);
}