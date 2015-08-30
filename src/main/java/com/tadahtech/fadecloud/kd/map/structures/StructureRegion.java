package com.tadahtech.fadecloud.kd.map.structures;

import com.google.common.collect.Lists;
import com.tadahtech.fadecloud.kd.map.Region;
import com.tadahtech.fadecloud.kd.map.StructureType;
import com.tadahtech.fadecloud.kd.map.structures.strucs.Archer;
import com.tadahtech.fadecloud.kd.map.structures.strucs.BlazeTower;
import com.tadahtech.fadecloud.kd.map.structures.strucs.Guardian;
import com.tadahtech.fadecloud.kd.map.structures.strucs.TeslaTower;
import com.tadahtech.fadecloud.kd.utils.Utils;
import org.bukkit.Location;

import java.util.List;
import java.util.Optional;

/**
 * Created by Timothy Andis
 */
public class StructureRegion extends Region {

    private Structure structure;

    private static List<StructureRegion> REGIONS = Lists.newArrayList();
    private Location firing;

    public StructureRegion(Location min, Location max, StructureType structureType, Location firing) {
        super(min, max);
        switch (structureType) {
            case BLAZE:
                this.structure = new BlazeTower();
                break;
            case TESLA:
                this.structure = new TeslaTower();
                break;
            case ARCHER:
                this.structure = new Archer();
                break;
            case GUARDIAN:
                this.structure = new Guardian();
                break;
        }
        this.firing = firing;
        this.structure.setFiring(firing);
        REGIONS.add(this);
    }

    public static Optional<Structure> getStructure(Location location) {
        Structure structure = null;
        for(StructureRegion region : REGIONS) {
            if(region.canBuild(location)) {
                structure = region.structure;
                break;
            }
        }
        return Optional.ofNullable(structure);
    }

    public static List<StructureRegion> getAll() {
        return REGIONS;
    }

    public String getType() {
        return this.structure.getStructureType().name();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(Utils.locToString(getMin()))
          .append(":")
          .append(Utils.locToString(getMax()))
          .append(":")
          .append(Utils.locToString(firing))
          .append(":")
          .append(structure.getStructureType().name());
        return builder.toString();
    }

    public static void from(String s) {
        String[] str = s.split(":");
        Location min = Utils.locFromString(str[0]);
        Location max = Utils.locFromString(str[1]);
        Location firing = Utils.locFromString(str[2]);
        StructureType type = StructureType.valueOf(str[3]);
        new StructureRegion(min, max, type, firing);
    }
}
