package com.tadahtech.fadecloud.kd.utils;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Timothy Andis
 */
public class Utils {

    public static String locToString(Location location) {
        if(location == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(location.getWorld().getName()).append(",").append(location.getBlockX()).append(",").append(location.getBlockY()).append(",").append(location.getBlockZ()).append(",").append(location.getYaw()).append(",").append(location.getPitch());
        return builder.toString();
    }

    public static String plural(String base, int i) {
        if(i == 1) {
            return base;
        }
        String last = base.substring(0, base.length() - 1);
        if(last.equalsIgnoreCase("e") || last.equalsIgnoreCase("x")) {
            return base.substring(0, base.length() - 2) + "es";
        }
        return base + "s";
    }

    public static Location locFromString(String s) {
        if (s == null) {
            return null;
        }
        String[] str = s.split(",");
        World world = Bukkit.getWorld(str[0]);
        int x = Integer.parseInt(str[1]);
        int y = Integer.parseInt(str[2]);
        int z = Integer.parseInt(str[3]);
        float yaw = Float.parseFloat(str[4]);
        float pitch = Float.parseFloat(str[5]);
        return new Location(world, x, y, z, yaw, pitch);
    }

    public static List<String> split(String str, String separator) {
        if (str == null || str.length() == 0) return Lists.newArrayList();
        if (separator == null || separator.length() == 0) return Lists.newArrayList();
        if (separator.length() > str.length()) return Lists.newArrayList();

        java.util.List<String> result = new java.util.ArrayList<String>();

        int index = str.indexOf(separator);
        while (index >= 0) {
            String newStr = str.substring(0, index);
            if (newStr.length() > 0) {
                result.add(newStr);
            }
            str = str.substring(index + separator.length());
            index = str.indexOf(separator);
        }

        if (str.length() > 0) {
            result.add(str);
        }

        return result;
    }

    public static Enchantment getEnchantmentName(String friendlyName) {
        if (friendlyName != null) {
            if (friendlyName.equalsIgnoreCase("Sharpness") || friendlyName.equalsIgnoreCase("Sharp"))
                return Enchantment.DAMAGE_ALL;
            else if (friendlyName.equalsIgnoreCase("Bane of Arthropods") || friendlyName.equalsIgnoreCase("Arthropods") || friendlyName.equalsIgnoreCase("Bane") || friendlyName.equalsIgnoreCase("Arthro"))
                return Enchantment.DAMAGE_ARTHROPODS;
            else if (friendlyName.equalsIgnoreCase("Smite") || friendlyName.equalsIgnoreCase("Undead"))
                return Enchantment.DAMAGE_UNDEAD;
            else if (friendlyName.equalsIgnoreCase("Power"))
                return Enchantment.ARROW_DAMAGE;
            else if (friendlyName.equalsIgnoreCase("Flame") || friendlyName.equalsIgnoreCase("Flames"))
                return Enchantment.ARROW_FIRE;
            else if (friendlyName.equalsIgnoreCase("Infinite") || friendlyName.equalsIgnoreCase("Infinity"))
                return Enchantment.ARROW_INFINITE;
            else if (friendlyName.equalsIgnoreCase("Punch") || friendlyName.equalsIgnoreCase("Push"))
                return Enchantment.ARROW_KNOCKBACK;
            else if (friendlyName.equalsIgnoreCase("Efficiency") || friendlyName.equalsIgnoreCase("Eff"))
                return Enchantment.DIG_SPEED;
            else if (friendlyName.equalsIgnoreCase("Unbreaking") || friendlyName.equalsIgnoreCase("Durability") || friendlyName.equalsIgnoreCase("Dura"))
                return Enchantment.DURABILITY;
            else if (friendlyName.equalsIgnoreCase("Fire Aspect") || friendlyName.equalsIgnoreCase("Fire"))
                return Enchantment.FIRE_ASPECT;
            else if (friendlyName.equalsIgnoreCase("Knockback") || friendlyName.equalsIgnoreCase("Knock"))
                return Enchantment.KNOCKBACK;
            else if (friendlyName.equalsIgnoreCase("Fortune") || friendlyName.equalsIgnoreCase("Fort"))
                return Enchantment.LOOT_BONUS_BLOCKS;
            else if (friendlyName.equalsIgnoreCase("Looting") || friendlyName.equalsIgnoreCase("Loot"))
                return Enchantment.LOOT_BONUS_MOBS;
            else if (friendlyName.equalsIgnoreCase("Luck"))
                return Enchantment.LUCK;
            else if (friendlyName.equalsIgnoreCase("Lure"))
                return Enchantment.LURE;
            else if (friendlyName.equalsIgnoreCase("Oxygen") || friendlyName.equalsIgnoreCase("Breathing") || friendlyName.equalsIgnoreCase("Respiration"))
                return Enchantment.OXYGEN;
            else if (friendlyName.equalsIgnoreCase("Protection") || friendlyName.equalsIgnoreCase("Prot"))
                return Enchantment.PROTECTION_ENVIRONMENTAL;
            else if (friendlyName.equalsIgnoreCase("Blast Protection") || friendlyName.equalsIgnoreCase("BlastProt"))
                return Enchantment.PROTECTION_EXPLOSIONS;
            else if (friendlyName.equalsIgnoreCase("Fall Protection") || friendlyName.equalsIgnoreCase("FallProt") || friendlyName.equalsIgnoreCase("Feather") || friendlyName.equalsIgnoreCase("Feather Falling"))
                return Enchantment.PROTECTION_FALL;
            else if (friendlyName.equalsIgnoreCase("Fire Protection") || friendlyName.equalsIgnoreCase("FireProt"))
                return Enchantment.PROTECTION_FIRE;
            else if (friendlyName.equalsIgnoreCase("Projectile Protection") || friendlyName.equalsIgnoreCase("ProjProt"))
                return Enchantment.PROTECTION_PROJECTILE;
            else if (friendlyName.equalsIgnoreCase("Silk Touch") || friendlyName.equalsIgnoreCase("SilkTouch") || friendlyName.equalsIgnoreCase("Silk"))
                return Enchantment.SILK_TOUCH;
            else if (friendlyName.equalsIgnoreCase("Thorns"))
                return Enchantment.THORNS;
            else if (friendlyName.equalsIgnoreCase("Water Worker") || friendlyName.equalsIgnoreCase("Aqua Affinity"))
                return Enchantment.WATER_WORKER;
        }
        return null;
    }

    public static String dyeName(byte data) {
        return pretty(DyeColor.getByData(data).name());
    }

    public static String pretty(String name) {
        //noinspection deprecation
        if(!name.contains("_")) {
            return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        }
        String[] str = name.split("_");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < str.length; i++) {
            String s = str[i];
            String main = s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
            builder.append(main);
            if ((i + 1) != str.length) {
                builder.append(" ");
            }
        }
        return builder.toString();
    }

