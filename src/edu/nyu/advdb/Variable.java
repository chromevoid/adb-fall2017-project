package edu.nyu.advdb;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to store general variable information which has (x1,x2...x20)
 */
public class Variable {
    private int number;
    //map stores on siteNumber, ValueInfo
    private Map<Integer, VariableInfo> siteToVariableMap;

    public Variable(int number) {
        this.number = number;
        //this.writeLock = false;
        siteToVariableMap = new HashMap<>();
        init(number);
    }

    public void init(int n) {
        int value = 10 * n;
        if (n % 2 == 1) {
            int siteNumber = n % 10 + 1;
            siteToVariableMap.put(siteNumber, new VariableInfo(value));
        }
        else if (n % 2 == 0) {
            for (int i = 1; i <= 10; i++) {
                siteToVariableMap.put(i, new VariableInfo(value));
            }
        }
    }

    public Map<Integer, VariableInfo> getSiteToVariableMap() {
        return siteToVariableMap;
    }

    public int getNumber() {
        return number;
    }

    public void print() {
        //print all sites' variable information (certain variable's distribution)
        for (int i = 1; i <= 10; i++) {
            if (siteToVariableMap.containsKey(i)) {
                VariableInfo info = siteToVariableMap.get(i);
                // e.g. x6.2 is the copy of variable x6 at site 2
                System.out.println("x" + this.number + "." + i + "=" + info.getValue());
            }
        }
    }
}
