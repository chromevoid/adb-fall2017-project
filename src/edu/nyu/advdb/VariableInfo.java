package edu.nyu.advdb;

/**
 * This class is used to record the certain variable on certain site,
 * including value, canRead, readLock and writeLock.
 */
public class VariableInfo {
    private int value;
    private boolean canRead;
    // readLock and writeLock are not really used in our program
    // we always check locks from transaction's locks recorder
    private int readLock;
    private boolean writeLock;

    public VariableInfo(int value) {
        this.value = value;
        this.canRead = true;
        this.writeLock = false;
        readLock = 0;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setCanRead(boolean canRead) {
        this.canRead = canRead;
    }

    public void minusReadLock() {
        this.readLock--;
    }

    public void addReadLock() {
        this.readLock++;
    }

    public void setWriteLock(boolean writeLock) {
        this.writeLock = writeLock;
    }

    public int getValue() {
        return value;
    }

    public boolean isCanRead() {
        return canRead;
    }

    public int getReadLock() {
        return readLock;
    }

    public boolean isWriteLock() {
        return writeLock;
    }
}
