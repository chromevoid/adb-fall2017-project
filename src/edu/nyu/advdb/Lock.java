package edu.nyu.advdb;

public class Lock {
    private String variable;
    private int siteNumber;
    private String type;

    public Lock(String variable, int siteNumber, String type) {
        this.variable = variable;
        this.siteNumber = siteNumber;
        this.type = type;
    }

    public String getVariable() {
        return variable;
    }

    public int getSiteNumber() {
        return siteNumber;
    }

    public String getType() {
        return type;
    }
}
