package edu.nyu.advdb;

/**
 * This class is used to record the certain variable on certain site,
 * including value, canRead, readLock and writeLock.
 */
public class VariableInfo {
    private int value;
    private boolean canRead;
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

    public void clearReadLock() {
        this.readLock = 0;
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
