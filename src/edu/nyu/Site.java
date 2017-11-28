package edu.nyu;

import java.util.ArrayList;
import java.util.List;

public class Site {
    int siteNumber;
    List<Variable> variables;
    boolean available;

    public Site(int siteNumber) {
        this.siteNumber = siteNumber;
        variables = new ArrayList<>();
        available = true;
    }

    public void addVariable(Variable v) {
        variables.add(v);
    }

    public void print() {
        for (Variable v : variables) {
            System.out.println("x" + v.number + "." + siteNumber + "=" + v.values.get(this.siteNumber).value);
        }
    }
}
