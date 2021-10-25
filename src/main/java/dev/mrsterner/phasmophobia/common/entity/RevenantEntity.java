package dev.mrsterner.phasmophobia.common.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import dev.mrsterner.phasmophobia.common.registry.PhasmoBrains;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;

public class RevenantEntity extends BaseGhostEntity implements IAnimatable {
    protected static final ImmutableList<SensorType<? extends Sensor<? super RevenantEntity>>> SENSORS;
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES;

    public RevenantEntity(EntityType<? extends BaseGhostEntity> entityType, World world) {
        super(entityType, world);
        this.ignoreCameraFrustum = true;
    }

    @Override
    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        return RevenantBrain.create(this.createBrainProfile().deserialize(dynamic));
    }

    @Override
    protected Brain.Profile<RevenantEntity> createBrainProfile() {
        return Brain.createProfile(MEMORY_MODULES, SENSORS);
    }

    @Override
    public Brain<RevenantEntity> getBrain() {
        return (Brain<RevenantEntity>) super.getBrain();
    }

    @Override
    protected void mobTick() {
        this.world.getProfiler().push("revenantBrain");
        this.getBrain().tick((ServerWorld) this.world, this);
        this.world.getProfiler().pop();
        this.world.getProfiler().push("revenantActivityUpdate");
        RevenantBrain.refreshActivities(this);
        this.world.getProfiler().pop();
        super.mobTick();
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        return new RevenantNavigation(this, world);
    }

    private static class RevenantNavigation extends MobNavigation {
        RevenantNavigation(RevenantEntity revenant, World world) {
            super(revenant, world);
        }

        @Override
        protected PathNodeNavigator createPathNodeNavigator(int range) {
            this.nodeMaker = new LandPathNodeMaker();
            return new PathNodeNavigator(this.nodeMaker, range);
        }
    }

    static {
        SENSORS = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.NEAREST_PLAYERS,
            PhasmoBrains.REVENANT_SPECIFIC_SENSOR);
        MEMORY_MODULES = ImmutableList.of(
            MemoryModuleType.HURT_BY,
            MemoryModuleType.HURT_BY_ENTITY,
            MemoryModuleType.MOBS,
            MemoryModuleType.VISIBLE_MOBS,
            MemoryModuleType.NEAREST_VISIBLE_PLAYER,
            MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER,
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.PATH,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.ATTACK_COOLING_DOWN,
            MemoryModuleType.INTERACTION_TARGET,
            MemoryModuleType.ANGRY_AT);
    }
}
