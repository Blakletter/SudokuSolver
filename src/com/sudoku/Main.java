package com.sudoku;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        char[][] board = new char[][] {
                {'5','3','.','.','7','.','.','.','.'},
                {'6','.','.','1','9','5','.','.','.'},
                {'.','9','8','.','.','.','.','6','.'},
                {'8','.','.','.','6','.','.','.','3'},
                {'4','.','.','8','.','3','.','.','1'},
                {'7','.','.','.','2','.','.','.','6'},
                {'.','6','.','.','.','.','2','8','.'},
                {'.','.','.','4','1','9','.','.','5'},
                {'.','.','.','.','8','.','.','7','9'}};

        Solver s = new Solver();

        char[][] createdBoard = s.createSudoku('.', 56, 9);

        if (createdBoard == null){
            System.out.println("Can not generate sudoku board with one solution!");
        } else {
            s.printBoard(createdBoard);
            s.solveSudoku(createdBoard, '.');
            s.printBoard(createdBoard);
        }
    }
}
