// Class that houses all logic related to the Constraints
// Author: Matthew Foreman
// Date: 09-21-2021

package com.company;

import java.util.List;

public class Constraint {

    private String name;
    private String type;
    private List<Variable> vars;

    public Constraint(String name, String type, List vars) {
        this.name = name;
        this.type = type;
        this.vars = vars;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public List getVars() {
        return vars;
    }

    public String toString() {
        return "name: " + name + " type: " + type + " vars: " + vars + "\n";
    }

    public void addVariable() {

    }

    // Finds the exact index value of the variable from the grid according to this constraint, then returns it
    public Variable getVariable() {
        String rowString = this.getName().split(",")[0];
        int row = Integer.parseInt(rowString.replaceAll("[^0-9]", ""));

        String colString = this.getName().split(",")[1];
        int col = Integer.parseInt(colString);

        return SudokuPuzzle.getGridPosition(row, col);
    }

    public void reviseAllDiff() {

        if (!vars.get(0).hasSupport_differentValue(vars.get(1))) {
            vars.get(1).removeFromDomain(vars.get(0).getAssignment());

            String var2Value = this.getName().split(":")[1];

            int rowV2 = Integer.parseInt(var2Value.split(",")[0]);
            int colV2 = Integer.parseInt(var2Value.split(",")[1]);

            SudokuPuzzle.reviseGrid(rowV2, colV2, vars.get(1));
        }

    }

}
