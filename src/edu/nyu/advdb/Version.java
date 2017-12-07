package edu.nyu.advdb;

import java.util.List;
import java.util.Map;

/**
 * This class is used to store the version information.
 */
public class Version {
    private int versionNumber;
    private Map<String, Integer> variableToValue;
    private Map<String, List<Integer>> variableToSite;

    public Version(int versionNumber, Map<String, Integer> variableToValue, Map<String, List<Integer>> variableToSite) {
        this.versionNumber = versionNumber;
        this.variableToValue = variableToValue;
        this.variableToSite = variableToSite;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public Map<String, Integer> getVariableToValue() {
        return variableToValue;
    }

    public Map<String, List<Integer>> getVariableToSite() {
        return variableToSite;
    }
}
