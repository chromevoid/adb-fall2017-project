package edu.nyu;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Site {
    int siteNumber;
    List<Variable> variables;
    boolean available;
    Set<Transaction> involvedTransactions;

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
            System.out.println("x" + v.number + "." + siteNumber + "=" + v.siteToVariable.get(this.siteNumber).value);
        }
    }

    public void addTransaction(Transaction t) {
        involvedTransactions.add(t);
    }

    public void removeTransaction(Transaction transaction) {
        involvedTransactions.remove(transaction);
    }
}
