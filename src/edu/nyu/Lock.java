package edu.nyu;

public class Lock {
    String variable;
    String type;
    int siteNumber;

    public Lock(String variable, int siteNumber, String type) {
        this.variable = variable;
        this.siteNumber = siteNumber;
        this.type = type;
    }
}
