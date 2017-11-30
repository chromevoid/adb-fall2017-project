package edu.nyu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transaction {
    List<Lock> locks;
    //TODO: this needs change
    Map<String , Variable> writeVariables;

    List<History> transactionHistory;
    int versionNumber;
    String transactionName;

    public Transaction(int versionNumber, String transactionName) {
        locks = new ArrayList<>();
        writeVariables = new HashMap<>();
        transactionHistory = new ArrayList<>();
        this.versionNumber = versionNumber;
        this.transactionName = transactionName;
    }
}
