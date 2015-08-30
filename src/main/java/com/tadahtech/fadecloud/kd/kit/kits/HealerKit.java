package com.tadahtech.fadecloud.kd.kit.kits;

import com.google.common.collect.Lists;
import com.tadahtech.fadecloud.kd.items.special.HealerItem;
import com.tadahtech.fadecloud.kd.kit.CSKit;
import net.md_5.bungee.api.ChatColor;

/**
 * Created by Timothy Andis
 */
public class HealerKit extends CSKit {

    public HealerKit() {
        super(Lists.newArrayList(new HealerItem().getItem()), "Healer", ChatColor.GREEN + "Keep your teammates alive!");
    }
}
