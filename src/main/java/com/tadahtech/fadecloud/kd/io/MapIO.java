package com.tadahtech.fadecloud.kd.io;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.map.GameMap;
import com.tadahtech.fadecloud.kd.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map.Entry;

/**
 * Created by Timothy Andis (TadahTech) on 7/28/2015.
 */
public class MapIO {

    private File dir;

    public MapIO() {
        this.dir = new File(KingdomDefense.getInstance().getDataFolder(), "maps");
        if(!dir.exists() && !dir.mkdirs()) {
            KingdomDefense.getInstance().getLogger().info("Failed maps subdir.");
        }
        load();
    }

    public void load() {
        File[] files = dir.listFiles();
        if(files == null) {
            KingdomDefense.getInstance().getLogger().info("Failed to load maps. None in there?");
            return;
        }
        StringBuilder builder = new StringBuilder();
        int len = files.length;
        for(int i = 0; i < files.length; i++) {
            File file = files[i];
            GameMap map = GameMap.load(file);
            KingdomDefense.getInstance().setMap(map);
            builder.append(map.getName()).append(" by ").append(Arrays.toString(map.getAuthors())
              .replace(",", ", ").replace("[", "").replace("]", ""));
            if((i + 1) == len) {
                builder.append(".");
                continue;
            }
            builder.append(", ");
        }
        KingdomDefense.getInstance().getLogger().info("Loaded " + len + Utils.plural("map", len));
        KingdomDefense.getInstance().getLogger().info(builder.toString());

    }

    public void save() {
        GameMap map = KingdomDefense.getInstance().getMap();
        File file = new File(dir, map.getName() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        for(Entry<String, Object> entry : map.save().entrySet()) {
            config.set(entry.getKey(), entry.getValue());
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
