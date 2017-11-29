package edu.nyu;

public class VariableInfo {
    int value;
    boolean canRead;
    //TODO: ask if readLock is needed each variable on each site??
    int readLock;
    boolean writeLock;

    public VariableInfo(int value) {
        this.value = value;
        this.canRead = true;
        this.writeLock = false;
        readLock = 0;

    }
}
