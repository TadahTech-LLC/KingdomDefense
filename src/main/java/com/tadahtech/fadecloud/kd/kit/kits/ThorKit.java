package com.tadahtech.fadecloud.kd.kit.kits;

import com.google.common.collect.Lists;
import com.tadahtech.fadecloud.kd.items.special.ThorItem;
import com.tadahtech.fadecloud.kd.kit.CSKit;
import net.md_5.bungee.api.ChatColor;

/**
 * Created by Timothy Andis
 */
public class ThorKit extends CSKit {

    public ThorKit() {
        super(Lists.newArrayList(new ThorItem().getItem()), "Thor", ChatColor.AQUA + "Rule the Skies!");
    }
}
