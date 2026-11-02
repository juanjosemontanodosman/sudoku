package com.example.sudoku.view;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Implementation of {@link IAlertBox} using JavaFX {@link Alert} windows.
 *
 * @author Anthony Alejandro Mora
 * @version 1.0
 */
public class AlertBox implements IAlertBox {

    /**
     * Creates an alert box.
     */
    public AlertBox() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean showConfirmation(String title, String header, String message) {
        Alert alert = createAlert(Alert.AlertType.CONFIRMATION, title, header, message);
        Optional<ButtonType> response = alert.showAndWait();
        return response.isPresent() && response.get() == ButtonType.OK;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showInformation(String title, String header, String message) {
        createAlert(Alert.AlertType.INFORMATION, title, header, message).showAndWait();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showWarning(String title, String header, String message) {
        createAlert(Alert.AlertType.WARNING, title, header, message).showAndWait();
    }

    /**
     * Creates an alert of the given type with its texts.
     *
     * @param type    type of the alert
     * @param title   title of the window
     * @param header  header text
     * @param message content of the alert
     * @return the configured alert
     */
    private Alert createAlert(Alert.AlertType type, String title, String header, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        return alert;
    }
}