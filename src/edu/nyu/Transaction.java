package edu.nyu;

import java.util.ArrayList;
import java.util.List;

public class Transaction {
    List<Lock> locks;
    List<History> transactionHistory;
    int versionNumber;
    String transactionName;

    public Transaction(int versionNumber, String transactionName) {
        locks = new ArrayList<>();
        transactionHistory = new ArrayList<>();
        this.versionNumber = versionNumber;
        this.transactionName = transactionName;
    }
}
