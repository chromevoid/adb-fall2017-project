package edu.nyu;

import java.util.ArrayList;
import java.util.List;

public class Database {
    List<Site> sites;

    public Database() {
        this.sites = new ArrayList<>();
    }

    public void addSite(Site site) {
        sites.add(site);
    }

    public void print() {
        for (Site site : sites) {
            System.out.println("site " + site.siteNumber + ": ");
            site.print();
        }
    }
}
