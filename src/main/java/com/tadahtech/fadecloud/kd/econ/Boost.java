package com.tadahtech.fadecloud.kd.econ;

/**
 * Created by Timothy Andis
 */
public class Boost {

    private String booster;
    private double amount;

    public Boost(String booster, double amount) {
        this.booster = booster;
        this.amount = amount;
    }

    public String getBooster() {
        return booster;
    }

    public double getAmount() {
        return amount;
    }
}
