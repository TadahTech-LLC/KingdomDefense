package com.tadahtech.fadecloud.kd.econ;

import net.md_5.bungee.api.ChatColor;

/**
 * Created by Timothy Andis (TadahTech) on 7/28/2015.
 */
public class EconomyReward {

    private String reason;
    private double amount;

    public EconomyReward(String reason, double amount) {
        this.reason = reason;
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(ChatColor.GREEN).append(reason)
          .append(ChatColor.WHITE).append(" - ").append(ChatColor.GOLD).append(amount);
        return builder.toString();
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
