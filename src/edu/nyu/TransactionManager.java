package edu.nyu;

import java.util.*;

/**
 * Transaction Manager:
 * A single transaction manager
 * that translates read and write requests on variables to read and write requests
 * on copies using the available copy algorithm described in the notes.
 * The transaction manager never fails.
 * https://cs.nyu.edu/courses/fall17/CSCI-GA.2434-001/
 */
public class TransactionManager {
    Database db;
    // have 20 variables
    Map<String, Variable> variableMap;
    // have 10 sites
    Map<Integer, Site> siteMap;
    Map<String, Transaction> transactionMap;
    List<Transaction> transactionAge;
    Map<Integer, Version> multiVersion;
    int latestVersionNumber;
    List<String> waitList;
    //after looping through current waitList, some commands still can't be executed.
    List<String> nextWaitList;

    public TransactionManager() {
        this.db = new Database();
        this.siteMap = new HashMap<>();
        this.variableMap = new HashMap<>();
        transactionMap = new HashMap<>();
        transactionAge = new ArrayList<>();
        multiVersion = new HashMap<>();
        latestVersionNumber = 0;
        this.waitList = new ArrayList<>();
        this.nextWaitList = new ArrayList<>();

        initializeDatabase();
        initializeSite();
        initializeVariable();
        createVersion(latestVersionNumber);
    }

    private void createVersion(int latestVersionNumber) {
        Map<String, Integer> variables = new HashMap<>();
        for (int i = 1; i <= 20; i++) {
            int value = -1;
            Map<Integer, VariableInfo> values = variableMap.get("x" + i).siteToVariable;
            for (VariableInfo info : values.values()) {
                value = info.value;
            }
            String variableName = "x" + i;
            variables.put(variableName, value);
        }
        Version version = new Version(latestVersionNumber, variables);
        multiVersion.put(latestVersionNumber, version);
    }

    private void initializeDatabase() {
        Database db = new Database();
    }

    public void initializeSite() {
        for (int i = 1; i <= 10; i++) {
            Site site = new Site(i);
            siteMap.put(i, site);
            db.addSite(site);
        }
    }

    public void initializeVariable() {
        for (int i = 1; i <= 20; i++) {
            String variableName = "x" + i;
            Variable v = new Variable(i);
            variableMap.put(variableName, v);
            //if variable is even, it exists on all sits
            if (i % 2 == 0) {
                for (int j = 1; j < 10; j++) {
                    siteMap.get(j).addVariable(v);
                }
            }
            else {
                int siteNumber = i % 10 + 1;
                siteMap.get(siteNumber).addVariable(v);
            }
        }
    }

    void runTest(String input) {
        String[] instructions = input.split("\n");
        for (String instruction : instructions) {
            // when each tick starts

            // cycle detection
            if (waitList.size() > 1) {
                List<Transaction> transactionsInCycle = cycleDetection(waitList);
                if (transactionsInCycle == null || transactionsInCycle.size() > 1) {
                    //abort the youngest transaction
                    Transaction youngest = getYoungestTransaction(transactionsInCycle);
                    abort(youngest);
                }
            }

            //check commands in waitinglist
            for (String waitingCommand : waitList) {
                checkCommand(waitingCommand);
            }
            //check commands in the new tick
            checkCommand(instruction);
            //update waitList for next tick
            waitList = new ArrayList<>(nextWaitList);
            nextWaitList = new ArrayList<>();
        }
    }

