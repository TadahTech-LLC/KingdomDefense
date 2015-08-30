package com.tadahtech.fadecloud.kd.map;

/**
 * Created by Timothy Andis (TadahTech) on 7/27/2015.
 */
public enum LocationType {

    CREEPER_SPAWN,
    CREEPER_KING,
    CREEPER_VILLAGER,

    ZOMBIE_SPAWN,
    ZOMBIE_KING,
    ZOMBIE_VILLAGER,

    ENDERMAN_SPAWN,
    ENDERMAN_KING,
    ENDERMAN_VILLAGER,

    SKELETON_SPAWN,
    SKELETON_KING,
    SKELETON_VILLAGER,

    LOBBY,
    CENTER,

    ;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
