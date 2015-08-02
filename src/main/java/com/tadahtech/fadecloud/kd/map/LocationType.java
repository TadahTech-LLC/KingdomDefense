package com.tadahtech.fadecloud.kd.map;

/**
 * Created by Timothy Andis (TadahTech) on 7/27/2015.
 */
public enum LocationType {

    CREEPER_SPAWN,
    CREEPER_KING,

    ZOMBIE_SPAWN,
    ZOMBIE_KING,

    ENDERMAN_SPAWN,
    ENDERMAN_KING,

    SKELETON_SPAWN,
    SKELETON_KING,

    LOBBY,

    ;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
