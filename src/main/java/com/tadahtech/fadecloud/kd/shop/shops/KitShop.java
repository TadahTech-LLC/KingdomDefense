package com.tadahtech.fadecloud.kd.shop.shops;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.items.ItemBuilder;
import com.tadahtech.fadecloud.kd.kit.CSKit;
import com.tadahtech.fadecloud.kd.lang.Lang;
import com.tadahtech.fadecloud.kd.menu.Button;
import com.tadahtech.fadecloud.kd.menu.Menu;
import com.tadahtech.fadecloud.kd.nms.CustomEntityType;
import com.tadahtech.fadecloud.kd.nms.mobs.KDVillager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

/**
 * Created by Timothy Andis
 */
public class KitShop {

    private List<CSKit> kits;
    private Location location;
    private Map<CSKit, Integer> prices;
    public static KitShop INSTANCE;

    public KitShop(Map<CSKit, Integer> prices, Location location) {
        this.kits = Lists.newArrayList();
        kits.addAll(CSKit.getAll());
        this.prices = prices;
        this.location = location;
        kits.stream().filter(kit -> this.prices.get(kit) == null).forEach(kit -> this.prices.put(kit, 100));
        if (INSTANCE == null) {
            INSTANCE = this;
        }
    }

    public Menu getMenu(Player player) {
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        return new Menu(ChatColor.DARK_PURPLE + "Purchase Kits") {
            @Override
            protected Button[] setUp() {
                Button[] buttons = new Button[((kits.size() + 8) / 9 * 9)];
                buttons = pane(buttons);
                List<CSKit> kits = Lists.newArrayList(getKits());
                for (int i = 0; i < kits.size(); i++) {
                    CSKit kit = kits.get(i);
                    Integer price = prices.get(kit);
                    String name = kit.getName();
                    String display = ChatColor.RED + name;
                    Result result = Result.DENY;
                    String[] lore = {
                      ChatColor.GRAY + "Price: " + ChatColor.AQUA + price.toString(),
                      " ",
                      ChatColor.RED + "Not enough coins!"
                    };
                    String permission = "kd.kits." + name.toLowerCase();
                    String command = "pex user %player% add " + permission;
                    ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.RED.getWoolData());
                    if (player.hasPermission(permission)) {
                        itemStack = kit.getItems().get(0);
                        display = ChatColor.AQUA + name;
                        lore = new String[]{ChatColor.YELLOW + "You've already unlocked this kit"};
                        result = Result.DEFAULT;
                    } else {
                        if (info.hasEnough(prices.get(kit))) {
                            result = Result.ALLOW;
                            display = ChatColor.GREEN + name;
                            itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.LIME.getWoolData());
                            lore = new String[]{
                              ChatColor.GREEN + "Click to purchase"
                            };
                        }
                    }
                    ItemBuilder builder = new ItemBuilder(itemStack)
                      .name(display)
                      .lore(lore);
                    final Result finalResult = result;
                    buttons[i] = new Button(builder.cloneBuild(), player -> {
                        if (finalResult == Result.DEFAULT) {
                            Lang.KIT_OWNED.send(player, ImmutableMap.of("kit", name));
                            return;
                        }
                        if (finalResult == Result.DENY) {
                            int remainder = (int) (price - info.getCoins());
                            Lang.NOT_ENOUGH_COINS.send(info, ImmutableMap.of("coins", price.toString(), "remainder", String.valueOf(remainder)));
                            return;
                        }
                        info.remove(price);
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
                        Lang.KIT_UNLOCKED.send(player, ImmutableMap.of("kit", name));
                    });
                }
                return buttons;
            }
        };
    }

    public List<CSKit> getKits() {
        return kits;
    }

    public void init() {
        KDVillager villager = (KDVillager) CustomEntityType.VILLAGER.spawn(location);
        assert villager != null : "Villager is null, duh.";
        LivingEntity livingEntity = (LivingEntity) villager.getBukkitEntity();
        livingEntity.setCustomNameVisible(true);
        livingEntity.setCustomName(ChatColor.AQUA + "Shop");
        livingEntity.setRemoveWhenFarAway(false);
        livingEntity.setCanPickupItems(false);
    }
}
