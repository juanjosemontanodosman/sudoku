package com.example.sudoku.model;
import java.util.ArrayList;

/**
 *  The Model class contains the game's current state
 *  It is in charge of generating a board validating its rules
 *
 *  @author Juan José Montaño Dósman, código 2538124-3743
 */

public class Model {
    // These are compile-time constants
    private static final int boardWidth = 6;
    private static final int boardHeight = 6;
    private static final int blockWidth = 3;
    private static final int blockHeight = 2;
    private static final short hintUsagesLimit = 10;
    private static final int maxNumber = boardWidth;

    private ArrayList<ArrayList<Integer>> board = new ArrayList<>();
    private short hintUsages = 0;

    public void initializeBoard(){
        board.clear();
        // Creates every row
        for (int i = 0; i < boardHeight; i++){
            board.add(new ArrayList<Integer>());
            for (int j = 0; j < boardHeight; j++) {
                board.get(i).add(0);
            }
        }
    }

    public void generateBoard(){
        int number;
        boolean boardIsIncomplete;
        do {
            initializeBoard();
            boardIsIncomplete = false;
            for (int i = 0; i < boardHeight; i++) {
                for (int j = 0; j < boardHeight; j++) {
                    try {
                        board.get(i).set(j, getValidNumber(i, j));
                    } catch (Exception e) {
                        boardIsIncomplete = true;
                        break;
                    }
                }
                if (boardIsIncomplete){ break; }
            }
        } while (boardIsIncomplete);
    }

    public static void printBoard(ArrayList<ArrayList<Integer>> board){
        for (int i = 0; i < boardWidth; i++){
            System.out.println(board.get(i));
        }
    }

    public ArrayList<ArrayList<Integer>> getBoard(){
        return board;
    }

    public int getValidNumber(int row, int col) throws Exception{
        int num = 1;
        while (!isEntryValid(num, row, col)) {
            num++;
        }

        if (num > maxNumber){
            throw new Exception("Ningún número es válido en la posición ("+row+", "+col+")");
        }


        do {
            num = (int)(Math.random()*maxNumber +1);
        } while (!isEntryValid(num, row, col));

        return num;
    }

    public int getHint(int row, int col) throws Exception{
        if (hintUsages > hintUsagesLimit) {
            throw new Exception("No se puede dar la pista. Se sobrepasó el límite de ayudas");
        }

        int num = getValidNumber(row, col);
        // The return value of getValidNumber is not returned immediately
        // because there might not be a possible valid number,
        // in which case hintUsages should not be increased
        hintUsages++;
        return num;
    }

    public boolean isEntryValid(int number, int row, int col){
        // If any method below returns true, then the given entry is not valid
        // The given number is already somewhere else, either in the same column, row or block
        return !isNumberOnBlock(number, row, col) && !isNumberOnColumn(number, col) && !isNumberOnRow(number, row);
    }

    public boolean isNumberOnColumn(int number, int column){
        for (int i = 0; i < boardHeight; i++){ // For every row i, on the same column...
            if (board.get(i).get(column) == number) {
                return true;
            }
        }
        return false;
    }

    public boolean isNumberOnRow(int number, int row){
        for (int j = 0; j < boardWidth; j++){ // For every column j, on the same row...
            if (board.get(row).get(j) == number) {
                return true;
            }
        }
        return false;
    }

    public boolean isNumberOnBlock(int number, int row, int col){
        //System.out.println("Comprobando si existe número en el bloque:");
        // A 6x6 size board is usually divided in rectangular 3x2 blocks
        // Other board sizes can be arranged in different ways
        for (int i = 0; i < blockHeight; i++){
            for (int j = 0; j < blockWidth; j++) {
                // Position of the corner of the block the number is placed in
                int[] corner = {row - row % blockHeight, col - col % blockWidth};
                // i and j are the position of a number relative to such corner
                // corner[0] +i and corner[1] + j are absolute

                if (board.get(corner[0] + i).get(corner[1] + j) == number) {
                    return true;
                }
            }
        }
        return false;
    }
}
