package com.tadahtech.fadecloud.kd.items.special;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.items.ModSpecialItem;
import com.tadahtech.fadecloud.kd.lang.Lang;
import com.tadahtech.fadecloud.kd.teams.CSTeam;
import com.tadahtech.fadecloud.kd.utils.ParticleEffect;
import com.tadahtech.fadecloud.kd.utils.RandomUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Timothy Andis
 */
public class HealerItem extends ModSpecialItem {

    private ParticleEffect particle = ParticleEffect.VILLAGER_HAPPY;
    private int radius = 3;
    private int particles = 250;
    private boolean sphere = true;
    private BukkitTask task;
    private List<Player> infos;

    public HealerItem() {
        super(new Potion(PotionType.INSTANT_HEAL).splash().toItemStack(1));
    }

    @Override
    public void onClick(Player player) {
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        CSTeam team = info.getCurrentTeam();
        if(team == null) {
            //huh
            return;
        }
        List<Player> players = team.getBukkitPlayers();
        this.infos = players;
        players.stream().forEach(player1 -> {
            setInvincible(player1);
            player1.playSound(player1.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
            player1.sendMessage(Lang.PREFIX + "You have become invincible thanks to " + player.getName());
        });
        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                for (int i = 0; i < particles; i++) {
                    Vector vector = RandomUtils.getRandomVector().multiply(radius);
                    if (!sphere) {
                        vector.setY(Math.abs(vector.getY()));
                    }
                    players.stream().forEach(player1 -> {
                        Location finalLoc = player1.getLocation().clone();
                        finalLoc.add(vector);
                        display(particle, finalLoc);
                        finalLoc.subtract(vector);
                    });
                }
            }
        }.runTaskTimer(KingdomDefense.getInstance(), 0L, 1L);

        end();
    }

    public void setInvincible(Player player) {
        KingdomDefense.getInstance().getInfoManager().get(player).setInvincible(true);
    }

    public void end() {
        new BukkitRunnable() {
            @Override
            public void run() {
                infos.stream().map(player -> KingdomDefense.getInstance().getInfoManager().get(player))
                  .collect(Collectors.toList())
                  .forEach(info -> {
                      info.setInvincible(false);
                      info.sendMessage(Lang.PREFIX + "Your invincibility has worn off!");
                  });
                infos.clear();
                task.cancel();
            }
        }.runTaskLater(KingdomDefense.getInstance(), 20L * 15);
    }

    protected void display(ParticleEffect particle, Location location) {
        particle.display(null, location, Color.GREEN, 32.0d, 0f, 0f, 0f, 0f, 1);
    }

}
