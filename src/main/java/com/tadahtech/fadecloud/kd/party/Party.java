package com.tadahtech.fadecloud.kd.party;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.lang.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Timothy Andis
 */
public class Party {

    private UUID owner;
    private List<UUID> players;

    public Party(UUID owner) {
        this.owner = owner;
        this.players = Lists.newArrayList();
    }

    public void invite(UUID uuid) {
        if(players.contains(uuid)) {
            Lang.PLAYER_ALREADY_IN_PARTY.send(getBukkitOwner(), ImmutableMap.of("other", Bukkit.getPlayer(uuid).getName()));
            return;
        }
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(uuid);
        if(info.getBukkitPlayer() == null) {
            //Not online, ummmm, I don't REALLY wanna do anything here....
            //Maybe latttter, but for now, there's only 1 lobby, so its all good
            Lang.PLAYER_NOT_FOUND.send(getBukkitOwner(), ImmutableMap.of());
            return;
        }
        if(info.getParty() != null) {
            Lang.PLAYER_IN_DIFFERENT_PARTY.send(getBukkitOwner(), ImmutableMap.of("other", info.getBukkitPlayer().getName()));
            return;
        }
        if(info.getInvitedTo() != null) {
            Lang.PLAYER_ALREADY_INVITED.send(getBukkitOwner(), ImmutableMap.of("other", info.getBukkitPlayer().getName()));
            return;
        }
        Lang.PARTY_INVITE_SUCCESS.send(getBukkitOwner(), ImmutableMap.of("other", info.getBukkitPlayer().getName()));
        Lang.PARTY_INVITED_TO.send(info, ImmutableMap.of("other", getBukkitOwner().getName()));
        info.setInvitedTo(this);
    }

    public void add(PlayerInfo info) {
        if(info.getInvitedTo() == null) {
            Lang.PARTY_NOT_INVITED.send(info);
            return;
        }
        Party invited = info.getInvitedTo();
        if(!invited.equals(this)) {
            Lang.PARTY_NOT_INVITED.send(info);
            return;
        }
        this.players.add(info.getUuid());
        getBukkitPlayers().stream().forEach(player -> Lang.PARTY_JOIN.send(player, ImmutableMap.of("other", info.getBukkitPlayer().getName())));
    }

    private List<Player> getBukkitPlayers() {
        return players.stream().map(Bukkit::getPlayer).collect(Collectors.toList());
    }

    public Player getBukkitOwner() {
        return Bukkit.getPlayer(owner);
    }

    public UUID getOwner() {
        return owner;
    }
}
