package com.tadahtech.fadecloud.kd.creation;

import com.google.common.collect.Maps;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.map.*;
import com.tadahtech.fadecloud.kd.teams.CSTeam.TeamType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

/**
 * Created by Timothy Andis (TadahTech) on 7/30/2015.
 */
public class GameMapCreator {

    private Player player;
    private Map<LocationType, Location> locations;
    private Map<TeamType, Island> islands;
    private String name;
    private int min, max;
    private Location bridgeMin, bridgeMax;

    public GameMapCreator(Player player) {
        this.player = player;
        this.locations = Maps.newHashMap();
        this.islands = Maps.newHashMap();
        new ConversationFactory(KingdomDefense.getInstance())
          .withPrefix(new GamePrefix())
          .withFirstPrompt(new NamePrompt())
          .buildConversation(player).begin();
    }

    private class GamePrefix implements ConversationPrefix {

        @Override
        public String getPrefix(ConversationContext context) {
            StringBuilder builder = new StringBuilder();
            builder.append(ChatColor.GRAY).append("[");
            builder.append(ChatColor.YELLOW).append("MapBuilder: ");
            if(context.getSessionData("name") != null) {
                builder.append(ChatColor.AQUA).append(context.getSessionData("name"));
            }
            builder.append(ChatColor.GRAY).append("] ");
            return builder.toString();
        }
    }

    private class NamePrompt extends ValidatingPrompt {

        @Override
        protected boolean isInputValid(ConversationContext conversationContext, String s) {
            return true;
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String s) {
            name = s;
            return new MinNumberPrompt();
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return "What's the name of this Game?";
        }


    }

    private class MinNumberPrompt extends ValidatingPrompt {

        @Override
        protected boolean isInputValid(ConversationContext conversationContext, String s) {
            try {
                Integer.parseInt(s);
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String s) {
            min = Integer.parseInt(s);
            return new MaxNumberPrompt();
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return "What's the minimum amount of players?";
        }


    }

    private class MaxNumberPrompt extends ValidatingPrompt {

        @Override
        protected boolean isInputValid(ConversationContext conversationContext, String s) {
            try {
                Integer.parseInt(s);
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String s) {
            max = Integer.parseInt(s);
            return new LobbyPrompt();
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return "What's the maximum amount of players?";
        }

    }

    private class LobbyPrompt extends ValidatingPrompt {

        @Override
        protected boolean isInputValid(ConversationContext conversationContext, String s) {
            return s.equalsIgnoreCase("lobby");
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String s) {
            locations.put(LocationType.LOBBY, player.getLocation());
            return new CenterLocationPrompt();
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return "Stand in the place where you want the lobby to be (waiting area before a game), then type \"lobby\'";
        }

    }

    private class CenterLocationPrompt extends ValidatingPrompt {

        @Override
        protected boolean isInputValid(ConversationContext conversationContext, String s) {
            return s.equalsIgnoreCase("next");
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String s) {
            locations.put(LocationType.CENTER, player.getLocation());
            return new BridgeMinPrompt();
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return "Stand in the place where you want the crate to fall to, then type \"next\'";
        }

    }

    private class BridgeMinPrompt extends ValidatingPrompt {

        @Override
        protected boolean isInputValid(ConversationContext conversationContext, String s) {
            return s.equalsIgnoreCase("next");
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String s) {
            bridgeMin = player.getLocation();
            return new BridgeMaxPrompt();
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return "Stand in the place where the bridge's min is, then type \"next\'";
        }

    }

    private class BridgeMaxPrompt extends ValidatingPrompt {

        @Override
        protected boolean isInputValid(ConversationContext conversationContext, String s) {
            return s.equalsIgnoreCase("next");
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String s) {
            bridgeMax = player.getLocation();
            return new CreeperSpawn();
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return "Stand in the place where the bridge's max is, then type \"next\'";
        }

    }

    private class CreeperSpawn extends ValidatingPrompt {

        @Override
        protected boolean isInputValid(ConversationContext conversationContext, String s) {
            return s.equalsIgnoreCase("next");
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String s) {
            locations.put(LocationType.CREEPER_SPAWN, player.getLocation());
            return new CreeperKing();
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return "Stand in the place where you want the Creeper Team to spawn, then type \"next\'";
        }

    }

    private class CreeperKing extends ValidatingPrompt {

