package com.tadahtech.fadecloud.kd.csc.serverComm;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.csc.ServerTeleporter;
import org.bukkit.entity.Player;

/**
 * Created by Timothy Andis
 */
public class BungeeServerTeleporter implements ServerTeleporter {

	private KingdomDefense plugin = KingdomDefense.getInstance();

	public BungeeServerTeleporter() {
		plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
	}

	@Override
	public void send(Player player, String server) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(server);
		player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
	}
}