    private void checkCommand(String instruction) {
        String[] commandFileds = instruction.split("\\(");
        String command = commandFileds[0];
        String[] fields = commandFileds[1].replace(")", "").split(",");
        if (command.equals("R")) {
            String transaction = fields[0];
            String variable = fields[1];
            //if current read can't be executed
            if (!read(transaction, variable)) {
                nextWaitList.add(instruction);
            }
        }
        else if (command.equals("W")) {
            String transaction = fields[0];
            String variable = fields[1];
            String value = fields[2];
            //if current write can't be executed
            if (!write(transaction, variable, value)) {
                nextWaitList.add(instruction);
            }
        }
        else if (command.equals("recover")) {
            String site = fields[0];
            recoverSite(site);
        }
        else if (command.equals("fail")) {
            String site = fields[0];
            failSite(site);
        }
        else if (command.equals("end")) {
            String transaction = fields[0];
            endTransaction(transaction);
        }
        else if (command.equals("begin")) {
            String transaction = fields[0];
            beginTransaction(transaction);

        }
        else if (command.equals("beginRO")) {
            String transaction = fields[0];
            beginReadOnlyTransaction(instruction, transaction);
        }
        else if (command.equals("dump")) {
            //dump()
            if (instruction.trim().equals("dump()")) {
                db.print();
            }
            //dump(xj)
            else if (instruction.contains("x")) {
                String variable = fields[0];
                variableMap.get(variable).print();
            }
            // dump(i)
            else {
                int site = Integer.parseInt(fields[0]);
                System.out.println("site " + site + ": ");
                siteMap.get(site).print();
            }
        }
    }


    private void abort(Transaction transaction) {
        /* need 1: release T's locks on variables -> update VariableInfo and Site */
        for (History history : transaction.transactionHistory) {
            Variable v = variableMap.get(history.variableName);
            List<Integer> sites = history.sites;
            for (Integer site : sites) {
                /* need 1.1 update the variable on that site -> release locks */
                v.siteToVariable.get(site).writeLock = false;
                v.siteToVariable.get(site).readLock = 0;

                /* need 1.2 update site's involved transaction. */
                siteMap.get(site).removeTransaction(transaction);
            }
        }

        transactionMap.remove(transaction.transactionName);
        transactionAge.remove(transaction);

        /* need 2: update waitList commands to remove transaction */
        for (int i = waitList.size(); i >= 0; i--) {
            if (waitList.get(i).contains(transaction.transactionName)) {
                waitList.remove(i);
            }
        }
    }

    //TODO: Cycle detection
    private List<Transaction> cycleDetection(List<String> waitList) {

    }

    private Transaction getYoungestTransaction(List<Transaction> transactionsInCycle) {
        int youngestIndex = Integer.MAX_VALUE;
        for (Transaction t : transactionsInCycle) {
            youngestIndex = Math.min(transactionAge.indexOf(t), youngestIndex);
        }
        return transactionAge.get(youngestIndex);
    }

    private void beginReadOnlyTransaction(String instruction, String transaction) {
        Transaction t = new Transaction(latestVersionNumber, transaction);
        transactionMap.put(transaction, t);
    }

    private void beginTransaction(String transaction) {
        Transaction t = new Transaction(-1, transaction); //not read-only transaction's versionNumber set to default -1
        transactionMap.put(transaction, t);
        transactionAge.add(t);
    }

    //TODO: commit transaction
    private void endTransaction(String transaction) {


    }

    private void failSite(String site) {
        int siteNumber = Integer.parseInt(site);
        Site s = siteMap.get(siteNumber);
        s.available = false;
        //site is down, lock table is cleared
        for (Variable v : s.variables) {
            v.siteToVariable.get(siteNumber).writeLock = false;
            v.siteToVariable.get(siteNumber).readLock = 0;
        }
        //abort transaction which has lock on this site.
        for (Transaction t : s.involvedTransactions) {
            abort(t);
        }
        //TODO: update version
    }

    private void recoverSite(String site) {
        int siteNumber = Integer.parseInt(site);
        Site s = siteMap.get(siteNumber);
        s.available = true;
    }

