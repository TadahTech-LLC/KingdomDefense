package com.tadahtech.fadecloud.kd.game;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.csc.packets.response.GameInfoResponsePacket;
import com.tadahtech.fadecloud.kd.econ.EconomyManager;
import com.tadahtech.fadecloud.kd.econ.EconomyReward;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.king.King;
import com.tadahtech.fadecloud.kd.kit.CSKit;
import com.tadahtech.fadecloud.kd.lang.Lang;
import com.tadahtech.fadecloud.kd.listeners.GameListener;
import com.tadahtech.fadecloud.kd.map.GameMap;
import com.tadahtech.fadecloud.kd.map.IslandOreGeneration;
import com.tadahtech.fadecloud.kd.map.LocationType;
import com.tadahtech.fadecloud.kd.menu.menus.TeamMenu;
import com.tadahtech.fadecloud.kd.nms.mobs.KDVillager;
import com.tadahtech.fadecloud.kd.scoreboard.Gameboard;
import com.tadahtech.fadecloud.kd.shop.shops.GameShop;
import com.tadahtech.fadecloud.kd.teams.CSTeam;
import com.tadahtech.fadecloud.kd.teams.CSTeam.TeamType;
import com.tadahtech.fadecloud.kd.teams.creeper.CreeperTeam;
import com.tadahtech.fadecloud.kd.teams.enderman.EndermanTeam;
import com.tadahtech.fadecloud.kd.teams.skeleton.SkeletonTeam;
import com.tadahtech.fadecloud.kd.teams.zombie.ZombieTeam;
import com.tadahtech.fadecloud.kd.utils.PacketUtil;
import com.tadahtech.fadecloud.kd.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Timothy Andis
 */
public class Game {

    public static World WORLD;

    private static final long PEACE = 20 * 60;

    private CSTeam creeper, skeleton, enderman, zombie;
    private List<PlayerInfo> players;
    private List<KDVillager> villagers;
    private int timeLeft = (int) TimeUnit.MINUTES.toSeconds(30);
    private int PEACE_TIMER = (int) TimeUnit.MINUTES.toSeconds(10);
    private GameState state;
    private GameMap map;
    private Map<TeamType, King> kings;
    private EconomyManager economyManager;
    private Gameboard gameboard;
    private CSTeam winner;
    private Location lobby;
    private int teamsLeft;
    private int peaceTime;

    public Game() {
        if (WORLD == null) {
            WORLD = Bukkit.getWorld("gameWorld");
        }
        WORLD.setAutoSave(false);
        this.players = new ArrayList<>();
        this.state = GameState.WAITING;
        this.kings = new HashMap<>();
        this.villagers = Lists.newArrayList();
        this.map = KingdomDefense.getInstance().getMap();
        this.creeper = new CreeperTeam(map.getIslands().get(TeamType.CREEPER));
        this.zombie = new ZombieTeam(map.getIslands().get(TeamType.ZOMBIE));
        this.enderman = new EndermanTeam(map.getIslands().get(TeamType.ENDERMAN));
        this.skeleton = new SkeletonTeam(map.getIslands().get(TeamType.SKELETON));
        this.economyManager = new EconomyManager();
        this.gameboard = new Gameboard(this);
        this.lobby = map.getLocation(LocationType.LOBBY).get();
        this.lobby = lobby.getWorld().getHighestBlockAt(lobby).getLocation();
        this.teamsLeft = 4;
//        this.kingSummonThread = new KingSummonThread();
        WORLD.setSpawnLocation(lobby.getBlockX(), lobby.getBlockY(), lobby.getBlockZ());
    }

    public void addPlayer(PlayerInfo info) {
        this.players.add(info);
        info.getBukkitPlayer().teleport(lobby);
        doTeam(info);
        String message = ChatColor.AQUA + info.getBukkitPlayer().getName() + ChatColor.GRAY + " has joined. (" + ChatColor.YELLOW + getPlayers().size() + "/" + map.getMax() + ChatColor.GRAY + ")";
        if (map.getMin() == getPlayers().size()) {
            countdown();
        }
        gameboard.add(info);
        gameboard.flip();

        getBukkitPlayers().stream().forEach(player -> player.sendMessage(message));
    }

