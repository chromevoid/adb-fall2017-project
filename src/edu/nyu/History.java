package edu.nyu;

import java.util.List;

public class History {
    String type;
    String variableName;
    int value;
    List<Integer> sites;
    public History(String type, String variableName, int value, List<Integer> sites) {
        this.type = type;
        this.variableName = variableName;
        this.value = value;
        this.sites = sites;
    }
}
