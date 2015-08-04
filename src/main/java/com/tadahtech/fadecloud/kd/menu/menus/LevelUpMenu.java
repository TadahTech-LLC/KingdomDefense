package com.tadahtech.fadecloud.kd.menu.menus;

import com.google.common.collect.Lists;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.items.ItemBuilder;
import com.tadahtech.fadecloud.kd.menu.Button;
import com.tadahtech.fadecloud.kd.menu.Menu;
import com.tadahtech.fadecloud.kd.teams.CSTeam.TeamType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.List;

/**
 * Created by Timothy Andis
 */
public class LevelUpMenu extends Menu {

    private PlayerInfo info;

    public LevelUpMenu(PlayerInfo info) {
        super(ChatColor.DARK_PURPLE + "Level Up");
        this.info = info;
    }

    @Override
    protected Button[] setUp() {
        Button[] buttons = new Button[36];
        int creeperLevel = info.getLevel(TeamType.CREEPER);
        int zombieLevel = info.getLevel(TeamType.ZOMBIE);
        int skeletonLevel = info.getLevel(TeamType.SKELETON);
        int endermanLevel = info.getLevel(TeamType.ENDERMAN);
        buttons = pane(buttons, DyeColor.RED);
        ItemStack creeper = new ItemBuilder(new ItemStack(Material.SKULL_ITEM))
          .data((byte) 4)
          .name(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Creeper Level Up")
          .lore(lore(TeamType.CREEPER))
          .build();
        ItemStack skeleton = new ItemBuilder(new ItemStack(Material.SKULL_ITEM))
          .data((byte) 3)
          .setOwner("MHF_Skeleton")
          .name(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Skeleton Level Up")
          .lore(lore(TeamType.SKELETON))
          .build();
        ItemStack zombie = new ItemBuilder(new ItemStack(Material.SKULL_ITEM))
          .data((byte) 2)
          .name(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Zombie Level Up")
          .lore(lore(TeamType.ZOMBIE))
          .build();
        ItemStack enderman = new ItemBuilder(new ItemStack(Material.SKULL_ITEM))
          .data((byte) 3)
          .setOwner("MHF_Enderman")
          .name(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Enderman Level Up")
          .lore(lore(TeamType.ENDERMAN))
          .build();
        buttons[13] = new Button(creeper, player -> {
            int cost = getCost(TeamType.CREEPER);
            if(!info.remove(cost)) {
                player.sendMessage(ChatColor.RED + "You cannot afford this!");
                player.playSound(player.getEyeLocation(), Sound.ANVIL_BREAK, 1.0F, 1.0F);
                player.closeInventory();
                return;
            }
            info.setLevel(TeamType.CREEPER, (creeperLevel + 1));
            player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
            player.closeInventory();
            player.sendMessage(ChatColor.GREEN + "Leveled up Creeper!");
            Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
            FireworkMeta meta = firework.getFireworkMeta();
            meta.setPower(1);
            meta.addEffect(FireworkEffect.builder()
              .withColor(Color.LIME, Color.GRAY)
              .withFade(Color.LIME)
              .withFlicker()
              .build());
            firework.setFireworkMeta(meta);
        });
        buttons[15] = new Button(zombie, player -> {
            int cost = getCost(TeamType.ZOMBIE);
            if(!info.remove(cost)) {
                player.sendMessage(ChatColor.RED + "You cannot afford this!");
                player.playSound(player.getEyeLocation(), Sound.ANVIL_BREAK, 1.0F, 1.0F);
                player.closeInventory();
                return;
            }
            info.setLevel(TeamType.ZOMBIE, (zombieLevel + 1));
            player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
            player.closeInventory();
            player.sendMessage(ChatColor.DARK_GREEN + "Leveled up Zombie!");
            Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
            FireworkMeta meta = firework.getFireworkMeta();
            meta.setPower(1);
            meta.addEffect(FireworkEffect.builder()
              .withColor(Color.GREEN, Color.BLACK)
              .withFade(Color.GREEN)
              .withTrail()
              .withFlicker()
              .build());
            firework.setFireworkMeta(meta);
        });
        buttons[21] = new Button(enderman, player -> {
            int cost = getCost(TeamType.ENDERMAN);
            if(!info.remove(cost)) {
                player.sendMessage(ChatColor.RED + "You cannot afford this!");
                player.playSound(player.getEyeLocation(), Sound.ANVIL_BREAK, 1.0F, 1.0F);
                player.closeInventory();
                return;
            }
            info.setLevel(TeamType.ENDERMAN, (endermanLevel + 1));
            player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
            player.closeInventory();
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Leveled up Enderman!");
            Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
            FireworkMeta meta = firework.getFireworkMeta();
            meta.setPower(1);
            meta.addEffect(FireworkEffect.builder()
              .withColor(Color.PURPLE, Color.GRAY)
              .withFade(Color.PURPLE)
              .withFlicker()
              .build());
            firework.setFireworkMeta(meta);
        });
        buttons[23] = new Button(skeleton, player -> {
            int cost = getCost(TeamType.SKELETON);
            if(!info.remove(cost)) {
                player.sendMessage(ChatColor.RED + "You cannot afford this!");
                player.playSound(player.getEyeLocation(), Sound.ANVIL_BREAK, 1.0F, 1.0F);
                player.closeInventory();
                return;
            }
            info.setLevel(TeamType.SKELETON, (skeletonLevel + 1));
            player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
            player.closeInventory();
            player.sendMessage(ChatColor.GRAY + "Leveled up Skeleton!");
            Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
            FireworkMeta meta = firework.getFireworkMeta();
            meta.setPower(1);
            meta.addEffect(FireworkEffect.builder()
              .withColor(Color.GRAY, Color.BLACK)
              .withFade(Color.GRAY)
              .withTrail()
              .withFlicker()
              .build());
            firework.setFireworkMeta(meta);
        });
        return buttons;
    }

    @Override
    public void onClose(Player player) {

    }

    public int getCost(TeamType teamType) {
        FileConfiguration config = KingdomDefense.getInstance().getConfig();
        String[] str = config.getString("level-prices." + teamType.name()).split("-");
        int level = info.getLevel(teamType) + 1;
        if(level == 5) {
            return -1;
        }
        return Integer.parseInt(str[level - 1]);
    }
    
    private String[] lore(TeamType teamType) {
        List<String> list = Lists.newArrayList();
        list.add(" ");
        int level = info.getLevel(teamType);
        int nextLevel = level + 1;
        int price = getCost(teamType);
        if(level == 4) {
            list.add(ChatColor.GOLD + "Max Level!");
            return list.toArray(new String[1]);
        }
        String curr = ChatColor.GRAY + "Current Level: " + ChatColor.AQUA + level;
        String next = ChatColor.GRAY + "Next Level: " + ChatColor.AQUA + nextLevel;
        String cost = ChatColor.GRAY + "Cost: " + ChatColor.AQUA + price;
        list.add(curr);
        list.add(next);
        list.add(cost);
        return list.toArray(new String[list.size()]);
    }
}