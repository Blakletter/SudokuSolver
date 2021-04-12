package com.sudoku;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Solver {
    public void solveSudoku(char[][] board, char blank) throws IOException {
        int size = board.length;
        long a = System.currentTimeMillis();
        int solutions = numSolutions(board, blank, size);
        System.out.print("Solving the above board...");

        if (sudokuSolver(board, blank, size)) {
            long b = System.currentTimeMillis();
            System.out.println("solved.");
            printBoard(board);
            System.out.println("Solved the board in " + (b-a) + " milliseconds");
            System.out.println("There are a total of " + solutions + " solutions");
        } else {
            System.out.println("Count not solve board! Uh-Oh!");
        }
        System.in.read();
    }

    public char[][] createSudoku(char blank, int numToRemove, int size) {
        //First generate a solved solution
        char[][] board = new char[size][size];
        for (int i=0; i<size; i++) {
            for (int j=0; j<size; j++) {
                board[i][j] = blank;
            }
        }
        sudokuSolver(board, blank, size);

        if (removeCharacter(board, blank, 0, numToRemove, size)) {
            return board;
        } else return null;
    }

    private boolean removeCharacter(char[][] board, char blank, int count, int max, int size) {
        //Try and remove the characters
        //For each cell in the thing
        if (count==max) return true;
        //Create random ordering to all of the columns/rows
        ArrayList<Integer> rows = new ArrayList<>();
        ArrayList<Integer> cols = new ArrayList<>();
        for (int i=0; i<size; i++) {
            rows.add(i);
            cols.add(i);
        }
        Collections.shuffle(rows);
        Collections.shuffle(cols);


        int r, c;
        for (int i=0; i<size; i++) {
            for (int j=0; j<size; j++) {
                r = rows.get(i);
                c = cols.get(j);
                //Only try and remove the cells with numbers in them
                if (board[r][c]==blank) continue;
                char ch = board[r][c];
                board[r][c] = blank;
                if (numSolutions(board, blank, size)==1 && removeCharacter(board, blank, count+1, max, size)) {
                    return true;
                }
                board[r][c] = ch;
            }
        }
        return false;
    }

    public void printBoard(char[][] board) {
        int size = board.length;
        for (int i=0; i<size; i++) {
            for (int j=0; j<size; j++) {
                System.out.print("  "+board[i][j]);
            }
            System.out.println("");
        }
        System.out.println("");
    }
    public int numSolutions(char[][] board, char blank, int size) {
        int solutions = 0;
        int[] nextSpot = nextBox(board, blank, size);
        //If there is a solution found, return a solution
        if (nextSpot == null) {
            return 1;
        }

        //Generate a random order to our list of nums
        ArrayList<Integer> nums = new ArrayList<>();
        for (int i=1; i<size+1; i++) nums.add(i);
        Collections.shuffle(nums);

        for (int i=0; i<size; i++) {
            char nextChar = (char)(nums.get(i)+'0');
            if (isValid(board, nextSpot[0], nextSpot[1], nextChar, size)) {
                board[nextSpot[0]][nextSpot[1]] = nextChar;
                //Add up all of the solutions that come back from recursive calls
                solutions += numSolutions(board, blank, size);
                board[nextSpot[0]][nextSpot[1]] = blank;
                if (solutions>1) return solutions;
            }
        }
        return solutions;
    }

    public boolean sudokuSolver(char[][] board, char blank, int size) {
        int[] nextSpot = nextBox(board, blank, size);
        //If there are no other spots to fill, it is done!
        if (nextSpot==null) return true;

        //Generate a random order to our list of nums
        ArrayList<Integer> nums = new ArrayList<>();
        for (int i=1; i<size+1; i++) nums.add(i);
        Collections.shuffle(nums);

        for (int i=0; i<size; i++) {

            char nextChar = (char)(nums.get(i)+'0');
            if (isValid(board, nextSpot[0], nextSpot[1], nextChar, size)) {
                board[nextSpot[0]][nextSpot[1]] = nextChar;
                if (sudokuSolver(board, blank, size)) {
                    return true;
                }
            }
        }
        board[nextSpot[0]][nextSpot[1]] = blank;
        return false;
    }

    private int[] nextBox (char[][] board, char blank, int size) {
        //Loop through the entire board and find the next empty slot
        for (int i=0; i<size; i++) {
            for (int j=0; j<size; j++) {
                if (board[i][j]==blank) {
                    return new int[] {i, j};
                }
            }
        }
        return null;
    }

    private boolean isValid(char[][] board, int h, int c, char n, int size) {
        //Get the upper left part of the board
        int r = size/3;
        int cellUpper = (h/r)*r;
        int cellLeft = (c/r)*r;

        for (int i=0; i<size; i++) {
            if (board[h][i]==n) return false;
            if (board[i][c]==n) return false;
        }

        for (int i=0; i<r; i++) {
            for (int j=0; j<r; j++) {
                if (board[cellUpper+i][cellLeft+j]==n) return false;
            }
        }
        return true;
    }
}
