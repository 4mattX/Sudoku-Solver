// Class that contains the logic for the display of the sudoku puzzle
// Author: Matthew Foreman
// Date: 09-21-2021

package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SudokuDisplay extends JPanel {

    SudokuPuzzle puzzle;
    static int cellSize = 50;
    static int startY = 50;
    static int startX = 50;

    static int offsetX = 15;
    static int offsetY = 35;
    boolean isFirstPaint = true;

    static Font numberFont = new Font("Arial", Font.PLAIN, 30);
    static Font searchFont = new Font("Arial", Font.BOLD, 15);

    Color numberColor = Color.decode("#cceaeb");
    Color initialColor = Color.decode("#4bd6db");
    Color backgroundColor = Color.decode("#182233");
    Color lineColor = Color.decode("#8e53a6");
    Color searchColor = Color.decode("#c27e11");
    Color initialBlockColor = Color.decode("#003b54");

    // Constructor
    public SudokuDisplay(SudokuPuzzle puzzle) {
        this.puzzle = puzzle;
        puzzle.setDisplay(this);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                nextMove();
            }
        });
    }

    public static void rescale(int dimension) {
        if (dimension == 16 || dimension == 25) {
            cellSize = (int) (cellSize / 1.3);
            startX = (int) (startX / 1.3);
            startY = (int) (startY / 1.3);

            offsetX = (int) (offsetX / 1.3);
            offsetY = (int) (offsetY / 1.3);

            numberFont = new Font("Arial", Font.PLAIN, 18);
            searchFont = new Font("Arial", Font.BOLD, 12);
        }
    }

    public void nextMove() {
        puzzle.checkForSingleton();
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setFont(numberFont);
        Graphics2D g2 = (Graphics2D) g;

        // This draws the background of the game
        g2.setColor(backgroundColor);
        g2.fillRect(0, 0, getWidth(), getHeight());


        for (int row = 0; row < puzzle.getSize(); row++) {

            for (int col = 0; col < puzzle.getSize(); col++) {
                g.drawRect(startX + col * cellSize, startY + row * cellSize, cellSize, cellSize);

                if (puzzle.getGridPosition(row, col).getAssignment() >= -1) {

                    // If number was initial
                    if (puzzle.isInitial(row, col)) {
                        // Draws initial square
                        g2.setColor(initialBlockColor);
                        g2.fillRect(startY + (col * cellSize), startX + (row * cellSize), cellSize, cellSize);
                        g.setColor(initialColor);
                    } else {
                        g.setColor(numberColor);
                    }

                    if (puzzle.getGridPosition(row, col).getAssignment() == -1) {
                        g.setColor(lineColor);
                        continue;
                    }

                    g.drawString("" + puzzle.getGridPosition(row, col).getAssignment(), startX + col * cellSize + offsetX, startY + row * cellSize + offsetY);
                    g.setColor(lineColor);
                }
                g.drawRect(startX + col * cellSize, startY + row * cellSize, cellSize, cellSize);
            }

        }

        g2.setStroke(new BasicStroke(4));

        int block = puzzle.getGroupSize();
        for (int row = 0; row < puzzle.getSize(); row+= block) {

            for (int col = 0; col < puzzle.getSize(); col+= block) {
                g.drawRect(startX + col * cellSize,startY + row * cellSize + block, cellSize * block, cellSize * block);
            }
        }

        g.setFont(searchFont);
        g.setColor(searchColor);
        g.drawString("" + puzzle.sizeOfSearchSpace(), startY, startX * 2 + (cellSize * puzzle.getSize()));
    }
}
