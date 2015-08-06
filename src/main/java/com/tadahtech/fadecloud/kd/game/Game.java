package com.tadahtech.fadecloud.kd.game;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.csc.packets.response.GameInfoResponsePacket;
import com.tadahtech.fadecloud.kd.econ.EconomyManager;
import com.tadahtech.fadecloud.kd.econ.EconomyReward;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.kit.CSKit;
import com.tadahtech.fadecloud.kd.map.GameMap;
import com.tadahtech.fadecloud.kd.map.LocationType;
import com.tadahtech.fadecloud.kd.map.Region;
import com.tadahtech.fadecloud.kd.menu.menus.TeamMenu;
import com.tadahtech.fadecloud.kd.nms.King;
import com.tadahtech.fadecloud.kd.scoreboard.Gameboard;
import com.tadahtech.fadecloud.kd.teams.CSTeam;
import com.tadahtech.fadecloud.kd.teams.CSTeam.TeamType;
import com.tadahtech.fadecloud.kd.teams.ModSpecialItem;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.bossbar.BossBarAPI;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Timothy Andis
 */
public class Game {

    public static World WORLD;

    private static final long PEACE = 15 * 60;

    private CSTeam creeper, skeleton, enderman, zombie;
    private List<PlayerInfo> players;
    private int timeLeft = (int) TimeUnit.MINUTES.toSeconds(20);
    private GameState state;
    private GameMap map;
    private Map<TeamType, King> kings;
    private EconomyManager economyManager;
    private Gameboard gameboard;
    private CSTeam winner;
    private Location lobby;
    private int teamsLeft;

    public Game() {
        if (WORLD == null) {
            System.out.println("World is not set! Setting to first world");
            WORLD = Bukkit.getWorlds().get(0);
        }
        WORLD.setAutoSave(false);
        this.players = new ArrayList<>();
        this.state = GameState.WAITING;
        this.kings = new HashMap<>();
        this.map = KingdomDefense.getInstance().getMap();
        this.creeper = new CreeperTeam(map.getIslands().get(TeamType.CREEPER));
        this.zombie = new ZombieTeam(map.getIslands().get(TeamType.ZOMBIE));
        this.enderman = new EndermanTeam(map.getIslands().get(TeamType.ENDERMAN));
        this.skeleton = new SkeletonTeam(map.getIslands().get(TeamType.SKELETON));
        this.economyManager = new EconomyManager();
        this.gameboard = new Gameboard(this);
        this.lobby = map.getLocation(LocationType.LOBBY).get();
        this.lobby = lobby.getWorld().getHighestBlockAt(lobby).getLocation();
    }

    public void addPlayer(PlayerInfo info) {
        this.players.add(info);
        info.getBukkitPlayer().setGameMode(GameMode.SURVIVAL);
        doTeam(info);
        String message = ChatColor.AQUA + info.getBukkitPlayer().getName() + ChatColor.GRAY + " has joined. (" + ChatColor.YELLOW + getPlayers().size() + "/" + map.getMax() + ChatColor.GRAY + ")";
        if(map.getMin() == getPlayers().size()) {
            countdown();
        }
        gameboard.add(info);
        gameboard.flip();
        info.getBukkitPlayer().teleport(lobby);
        getBukkitPlayers().stream().forEach(player -> player.sendMessage(message));
    }

    @SuppressWarnings("deprecation")
    private void doTeam(PlayerInfo player) {
        if(!player.getBukkitPlayer().hasPermission("kd.chooseTeam") && !player.isBeta()) {
            List<CSTeam> teamList = new ArrayList<>(Arrays.asList(teams()));
            Collections.shuffle(teamList);
            CSTeam[] teams = teamList.toArray(new CSTeam[4]);
            CSTeam team = teams[0];
            for(CSTeam csTeam : teams) {
                if(csTeam.getSize() < team.getSize()) {
                    team = csTeam;
                }
            }
            team.add(player.getBukkitPlayer());
            player.setCurrentTeam(team);
            Player bukkitPlayer = player.getBukkitPlayer();
            bukkitPlayer.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "You're now on the " + team.getType().fancy() + ChatColor.YELLOW + ChatColor.BOLD + " team");
            bukkitPlayer.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Want to chose your own team? Consider donating @ store.fadecloudmc.com");
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                TeamMenu menu = new TeamMenu(teams());
                menu.open(player.getBukkitPlayer());
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
        WORLD.getEntities().stream().filter(entity -> !(entity instanceof Player)).forEach(Entity::remove);
        this.kings.put(TeamType.CREEPER, new King(creeper, map));
        this.kings.put(TeamType.ZOMBIE, new King(zombie, map));
        this.kings.put(TeamType.ENDERMAN, new King(enderman, map));
        this.kings.put(TeamType.SKELETON, new King(skeleton, map));
        this.state = GameState.PEACE;
        new GameInfoResponsePacket().write();
        this.map.getBridge().clear();
        flip();
        getPlayers().stream().forEach(info -> {
            double starting = economyManager.getStarting(info);
            if (starting > 0) {
                info.setCoins(info.getCoins() + starting);
            }
            CSTeam team = info.getCurrentTeam();
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
            player.teleport(location);
            try {
                CSKit.getDefault().give(player);
            } catch (Exception e) {
                System.out.println("No default kit specified. Skipping this step...");
            }
            if (info.isBeta()) {
                player.sendMessage(ModSpecialItem.PREFIX + "You have the Thor kit unlocked, check your king to get it!");
            }
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
            PacketUtil.sendTitleToPlayer(player, ChatColor.AQUA + "BEGIN", "");
        });
        new BukkitRunnable() {

            @Override
            public void run() {
                timeLeft--;
                if(timeLeft == 0) {
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

                if(timeLeft == PEACE) {
                    state = GameState.BATTLE;
                    flip();
                    new GameInfoResponsePacket().write();
                    map.dropBridge();
                    getBukkitPlayers().stream().forEach(player ->{
                        String title = ChatColor.DARK_RED + "ATTACK!";
                        String subtitle = ChatColor.GRAY + "Protect your king!";
                        PacketUtil.sendTitleToPlayer(player, title, subtitle);
                        player.playSound(player.getLocation(), Sound.AMBIENCE_THUNDER, 1.0F, 1.0F);
                    });
                }
                if(state == GameState.PEACE && seconds == 0 && minutes > 15) {
                    int time = Math.abs(15 - minutes);
                    String title = ChatColor.DARK_RED.toString() + time;
                    String subtitle = ChatColor.GRAY + Utils.plural("Minute", time) + " Till Battle";
                    getBukkitPlayers().stream().forEach(player -> PacketUtil.sendTitleToPlayer(player, title, subtitle));
                }
            }
        }.runTaskTimer(KingdomDefense.getInstance(), 0L, 20L);
    }

    public void countdown() {
        List<Player> players = getBukkitPlayers();
        this.state = GameState.COUNTDOWN;
        new BukkitRunnable() {

            private int time = 30;

            @Override
            public void run() {
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
                    players.stream().forEach(player -> player.playSound(player.getLocation(), Sound.ORB_PICKUP, 5.0F, 1.0F));
                }
                time--;
            }
        }.runTaskTimer(KingdomDefense.getInstance(), 0L, 20L);
    }

