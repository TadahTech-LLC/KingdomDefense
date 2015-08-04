package com.tadahtech.fadecloud.kd.kit.kits;

import com.tadahtech.fadecloud.kd.items.ThorItem;
import com.tadahtech.fadecloud.kd.kit.CSKit;

import java.util.Collections;

/**
 * Created by Timothy Andis
 */
public class ThorKit extends CSKit {

    public ThorKit() {
        super(Collections.singletonList(new ThorItem().getItem()), "Thor", "Rule the Skies!");
    }
}
