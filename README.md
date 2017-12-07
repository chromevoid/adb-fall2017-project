
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



```
Give examples
```

## Running the tests

Explain how to run the tests for this system


```
Give an example
```




## Authors

* **Juanlu Yu**
* **Mengna Qiu**



## Acknowledgments

* 

