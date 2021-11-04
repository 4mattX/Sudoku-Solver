// Class that contains the logic for Sudoku
// Author: Matthew Foreman
// Date: 09-21-2021

package com.company;

import javax.swing.*;
import java.io.File;
import java.math.BigInteger;
import java.util.*;

public class SudokuPuzzle {

    private static int size;
    private static int groupSize;
    private static Variable[][] grid;
    private List<Variable> assignment;
    private int[] domainElement;
    private static int[][] initialGrid;
    private HashMap<String, Constraint> constraints = new HashMap<String, Constraint>();
    private SudokuDisplay display;

    // Used to color console
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_WHITE = "\u001B[37m";
    private static final String ANSI_YELLOW = "\u001B[33m";

    private static int min = 0;
    private static int iter = 0;

    public SudokuPuzzle(String fileName) {
        loadPuzzleFromFile(fileName);
    }

    private void loadPuzzleFromFile(String fileName) {
        File file = new File(fileName);

        try {

            Scanner scanner = new Scanner(file);

            size = scanner.nextInt();
            groupSize = scanner.nextInt();

            grid = new Variable[size][size];
            initialGrid = new int[size][size];

            for (int row = 0; row < size; row++) {

                for (int col = 0; col < size; col++) {
                    int newValue = scanner.nextInt();
                    Variable newVar;

                    String name = "" + row + "," + col;
                    if (newValue == 0) {
                        newVar = new Variable(-1, 1, size, name);

                    } else {
                        newVar = new Variable(newValue, newValue, newValue, name); // third one might be size?
                        initialGrid[row][col] = 1;
                    }
                    grid[row][col] = newVar;
                }
            }

            scanner.close();

        } catch (Exception e) {
            String errorMessage = "An IOException was thrown";
            JOptionPane.showMessageDialog(null, errorMessage, "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }


    // Re-evaluate all variables and adjust grid
    public static void reviseGrid(int row, int col, Variable newVar) {
        grid[row][col] = newVar;
    }

    // Where constraints are created
    public void checkForSingleton() {
        // Loop for every Variable in grid[][]
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {

                // Check if Singleton
                if (isSingleton(row, col)) {
                    arcConsistency_1Round(row, col);
                }

            }
        }
        display.paintImmediately(0, 0, display.getWidth(), display.getHeight());
    }

    public void setDisplay(SudokuDisplay display) {
        this.display = display;
    }

    public void arcConsistency_1Round(int row, int col) {
        // Create a constraint for each type: row, col, group
        // if constraint already exists, then it will override

        List<String> notNeededSearch = new ArrayList<String>();

        // Row Constraint
        for (int i = 0; i < size; i++) {
            if (i != col) {
                String name = row + "," + col + ":" + row + "," + i;
                constraints.put(name, new Constraint(name, "row", getBinaryList(row, col, row, i)));
                constraints.get(name).reviseAllDiff();
                notNeededSearch.add(name.split(":")[1]);
            }
        }

        // Col Constraint
        for (int i = 0; i < size; i++) {
            if (i != row) {
                String name = row + "," + col + ":" + i + "," + col;
                constraints.put(name, new Constraint(name, "col", getBinaryList(row, col, i, col)));
                constraints.get(name).reviseAllDiff();
                notNeededSearch.add(name.split(":")[1]);
            }
        }

        // Group Constraint
        getGroup(row, col).forEach(var -> {
            // Tests all values but itself
            if (grid[row][col].getName().equalsIgnoreCase(var.getName())) {
                return;
            }

            if (notNeededSearch.contains(var.getName())) {
                return;
            }

            String name = row + "," + col + ":" + var.getName();
            constraints.put(name, new Constraint(name, "group", getBinaryList(row, col, Integer.parseInt(var.getName().split(",")[0]), Integer.parseInt(var.getName().split(",")[1]))));
            constraints.get(name).reviseAllDiff();
        });

    }

    public void backTrackSearch() {
        // traverses through entire assignment list

        for (int i = 0; i < size * size; i++) {
            if (assignment.get(i).getDomain().size() == 2) {
                iter = i;
                min = iter;
                break;
            }
        }

        // Instantiate domainElement array
        domainElement = new int[size * size];

        long startTime = System.currentTimeMillis();

        while (iter >= min && iter < size * size) {
            findSuccessfulAssignment();
            display.paintImmediately(0, 0, display.getWidth(), display.getHeight());
        }
        checkForSingleton();

        long endTime = System.currentTimeMillis();
        System.out.println(ANSI_RED + "Total Elapsed Time: " + ANSI_WHITE + (endTime - startTime) + ANSI_YELLOW + " milliseconds");


        if (iter < min - 1) {
            System.out.println("!!! NO SOLUTION POSSIBLE !!!");
        }

    }

    // Loads values in order of domain into assignment ArrayList
    public void loadToAssignment() {
        assignment = new ArrayList<>();
        // Will add all variables into assignment list based
        // on domain size
        for (int dSize = 0; dSize < size; dSize++) {
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {

                    if (grid[row][col].getDomain().size() == dSize) {
                        assignment.add(grid[row][col]);
                    }

                }
            }
        }

        // Once every variables is loaded initially,
        // start the backTrackSearch
        backTrackSearch();
    }

    // Verifies that the assignment of a variable is a valid assignment
    public boolean isConsistentAssignment(Variable variable, int value) {

        int varRow = Integer.parseInt(variable.getName().split(",")[0]);
        int varCol = Integer.parseInt(variable.getName().split(",")[1]);

        // Checks for row constraints
        for (int i = 0; i < size; i++) {
            if (grid[varRow][i].getAssignment() == value) {
                return false;
            }
        }

        // Checks for col constraints
        for (int i = 0; i < size; i++) {
            if (grid[i][varCol].getAssignment() == value) {
                return false;
            }
        }

        // Checks for group constraints
        Variable[] groupArray = getGroup(varRow, varCol).toArray(new Variable[0]);
        for (int i = 0; i < groupArray.length; i++) {
                if (value == groupArray[i].getAssignment() && value != -1) {
                    return false;
                }
        }

        return true;
    }

    public void findSuccessfulAssignment() {
        // Loop through entire domain of Variable
        for (int i = 0; i < assignment.get(iter).getDomain().size(); i++) {
            // The current domain value that we are looking at
            int domain = assignment.get(iter).getDomainElement(i);
            // The size of the domain that we are searching through
            int domainSize = assignment.get(iter).getDomain().size();

            // Check if domain is consistent with Variable
            if (isConsistentAssignment(assignment.get(iter), domain)) {

                // Check if we haven't used this domain before
                int indexDomainElement = assignment.get(iter).getDomain().indexOf(domainElement[iter]);
                if (indexDomainElement <= i) {

                    assignment.get(iter).setAssignment(domain);
                    int row = Integer.parseInt(assignment.get(iter).getName().split(",")[0]);
                    int col = Integer.parseInt(assignment.get(iter).getName().split(",")[1]);
                    reviseGrid(row, col, assignment.get(iter));

                    domainElement[iter] = domain;
                    iter++;
                    return;
                }
                continue;
            }

            // Check if domain search is exhausted
            if (domainSize == (i + 1)) {
                assignment.get(iter).setAssignment(-1);
                int row = Integer.parseInt(assignment.get(iter).getName().split(",")[0]);
                int col = Integer.parseInt(assignment.get(iter).getName().split(",")[1]);
                reviseGrid(row, col, assignment.get(iter));

                domainElement[iter] = 0;
                iter--;
                return;
            }

        }
    }

    public boolean isSingleton(int row, int col) {

        if (grid[row][col].getAssignment() != -1) {
            return true;
        }
        return false;
    }

    public void printDomain() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                System.out.print(grid[row][col].getDomain() + " ");
            }
            System.out.println();
        }
    }

    public boolean isInitial(int row, int col) {
        if (initialGrid[row][col] == 1) {
            return true;
        }
        return false;
    }

    public static Variable getGridPosition(int row, int col) {
        return grid[row][col];
    }

    public static int getSize() {
        return size;
    }

    public static int getGroupSize() {
        return groupSize;
    }

    public List<Variable> getBinaryList(int rowV1, int colV1, int rowV2, int colV2) {
        List<Variable> list = new ArrayList<Variable>();
        list.add(grid[rowV1][colV1]);
        list.add(grid[rowV2][colV2]);
        return list;
    }

    public List<Variable> getGroup(int row, int col) {

        List<Variable> groupValues = new ArrayList<>();

        int groupRow = row / groupSize;
        int groupCol = col / groupSize;

        // Loop through entire grid
        for (int gridRow = 0; gridRow < size; gridRow++) {
            for (int gridCol = 0; gridCol < size; gridCol++) {

                // If looped Variable is in same group as value passed in, then add it to List
                if ((gridRow / groupSize) == groupRow && (gridCol / groupSize) == groupCol) {
                    groupValues.add(grid[gridRow][gridCol]);
                }

            }
        }
        return groupValues;
    }

    public static int[][] getInitialGrid() {
        return initialGrid;
    }

    public BigInteger sizeOfSearchSpace() {
        BigInteger searchSpace = new BigInteger("1");

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                searchSpace = searchSpace.multiply(BigInteger.valueOf(this.grid[row][col].domainSize()));
            }
        }

        return searchSpace;
    }

    public String toString() {
        return grid.toString();
    }

}