package edu.nyu;

import java.util.HashMap;
import java.util.Map;

public class Variable {
    int number;
    //map stores on siteNumber, ValueInfo
    Map<Integer, VariableInfo> siteToVariable;

    public Variable (int number) {
        this.number = number;
        //this.writeLock = false;
        siteToVariable = new HashMap<>();
        init(number);
    }
    public void init (int n) {
        int value = 10 * n;
        if (n % 2 == 1) {
            int siteNumber = n % 10 + 1;
            siteToVariable.put(siteNumber, new VariableInfo(value));
        }
        else if (n % 2 == 0) {
            for(int i = 1; i <= 10; i++) {
                siteToVariable.put(i, new VariableInfo(value));
            }
        }
    }
    public void print() {
        //print all sites' variable information (certain variable's distribution)
        for(int i = 1; i <= 10; i++) {
            if(siteToVariable.containsKey(i)) {
                VariableInfo info = siteToVariable.get(i);
                // e.g. x6.2 is the copy of variable x6 at site 2
                System.out.println("x" + this.number + "." + i + "=" + info.value);
            }
        }
    }
}