        @Override
        protected boolean isInputValid(ConversationContext conversationContext, String s) {
            return s.equalsIgnoreCase("next");
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String s) {
            locations.put(LocationType.CREEPER_KING, player.getLocation());
            return new CreeperVillagerPrompt();
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return "Stand in the place where you want the Creeper King to be, then type \"next\'";
        }

    }

    private class CreeperVillagerPrompt extends ValidatingPrompt {

        @Override
        protected boolean isInputValid(ConversationContext conversationContext, String s) {
            return s.equalsIgnoreCase("next");
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String s) {
            locations.put(LocationType.CREEPER_VILLAGER, player.getLocation());
            return new ZombieSpawn();
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return "Stand in the place where you want the Creeper Shop to be, then type \"next\'";
        }

    }

    private class ZombieSpawn extends ValidatingPrompt {

        @Override
        protected boolean isInputValid(ConversationContext conversationContext, String s) {
            return s.equalsIgnoreCase("next");
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String s) {
            locations.put(LocationType.ZOMBIE_SPAWN, player.getLocation());
            return new ZombieKing();
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return "Stand in the place where you want the Zombie Team to spawn, then type \"next\'";
        }

    }

    private class ZombieKing extends ValidatingPrompt {

        @Override
        protected boolean isInputValid(ConversationContext conversationContext, String s) {
            return s.equalsIgnoreCase("next");
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String s) {
            locations.put(LocationType.ZOMBIE_KING, player.getLocation());
            return new ZombieVillagerPrompt();
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return "Stand in the place where you want the Zombie King to be, then type \"next\'";
        }

    }

    private class ZombieVillagerPrompt extends ValidatingPrompt {

        @Override
        protected boolean isInputValid(ConversationContext conversationContext, String s) {
            return s.equalsIgnoreCase("next");
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String s) {
            locations.put(LocationType.ZOMBIE_VILLAGER, player.getLocation());
            return new EndermanSpawn();
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return "Stand in the place where you want the Zombie Shop to be, then type \"next\'";
        }

    }

    private class EndermanSpawn extends ValidatingPrompt {

        @Override
        protected boolean isInputValid(ConversationContext conversationContext, String s) {
            return s.equalsIgnoreCase("next");
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String s) {
            locations.put(LocationType.ENDERMAN_SPAWN, player.getLocation());
            return new EndermanKing();
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return "Stand in the place where you want the Enderman Team to spawn, then type \"next\'";
        }

    }

    private class EndermanKing extends ValidatingPrompt {

        @Override
        protected boolean isInputValid(ConversationContext conversationContext, String s) {
            return s.equalsIgnoreCase("next");
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String s) {
            locations.put(LocationType.ENDERMAN_KING, player.getLocation());
            return new EndermanVillagerPrompt();
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return "Stand in the place where you want the Enderman King to be, then type \"next\'";
        }

    }

    private class EndermanVillagerPrompt extends ValidatingPrompt {

        @Override
        protected boolean isInputValid(ConversationContext conversationContext, String s) {
            return s.equalsIgnoreCase("next");
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String s) {
            locations.put(LocationType.ENDERMAN_VILLAGER, player.getLocation());
            return new SkeletonSpawn();
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return "Stand in the place where you want the Enderman Shop to be, then type \"next\'";
        }

    }

    private class SkeletonSpawn extends ValidatingPrompt {

        @Override
        protected boolean isInputValid(ConversationContext conversationContext, String s) {
            return s.equalsIgnoreCase("next");
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String s) {
            locations.put(LocationType.SKELETON_SPAWN, player.getLocation());
            return new SkeletonKing();
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return "Stand in the place where you want the Skeleton Team to spawn, then type \"next\'";
        }

    }

    private class SkeletonKing extends ValidatingPrompt {

        @Override
        protected boolean isInputValid(ConversationContext conversationContext, String s) {
            return s.equalsIgnoreCase("next");
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String s) {
            locations.put(LocationType.SKELETON_KING, player.getLocation());
            return new SkeletonVillagerPrompt();
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return "Stand in the place where you want the Skeleton King to be, then type \"next\'";
        }

    }

    private class SkeletonVillagerPrompt extends ValidatingPrompt {

        @Override
        protected boolean isInputValid(ConversationContext conversationContext, String s) {
            return s.equalsIgnoreCase("next");
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String s) {
            locations.put(LocationType.SKELETON_VILLAGER, player.getLocation());
            return new SkeletonIslandMin();
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return "Stand in the place where you want the Skeleton Shop to be, then type \"next\'";
        }

    }

