package edu.nyu.advdb;

import java.util.Map;

/**
 * This class is used to store the version information.
 */
public class Version {
    private int versionNumber;
    private Map<String, Integer> variables;

    public Version(int versionNumber, Map<String, Integer> variables) {
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
