
# Advanced Database System - 17fall Project

This project implements a small size distributed database, complete with multiversion concurrency control, deadlock detection, replication, and
failure recovery.

## Design 

### Requirement

According to the requirement, there are two kinds of transactions.

- Read-Only Transaction

    - It uses multi-version.
    - It only reads from the copy version when the transaction starts
    - If there are no available copies for read (such as all sites contains variableToValue are down), then the operation will wait

- Read-Write Transaction

    - It uses strict two phase locking. 
        - Acquire locks as the program goes, release locks at end and acquire all locks before releasing any.
        - A variable can't get the exclusive lock when it already has a read lock.
        - A variable can't get any other locks when it already has an exclusive lock.
        - A variable can have multiple read locks.
    - It uses instructors' available copies algorithm.
        - Available copies allows writes and commits to just available sites. So if site A is down, its last committed value of x may be different from site B which is up. 
    - It has deadlock detection.
        - Detect deadlocks using cycle detection and abort the youngest transaction in the cycle.
    - Some sites will fail and recover during the program.
        - Transaction will abort if site it chose to read or write from fails later but before commit.
        - When the site recovers, unreplicated copy will be available immediately and replicated will be available after next write commit change.
    - For the same transaction has read and write on the same variable on the same site, it is allowed.
        
 
        
### Details
    
- Takeaway:
    - Single command for one transaction each line of input
    - The input will not have the command for the transaction while the one command is in the waitlist.


- In transaction manager, since it's a small database, so we store all global information in the transaction manager.
```
    /* all information in database.* /
    private Database db;
    
    /* variable collection */
    private Map<String, Variable> variableMap; 
    
    /* variable collection */
    private Map<Integer, Site> siteMap; //site collection
    
    /* transaction collection */
    private Map<String, Transaction> transactionMap; 
    
    /* used to record transaction age */
    private List<String> transactionAge; 
    
    /* Version collection */
    private Map<Integer, Version> multiVersion;
    
    /* increments when any transaction commits */
    private int latestVersionNumber;
    
    /* command can't be executed right now are put into waitlist */
    private List<String> waitList; 
    
    /* after this tick, still some commands still can't be executed are accumulated. */
    private List<String> nextWaitList; 

```

- In transaction Manager, we take each line of inputs from the file and then feed it to corresponding functions. 
- In transaction Manager, we do deadlock detection when every tick starts and then check commands in the waitlist, and at last we execute the new command in the new tick.
- As for other classes
```
public class Database {
    private List<Site> sites;
}
```
```
public class Transaction {
    private List<Lock> locks;
    private List<History> transactionHistory;
    private int versionNumber;
    private String transactionName;
}
```
```
public class Site {
    private int siteNumber;
    private List<Variable> variables;
    private boolean available;
    private Set<Transaction> involvedTransactions;
}
```
```
public class Lock {
    private String variable;
    private int siteNumber;
    private String type;
}
```
```
public class Variable {
    private int number;
    
    /* map stores on siteNumber, ValueInfo */
    private Map<Integer, VariableInfo> siteToVariableMap;
}
```
```
public class VariableInfo {
    private int value;
    
    /* when site fails, canRead is false. when the site recover, 
    only odd variable canRead = true, even variable canRead = true after new commit */
    private boolean canRead;
    
    private int readLock;
    private boolean writeLock;
}
```
```
public class Version {
    private int versionNumber;
    
    /* variable's value at this version */
    private Map<String, Integer> variableToValue;
    
    /* this version can only be read from what sites */
    private Map<String, List<Integer>> variableToSite;
}
```
```
public class History {
    private String type;
    private String variableName;
    private int value;
    private List<Integer> sites;
}
```
## Running the tests

You can use standard input or input files to test the program.

Compile all java files.
```
$ javac *.java
```
Run the program using standard input.
```
$ java Application
```
Run the program with input files.
```
$ java Application ./input/test1.txt ./input/test2.txt ...
```


## Authors

* **Juanlu Yu (jy2234)**
* **Mengna Qiu (mq438)**



## Acknowledgments

* Thanks for Professor Dennis Shasha (shasha@cs.nyu.edu)'s Advanced Database System Course.

