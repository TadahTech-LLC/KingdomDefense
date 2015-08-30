package com.tadahtech.fadecloud.kd.teams.enderman;

import com.google.common.collect.Lists;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.items.ItemBuilder;
import com.tadahtech.fadecloud.kd.map.Island;
import com.tadahtech.fadecloud.kd.map.LocationType;
import com.tadahtech.fadecloud.kd.teams.CSTeam;
import com.tadahtech.fadecloud.kd.teams.Loadout;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by Timothy Andis
 */
public class EndermanTeam extends CSTeam {

    private int ran = 0;
    private Random random = new Random();
    protected static PotionEffect EFFECT = new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 10, 3);

    public EndermanTeam(Island island) {
        super(TeamType.ENDERMAN, island, Loadout.ENDERMAN);
    }

    public void onMove(PlayerInfo info) {
        if (!info.getBukkitPlayer().hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            info.setInvisible(false);
            info.setInvisibleFromChance(true);
            return;
        }
        Player player = info.getBukkitPlayer();
        Location location = player.getLocation();
        for (int i = 0; i < 10; i++) {
            location.getWorld().playEffect(location, Effect.SMOKE, 0);
        }
    }

    @Override
    public void onOtherDamage(EntityDamageByEntityEvent event, PlayerInfo info) {
        Entity damagerRaw = event.getDamager();
        if (!(damagerRaw instanceof EnderPearl)) {
            return;
        }
        int damageReduc = (info.getLevel(TeamType.ENDERMAN) * 10) + 60;
        event.setDamage(event.getFinalDamage() / damageReduc);
    }

    @Override
    public void onDamage(EntityDamageByEntityEvent event, PlayerInfo info) {
        int next = random.nextInt(100);
        if (next <= 8) {
            info.getBukkitPlayer().addPotionEffect(EFFECT);
            info.setInvisibleFromChance(true);
            info.getBukkitPlayer().playSound(info.getBukkitPlayer().getLocation(), Sound.ENDERMAN_TELEPORT, 2.0F, 1.0F);
        }
    }

    @Override
    public void onHit(EntityDamageByEntityEvent event, PlayerInfo info) {
        if (info.isInvisible()) {
            Player player = info.getBukkitPlayer();
            if (player.getItemInHand() == null || player.getItemInHand().getType() != Material.AIR) {
                return;
            }
            event.setCancelled(true);
            return;
        }
        if (info.isInvisibleFromChance()) {
            event.setCancelled(true);
        }
    }

    @Override
    public void applyEffects(Player player) {

    }

    @Override
    public LocationType getLocationType() {
        return LocationType.ENDERMAN_KING;
    }

    @Override
    public ItemStack getMenuIcon() {
        ItemStack itemStack = new ItemStack(Material.SKULL_ITEM);
        ItemBuilder builder = new ItemBuilder(itemStack);
        builder.data((byte) 3);
        builder.amount(getSize());
        builder.name(ChatColor.LIGHT_PURPLE + "Enderman Team");
        List<String> loreRaw = KingdomDefense.getInstance().getConfig().getStringList("enderman-desc");
        List<String> lore = Lists.newArrayList();
        lore.addAll(loreRaw.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList()));
        builder.lore(lore.toArray(new String[lore.size()]));
        itemStack = builder.build();
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        meta.setOwner("MHF_Enderman");
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @Override
    public void tick() {

    }
}
