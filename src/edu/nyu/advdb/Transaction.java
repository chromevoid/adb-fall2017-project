package edu.nyu.advdb;

import java.util.ArrayList;
import java.util.List;

/**
 * This method stores transaction information.
 */
public class Transaction {
    private List<Lock> locks;
    private List<History> transactionHistory;
    private int versionNumber;
    private String transactionName;

    public Transaction(int versionNumber, String transactionName) {
        locks = new ArrayList<>();
        transactionHistory = new ArrayList<>();
        this.versionNumber = versionNumber;
        this.transactionName = transactionName;
    }

    public void addLock(Lock lock) {
        this.locks.add(lock);
    }

    public void addTransactionHistory(History history) {
        this.transactionHistory.add(history);
    }

    public List<History> getTransactionHistory() {
        return transactionHistory;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public String getTransactionName() {
        return transactionName;
    }

    public List<Lock> getLocks() {
        return locks;
    }
}
