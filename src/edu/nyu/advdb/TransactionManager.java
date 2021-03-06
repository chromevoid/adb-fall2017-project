package edu.nyu.advdb;

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
    private Database db;
    private Map<String, Variable> variableMap;
    private Map<Integer, Site> siteMap;
    private Map<String, Transaction> transactionMap;
    private List<String> transactionAge;
    private Map<Integer, Version> multiVersion;
    private int latestVersionNumber;
    private List<String> waitList;
    //after looping through current waitList, some commands still can't be executed.
    private List<String> nextWaitList;

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

    /**
     * This method creates a new version based on current version number
     * and every variable value from available sites.
     *
     * @param latestVersionNumber current version number
     */
    private void createVersion(int latestVersionNumber) {
        Map<String, Integer> variableToValue = new HashMap<>();
        Map<String, List<Integer>> variableToSite = new HashMap<>();
        for (int i = 1; i <= Constants.VARIABLE_AMOUNT; i++) {
            int value = -1;
            // siteToVariableMap: Map<siteNumber, variableInfo(value, canRead, readLock, writeLock)>
            Map<Integer, VariableInfo> siteToVariableMap = variableMap.get("x" + i).getSiteToVariableMap();
            // entry: siteNumber
            // for (each site that has xi)
            for (Map.Entry<Integer, VariableInfo> entry : siteToVariableMap.entrySet()) {
                // site is available &&
                // entry.getValue() is a type VariableInfo object
                // entry.getValue().isCanRead() returns if we can read the variable on this site
                boolean isUpAndCanRead = siteMap.get(entry.getKey()).isAvailable() && entry.getValue().isCanRead();
                if(isUpAndCanRead) {
                    value = entry.getValue().getValue();
                    String variableName = "x" + i;
                    // the next statement is duplicated for variables with replication
                    // BUT: since put method also updates existing key-value pair,
                    //      and the value for variables with replication is consistent on each up adn canRead site
                    //      therefore, we can just execute the next statement without any other operations
                    variableToValue.put(variableName, value);
                    // this site has the xi of the latest version,
                    // so add this site to the version site recorder
                    variableToSite.putIfAbsent(variableName, new ArrayList<>());
                    variableToSite.get(variableName).add(entry.getKey());
                }
            }

        }
        Version version = new Version(latestVersionNumber, variableToValue, variableToSite);
        multiVersion.put(latestVersionNumber, version);
    }

    /**
     * This method is used to create a new database wrapping up every data and information.
     */
    private void initializeDatabase() {
        Database db = new Database();
    }

    /**
     * This method is used to create a new site.
     */
    public void initializeSite() {
        for (int i = 1; i <= Constants.SITE_AMOUNT; i++) {
            Site site = new Site(i);
            siteMap.put(i, site);
            db.addSite(site);
        }
    }

    /**
     * This method is used to give variables initial value.
     */
    public void initializeVariable() {
        for (int i = 1; i <= Constants.VARIABLE_AMOUNT; i++) {
            String variableName = "x" + i;
            Variable v = new Variable(i);
            variableMap.put(variableName, v);
            //if variable is even, it exists on all sits
            if (i % 2 == 0) {
                for (int j = 1; j <= Constants.SITE_AMOUNT; j++) {
                    siteMap.get(j).addVariable(v);
                }
            }
            else {
                int siteNumber = i % Constants.SITE_AMOUNT + 1;
                siteMap.get(siteNumber).addVariable(v);
            }
        }
    }

    /**
     * This method runs every line from given input and parses the input to execute in the database.
     *
     * @param input commands to be executed
     */
    void runTest(String input) {
        System.out.println("\nNew Test starts: " + "\n");
        if (input.equals("")) {
            // standard input
            Scanner scan = new Scanner(System.in);
            String inputLine = "start";
            while(!inputLine.equals("exit")) {
                if (inputLine.equals("start")) {
                    System.out.println("Input: ");
                    inputLine = scan.nextLine();
                }
                if (inputLine.equals("exit"))
                    return;
                System.out.println("!!! New time step, Input line is： " + inputLine);
                // when each tick starts
                // cycle detection
                if (waitList.size() > 1) {
                    List<String> transactionsInCycle = cycleDetection(waitList);
                    if (transactionsInCycle.size() != 0) {
                        //abort the youngest transaction
                        Transaction youngest = transactionMap.get(getYoungestTransaction(transactionsInCycle));
                        abort(youngest, "youngest in deadlock ");
                    }
                }

                /* step 1: check commands in waitinglist */
                for (String waitingCommand : waitList) {
                    checkCommand(waitingCommand);
                }

                /* step 2: check commands in the new tick */
                checkCommand(inputLine);
                //update waitList for next tick
                waitList = new ArrayList<>(nextWaitList);
                nextWaitList = new ArrayList<>();
                System.out.println("== WaitList:");
                for (String waitingCommand : waitList) {
                    System.out.println("\t" + waitingCommand);
                }
                System.out.println("\n");
                System.out.println("Input: ");
                inputLine = scan.nextLine();
            }
            return;
        }

        String[] instructions = input.split("\n");

        for (String instruction : instructions) {
            System.out.println("!!! New time step, Input line is： " + instruction);
            // when each tick starts
            // cycle detection
            if (waitList.size() > 1) {
//                System.out.println("Run cycle detection:");
                List<String> transactionsInCycle = cycleDetection(waitList);
                if (transactionsInCycle.size() != 0) {
                    //abort the youngest transaction
                    Transaction youngest = transactionMap.get(getYoungestTransaction(transactionsInCycle));
                    abort(youngest, "youngest in deadlock ");
                }
            }

            /* step 1: check commands in waitinglist */
            for (String waitingCommand : waitList) {
                checkCommand(waitingCommand.replaceAll("\\s+",""));
            }

            /* step 2: check commands in the new tick */
            checkCommand(instruction.replaceAll("\\s+",""));
            //update waitList for next tick
            waitList = new ArrayList<>(nextWaitList);
            nextWaitList = new ArrayList<>();
            System.out.println("== WaitList:");
            for (String waitingCommand : waitList) {
                System.out.println("\t" + waitingCommand);
            }
            System.out.println("\n");
        }
    }

    /**
     * This method checks each instruction, executes it if it could be executed,
     * otherwise, the instruction will be put into waitlist.
     *
     * @param instruction instruction to be executed
     */
    private void checkCommand(String instruction) {
        System.out.println("Try command: " + instruction);
        String[] commandFileds = instruction.split("\\(");
        String command = commandFileds[0];
        String[] fields = commandFileds[1].replace(")", "").split(",");
        if (command.toLowerCase().equals("r")) {
            String transaction = fields[0].trim();
            String variable = fields[1].trim();
            //if current read can't be executed
            if (!read(transaction, variable)) {
                nextWaitList.add(instruction);
            }
        }
        else if (command.toLowerCase().equals("w")) {
            String transaction = fields[0].trim();
            String variable = fields[1].trim();
            String value = fields[2].trim();
            //if current write can't be executed
            if (!write(transaction, variable, value)) {
                nextWaitList.add(instruction);
            }
        }
        else if (command.toLowerCase().equals("recover")) {
            String site = fields[0].trim();
            recoverSite(site);
        }
        else if (command.toLowerCase().equals("fail")) {
            String site = fields[0].trim();
            failSite(site);
        }
        else if (command.toLowerCase().equals("end")) {
            String transaction = fields[0].trim();
            endTransaction(transaction);
        }
        else if (command.toLowerCase().equals("begin")) {
            String transaction = fields[0].trim();
            beginTransaction(transaction);

        }
        else if (command.toLowerCase().equals("beginro")) {
            String transaction = fields[0].trim();
            beginReadOnlyTransaction(instruction, transaction);
        }
        else if (command.toLowerCase().equals("dump")) {
            //dump()
            if (instruction.trim().equals("dump()")) {
                db.print();
            }
            //dump(xj)
            else if (instruction.contains("x")) {
                String variable = fields[0].trim();
                variableMap.get(variable).print();
            }
            // dump(i)
            else {
                int site = Integer.parseInt(fields[0].trim());
                System.out.println("site " + site + ": ");
                siteMap.get(site).print();
            }
        }
    }


    /**
     * This method aborts the transaction.
     *
     * @param transaction transaction instance to be aborted
     * @param reason      reason why the transaction is aborted
     */
    private void abort(Transaction transaction, String reason) {
        removeTransactionFromProcess(transaction);
        System.out.println("Transaction " + transaction.getTransactionName() + " is aborted. Reason: " + reason);
    }

    /**
     * This method remove the transaction from the process after transaction is aborted or committed.
     *
     * @param transaction transaction instance to be remove from the process
     */
    private void removeTransactionFromProcess(Transaction transaction) {
        /* need 1: release T's locks on variables -> update VariableInfo and Site */
        for (History history : transaction.getTransactionHistory()) {
            Variable v = variableMap.get(history.getVariableName());
            List<Integer> sites = history.getSites();
            for (Integer site : sites) {
                /* need 1.1 update the variable on that site -> release locks */
                if (history.getType().equals(Constants.WRITE_LOCK)) {
                    //System.out.println("releasing write lock on " + history.variableName + " " + site);
                    // writeLock in class VariableInfo is not really used in our program
                    // therefore, this statement is actually useless
                    v.getSiteToVariableMap().get(site).setWriteLock(false);

                }
                else {
                    // readLock in class VariableInfo is not really used in our program
                    // therefore, this statement is actually useless
                    v.getSiteToVariableMap().get(site).minusReadLock();
                }

                /* need 1.2 update site's involved transaction. */
                siteMap.get(site).removeTransaction(transaction);
            }
        }

        transactionMap.remove(transaction.getTransactionName());
        transactionAge.remove(transaction.getTransactionName());

        /* need 2: update waitList commands to remove transaction */
        List<String> waitListAfterAbort = new ArrayList<>();
        for (int i = 0; i < waitList.size(); i++) {
            if (!waitList.get(i).contains(transaction.getTransactionName())) {
                waitListAfterAbort.add(waitList.get(i));
            }
        }
        waitList = new ArrayList<>(waitListAfterAbort);
    }

    /**
     * This method detects cycle in the command waitList.
     *
     * @param waitList command waitList which contains commands can't be executed right now
     * @return a list of transaction names which are in cycle
     */
    private List<String> cycleDetection(List<String> waitList) {
        Map<String, List<String>> waitsForGraph = new HashMap<>();
        Map<String, Integer> inDegreeMap = new HashMap<>();

        //initialize the waitsForGraph
        for (int i = 0; i < waitList.size(); i++) {
            // t is the transaction in the command
            // v is the variable in the command
            String t = waitList.get(i).split("\\(")[1].trim().split(",")[0].trim();
            String v = waitList.get(i).split("\\(")[1].trim().split(",")[1].trim();
            // add the transaction in the waitList into the in-degreeMap with in-degree = 0
            inDegreeMap.putIfAbsent(t, 0);
            for (Transaction transaction : transactionMap.values()) {
                for (Lock lock : transaction.getLocks()) {
                    if (lock.getType().equals(Constants.WRITE_LOCK)) {
                        if (waitsForGraph.containsKey(t)) {
                            if (waitsForGraph.get(t).contains(transaction.getTransactionName())) {
                                continue;
                            }
                        }
                    }
                    if (lock.getVariable().equals(v)) {
                        if (!waitsForGraph.containsKey(t)) {
                            waitsForGraph.put(t, new ArrayList<>());
                        }
                        if (!inDegreeMap.containsKey(t)) {
                            inDegreeMap.put(t, 0);
                        }
                        // t waits for transaction
                        if(!t.equals(transaction.getTransactionName())){
                            waitsForGraph.get(t).add(transaction.getTransactionName());
                            // update the pointsToTransaction's in-degree: add by 1
                            inDegreeMap.put(transaction.getTransactionName(), inDegreeMap.getOrDefault(transaction.getTransactionName(), 0) + 1);
                        }
                    }
                }
            }
        }

        // print waitsForGraph
//        for (Map.Entry<String, List<String>> entry : waitsForGraph.entrySet()) {
//            System.out.println(entry.getKey() + " is waiting for :" );
//            entry.getValue().forEach(System.out::println);
//        }

        //topological sort detect cycle
        Queue<String> queue = new LinkedList<>();

        // start the topological sort from roots
        Iterator<String> it = inDegreeMap.keySet().iterator();
        while (it.hasNext()) {
            String t = it.next();
            if (inDegreeMap.get(t) == 0) {
                queue.add(t);
                it.remove();
            }
        }

        while (!queue.isEmpty()) {
            String t = queue.poll();
            if (!waitsForGraph.containsKey(t)) {
                continue;
            }
            for (String neighbor : waitsForGraph.get(t)) {
                inDegreeMap.put(neighbor, inDegreeMap.get(neighbor) - 1);
                if (inDegreeMap.get(neighbor) == 0) {
                    queue.add(neighbor);
                    inDegreeMap.remove(neighbor);
                }
            }
        }
        List<String> transactionsInCycle = new ArrayList<>();
        for (String t : inDegreeMap.keySet()) {
            transactionsInCycle.add(t);
        }
        return transactionsInCycle;
    }

    /**
     * This method gets the transaction name which is youngest in the list.
     *
     * @param transactionsInCycle a list of transactions in cycle
     * @return the youngest transaction name
     */
    private String getYoungestTransaction(List<String> transactionsInCycle) {
        int youngestIndex = Integer.MIN_VALUE;
        for (String t : transactionsInCycle) {
            youngestIndex = Math.max(transactionAge.indexOf(t), youngestIndex);
        }
        return transactionAge.get(youngestIndex);
    }

    /**
     * This methods executes the begin read-only transaction command.
     *
     * @param instruction command
     * @param transaction transaction name
     */
    private void beginReadOnlyTransaction(String instruction, String transaction) {
        Transaction t = new Transaction(latestVersionNumber, transaction);
        transactionMap.put(transaction, t);
    }

    /**
     * This method executes the begin read-write transaction command.
     *
     * @param transaction transaction name
     */
    private void beginTransaction(String transaction) {
        Transaction t = new Transaction(Constants.READ_WRITE_TRANSACTION_VERSION, transaction); //not read-only transaction's versionNumber set to default -1
        transactionMap.put(transaction, t);
        transactionAge.add(transaction);
    }

    /**
     * This method executes the end transaction command.
     *
     * @param transaction transaction name
     */
    private void endTransaction(String transaction) {
        //if transaction is already, then skip the command
        if (isTransactionDead(transaction)) {
            return;
        }

        /* need 1: update value for write history and print out read history */
        System.out.println("=== Commit transaction " + transaction + " ===");
        Transaction t = transactionMap.get(transaction);
        /* According to the requirement, all read prints as the program goes. even later the transaction is aborted. */
//        if (t.getVersionNumber() != Constants.READ_WRITE_TRANSACTION_VERSION) {
//            System.out.println("It's a Read-Only transaction reading variables as the program goes");
//        }
        for (History history : t.getTransactionHistory()) {
            /* According to the requirement, all read prints as the program goes. even later the transaction is aborted. */
//            if (history.getType().equals("read")) {
//                /* need 1.1 get read value from the current site,
//                for the case that same transaction first write and then read the same variable on the same site */
//                int value = variableMap.get(history.getVariableName()).getSiteToVariableMap().get(history.getSites().get(0)).getValue();
//                System.out.print("R(" + transaction + "," + history.getVariableName() + ") at Site " + history.getSites().get(0) + " = " + value + "\n");
//
//            }
            if(history.getType().equals("write")) {
                //for stdout
                List<Integer> sites = history.getSites();
                System.out.print("W(" + transaction + "," + history.getVariableName() + "," + history.getValue() + ") at available sites: ");
                for (Integer site : history.getSites()) {
                    System.out.print(site + " ");
                }
                System.out.println("");
                Variable v = variableMap.get(history.getVariableName());
                for (Map.Entry<Integer, VariableInfo> entry : v.getSiteToVariableMap().entrySet()) {
                    if (sites.contains(entry.getKey())) {
                        /* need 1.2 update committed variable's new value */
                        entry.getValue().setValue(history.getValue());
                        /* need 1.3 update committed variable on that site canRead = true; */
                        entry.getValue().setCanRead(true);
                    }
                }
            }
        }

        /* need 2: update version if has write history */
        createVersion(++latestVersionNumber);

        /* need 3: remove transaction from the process */
        removeTransactionFromProcess(transactionMap.get(transaction));

    }

    /**
     * This method executes the fail site command.
     *
     * @param site site name
     */
    private void failSite(String site) {
        int siteNumber = Integer.parseInt(site);
        Site s = siteMap.get(siteNumber);
        s.setAvailable(false);
        //site is down, lock table is cleared
        for (Variable v : s.getVariables()) {
            VariableInfo variableOnSite = v.getSiteToVariableMap().get(siteNumber);
            variableOnSite.setWriteLock(false);
            //will do this in abort transaction
            //variableOnSite.minusReadLock();
            variableOnSite.setCanRead(false);
        }
        //abort transaction which has lock on this site.
        System.out.println("number of involved transcation");

        List<Transaction> transactionsToRemove = new ArrayList<>();
        for(Transaction t: s.getInvolvedTransactions()) {
            transactionsToRemove.add(t);
        }
        for (Transaction t : transactionsToRemove) {
            abort(t, " involved Site " + siteNumber + " is failed ");
        }
    }

    /**
     * This method executes the recover site command.
     *
     * @param site site name
     */
    private void recoverSite(String site) {
        int siteNumber = Integer.parseInt(site);
        Site s = siteMap.get(siteNumber);
        s.setAvailable(true);
        //site is up, odd variable is immediately available for read, even variable has to wait for new commit
        for (Variable v : s.getVariables()) {
            if (v.getNumber() % 2 == 1) {
                VariableInfo variableOnSite = v.getSiteToVariableMap().get(siteNumber);
                variableOnSite.setCanRead(true);
            }
        }
    }

    /**
     * This method executes the write command.
     *
     * @param transaction transaction name
     * @param variable    variable name
     * @param valueString value in string format
     * @return true - write command is executed; false - write command can't be executed now.
     */
    private boolean write(String transaction, String variable, String valueString) {
        // if the transaction already died, then skip this command, return true
        if (isTransactionDead(transaction)) {
            return true;
        }

        Transaction t = transactionMap.get(transaction);
        Variable v = variableMap.get(variable);
        int value = Integer.parseInt(valueString);
        boolean getLock = true;

        /* scenario 1: if every site containing x is down,
        then T can't get write lock on x
        */
        if (isNoUpSiteForCopy(variable)) {
            getLock = false;
            //            System.out.println("in scenario 1 can't get lock");
        }

        /* scenario 2: if one of the available variables already has write lock or read lock,
        then T can't get write lock (except that transaction is the same transaction)
        */
        for (Transaction tr : transactionMap.values()) {
            for (Lock lock : tr.getLocks()) {
                if (lock.getVariable().equals(variable) && !tr.getTransactionName().equals(transaction)) {
                    getLock = false;
                    //                    System.out.println("in scenario 2 can't get lock");
                    break;
                }
            }
        }

        if (getLock) {
            //write lock on each available site containing the variable
            List<Integer> availableSites = new ArrayList<>();
            for (Map.Entry<Integer, VariableInfo> entry : v.getSiteToVariableMap().entrySet()) {
                int site = entry.getKey();
                if (siteMap.get(site).isAvailable()) {
                    entry.getValue().setWriteLock(true);
                    //update transaction
                    Lock lock = new Lock(variable, site, Constants.WRITE_LOCK);
                    transactionMap.get(transaction).addLock(lock);
                    availableSites.add(site);

                }
            }
            addHistoryToTransaction(variable, value, availableSites, transaction, Constants.WRITE_LOCK);
            addTransactionToSite(availableSites, t);
            return true;
        }

        //else can't execute this command right now
        return false;
    }

    /**
     * This method executes the read command.
     *
     * @param transaction transaction name
     * @param variable    variable name
     * @return true - read command is executed; false - read command can't be executed right now.
     */
    private boolean read(String transaction, String variable) {
        // if the transaction is already dead, then skip this command, return true
        if (isTransactionDead(transaction)) {
            return true;
        }

        Transaction t = transactionMap.get(transaction);
        // if T is read-only transaction
        if (t.getVersionNumber() != Constants.READ_WRITE_TRANSACTION_VERSION) {
            Version version = multiVersion.get(t.getVersionNumber());
            // if there is no available site for reading
            if (isNoUpSiteForCopy(variable)) {
                // can't execute read, the transaction has to wait for the site up
                return false;
            }
            // if the available site's version is not the latest (for the time of read-only transaction starts, then wait)
            List<Integer> sites = multiVersion.get(t.getVersionNumber()).getVariableToSite().get(variable);
            int readFromSite = -1;
            for(Integer site : sites) {
                if(siteMap.get(site).isAvailable()) {
                    readFromSite = site;
                    break;
                }
            }
            if(readFromSite == -1) {
                return false;
            }

            int value = version.getVariableToValue().get(variable);

            // read-only transactions could print out directly
            System.out.println(transaction + " reads version " + t.getVersionNumber() + "'s " + variable + " = " + value + " from site " + readFromSite);
            return true;
        }
        // if T is not a read-only transaction, i.e. T.versionNumber == -1
        else {
            boolean getLock = true;

            /* scenario 1:
             *   if a transaction in the waitList comes before T requires a write lock on x,
             *   then T can't get read lock on x (see exceptions in scenario 2 line 668)
             */
            for (String waitCommand : waitList) {
//                boolean isOlder = transactionAge.indexOf(waitCommand) < transactionAge.indexOf(transaction);
                if (waitCommand.contains(variable) && waitCommand.contains(Constants.WRITE_OPERATION) && !waitCommand.contains(transaction)) {
                    getLock = false;
                    break;
                }
            }

            /* scenario 2:
             *   if x already has write lock from a transaction (not T itself),
             *   then T can't get read lock on x
             */
            for (Transaction trans : transactionMap.values()) {
                for (Lock lock : trans.getLocks()) {
                    // if another transaction holds the write lock on the variable
                    // then T can't read lock the variable
                    if (lock.getVariable().equals(variable) && !(trans.getTransactionName().equals(transaction)) && lock.getType().equals(Constants.WRITE_LOCK)) {
                        getLock = false;
                    }
                }
            }

            /* scenario 3:
             *   If every site containing x is down,
             *   then T can't get the read lock on x.
             */
            if (isNoUpSiteForCopy(variable)) {
                getLock = false;
            }

            /* scenario 4:
             *   If, on every available site, x.canRead = false,
             *   then T can't get read lock on x.
             */
            int upSites = 0;
            int upButCanNotReadSites = 0;
            for (Map.Entry<Integer, VariableInfo> entry : variableMap.get(variable).getSiteToVariableMap().entrySet()) {
                int site = entry.getKey();
                if (siteMap.get(site).isAvailable()) {
                    upSites++;
                    if (!entry.getValue().isCanRead()) {
                        upButCanNotReadSites++;
                    }
                }
            }
            if (upSites == upButCanNotReadSites) {
                getLock = false;
            }

            /* scenario 5:
             *  If T itself holds the write lock on the variable,
             *  then ignore scenario 1~4, and T can read lock the variable.
             *
             *  Reason to ignore scenario 1:
             *    We just don't care about the waitList since T already locks x on all available sites.
             *
             *  Reason to ignore scenario 2:
             *    Only one transaction can hold the write lock on x.
             *    If any other transaction already has the write lock on x,
             *    then scenario 5 won't happen.
             *
             *  Reason to ignore scenario 3:
             *    If T already has the write lock, then, at the time T acquires the write lock,
             *    some sites that containing x must be up.
             *    Now, since every site containing x is down, T must already be aborted.
             *    Therefore, this scenario doesn't apply to the condition that T already has the write lock.
             *
             *  Reason to ignore scenario 4:
             *    If T already has the write lock on x (i.e. at least one site containing x is up),
             *    then T can read the value from the v of W(T,x,v), i.e. T doesn't need to care about canRead.
             */
            for (Lock lock : transactionMap.get(transaction).getLocks()) {
                if (lock.getVariable().equals(variable) && lock.getType().equals(Constants.WRITE_LOCK)) {
                    getLock = true;
                }
            }

            if (getLock) {
                // If T already has the write lock on x,
                // then T reads from it's own latest write command on x.
                // Choose the site with the smallest index from the sites that the write command writes to.
                for (Lock lock : transactionMap.get(transaction).getLocks()) {
                    if (lock.getVariable().equals(variable) && lock.getType().equals(Constants.WRITE_LOCK)) {
                        for (History h : transactionMap.get(transaction).getTransactionHistory()) {
                            if (h.getType().equals(Constants.WRITE_LOCK) && h.getVariableName().equals(variable)) {
                                int userReadValueFromPreviousWrite = h.getValue();
                                System.out.print("R(" + transaction + "," + variable + ") from it's own Write commands at Site " + h.getSites().get(0) + " = " + userReadValueFromPreviousWrite + "\n");
                                // readLock in class VariableInfo is not really used in our program.
                                // Therefore, this statement is actually useless.
                                variableMap.get(variable).getSiteToVariableMap().get(h.getSites().get(0)).addReadLock();
                                // we don't need to addTransactionToSite here
                                // because in former Write commands of T
                                // we already put T into involvedTransactionsRecorder of the site
                                // therefore, if the site fails, this T will be successfully aborted.
                                // BUT I add it anyway...
                                Lock readLock = new Lock(variable, h.getSites().get(0), Constants.READ_LOCK);
                                transactionMap.get(transaction).addLock(readLock);
                                return true;
                            }
                        }
                    }
                }

                // read lock on one site, we use the smallest index available site
                List<Integer> sites = new ArrayList<>(variableMap.get(variable).getSiteToVariableMap().keySet());
                Collections.sort(sites);
                int targetSite = -1;
                for (Integer site : sites) {
                    //can only read from available site and that variable is canRead
                    if (siteMap.get(site).isAvailable() && variableMap.get(variable).getSiteToVariableMap().get(site).isCanRead()) {
                        targetSite = site;
                        break;
                    }
                }
                // Update variableInfo in Variable:
                //   readLock in class VariableInfo is not really used in our program.
                //   Therefore, this statement is actually useless.
                variableMap.get(variable).getSiteToVariableMap().get(targetSite).addReadLock();

                // update transaction
                Lock readLock = new Lock(variable, targetSite, Constants.READ_LOCK);
                transactionMap.get(transaction).addLock(readLock);
                int userReadValue = variableMap.get(variable).getSiteToVariableMap().get(targetSite).getValue();

                List<Integer> targetSites = new ArrayList<>();
                targetSites.add(targetSite);
//                addHistoryToTransaction(variable, value, targetSites, transaction, Constants.READ_LOCK);

                //read prints as the program goes
                System.out.print("R(" + transaction + "," + variable + ") at Site " + targetSite + " = " + userReadValue + "\n");
                addTransactionToSite(targetSites, t);
                return true;
            }
            //else can't execute this command right now
            return false;
        }
    }

    /**
     * This method checks that if transaction is already died. (aborted or committed)
     *
     * @param transaction transaction name
     * @return true - transaction still exists in the process; false - transaction is aborted or committed
     */
    private boolean isTransactionDead(String transaction) {
        //if this transaction already been aborted, then skip this command
        if (!transactionMap.containsKey(transaction)) {
            return true;
        }
        return false;
    }

    /**
     * This method adds transaction history record to transaction.
     *
     * @param variable    variable name
     * @param value       value
     * @param sites       a list of involved sites
     * @param transaction transaction name
     * @param type        operation type (read or write)
     */
    private void addHistoryToTransaction(String variable, int value, List<Integer> sites, String transaction, String type) {
        History history = new History(type, variable, value, sites);
        transactionMap.get(transaction).addTransactionHistory(history);

    }

    /**
     * This method adds Transaction info to site.
     *
     * @param sites       a list of involved sites
     * @param transaction transaction instance
     */
    private void addTransactionToSite(List<Integer> sites, Transaction transaction) {
        for (Integer site : sites) {
            siteMap.get(site).addTransaction(transaction);
        }
    }

    /**
     * This method checks if all sites contains the variable are down.
     *
     * @param variable variable name
     * @return true - no available sites; false - has available sites.
     */
    private boolean isNoUpSiteForCopy(String variable) {
        boolean noUpSite = true;

        for (Integer site : variableMap.get(variable).getSiteToVariableMap().keySet()) {
            if (siteMap.get(site).isAvailable()) {
                noUpSite = false;
            }
        }
        return noUpSite;
    }
}