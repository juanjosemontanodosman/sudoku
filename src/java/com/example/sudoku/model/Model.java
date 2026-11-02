package com.example.sudoku.model;
import java.util.ArrayList;

/**
 * The Model class contains the game's current state
 * It is in charge of generating a board validating its rules
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

public class Model {
    /** These are all compile-time constants: */

    /** The width of the board*/
    private static final int boardWidth = 6;

    /** The height of the board. It should be the same as boardWidth, but separating them improves legibility*/
    private static final int boardHeight = 6;

    /** Width of the blocks that subdivide the board*/
    private static final int blockWidth = 3;

    /** Height of the blocks that subdivide the board*/
    private static final int blockHeight = 2;

    /** Maximum amount of hints the user gets*/
    private static final short hintUsagesLimit = 10;

    /** Maximum number that can be placed on the board*/
    private static final int maxNumber = boardWidth;

    /** These are actual variables that are used in run-time*/

    /** Contains a 2-dimensional ArrayList containing the numbers in game. This board is accessible trough {@link getBoard()}*/
    private ArrayList<ArrayList<Integer>> board = new ArrayList<>();

    /** Contains an already solved board. This board is only accessible within this class */
    private ArrayList<ArrayList<Integer>> solutionBoard = new ArrayList<>();

    /** The number of hints used */
    private short hintUsages = 0;

    /**
     * Generates a new solver board and copies it to solutionBoard
     * board is then cleared with initializeBoard()
     */
    public void generateSolution(){
        int number;
        boolean boardIsIncomplete;
        do {
            solutionBoard.clear();
            boardIsIncomplete = false;
            for (int i = 0; i < boardHeight; i++) {
                solutionBoard.add(new ArrayList<>());
                for (int j = 0; j < boardWidth; j++) {
                    try {
                        solutionBoard.get(i).add(getValidNumber(i, j, solutionBoard));
                    } catch (Exception e) {
                        boardIsIncomplete = true;
                        break;
                    }
                }
                if (boardIsIncomplete){ break; }
            }
        } while (boardIsIncomplete);
    }


    /* This method prints a board in console. It is useful for debugging
    public static void printBoard(ArrayList<ArrayList<Integer>> board){
        for (int i = 0; i < boardWidth; i++){
            System.out.println(board.get(i));
        }
    }
    */

    /** Public method for accessing the board*/
    public ArrayList<ArrayList<Integer>> getBoard(){
        return board;
    }

    /**
     * Takes a board and finds a possible value that could be placed on a specific position in it without violating Sudoku's rules
     *
     * @param row Row where the value is to be located
     * @param col Column where the value is to be located
     * @param board The board in which the value will be placed
     * @return Returns a number that is valid in the given position
     * @throws Exception Throws an error when no number is valid for the specified position
     */
    public int getValidNumber(int row, int col, ArrayList<ArrayList<Integer>> board) throws Exception{
        int num = 1;
        while (!isEntryValid(num, row, col, board)) {
            num++;
        }

        if (num > maxNumber){
            throw new Exception("Ningún número es válido en la posición ("+row+", "+col+")");
        }

        do {
            num = (int)(Math.random()*maxNumber +1);
        } while (!isEntryValid(num, row, col, board));

        return num;
    }

    /**
     * Returns a value from a valid solution (solutionBoard) at a given position, with a usage limit
     * <p>
     * Other solutions different from the one in solutionBoard may exist. This method does not account for them
     *
     * @param row Row where the value is located
     * @param col Column where the value is located
     * @return The solution's value
     * @throws Exception Throws an error when the usage limit has been reached
     */
    public int getHint(int row, int col) throws Exception{
        if (hintUsages >= hintUsagesLimit) {
            throw new Exception("No se puede dar la pista. Se sobrepasó el límite de ayudas");
        }

        hintUsages++;
        return solutionBoard.get(row).get(col);
    }

    /**
     * Check if a value is valid (does not violate Sudoku's rules) in a given position
     * @param number The value
     * @param row The row where it is located
     * @param col The column where it is located
     * @param board The board it is placed in
     * @return Returns false when the value is not valid, true when it is
     */
    public boolean isEntryValid(int number, int row, int col, ArrayList<ArrayList<Integer>> board){
        // If any method below returns true, then the given entry is not valid
        // The given number is already somewhere else, either in the same column, row or block
        return !isNumberOnBlock(number, row, col, board) && !isNumberOnColumn(number, col, board) && !isNumberOnRow(number, row, board);
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
    public boolean isNumberOnBlock(int number, int row, int col, ArrayList<ArrayList<Integer>> board){
        // A 6x6 size board is usually divided in rectangular 3x2 blocks
        // Other board sizes can be arranged in different ways
        for (int i = 0; i < blockHeight; i++){
            for (int j = 0; j < blockWidth; j++) {
                // i and j are the position of a number relative to the corner of the block the number is placed in
                int[] absolutePos = {i + row-(row%blockHeight), j + (col-col%blockWidth)};
                // i and j are the position of a number relative to such corner
                // i + row-(row%blockHeight) and j + (col-col%blockWidth) are absolute

                if (absolutePos[0] >= board.size() || absolutePos[1] >= board.get(absolutePos[0]).size()){
                    // If at this point the number hasn't been found then it isn't there
                    return false;
                }

                //System.out.println(absolutePos+ " está dentro de ["+board.size()+", "+board.get)
                if (board.get(absolutePos[0]).get(absolutePos[1]) == number) {
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
     * @param column The column where it is located
     * @param board The board it is placed in
     * @return Returns true if the value is already there
     */
    public boolean isNumberOnColumn(int number, int column, ArrayList<ArrayList<Integer>> board){
        for (int i = 0; i < board.size() && board.get(i).size() < column; i++){ // For every row i, on the same column...
            if (board.get(i).get(column) == number) {
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
    public boolean isNumberOnRow(int number, int row, ArrayList<ArrayList<Integer>> board){
        for (int j = 0; j < board.get(row).size(); j++){ // For every column j, on the same row...
            if (board.get(row).get(j) == number) {
                return true;
            }
        }
        return false;
    }
}
