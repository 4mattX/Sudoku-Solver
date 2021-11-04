// Class that houses all logic related to the Variable
// Author: Matthew Foreman
// Date: 09-21-2021

package com.company;

import java.util.LinkedList;
import java.util.List;

public class Variable {

    private String name;
    private LinkedList<Integer> domain;
    private int assignment;

    public Variable(int assignment, int startRange, int endRange, String name ){

        this.name = name;
        this.domain = new LinkedList<Integer>();
        this.assignment = assignment;

        for (int i = startRange; i <= endRange; i++) {
            domain.add(i);
        }

    }

    public void setAssignment(int assignment) {
        this.assignment = assignment;
    }

    public String getName() {
        return name;
    }

    public int getAssignment() {
        return assignment;
    }

    public int getDomainElement(int index) {
        return domain.get(index);
    }

    public void removeFromDomain(int value) {
        domain.remove(domain.indexOf(value));
        if (domain.size() == 1) {
            this.setAssignment(domain.get(0));
            return;
        }
    }

    public LinkedList<Integer> getDomain() {
        return domain;
    }

    public int domainSize() {
        return domain.size();
    }

    public boolean hasSupport_differentValue(Variable v2) {
        // If this values has support within the variables domain
        if (v2.getDomain().contains(this.getAssignment())) {
            return false;
        }

        return true;
    }

    public String toString() {
        String message = "name: " + name + " assignment: " + assignment;
        return message;
    }
}
