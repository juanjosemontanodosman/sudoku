package com.example.sudoku.model;

/**
 * The Model class is in charge of validating the game's rules
 * <p>
 * Contains a 6x6 size sudoku board, divided in 6 3x2 blocks
 * <br>
 * Rules:
 * <ul>
 *     <li>Every number must appear only once on every row</li>
 *     <li>Every number must appear only once on every column</li>
 *     <li>Every number must appear only once on every block</li>
 * </ul>
 *
 * @author Juan José Montaño Dosman, código 2538124-3743
 * @version 1.0
 */

public class Model implements IModel {
    // These are all compile-time constants:

    /** The width of the board*/
    private static final int boardWidth = 6;

    /** The height of the board. <p>It should be the same as boardWidth, but separating them improves legibility*/
    private static final int boardHeight = 6;

    /** Width of the blocks that subdivide the board*/
    private static final int blockWidth = 3;

    /** Height of the blocks that subdivide the board*/
    private static final int blockHeight = 2;

    /** Maximum amount of hints the user gets*/
    private static final short hintUsagesLimit = 2;

    /** The number of initial hints the user gets per block*/
    private static final int initialEntriesPerBlock = 2;

    /** Maximum number that can be placed on the board*/
    private static final int maxNumber = boardWidth;

    // These are actual variables that are used in run-time

    /**
     * Inner class for storing a board with the hints given and the automatically generated initial values from a solution board
     */
    class HintsBoard extends Board {
        public void generateHints(Board solution){
            for (int a = 0; a < boardHeight/blockHeight; a++){
                for (int b = 0; b < boardWidth/blockWidth; b++){
                    // For every block:
                    int entriesOnBlock = 0;
                    for (int i = 0; i < blockHeight; i++){
                        for (int j = 0; j < blockWidth; j++){
                            // For every entry on that block
                            if (entriesOnBlock <initialEntriesPerBlock && ((int)(Math.random()*(6-3*i-j)) <= 1 || (6-3*i-j <=  initialEntriesPerBlock-entriesOnBlock))){
                                entriesOnBlock++;
                                this.setEntry(i+(a*blockHeight), j+(b*blockWidth), solution.getEntry(i+(a*blockHeight), j+(b*blockWidth)));
                            }
                        }
                        if (entriesOnBlock >= initialEntriesPerBlock){ break; }
                    }
                }
            }
        }
    }

    /** Board containing the numbers in game. This board is accessible trough {@link getBoard()}*/
    private Board board = new Board();

    /** Contains an already solved board. This board is only accessible within this class */
    private Board solutionBoard = new Board();

    /** Contains the initial values and hints given to the user. Somewhat of an intermediate board */
    private HintsBoard hintsBoard = new HintsBoard();

    /** The number of hints used */
    private short hintUsages = 0;

    /**
     * Generates a new solved board and copies it to solutionBoard
     */
    public void resetGame(){
        hintUsages = 0;
        hintsBoard.initialize(boardWidth, boardHeight);
        board.initialize(boardWidth, boardHeight);


        boolean boardIsIncomplete;
        do {
            solutionBoard.initialize(boardWidth, boardHeight);
            boardIsIncomplete = false;
            for (int i = 0; i < boardHeight; i++) {
                for (int j = 0; j < boardWidth; j++) {
                    try {
                        solutionBoard.setEntry(i, j, getValidNumber(i, j, solutionBoard));
                    } catch (Exception e) {
                        boardIsIncomplete = true;
                    }
                }
                if (boardIsIncomplete){ break; }
            }
        } while (boardIsIncomplete);
        hintsBoard.generateHints(solutionBoard);
        board = new Board(hintsBoard);
    }

    /** Public method for accessing the board*/
    public Board getBoard(){
        return new Board(board);
    }

    public int getBoardWidth(){
        return boardWidth;
    }

    public int getBoardHeight(){
        return boardHeight;
    }

    public int getBlockWidth(){
        return blockWidth;
    }

    public int getBlockHeight(){
        return blockHeight;
    }

    /** Public method for accessing the hints board*/
    public Board getHintsBoard(){
        return new Board(hintsBoard);
    }

    /**
     * Public method for changing an entry's value in the board
     * @param row Row where it is located
     * @param col Column where it is located
     * @param value The value to be placed in it
     * @throws IllegalArgumentException Throws when the value in that position was given as an initial value or a hint
     */
    public void setEntry(int row, int col, int value) throws IllegalArgumentException{
        if (!hintsBoard.isEmpty(row, col)){
            throw new IllegalArgumentException("No se puede cambiar el valor en esa posición");
        }
        board.setEntry(row, col, value);
    }

    /**
     * Public method for getting the board's value at a position
     * @param row The row where it is located
     * @param col The column where it is located
     * @return Returns the value in that position
     */
    public int getEntry(int row, int col){
        return board.getEntry(row, col);
    }


    public boolean isEntryValid(int row, int col){
        return isEntryValid(row, col, getEntry(row, col), board);
    }

