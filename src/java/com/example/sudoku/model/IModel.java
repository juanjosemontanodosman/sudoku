package com.example.sudoku.model;

public interface IModel {

    void resetGame();

    Board getBoard();
    int getBoardWidth();
    int getBoardHeight();
    int getBlockWidth();
    int getBlockHeight();

    /** Returns a matrix with the hints that have been given to the user previously*/
    Board getHintsBoard();

    // Throws an exception when it is used for changing values that cannot be changed
    void setEntry(int row, int col, int value) throws IllegalArgumentException;
    int getEntry(int row, int col);

    boolean isEntryValid(int row, int col);
    int getHint(int row, int col) throws UnsupportedOperationException;

    boolean isBoardComplete();

}