    public static String friendlyName(String enchName) {
        switch (enchName) {
            case "PROTECTION_ENVIRONMENTAL":
                return "protection";
            case "PROTECTION_FIRE":
                return "fire protection";
            case "PROTECTION_FALL":
                return "feather falling";
            case "PROTECTION_EXPLOSIONS":
                return "blast protection";
            case "PROTECTION_PROJECTILE":
                return "projectile protection";
            case "OXYGEN":
                return "respiration";
            case "WATER_WORKER":
                return "aqua affinity";
            case "THORNS":
                return "thorns";
            case "DAMAGE_ALL":
                return "sharpness";
            case "DAMAGE_UNDEAD":
                return "smite";
            case "DAMAGE_ARTHROPODS":
                return "bane of arthropods";
            case "KNOCKBACK":
                return "knockback";
            case "FIRE_ASPECT":
                return "fire aspect";
            case "LOOT_BONUS_MOBS":
                return "looting";
            case "DIG_SPEED":
                return "efficiency";
            case "SILK_TOUCH":
                return "silk touch";
            case "DURABILITY":
                return "unbreaking";
            case "ARROW_DAMAGE":
                return "power";
            case "ARROW_KNOCKBACK":
                return "punch";
            case "ARROW_FIRE":
                return "flame";
            case "ARROW_INFINITE":
                return "infinity";
            case "LUCK":
                return "luck of the sea";
            case "LURE":
                return "lure";
        }
        return "";
    }

    public static List<Location> circle(Location loc, Integer r, Integer h, Boolean hollow, Boolean sphere, int plus_y) {
        List<Location> circleblocks = new ArrayList<>();
        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();
        for (int x = cx - r; x <= cx +r; x++)
            for (int z = cz - r; z <= cz +r; z++)
                for (int y = (sphere ? cy - r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r*r && !(hollow && dist < (r-1)*(r-1))) {
                        Location l = new Location(loc.getWorld(), x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }

        return circleblocks;
    }

    public static String formatTime(int timeLeft) {
        int seconds = timeLeft;
        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;

        int minutes = seconds / SECONDS_IN_A_MINUTE;
        seconds -= minutes * SECONDS_IN_A_MINUTE;

        int hours = minutes / MINUTES_IN_AN_HOUR;
        minutes -= hours * MINUTES_IN_AN_HOUR;

        String min = (minutes > 10 ? "0" + minutes : "" + minutes);
        String sec = (seconds > 10 ? "0" + seconds : "" + seconds);
        return min + ":" + sec;
    }

    public static long getHours(long millis) {
        return TimeUnit.MILLISECONDS.toHours(millis);
    }

    public static Enchantment getFromString(String s){
        return Enchantment.getByName(s);
    }

    public static float percent(int amount, int total) {
        return (amount * 100.0f) / total;
    }

    public static String friendly(String name) {
        return pretty(name);
    }

    public static String toString(List<String> collect) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < collect.size(); i++) {
            String s = collect.get(i);
            builder.append(s);
            if((i + 1) != collect.size()) {
                builder.append(",");
            }
        }
        return builder.toString();
    }

    public static int parse(String s) {
        return Integer.parseInt(s);
    }
}
