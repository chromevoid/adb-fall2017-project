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
//    Queue<Transaction> wailiList;
//    List<Integer>

    public TransactionManager() {
        this.db = new Database();
        this.siteMap = new HashMap<>();
        this.variableMap = new HashMap<>();

        initializeDatabase();
        initializeSite();
        initializeVariable();
//        wailiList = new LinkedList<>();

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
            String[] commandFileds = instruction.split("\\(");
            String command = commandFileds[0];
            String[] fields = commandFileds[1].replace(")", "").split(",");
            if (command.equals("R")) {
                String transaction = fields[0];
                String variable = fields[1];
                read(transaction, variable);
            }
            else if (command.equals("W")) {
                String transaction = fields[0];
                String variable = fields[1];
                String value = fields[2];
                write(transaction, variable, value);
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
                //start

            }
            else if (command.equals("beginRO")) {

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
    }

    private void endTransaction(String transaction) {
    }

    private void failSite(String site) {
    }

    private void recoverSite(String site) {
    }

    private void write(String transaction, String variable, String value) {
    }

    private void read(String transaction, String variable) {
    }

}