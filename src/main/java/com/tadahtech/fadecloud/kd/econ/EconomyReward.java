package com.tadahtech.fadecloud.kd.econ;

import net.md_5.bungee.api.ChatColor;

/**
 * Created by Timothy Andis (TadahTech) on 7/28/2015.
 */
public class EconomyReward {

    private final String BLUE = ChatColor.BLUE.toString() + ChatColor.BOLD + "=";
    private final String GRAY = ChatColor.GRAY.toString() + ChatColor.BOLD + "=";

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
        for (int i = 0; i < 16; i++) {
            builder.append(BLUE).append(GRAY);
        }
        builder.append(ChatColor.GREEN).append(ChatColor.BOLD).append(reason)
          .append(ChatColor.WHITE).append(" - ").append(ChatColor.GOLD).append(ChatColor.BOLD).append(amount);
        return builder.toString();
    }
}
