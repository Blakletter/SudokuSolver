package com.sudoku;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MyRunnable implements Runnable {
    private volatile char[][] board;
    private final char blank;
    private final int numToRemove;
    private int tries = 0;
    private int size;
    MyRunnable(char blank, int numToRemove, int size) {
        this.size = size;
        this.blank = blank;
        this.numToRemove = numToRemove;
    }
    @Override
    public void run() {
        board = new char[size][size];
        for (int j=0; j<size; j++) {
            for (int k=0; k<size; k++) {
                board[j][k] = blank;
            }
        }
        sudokuSolver(blank, size);

        while (!removeCharacter(blank, 0, numToRemove, board.length)) {
            board = new char[size][size];
            for (int j=0; j<size; j++) {
                for (int k=0; k<size; k++) {
                    board[j][k] = blank;
                }
            }
            sudokuSolver(blank, size);
            tries = 0;
        }
        String b = "";
        int size = board.length;
        for (int i=0; i<size; i++) {
            for (int j=0; j<size; j++) {
                if (board[i][j]=='.') {
                    b+="&nbsp";
                }
                b+= "&nbsp&nbsp&nbsp" + board[i][j];
            }
            b+="</br>";
        }
        b+="</br>";
        sendMessage(b);
    }

    public void sendMessage(String message) {
        System.out.println(message);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.noBody())
                .setHeader("msg",message+ "</br>")
                .uri(URI.create("http://localhost:3000/console"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public boolean sudokuSolver(char blank, int size) {
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
                if (sudokuSolver(blank, size)) {
                    return true;
                }
            }
        }
        board[nextSpot[0]][nextSpot[1]] = blank;
        return false;
    }
    public char[][] getBoard() {
        return board;
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

    private void deepClone(char[][] source, char[][] child) {
        for (int i=0; i<source.length; i++) {
            for (int j=0; j<source.length; j++) {
                child[i][j] = source[i][j];
            }
        }
    }

    private boolean removeCharacter(char blank, int count, int max, int size) {
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
                if (numSolutions(blank, size)==1 && removeCharacter(blank, count+1, max, size)) {
                    rows = null;
                    cols = null;
                    return true;
                }
                board[r][c] = ch;
                if (tries>12) {
                    return false;
                }
            }
        }
        tries++;
        rows = null;
        cols = null;
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

    public int numSolutions(char blank, int size) {
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
                solutions += numSolutions(blank, size);
                board[nextSpot[0]][nextSpot[1]] = blank;
                if (solutions>1) {
                    nums = null;
                    return solutions;
                }
            }
        }
        return solutions;
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