    @SuppressWarnings("deprecation")
    private void doTeam(PlayerInfo info) {
        if (!info.getBukkitPlayer().hasPermission("kd.chooseTeam") && !info.isBeta()) {
            List<CSTeam> teamList = Lists.newArrayList(teams());
            Collections.shuffle(teamList);
            CSTeam[] teams = teamList.toArray(new CSTeam[4]);
            CSTeam team = teams[0];
            for (CSTeam csTeam : teams) {
                if (csTeam.getSize() < team.getSize()) {
                    team = csTeam;
                    break;
                }
            }
            team.add(info.getBukkitPlayer());
            info.setCurrentTeam(team);
            Lang.GAME_JOIN.send(info, ImmutableMap.of("team", team.getType().fancy()));
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                TeamMenu menu = new TeamMenu(teams());
                menu.open(info.getBukkitPlayer());
            }
        }.runTaskLater(KingdomDefense.getInstance(), 5L);

    }

    public void flip() {
        this.gameboard.flip();
    }

    public void removePlayer(PlayerInfo info) {
        this.players.remove(info);
        KingdomDefense.getInstance().getInfoStore().save(info);
        KingdomDefense.getInstance().redirect(KingdomDefense.getInstance().getHubServerName(), info.getBukkitPlayer());
        this.gameboard.flip();
    }

    public void start() {
        this.state = GameState.PEACE;
        new GameInfoResponsePacket().write();
        flip();
        this.map.getBridge().collect();
        this.map.getBridge().clear();
        getPlayers().stream().forEach(info -> {
            double starting = economyManager.getStarting(info);
            if (starting > 0) {
                info.setCoins(info.getCoins() + starting);
            }
            CSTeam team = info.getCurrentTeam();
            if(team == null) {
                info.setCurrentTeam(Lists.newArrayList(teams()).get(new Random().nextInt(4)));
            }
            team.add(info.getBukkitPlayer());
            info.getBukkitPlayer().getInventory().clear();
            team.loadout(info.getBukkitPlayer());
            LocationType type;
            switch (team.getType()) {
                case SKELETON:
                    type = LocationType.SKELETON_SPAWN;
                    break;
                case ZOMBIE:
                    type = LocationType.ZOMBIE_SPAWN;
                    break;
                case ENDERMAN:
                    type = LocationType.ENDERMAN_SPAWN;
                    break;
                case CREEPER:
                    type = LocationType.CREEPER_SPAWN;
                    break;
                default:
                    return;
            }
            Location location = map.getLocation(type).get();
            Player player = info.getBukkitPlayer();
            team.applyEffects(player);
            player.teleport(location);
            player.setLevel(0);
            try {
                CSKit.getDefault().give(player);
            } catch (Exception e) {
                System.out.println("No default kit specified. Skipping this step...");
            }
            Lang.GAME_START.send(info);
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
            PacketUtil.sendTitleToPlayer(player, ChatColor.AQUA + "BEGIN", "Bridge will form in 10 minutes");
        });
        new BukkitRunnable() {

            @Override
            public void run() {
                timeLeft--;
                flip();
                if (timeLeft == 0) {
                    end();
                    cancel();
                    return;
                }
                int seconds = timeLeft;
                final int MINUTES_IN_AN_HOUR = 60;
                final int SECONDS_IN_A_MINUTE = 60;

                int minutes = seconds / SECONDS_IN_A_MINUTE;
                seconds -= minutes * SECONDS_IN_A_MINUTE;

                int hours = minutes / MINUTES_IN_AN_HOUR;
                minutes -= hours * MINUTES_IN_AN_HOUR;

                if (timeLeft == PEACE) {
                    state = GameState.BATTLE;
                    new GameInfoResponsePacket().write();
                    getBukkitPlayers().stream().forEach(player -> {
                        String title = ChatColor.DARK_RED + "ATTACK!";
                        String subtitle = ChatColor.GRAY + "Protect your king!";
                        PacketUtil.sendTitleToPlayer(player, title, subtitle);
                        player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 1.0F, 1.0F);
                    });
//                    kingSummonThread.runTaskTimer(KingdomDefense.getInstance(), 0L, 30L * 20);
                    map.getBridge().place();
                    return;
                }
                if (state == GameState.PEACE && seconds == 0 && minutes > 20) {
                    PEACE_TIMER--;
                    int time = Math.abs(20 - minutes);
                    String title = ChatColor.RED.toString() + time;
                    String subtitle = ChatColor.GRAY + Utils.plural("Minute", time) + " Until Battle";

                    getBukkitPlayers().stream().forEach(player -> {
                        player.sendMessage(Lang.PREFIX + title + " " + subtitle);
                        PacketUtil.sendTitleToPlayer(player, title, subtitle);
                        player.playSound(player.getEyeLocation(), Sound.NOTE_PLING, 0.7F, 1.0F);
                    });
                    return;
                }
                if ((minutes % 5 == 0 || minutes < 5) && seconds == 0) {
                    String title = ChatColor.DARK_RED.toString() + minutes;
                    String subtitle = ChatColor.GRAY + Utils.plural("Minute", minutes) + " left!";

                    final int finalMinutes = minutes;
                    getBukkitPlayers().stream().forEach(player -> {
                        Lang.TIME_LEFT.send(player, ImmutableMap.of("minutes", String.valueOf(finalMinutes)));
                        PacketUtil.sendTitleToPlayer(player, title, subtitle);
                        player.playSound(player.getEyeLocation(), Sound.NOTE_STICKS, 0.7F, 1.0F);
                    });
                }
            }
        }.runTaskTimer(KingdomDefense.getInstance(), 0L, 20L);
    }

    public void countdown() {
        List<Player> players = getBukkitPlayers();
        this.state = GameState.COUNTDOWN;
        new BukkitRunnable() {

            private boolean generated = false;
            private int time = 30;

            @Override
            public void run() {
                if(!generated) {
                    generated = true;
                    Lists.newArrayList(teams()).forEach(csTeam -> IslandOreGeneration.generate(csTeam.getIsland()));
                    WORLD.getEntities().stream().filter(entity -> !(entity instanceof Player)).forEach(Entity::remove);
                    kings.put(TeamType.CREEPER, new King(creeper, map));
                    kings.put(TeamType.ZOMBIE, new King(zombie, map));
                    kings.put(TeamType.ENDERMAN, new King(enderman, map));
                    kings.put(TeamType.SKELETON, new King(skeleton, map));
                    villagers.add(GameShop.INSTANCE.init(map.getLocation(LocationType.CREEPER_VILLAGER).get()));
                    villagers.add(GameShop.INSTANCE.init(map.getLocation(LocationType.ZOMBIE_VILLAGER).get()));
                    villagers.add(GameShop.INSTANCE.init(map.getLocation(LocationType.ENDERMAN_VILLAGER).get()));
                    villagers.add(GameShop.INSTANCE.init(map.getLocation(LocationType.SKELETON_VILLAGER).get()));
                }
                if (players.size() < map.getMin()) {
                    cancel();
                    players.stream().forEach(player -> player.sendMessage(ChatColor.RED + "Not enough players to start, stopping countdown"));
                    return;
                }
                if (time == 0) {
                    cancel();
                    start();
                    return;
                }
                String title = ChatColor.YELLOW.toString() + ChatColor.BOLD + time;
                players.stream().forEach(player1 -> player1.setLevel(time));
                if (time % 5 == 0 || time < 10) {
                    players.stream().forEach(player -> PacketUtil.sendTitleToPlayer(player, title, ChatColor.YELLOW + "Game starts in"));
                    players.stream().forEach(player -> player.playSound(player.getLocation(), Sound.NOTE_STICKS, 0.7F, 1.0F));
                }
                time--;
            }
        }.runTaskTimer(KingdomDefense.getInstance(), 0L, 20L);
    }

    public void end() {
//        this.kingSummonThread.cancel();
        for (Player player : Utils.getOnlinePlayers()) {
            getBukkitPlayers().stream().forEach(player1 -> {
                if (player.equals(player1)) {
                    return;
                }
                player.showPlayer(player1);
                player1.showPlayer(player);
            });
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                KingdomDefense.getInstance().getServer().shutdown();
            }
        }.runTaskLater(KingdomDefense.getInstance(), 20L * 11);
        if (winner == null) {
            getPlayers().stream().forEach(info -> {
                Player player = info.getBukkitPlayer();
                this.economyManager.add(new EconomyReward("Roughing It Out", 50), info);
                this.economyManager.display(info);
                player.playSound(player.getLocation(), Sound.ZOMBIE_WOODBREAK, 1.0F, 1.0F);
                PacketUtil.sendTitleToPlayer(player, "Time Expired", ChatColor.YELLOW + "Nobody won!");
                new BukkitRunnable() {

                    private int runs = 10;

                    @Override
                    public void run() {
                        runs--;
                        if (runs <= 0) {
                            cancel();
                            KingdomDefense.getInstance().getInfoStore().save(info);
                            KingdomDefense.getInstance().redirect(KingdomDefense.getInstance().getHubServerName(), info.getBukkitPlayer());
                            return;
                        }
                        Firework firework = player.getWorld().spawn(player.getEyeLocation(), Firework.class);
                        FireworkMeta meta = firework.getFireworkMeta();
                        meta.setPower(1);
                        meta.addEffect(FireworkEffect.builder()
                          .withColor(Color.AQUA, Color.GRAY, Color.AQUA.mixColors(Color.AQUA, Color.RED))
                          .withTrail()
                          .withFlicker()
                          .build());
                        firework.setFireworkMeta(meta);
                    }
                }.runTaskTimer(KingdomDefense.getInstance(), 0L, 20L);

            });
            return;
        }
        String team = this.winner.getType().fancy();
        getPlayers().stream().forEach(info -> {
            Player player = info.getBukkitPlayer();
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
            boolean won = false;
            if (info.getCurrentTeam().equals(winner)) {
                this.economyManager.add(new EconomyReward("Winning Team", 200), info);
                info.addWin(winner.getType());
                won = true;
            } else {
                this.economyManager.add(new EconomyReward("Finishing", 50), info);
            }

            this.economyManager.display(info);
            PacketUtil.sendTitleToPlayer(player, team, ChatColor.YELLOW + "has won!");
            final boolean finalWon = won;
            new BukkitRunnable() {

                private int runs = 10;

                @Override
                public void run() {
                    runs--;
                    if (runs <= 0) {
                        cancel();
                        KingdomDefense.getInstance().getInfoStore().save(info);
                        KingdomDefense.getInstance().redirect(KingdomDefense.getInstance().getHubServerName(), info.getBukkitPlayer());
                        return;
                    }
                    if (!finalWon) {
                        return;
                    }
                    Firework firework = player.getWorld().spawn(player.getEyeLocation(), Firework.class);
                    FireworkMeta meta = firework.getFireworkMeta();
                    meta.setPower(1);
                    meta.addEffect(FireworkEffect.builder()
                      .withColor(Color.AQUA, Color.GRAY, Color.AQUA.mixColors(Color.AQUA, Color.RED))
                      .withTrail()
                      .withFlicker()
                      .build());
                    firework.setFireworkMeta(meta);
                }
            }.runTaskTimer(KingdomDefense.getInstance(), 0L, 20L);
        });
    }

    public List<Player> getBukkitPlayers() {
        return getPlayers().stream().map(PlayerInfo::getBukkitPlayer).collect(Collectors.toList());
    }

    public List<PlayerInfo> getPlayers() {
        return players;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public GameState getState() {
        return state;
    }

    public GameMap getMap() {
        return map;
    }

    public void setMap(GameMap map) {
        this.map = map;
    }

    public CSTeam[] teams() {
        return new CSTeam[]{
          creeper, zombie, skeleton, enderman
        };
    }

    public King getKing(TeamType teamType) {
        return kings.get(teamType);
    }

    public CSTeam getTeam(TeamType teamType) {
        switch (teamType) {
            case ENDERMAN:
                return enderman;
            case ZOMBIE:
                return zombie;
            case CREEPER:
                return creeper;
            case SKELETON:
                return skeleton;
        }
        return null;
    }

    public int getTeamsLeft() {
        return teamsLeft;
    }

    public void setTeamsLeft(int teamsLeft) {
        this.teamsLeft = teamsLeft;
        if (teamsLeft == 1) {
            this.winner = getWinningTeam();
            end();
        }
    }

    public CSTeam getWinningTeam() {
        for (CSTeam team : teams()) {
            if (team.hasLost()) {
                continue;
            }
            return team;
        }
        return null;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public Location getLobby() {
        return lobby;
    }

    private List<PotionEffect> spectatorEffects = new ArrayList<PotionEffect>() {{
        add(new PotionEffect(PotionEffectType.INVISIBILITY, 1, Integer.MAX_VALUE, true));
        add(new PotionEffect(PotionEffectType.SPEED, 4, Integer.MAX_VALUE, true));
        add(new PotionEffect(PotionEffectType.NIGHT_VISION, 1, Integer.MAX_VALUE, true));
    }};

    public void spectate(Player player) {
        player.playSound(player.getLocation(), Sound.VILLAGER_DEATH, 1.0F, 1.0F);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        GameListener.SPECTATOR.give(player, 1);
        for(Player player1 : Utils.getOnlinePlayers()) {
            if(player1.equals(player)) {
                continue;
            }
            player1.hidePlayer(player);
        }
        GameListener.HUB.give(player, 8);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.addPotionEffects(spectatorEffects);
        player.teleport(this.map.getLocation(LocationType.CENTER).get().clone().add(0, 10, 0));
    }

    public int getPeaceTime() {
        return PEACE_TIMER;
    }
}
