package edu.nyu;

import java.util.Map;

public class Version {
    int versionNumber;
    Map<String, Integer> variables;
   // involvedTransaction;
    public Version (int versionNumber, Map<String, Integer> variables) {
        this.versionNumber = versionNumber;
        this.variables = variables;
    }
}
