package edu.nyu.advdb;

import java.util.Map;

public class Version {
    private int versionNumber;
    private Map<String, Integer> variables;
   // involvedTransaction;
    public Version (int versionNumber, Map<String, Integer> variables) {
        this.versionNumber = versionNumber;
        this.variables = variables;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public Map<String, Integer> getVariables() {
        return variables;
    }
}
