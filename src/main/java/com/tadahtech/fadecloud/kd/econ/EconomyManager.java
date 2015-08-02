package com.tadahtech.fadecloud.kd.econ;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import net.md_5.bungee.api.ChatColor;

import java.util.List;
import java.util.Map;

/**
 * Created by Timothy Andis (TadahTech) on 7/28/2015.
 */
public class EconomyManager {

    private Map<PlayerInfo, List<EconomyReward>> rewards;

    public EconomyManager() {
        this.rewards = Maps.newHashMap();
    }

    public void add(EconomyReward reward, PlayerInfo info) {
        List<EconomyReward> rewards = this.rewards.remove(info);
        if(rewards == null) {
            rewards = Lists.newArrayList();
        }
        rewards.add(reward);
        this.rewards.putIfAbsent(info, rewards);
    }

    public void display(PlayerInfo info) {
        List<EconomyReward> rewards = this.rewards.remove(info);
        if(rewards == null) {
            rewards = Lists.newArrayList();
        }
        rewards.add(new ParticipationReward(10));
        info.sendMessage(ChatColor.BLUE.toString() + ChatColor.BOLD + "============================");
        info.sendMessage("Kingdom Defense: ");
        for(EconomyReward reward : rewards) {
            info.sendMessage("  " + reward.toString());
            info.addCoins(reward.getAmount());
        }
        info.sendMessage(ChatColor.BLUE.toString() + ChatColor.BOLD + "============================");
        info.sendMessage(" ");
    }

    private class ParticipationReward extends EconomyReward {

        public ParticipationReward(int i) {
            super("Participation", i);
        }
    }
}
