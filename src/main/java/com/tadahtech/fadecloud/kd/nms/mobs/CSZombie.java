package com.tadahtech.fadecloud.kd.nms.mobs;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.nms.NMS;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R2.*;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Created by Timothy Andis
 */
public class CSZombie extends EntityZombie {

    public int ticks;
    private boolean a;

    public CSZombie(World world, boolean attack) {
        super(world);
        this.a = attack;
        NMS.clearGoals(targetSelector, goalSelector);
        if(attack) {
            this.goalSelector.a(1, new PathfinderGoalMeleeAttack(this, 1.0D, true));
            this.goalSelector.a(0, new PathfinderGoalMoveTowardsTarget(this, 1.0D, 1F));
            this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
            this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true));
            this.getAttributeInstance(GenericAttributes.maxHealth).setValue(100.0D);
            setCustomNameVisible(true);
            setCustomName(ChatColor.DARK_GRAY + "Undead Warrior");
            this.setEquipment(0, new ItemStack(Items.IRON_SWORD));
        } else {
            setCustomNameVisible(true);
            setCustomName(ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + "Zombie King");
            NMS.equip(this);
        }
        getBukkitEntity().setMetadata("noBurn", new FixedMetadataValue(KingdomDefense.getInstance(), true));
    }

    @Override
    public void collide(Entity entity) {
        if(a) {
            super.collide(entity);
        }
    }

    @Override
    public void g(double d, double d2, double d1){

    }
}
