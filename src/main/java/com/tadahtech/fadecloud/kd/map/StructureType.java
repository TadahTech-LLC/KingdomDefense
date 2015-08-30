package com.tadahtech.fadecloud.kd.map;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Created by Timothy Andis (TadahTech) on 8/1/2015.
 */
public enum StructureType {

    GUARDIAN,
    ARCHER,
    BLAZE,
    TESLA
    ;

    public int getCost(int level) {
        if(level == 5) {
            return -1;
        }
        FileConfiguration config = KingdomDefense.getInstance().getConfig();
        String costRaw = config.getString("costs." + name());
        if(costRaw == null) {
            return 100 * level;
        }
        String[] str = costRaw.split("-");
        return Integer.parseInt(str[level - 1]);
    }

    public int getCost() {
        return getCost(1);
    }

}
