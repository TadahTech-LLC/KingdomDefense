package com.tadahtech.fadecloud.kd.map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * Created by Timothy Andis
 */
public class BlockData {

    private Material type;
    private byte data;

    public BlockData(Block block) {
        this.type = block.getType();
        this.data = block.getData();
    }

    public BlockData(Material type, byte data) {
        this.type = type;
        this.data = data;
    }

    public void set(Location location) {
        location.getBlock().setTypeIdAndData(type.getId(), data, true);
    }

    @Override
    public String toString() {
        return type.name() + "&" + data;
    }

    public static BlockData from(String s) {
        String[] str = s.split("&");
        Material material = Material.getMaterial(str[0]);
        byte data = Byte.parseByte(str[1]);
        return new BlockData(material, data);
    }


}
