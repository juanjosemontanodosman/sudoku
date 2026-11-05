/**
 * Module of the Sudoku 6x6 game.
 */
module com.example.sudoku {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.sudoku.controller to javafx.fxml;
    exports com.example.sudoku;
    exports com.example.sudoku.controller;
    exports com.example.sudoku.model;
    exports com.example.sudoku.view;
}