    private Island island;
    private Location regionmin, regionmax;

    private class SkeletonIslandMin extends ValidatingPrompt {

        @Override
        protected boolean isInputValid(ConversationContext conversationContext, String s) {
            return s.equalsIgnoreCase("next");
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String s) {
            regionmin = player.getLocation();
            return new SkeletonIslandMax();
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return "Stand in the place where you want the Skeleton Island Min Point to be, then type \"next\'";
        }

    }

    private class SkeletonIslandMax extends ValidatingPrompt {

        @Override
        protected boolean isInputValid(ConversationContext conversationContext, String s) {
            return s.equalsIgnoreCase("next");
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String s) {
            regionmax = player.getLocation();
            Region region = new Region(regionmin, regionmax);
            island = new Island(region);
            islands.put(TeamType.SKELETON, island);
            return new ZombieIslandMin();
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return "Stand in the place where you want the Skeleton Island Max Point to be, then type \"next\'";
        }

    }

    private class ZombieIslandMin extends ValidatingPrompt {

        @Override
        protected boolean isInputValid(ConversationContext conversationContext, String s) {
            return s.equalsIgnoreCase("next");
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String s) {
            regionmin = player.getLocation();
            return new ZombieIslandMax();
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return "Stand in the place where you want the Zombie Island Min Point to be, then type \"next\'";
        }

    }

    private class ZombieIslandMax extends ValidatingPrompt {

        @Override
        protected boolean isInputValid(ConversationContext conversationContext, String s) {
            return s.equalsIgnoreCase("next");
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String s) {
            regionmax = player.getLocation();
            Region region = new Region(regionmin, regionmax);
            island = new Island(region);
            islands.put(TeamType.ZOMBIE, island);
            return new CreeperIslandMin();
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return "Stand in the place where you want the Zombie Island Max Point to be, then type \"next\'";
        }

    }

    private class CreeperIslandMin extends ValidatingPrompt {

        @Override
        protected boolean isInputValid(ConversationContext conversationContext, String s) {
            return s.equalsIgnoreCase("next");
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String s) {
            regionmin = player.getLocation();
            return new CreeperIslandMax();
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return "Stand in the place where you want the Creeper Island Min Point to be, then type \"next\'";
        }

    }

    private class CreeperIslandMax extends ValidatingPrompt {

        @Override
        protected boolean isInputValid(ConversationContext conversationContext, String s) {
            return s.equalsIgnoreCase("next");
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String s) {
            regionmax = player.getLocation();
            Region region = new Region(regionmin, regionmax);
            island = new Island(region);
            islands.put(TeamType.CREEPER, island);
            return new EndermanIslandMin();
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return "Stand in the place where you want the Creeper Island Max Point to be, then type \"next\'";
        }

    }


    private class EndermanIslandMin extends ValidatingPrompt {

        @Override
        protected boolean isInputValid(ConversationContext conversationContext, String s) {
            return s.equalsIgnoreCase("next");
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String s) {
            regionmin = player.getLocation();
            return new EndermanIslandMax();
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return "Stand in the place where you want the Enderman Island Min Point to be, then type \"next\'";
        }

    }

    private class EndermanIslandMax extends ValidatingPrompt {

        @Override
        protected boolean isInputValid(ConversationContext conversationContext, String s) {
            return s.equalsIgnoreCase("next");
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String s) {
            regionmax = player.getLocation();
            Region region = new Region(regionmin, regionmax);
            island = new Island(region);
            islands.put(TeamType.ENDERMAN, island);
            new BukkitRunnable() {
                @Override
                public void run() {
                    done();
                }
            }.runTaskLater(KingdomDefense.getInstance(), 1L);
            return END_OF_CONVERSATION;
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return "Stand in the place where you want the Enderman Island Max Point to be, then type \"next\'";
        }

    }

    public void done() {
        String[] authors = { "TadahTech", "Eriic", "DaddyMew" };
        Bridge bridge = new Bridge(bridgeMin, bridgeMax);
        GameMap map = new GameMap(locations, authors, name, min, max, islands, bridge);
        player.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "Map Built! Saving....");
        KingdomDefense.getInstance().setMap(map);
        KingdomDefense.getInstance().getMapIO().save();
        player.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "Map Saved....");
    }




}
