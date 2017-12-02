package edu.nyu.advdb;

import java.util.List;

public class History {
    private String type;
    private String variableName;
    private int value;
    private List<Integer> sites;
    public History(String type, String variableName, int value, List<Integer> sites) {
        this.type = type;
        this.variableName = variableName;
        this.value = value;
        this.sites = sites;
    }

    public String getType() {
        return type;
    }

    public String getVariableName() {
        return variableName;
    }

    public int getValue() {
        return value;
    }

    public List<Integer> getSites() {
        return sites;
    }
}
