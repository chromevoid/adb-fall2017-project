package edu.nyu.advdb;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to store whole database information.
 */
public class Database {
    private List<Site> sites;

    public Database() {
        this.sites = new ArrayList<>();
    }

    public void addSite(Site site) {
        sites.add(site);
    }

    public void print() {
        for (Site site : sites) {
            System.out.println("site " + site.getSiteNumber() + ": ");
            site.print();
        }
    }
}
