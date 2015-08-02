package com.tadahtech.fadecloud.kd.nms;

import com.tadahtech.fadecloud.kd.nms.mobs.CSCreeper;
import com.tadahtech.fadecloud.kd.nms.mobs.CSEnderman;
import com.tadahtech.fadecloud.kd.nms.mobs.CSSkeleton;
import com.tadahtech.fadecloud.kd.nms.mobs.CSZombie;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Created by Timothy Andis
 */
public enum CustomEntityType {

    ZOMBIE("Zombie", 54, CSZombie.class),
    SKELETON("Skeleton", 54, CSZombie.class),
    CREEPER("Creeper", 54, CSZombie.class),
    ENDERMAN("Enderman", 54, CSZombie.class),
    ;

    CustomEntityType(String name, int id, Class<? extends EntityInsentient> custom) {
        addToMaps(custom, name, id);
    }

    public static CSZombie spawnZombie(Location loc, boolean attack) {
        World world = ((CraftWorld) loc.getWorld()).getHandle();
        CSZombie entity = new CSZombie(world, attack);
        entity.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        ((CraftWorld) loc.getWorld()).getHandle().addEntity(entity);
        return entity;
    }

    public EntityLiving spawn(Location location) {
        World world = ((CraftWorld) location.getWorld()).getHandle();
        EntityLiving entity;
        switch (this) {
            case SKELETON:
                entity = new CSSkeleton(world);
                break;
            case CREEPER:
                entity = new CSCreeper(world);
                break;
            case ENDERMAN:
                entity = new CSEnderman(world);
                break;
            case ZOMBIE:
                entity = new CSZombie(world, false);
                break;
            default:
                return null;
        }
        entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        ((CraftWorld) location.getWorld()).getHandle().addEntity(entity);
        return entity;
    }

    @SuppressWarnings("unchecked")
    private static void addToMaps(Class clazz, String name, int id) {
        ((Map) getPrivateField("d", net.minecraft.server.v1_8_R3.EntityTypes.class, null)).put(clazz, name);
        ((Map) getPrivateField("f", net.minecraft.server.v1_8_R3.EntityTypes.class, null)).put(clazz, id);
    }

    public static Object getPrivateField(String fieldName, Class clazz, Object object) {
        Field field;
        Object o = null;
        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            o = field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return o;
    }
}