    public boolean isBoardComplete(){
        for (int i = 0; i < boardHeight; i++){
            for (int j = 0; j< boardWidth; j++){
                if (board.isEmpty(i, j)){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Check if a value is valid (does not violate Sudoku's rules) in a given position
     * @param number The value
     * @param row The row where it is located
     * @param col The column where it is located
     * @param board The board it is placed in
     * @return Returns false when the value is not valid, true when it is
     */
    private boolean isEntryValid(int row, int col, int number, Board board){
        // If any method below returns true, then the given entry is not valid
        // The given number is already somewhere else, either in the same column, row or block
        return !isNumberOnBlock(row, col, number, board) && !isNumberOnColumn(row, col, number, board) && !isNumberOnRow(row, col, number, board);
        /*if (isNumberOnBlock(row, col, number, board)){
            System.out.println("El número "+number+" ya está en el bloque");
            return false;
        }
        if (isNumberOnRow(row, col, number, board)){
            System.out.println("El número "+number+" ya está en la fila");
            return false;
        }
        if (isNumberOnColumn(row, col, number, board)){
            System.out.println("El número "+number+" ya está en la columna");
            return false;
        }
        return true;*/
    }

    /**
     * Returns a value from a valid solution (solutionBoard) at a given position, with a usage limit
     * <p>
     * Other solutions different from the one in solutionBoard may exist. This method does not account for them
     *
     * @param row Row where the value is located
     * @param col Column where the value is located
     * @return The solution's value
     * @throws UnsupportedOperationException Throws an error when the usage limit has been reached
     */
    public int getHint(int row, int col) throws UnsupportedOperationException{

        if (!hintsBoard.isEmpty(row, col)){
            throw new IllegalArgumentException("Se pidió una pista de una casilla que ya se había dado: "+row+", "+col);
        }
        hintUsages++;
        if (hintUsages > hintUsagesLimit) {
            throw new UnsupportedOperationException("No se puede dar la pista. Se sobrepasó el límite de ayudas");
        }
        return solutionBoard.getEntry(row, col);
    }

    /**
     * Takes a board and finds a possible value that could be placed on a specific position in it without violating Sudoku's rules
     *
     * @param row Row where the value is to be located
     * @param col Column where the value is to be located
     * @param board The board in which the value will be placed
     * @return Returns a number that is valid in the given position
     */
    private int getValidNumber(int row, int col, Board board){
        int num = 1;
        while (!isEntryValid(row, col, num, board)) {
            num++;
        }

        if (num > maxNumber){
            throw new ArithmeticException("Ningún número es válido en la posición ("+row+", "+col+")");
        }

        do {
            num = (int)(Math.random()*maxNumber +1);
        } while (!isEntryValid(row, col, num, board));

        return num;
    }

    /**
     * Check if the same value is already present in the block it will be placed in
     *
     * @param number The value
     * @param row The row where it is located
     * @param col The column where it is located
     * @param board The board it is placed in
     * @return Returns true if the value is already there
     */
    private boolean isNumberOnBlock(int row, int col, int number,  Board board){
        // A 6x6 size board is usually divided in rectangular 3x2 blocks
        // Other board sizes can be arranged in different ways
        for (int i = 0; i < blockHeight; i++){
            for (int j = 0; j < blockWidth; j++) {
                // i and j are the position of a number relative to the corner of the block the number is placed in
                int[] absolutePos = {i + row-(row%blockHeight), j + (col-col%blockWidth)};
                // i and j are the position of a number relative to such corner
                // i + row-(row%blockHeight) and j + (col-col%blockWidth) are absolute
                if (absolutePos[0] == row && absolutePos[1] == col){ continue; } // Ignore the value at the actual position

                if (absolutePos[0] >= board.size() || absolutePos[1] >= board.get(absolutePos[0]).size()){
                    // If at this point the number hasn't been found then it isn't there
                    return false;
                }

                //System.out.println(absolutePos+ " está dentro de ["+board.size()+", "+board.get)
                if (board.getEntry(absolutePos[0], absolutePos[1]) == number) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks if the value is already present in the column it will be placed in
     *
     * @param number The value
     * @param col The column where it is located
     * @param board The board it is placed in
     * @return Returns true if the value is already there
     */
    private boolean isNumberOnColumn(int row, int col, int number, Board board){
        for (int i = 0; i < board.size(); i++){ // For every row i, on the same column...
            if (i == row){ continue; } // Ignore the value at the actual position
            if (board.get(i).get(col) == number) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the value is already present in the row it will be placed in
     *
     * @param number The value
     * @param row The row where it is located
     * @param board The board it is placed in
     * @return Returns true if the value is already there
     */
    private boolean isNumberOnRow(int row, int col, int number, Board board){
        for (int j = 0; j < board.get(row).size(); j++){ // For every column j, on the same row...
            if (j == col){
                continue;
            } // Ignore the value at the actual position
            if (board.get(row).get(j) == number) {
                return true;
            }
        }
        return false;
    }
}
