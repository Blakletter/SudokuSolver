package com.sudoku;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Solver {

    public void solveSudoku(char[][] board, char blank) throws IOException {
        int size = board.length;
        if (sudokuSolver(board, blank, size)) {

        } else {
            sendMessage("Can not solve the board.");
        }
    }

    public char[][] createSudoku(int size, char blank, int numToRemove, int threads) throws IOException {
        //Create our thread pool
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        MyRunnable[] threadList = new MyRunnable[threads];
        sendMessage("Spinning up threads 1-" + threads + ". Trying to remove "+ numToRemove + " cells.");
        for (int i=0; i<threads; i++) {
            //Pass in the board to each of our threads, letting them work concurrently on a solution
            MyRunnable worker = new MyRunnable(blank, numToRemove, size);
            threadList[i] = worker;
            executorService.execute(worker);
        }
        executorService.shutdown();
        while (!executorService.isTerminated());
        sendMessage("All threads finished.\n");
        return null;
    }
    public void sendMessage(String message) {
        System.out.println(message);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.noBody())
                .setHeader("msg",message + "</br>")
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
    public char[][] createSudoku(char blank, int numToRemove, int size) {
        //First generate a solved solution
        char[][] board = new char[size][size];
        for (int i=0; i<size; i++) {
            for (int j=0; j<size; j++) {
                board[i][j] = blank;
            }
        }
        sudokuSolver(board, blank, size);
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
                if (solutions>1) {
                    nums = null;
                    return solutions;
                }
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

