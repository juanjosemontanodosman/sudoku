package com.example.sudoku.model;

import java.util.ArrayList;

/** Class for setting and getting elements from a board*/
public class Board extends ArrayList<ArrayList<Integer>> {

    Board(){};

    Board(Board other){ // Copy constructor
        this.initialize(other.size(), other.size());
        for (int i = 0; i < other.size(); i++){
            for (int j = 0; j < other.size(); j++){
                this.setEntry(i, j, other.getEntry(i, j));
            }
        }
    }

    public int getEntry(int row, int col){
        return this.get(row).get(col);
    }

    public void setEntry(int row, int col, int value){
        this.get(row).set(col, value);
    }

    public boolean isEmpty(int row, int col){ return this.getEntry(row, col) == 0; };

    public void initialize(int width, int height){
        this.clear();
        for (int i = 0; i < height; i++){
            this.add(new ArrayList<>());
            for (int j = 0; j < width; j++){
                this.get(i).add(0);
            }
        }
    }

}
