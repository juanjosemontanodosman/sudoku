package com.example.sudoku.model;
import java.util.ArrayList;

/**
 *  The Model class contains the game's current state
 *  It is in charge of validating its rules
 *  The mechanism for generating boards was created with AI
 *
 *  @author Juan José Montaño Dósman, código 2538124-3743
 */

public class Model {
    // These are compile-time constants
    private static final int blockWidth = 3;
    private static final int blockHeight = 2;
    private static final short hintUsagesLimit = 0;

    private ArrayList<ArrayList<Integer>> board;
    private short hintUsages = 0;

    public int getHint(int row, int col) throws Exception{
        if (hintUsages > hintUsagesLimit){
            throw new Exception("No se puede dar la pista. Se sobrepasó el límite de ayudas");
        }

        int num = 1;
        while (!isEntryValid(num, row, col)) {
            num++;
        }

        if (num > 6){
            throw new Exception("Ningún número es válido en la posición ("+row+", "+col+")");
        }

        hintUsages++;
        return num;
    }

    public boolean isEntryValid(int number, int row, int col){
        // If any method below returns true, then the given entry is not valid
        // The given number is already somewhere else, either in the same column, row or block
        return !isNumberOnBlock(number, row, col) && !isNumberOnColumn(number, col) && !isNumberOnRow(number, row);
    }

    public boolean isNumberOnColumn(int number, int column){
        for (int i = 0; i < board.get(0).size(); i++){ // For every row i, on the same column...
            if (board.get(i).get(column) == number) {
                return true;
            }
        }
        return false;
    }

    public boolean isNumberOnRow(int number, int row){
        for (int i = 0; i < board.size(); i++){ // For every column, on the same row...
            if (board.get(i).get(row) == number) {
                return true;
            }
        }
        return false;
    }

    public boolean isNumberOnBlock(int number, int row, int col){
        // A 6x6 size board is usually divided in rectangular 3x2 blocks
        // Other board sizes can be arranged in different ways
        for (int i = 0; i < blockWidth; i++){
            for (int j = 0; j < blockHeight; j++) {
                // Position of the corner of the block the number is placed in
                int[] corner = {row - row % blockWidth, col - col % blockHeight};
                // i and j are the position of a number relative to such corner
                // corner[0] +i and corner[1] + j are absolute
                if (board.get(corner[0] + i).get(corner[0] + j) == number) {
                    return true;
                }
            }
        }
        return false;
    }
}
