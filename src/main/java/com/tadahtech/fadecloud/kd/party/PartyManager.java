package com.tadahtech.fadecloud.kd.party;


import com.google.common.collect.Maps;

import java.util.Map;
import java.util.UUID;

/**
 * Created by Timothy Andis
 */
public class PartyManager {

    private Map<UUID, Party> parties = Maps.newHashMap();

    public PartyManager() {

    }

    public void addParty(Party party) {
        this.parties.put(party.getOwner(), party);
    }

}
