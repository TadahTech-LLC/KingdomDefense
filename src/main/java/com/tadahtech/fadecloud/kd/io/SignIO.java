package com.tadahtech.fadecloud.kd.io;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.sign.LobbySign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Created by Timothy Andis (TadahTech) on 7/27/2015.
 */
public class SignIO {

    private File dir;
    private FileConfiguration config;

    public SignIO() {
        this.dir = new File(KingdomDefense.getInstance().getDataFolder(), "signs.yml");
        try {
            if(!dir.exists() && !dir.createNewFile()) {
                KingdomDefense.getInstance().getLogger().info("Failed to create signs.yml.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.config = YamlConfiguration.loadConfiguration(dir);
        LobbySign.load(config);
    }

    public void load() {

    }

    public void save() {
        for(LobbySign sign : LobbySign.getAll()) {
            config.set("signs." + sign.getArena(), sign.save());
            try {
                config.save(dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
