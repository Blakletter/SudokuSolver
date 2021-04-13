package com.sudoku;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        int num = Integer.valueOf(args[0]);
        int threads= Integer.valueOf(args[1]);
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
        char[][] finishedBoard = s.createSudoku( 9,'.',num, threads);
        //s.printBoard(finishedBoard);
    }
}
