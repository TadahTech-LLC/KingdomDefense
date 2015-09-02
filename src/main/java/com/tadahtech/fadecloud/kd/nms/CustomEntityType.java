package com.tadahtech.fadecloud.kd.nms;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.nms.mobs.*;
import net.minecraft.server.v1_7_R4.EntityInsentient;
import net.minecraft.server.v1_7_R4.EntityLiving;
import net.minecraft.server.v1_7_R4.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by Timothy Andis
 */
public enum CustomEntityType {

    ZOMBIE("Zombie", 54, CSZombie.class),
    SKELETON("Skeleton", 51, CSSkeleton.class),
    CREEPER("Creeper", 50, CSCreeper.class),
    ENDERMAN("Enderman", 58, CSEnderman.class),
    VILLAGER("Villager", 120, KDVillager.class);

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
            case VILLAGER:
                entity = new KDVillager(world);
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
        ((Map) getPrivateField("d", net.minecraft.server.v1_7_R4.EntityTypes.class, null)).put(clazz, name);
        ((Map) getPrivateField("f", net.minecraft.server.v1_7_R4.EntityTypes.class, null)).put(clazz, id);
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

    public static void register() {
        Logger logger = KingdomDefense.getInstance().getLogger();
        logger.info("Registered " + CustomEntityType.ZOMBIE.name() );
        logger.info("Registered " + CustomEntityType.ENDERMAN.name() );
        logger.info("Registered " + CustomEntityType.SKELETON.name() );
        logger.info("Registered " + CustomEntityType.CREEPER.name() );

    }
}
