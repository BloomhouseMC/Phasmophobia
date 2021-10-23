package dev.mrsterner.phasmophobia.common.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.NavigationConditions;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AbstractGhostEntity extends HostileEntity {
    protected int timeInOverworld;

    public AbstractGhostEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.setCanPickUpLoot(true);
        this.setCanPathThroughDoors();
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 16.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, -1.0F);
    }

    private void setCanPathThroughDoors() {
        if (NavigationConditions.hasMobNavigation(this)) {
            ((MobNavigation)this.getNavigation()).setCanPathThroughDoors(true);
        }

    }

    public boolean canHurt(){
        return true;
    }

    protected void initDataTracker() {
        super.initDataTracker();
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("TimeInOverworld", this.timeInOverworld);
    }

    public double getHeightOffset() {
        return this.isBaby() ? -0.05D : -0.45D;
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.timeInOverworld = nbt.getInt("TimeInOverworld");
    }

    protected void mobTick() {
        super.mobTick();
    }


    @Nullable
    public LivingEntity getTarget() {
        return (LivingEntity)this.brain.getOptionalMemory(MemoryModuleType.ATTACK_TARGET).orElse((LivingEntity) null);
    }


    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugInfoSender.sendBrainDebugData(this);
    }
}