    private boolean write(String transaction, String variable, String valueString) {
        //if this transaction already been aborted, then skip this command
        if (!transactionMap.containsKey(transaction)) {
            //return true to skip this command.
            return true;
        }

        Transaction t = transactionMap.get(transaction);
        Variable v = variableMap.get(variable);
        int value = Integer.parseInt(valueString);
        boolean getLock = true;
        /* scenario 1: if a transaction is older than T requires an write lock or read lock on x in the waitList,
        then T can't get write lock on x
        */
        for (String waitCommand : waitList) {
            if (waitCommand.contains(variable)) {
                getLock = false;
                break;
            }
        }

        /* scenario 2: if every site containing x is down,
        then T can't get write lock on x
        */
        if (isNoAvailableCopy(variable)) {
            getLock = false;
        }

        /* if one of the available variables already has write lock or read lock,
        then T can't get write lock (except that transaction is the same transaction)
        */
        for (Transaction tr : transactionMap.values()) {
            for (Lock lock : tr.locks) {
                if (lock.variable.equals(variable) && !tr.transactionName.equals(transaction)) {
                    getLock = false;
                    break;
                }
            }
        }

        if (getLock) {
            //write lock on each available site containing the variable
            List<Integer> availableSites = new ArrayList<>();
            for (Map.Entry<Integer, VariableInfo> entry : v.siteToVariable.entrySet()) {
                int site = entry.getKey();
                if (siteMap.get(site).available) {
                    entry.getValue().writeLock = true;
                    //update transaction
                    Lock lock = new Lock(variable, site, "write");
                    transactionMap.get(transaction).locks.add(lock);
                    availableSites.add(site);

                }
            }
            addHistoryToTransaction(variable, value, availableSites, transaction, "write");
            addTransactionToSite(availableSites, t);
            return true;
        }

        //else can't execute this command right now
        return false;
    }

    private boolean read(String transaction, String variable) {
        //if this transaction already been aborted, then skip this command
        if (!transactionMap.containsKey(transaction)) {
            //return true to skip this command.
            return true;
        }

        Transaction t = transactionMap.get(transaction);
        //if T is read-only transaction
        if (t.versionNumber != -1) {
            Version version = multiVersion.get(t.versionNumber);
            // if there is no available site for reading
            if (isNoAvailableCopy(variable)) {
                //can't execute read, have to wait for the site up
                return false;
            }
            int value = version.variables.get(variable);

            //read-only transaction could print out directly
            System.out.println(transaction + "reads version " + t.versionNumber + "'s " + variable + ":" + value);
            return true;
        }
        //if T is not a read-only transaction, t.versionNumber == -1
        else {
            boolean getLock = true;

            /* scenario 1: if a transaction is older than T requires an write lock on x in the waitList,
            then T can't get read lock on x
            */
            for (String waitCommand : waitList) {
                if (waitCommand.contains(variable) && waitCommand.contains("W")) {
                    getLock = false;
                    break;
                }
            }
            /* scenario 2: if x already has write lock,
            then T can't get read lock on x
            */
            for (VariableInfo v : variableMap.get(variable).siteToVariable.values()) {
                //if one variable at one site has a write lock, then getLock = false
                if (v.writeLock = true) {
                    getLock = false;
                }
            }
            /* scenario 3: if every site containing x is down,
            then T can't get read lock on x
             */
            if (isNoAvailableCopy(variable)) {
                getLock = false;
            }
            if (getLock) {
                //read lock on one site, we use the smallest index available site
                List<Integer> sites = new ArrayList<>(variableMap.get(variable).siteToVariable.keySet());
                Collections.sort(sites);
                int targetSite = -1;
                for (Integer site : sites) {
                    if (siteMap.get(site).available) {
                        targetSite = site;
                        break;
                    }
                }
                //update variableInfo in Variable
                variableMap.get(variable).siteToVariable.get(targetSite).readLock++;

                //update transaction
                Lock readLock = new Lock(variable, targetSite, "read");
                transactionMap.get(transaction).locks.add(readLock);
                int value = variableMap.get(variable).siteToVariable.get(targetSite).value;

                List<Integer> targetSites = new ArrayList<>();
                targetSites.add(targetSite);
                addHistoryToTransaction(variable, value, targetSites, transaction, "read");
                addTransactionToSite(targetSites, t);
                return true;
            }
            //else can't execute this command right now
            return false;
        }
    }

    private void addHistoryToTransaction(String variable, int value, List<Integer> sites, String transaction, String type) {
        History history = new History("read", variable, value, sites);
        transactionMap.get(transaction).transactionHistory.add(history);

    }

    private void addTransactionToSite(List<Integer> sites, Transaction transaction) {
        for (Integer site : sites) {
            siteMap.get(site).addTransaction(transaction);
        }
    }

    private boolean isNoAvailableCopy(String variable) {
        boolean noCopies = true;
        for (Integer site : variableMap.get(variable).siteToVariable.keySet()) {
            if (siteMap.get(site).available) {
                noCopies = false;
            }
        }
        return noCopies;
    }

}