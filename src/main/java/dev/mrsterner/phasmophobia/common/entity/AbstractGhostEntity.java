package dev.mrsterner.phasmophobia.common.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.NavigationConditions;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.Random;

public class AbstractGhostEntity extends HostileEntity {
    protected int timeInOverworld;

    public AbstractGhostEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
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
