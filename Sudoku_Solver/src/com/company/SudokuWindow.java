// Class that houses all logic related to the sudoku puzzle
// Author: Matthew Foreman
// Date: 09-21-2021

package com.company;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

public class SudokuWindow extends JFrame {

    private int winHeight = 600;
    private int winWidth = 800;
    private String file;
    private SudokuPuzzle puzzle;
    private SudokuDisplay display;


    public SudokuWindow(String fileName) {
        file = fileName;
        dynamicScaling();

        this.setTitle("Sudoku Solver");
        this.setSize(winWidth, winHeight);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        puzzle = new SudokuPuzzle(fileName);
        puzzle.setDisplay(display);
        display = new SudokuDisplay(puzzle);

        this.add(display);

        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                puzzle.loadToAssignment();
            }
        });

        this.setVisible(true);
    }

    public void dynamicScaling() {
        if (file.contains("16x16")) {
            winHeight = 800;
            winWidth = 1300;
            SudokuDisplay.rescale(16);
        }

        if (file.contains("25x25")) {
            winHeight = 1080;
            winWidth = 1920;
            SudokuDisplay.rescale(25);
        }
    }

    public static void main(String[] args) {
        String fileName = "data" + File.separator + "16x16_medium.txt";
        SudokuWindow sudokuWindow = new SudokuWindow(fileName);
    }


}
