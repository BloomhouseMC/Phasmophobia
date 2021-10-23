package dev.mrsterner.phasmophobia.common.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import dev.mrsterner.phasmophobia.common.registry.PhasmoBrains;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.LocalDifficulty;
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

import java.util.UUID;

public class RevenantEntity extends HostileEntity implements IAnimatable {
    public static final TrackedData<Integer> STATE = DataTracker.registerData(RevenantEntity.class,
                                                                              TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Boolean> HAS_TARGET = DataTracker.registerData(RevenantEntity.class,
                                                                                   TrackedDataHandlerRegistry.BOOLEAN);
    protected static final ImmutableList<SensorType<? extends Sensor<? super RevenantEntity>>> SENSOR_TYPES = ImmutableList.of(
        PhasmoBrains.REVENANT_SPECIFIC_SENSOR);
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULE_TYPES = ImmutableList.of(MemoryModuleType.LOOK_TARGET,
                                                                                                     MemoryModuleType.VISIBLE_MOBS,
                                                                                                     MemoryModuleType.WALK_TARGET,
                                                                                                     MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
                                                                                                     MemoryModuleType.PATH,
                                                                                                     MemoryModuleType.ATE_RECENTLY,
                                                                                                     MemoryModuleType.BREED_TARGET,
                                                                                                     MemoryModuleType.LONG_JUMP_COOLING_DOWN,
                                                                                                     MemoryModuleType.LONG_JUMP_MID_JUMP,
                                                                                                     MemoryModuleType.TEMPTING_PLAYER,
                                                                                                     MemoryModuleType.NEAREST_VISIBLE_ADULT,
                                                                                                     MemoryModuleType.TEMPTATION_COOLDOWN_TICKS,
                                                                                                     new MemoryModuleType[]{MemoryModuleType.IS_TEMPTED, MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryModuleType.RAM_TARGET});
    private static final TrackedData<Integer> ANGER_TIME = DataTracker.registerData(RevenantEntity.class,
                                                                                    TrackedDataHandlerRegistry.INTEGER);
    private static final UniformIntProvider ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 39);
    AnimationFactory factory = new AnimationFactory(this);
    private UUID targetUuid;

    public RevenantEntity(EntityType<? extends RevenantEntity> type, World worldIn) {
        super(type, worldIn);
        this.ignoreCameraFrustum = true;
    }

    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return LivingEntity.createLivingAttributes()
                           .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20.0D)
                           .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.15D)
                           .add(EntityAttributes.GENERIC_MAX_HEALTH, Double.MAX_VALUE)
                           .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, Double.MAX_VALUE)
                           .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.0D)
                           .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, Double.MAX_VALUE);

    }

    @Override
    public void tick() {
        super.tick();
        if (world.isDay()) this.kill();
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    protected Brain.Profile<RevenantEntity> createBrainProfile() {
        return Brain.createProfile(MEMORY_MODULE_TYPES, SENSOR_TYPES);
    }

    @Override
    protected int computeFallDamage(float fallDistance, float damageMultiplier) {
        return 0;
    }

    @Override
    public boolean canTakeDamage() {
        return false;
    }

    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        return RevenantBrain.create(this.createBrainProfile().deserialize(dynamic));
    }

    private <E extends IAnimatable> PlayState predicate2(AnimationEvent<E> event) {
        if (event.getLimbSwingAmount() > 0.01F) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.revenant.move", true));
        } else {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.revenant.idle", true));
        }
        return PlayState.CONTINUE;
    }

    private <E extends IAnimatable> PlayState predicate3(AnimationEvent<E> event) {
        if (this.dataTracker.get(STATE) == 1) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.revenant.attack", true));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ANGER_TIME, 0);
        this.dataTracker.startTracking(STATE, 0);
        this.dataTracker.startTracking(HAS_TARGET, false);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller3", 0, this::predicate3));
        data.addAnimationController(new AnimationController<>(this, "controller2", 10, this::predicate2));
    }

    public Brain<RevenantEntity> getBrain() {
        return (Brain<RevenantEntity>) super.getBrain();
    }

    protected void mobTick() {
        this.world.getProfiler().push("ungodlyRevenantBrain");
        this.getBrain().tick((ServerWorld) this.world, this);
        this.world.getProfiler().pop();
        this.world.getProfiler().push("ungodlyRevenantActivityUpdate");
        RevenantBrain.refreshActivities(this);
        this.world.getProfiler().pop();
        super.mobTick();
    }

    public EntityData initialize(ServerWorldAccess world,
                                 LocalDifficulty difficulty,
                                 SpawnReason spawnReason,
                                 @Nullable EntityData entityData,
                                 @Nullable NbtCompound entityNbt) {
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugInfoSender.sendBrainDebugData(this);
    }

    protected EntityNavigation createNavigation(World world) {
        return new RevenantNavigation(this, world);
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public boolean canHaveStatusEffect(StatusEffectInstance effect) {
        return false;
    }

    private static class RevenantNavigation extends MobNavigation {
        RevenantNavigation(RevenantEntity revenant, World world) {
            super(revenant, world);
        }

        protected PathNodeNavigator createPathNodeNavigator(int range) {
            this.nodeMaker = new LandPathNodeMaker();
            return new PathNodeNavigator(this.nodeMaker, range);
        }
    }
}
