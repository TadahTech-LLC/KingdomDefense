package com.tadahtech.fadecloud.kd.creation;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tadahtech.fadecloud.kd.items.ItemBuilder;
import com.tadahtech.fadecloud.kd.map.StructureType;
import com.tadahtech.fadecloud.kd.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Timothy Andis
 */
public class StructureRegionCreator {

    private StructureType type;
    private Location min, max, firing;
    private Player player;

    private static Map<UUID, StructureRegionCreator> in = Maps.newHashMap();

    private static final ItemStack MIN = new ItemBuilder(new ItemStack(Material.WOOL))
      .data(DyeColor.LIME.getWoolData())
      .amount(1)
      .name(ChatColor.AQUA + "Minimum Corner")
      .build();
    private static final ItemStack MAX = new ItemBuilder(new ItemStack(Material.WOOL))
      .data(DyeColor.YELLOW.getWoolData())
      .amount(1)
      .name(ChatColor.AQUA + "Maximum Corner")
      .build();
    private static final ItemStack FIRING = new ItemBuilder(new ItemStack(Material.REDSTONE_BLOCK))
      .amount(1)
      .name(ChatColor.AQUA + "Firing Location")
      .lore(ChatColor.GRAY + "This is the location from which all ", ChatColor.GRAY + "calculations are done")
      .build();

    public StructureRegionCreator(Player player) {
        this.player = player;
        this.player.setItemInHand(MIN);
        this.player.updateInventory();
        in.put(player.getUniqueId(), this);
    }

    public static Optional<StructureRegionCreator> get(Player player) {
        return Optional.ofNullable(in.get(player.getUniqueId()));
    }

    public void onClick(Location location) {
        if(player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
            return;
        }
        if(player.getItemInHand().equals(MIN)) {
            this.min = location;
            this.player.setItemInHand(MAX);
            this.player.updateInventory();
        } else if(player.getItemInHand().equals(MAX)) {
            this.max = location;
            this.player.setItemInHand(FIRING);
            this.player.updateInventory();
        } else if(player.getItemInHand().equals(FIRING)){
            this.firing = location;
            this.player.setItemInHand(null);
            this.player.updateInventory();
            TextComponent base = new TextComponent(TextComponent.fromLegacyText(ChatColor.AQUA + "Please Select on of the Following: "));
            TextComponent archer = new TextComponent(TextComponent.fromLegacyText(ChatColor.GOLD + "Archer"));
            TextComponent guardian = new TextComponent(TextComponent.fromLegacyText(ChatColor.GOLD + "Guardian"));
            TextComponent blaze = new TextComponent(TextComponent.fromLegacyText(ChatColor.GOLD + "Blaze"));
            TextComponent tesla = new TextComponent(TextComponent.fromLegacyText(ChatColor.GOLD + "Tesla"));
            setEvents(archer, StructureType.ARCHER);
            setEvents(guardian, StructureType.GUARDIAN);
            setEvents(blaze, StructureType.BLAZE);
            setEvents(tesla, StructureType.TESLA);
            base.setExtra(Lists.newArrayList());
            base.addExtra(new TextComponent(TextComponent.fromLegacyText(ChatColor.AQUA + "Please Select on of the Following: ")));
            base.addExtra(archer);
            base.addExtra(" ");
            base.addExtra(guardian);
            base.addExtra(" ");
            base.addExtra(blaze);
            base.addExtra(" ");
            base.addExtra(tesla);
            player.spigot().sendMessage(base);
            in.remove(player.getUniqueId());
        }
    }

    private void setEvents(TextComponent component, StructureType type) {
        ClickEvent event =
          new ClickEvent(Action.RUN_COMMAND,
            "/kd createStruc " +
              Utils.locToXYZ(min) + ":" + Utils.locToXYZ(max) + ":" + Utils.locToXYZ(firing) + " " + type.name());
        HoverEvent hoverEvent =
          new HoverEvent(HoverEvent.Action.SHOW_TEXT,
            new TextComponent[]{
              new TextComponent(
                TextComponent.fromLegacyText(ChatColor.AQUA + "Click to set this region to a " + type.name() + " region"))
          });
        component.setClickEvent(event);
        component.setHoverEvent(hoverEvent);
    }

}