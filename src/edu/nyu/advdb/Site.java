package edu.nyu.advdb;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class stores the site information.
 */
public class Site {
    private int siteNumber;
    private List<Variable> variables;
    private boolean available;
    private Set<Transaction> involvedTransactions;

    public Site(int siteNumber) {
        this.siteNumber = siteNumber;
        variables = new ArrayList<>();
        available = true;
        this.involvedTransactions = new HashSet<>();
    }

    public void addVariable(Variable v) {
        variables.add(v);
    }

    public void print() {
        for (Variable v : variables) {
            System.out.println("x" + v.getNumber() + "." + siteNumber + "=" + v.getSiteToVariableMap().get(this.siteNumber).getValue());
        }
    }

    public void addTransaction(Transaction t) {
        involvedTransactions.add(t);
    }

    public void removeTransaction(Transaction transaction) {
        involvedTransactions.remove(transaction);
    }

    public int getSiteNumber() {
        return siteNumber;
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public boolean isAvailable() {
        return available;
    }

    public Set<Transaction> getInvolvedTransactions() {
        return involvedTransactions;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
