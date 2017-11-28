package edu.nyu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transaction {
    List<Lock> locks;
    Map<String , Variable> writeVariables;
    int versionNumber;
    String transactionName;

    public Transaction(int versionNumber, String transactionName) {
        locks = new ArrayList<>();
        writeVariables = new HashMap<>();
        this.versionNumber = versionNumber;
        this.transactionName = transactionName;
    }

}