    public void end() {
        String hub = KingdomDefense.getInstance().getHubServerName();
        new BukkitRunnable() {
            @Override
            public void run() {
                KingdomDefense.getInstance().getServer().shutdown();
            }
        }.runTaskLater(KingdomDefense.getInstance(), 20L * 11);
        if(winner == null) {
            getPlayers().stream().forEach(info -> {
                Player player = info.getBukkitPlayer();
                this.economyManager.add(new EconomyReward("Finishing", 50), info);
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
                            KingdomDefense.getInstance().redirect(hub, player);
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
            //lame asses cant even do this shit right. whut the fuck am I supposed to do?
            //Make everyone win? No bitch you dont deserve it. Like win next time you bitches.
            //You know wha, I ought to take money from you for wasting my time
            //BUT NO! I'll give you some participation money, but you're still a failure to me.
            //Fucking noobs.
            return;
        }
        String team = this.winner.getType().fancy();
        getPlayers().stream().forEach(info -> {
            Player player = info.getBukkitPlayer();
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
            if (info.getCurrentTeam().equals(winner)) {
                this.economyManager.add(new EconomyReward("Winning Team", 200), info);
            } else {
                this.economyManager.add(new EconomyReward("Finishing", 50), info);
            }

            this.economyManager.display(info);
            PacketUtil.sendTitleToPlayer(player, team, ChatColor.YELLOW + "has won!");
            new BukkitRunnable() {

                private int runs = 10;

                @Override
                public void run() {
                    runs--;
                    if (runs <= 0) {
                        cancel();
                        KingdomDefense.getInstance().redirect(hub, player);
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

    public double getHealth(TeamType type) {
        return kings.get(type).getHealth();
    }

    public void moveCheck(Player player, Location to) {
        if(state != GameState.BATTLE) {
            String message = ChatColor.RED + "Kingdom Defense : " + ChatColor.AQUA + "fadecloudmc.com";
            BossBarAPI.setMessage(player, message);
            return;
        }
        Region creeper = this.creeper.getIsland().getRegion();
        Region zombie = this.zombie.getIsland().getRegion();
        Region enderman = this.enderman.getIsland().getRegion();
        Region skeleton = this.skeleton.getIsland().getRegion();
        float health = 100F;
        String message = ChatColor.RED + "Kingdom Defense : " + ChatColor.AQUA + "fadecloudmc.com";
        if (creeper.canBuild(to)) {
            health = (float) getHealth(TeamType.CREEPER) / 100F;
            message = ChatColor.GREEN + "Creeper King Health";
        } else if (zombie.canBuild(to)) {
            health = (float) getHealth(TeamType.ZOMBIE) / 100F;
            message = ChatColor.DARK_GREEN + "Zombie King Health";
        } else if (enderman.canBuild(to)) {
            health = (float) getHealth(TeamType.ENDERMAN) / 100F;
            message = ChatColor.LIGHT_PURPLE + "Enderman King Health";
        } else if (skeleton.canBuild(to)) {
            health = (float) getHealth(TeamType.SKELETON) / 100F;
            message = ChatColor.GRAY + "Skeleton King Health";
        }
        BossBarAPI.setMessage(player, message, health);
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
        if(teamsLeft == 1) {
            this.winner = getWinningTeam();
            end();
        }
    }

    public CSTeam getWinningTeam() {
        for (CSTeam team : teams()) {
            if(team.hasLost()) {
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
}
