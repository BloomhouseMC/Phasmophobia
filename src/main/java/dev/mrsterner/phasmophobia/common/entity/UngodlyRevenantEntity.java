package dev.mrsterner.phasmophobia.common.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import dev.mrsterner.phasmophobia.common.block.entity.PlaceableBlockEntity;
import dev.mrsterner.phasmophobia.common.item.CrucifixItem;
import dev.mrsterner.phasmophobia.common.registry.PhasmoBrains;
import dev.mrsterner.phasmophobia.common.world.PhasmoWorldState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.UniformIntProvider;
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
import java.util.UUID;

public class UngodlyRevenantEntity extends HostileEntity implements IAnimatable, Angerable {
    AnimationFactory factory = new AnimationFactory(this);
    private static final TrackedData<Integer> ANGER_TIME = DataTracker.registerData(UngodlyRevenantEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> STATE = DataTracker.registerData(UngodlyRevenantEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Boolean> HAS_TARGET = DataTracker.registerData(UngodlyRevenantEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    protected static final ImmutableList<SensorType<? extends Sensor<? super UngodlyRevenantEntity>>> SENSORS;
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES;
    private static final UniformIntProvider ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 39);
    private UUID targetUuid;

    public UngodlyRevenantEntity(EntityType<? extends HostileEntity> type, World worldIn) {
        super(type, worldIn);
        this.ignoreCameraFrustum = true;
    }

    @Override
    public void tick() {
        super.tick();
        if(world.isDay())this.kill();
    }

    @Override
    public boolean isPushable() {
        return false;
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

    protected Brain.Profile<UngodlyRevenantEntity> createBrainProfile() {
        return Brain.createProfile(MEMORY_MODULES, SENSORS);
    }

    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        return UngodlyRevenantBrain.create(this, this.createBrainProfile().deserialize(dynamic));
    }

    public Brain<UngodlyRevenantEntity> getBrain() {
        return (Brain<UngodlyRevenantEntity>) super.getBrain();
    }

    @Override
    protected void mobTick() {
        this.world.getProfiler().push("revenantBrain");
        this.getBrain().tick((ServerWorld)this.world, this);
        this.world.getProfiler().pop();
        UngodlyRevenantBrain.refreshActivities(this);
        super.mobTick();
    }

    static {
        SENSORS = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.NEAREST_PLAYERS,
            SensorType.NEAREST_ITEMS,
            SensorType.HURT_BY,
            PhasmoBrains.REVENANT_SPECIFIC_SENSOR);
        MEMORY_MODULES = ImmutableList.of(
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.MOBS,
            MemoryModuleType.VISIBLE_MOBS,
            MemoryModuleType.NEAREST_VISIBLE_PLAYER,
            MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER,
            MemoryModuleType.HURT_BY,
            MemoryModuleType.HURT_BY_ENTITY,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.ATTACK_COOLING_DOWN,
            MemoryModuleType.INTERACTION_TARGET,
            MemoryModuleType.PATH,
            MemoryModuleType.ANGRY_AT);
    }

    public static boolean canSpawn(EntityType<UngodlyRevenantEntity> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return locate(world, pos) == null && MobEntity.canMobSpawn(type, world, spawnReason, pos, random);
    }

    public int getAttckingState() {
        return this.dataTracker.get(STATE);
    }

    public void setAttackingState(int time) {
        this.dataTracker.set(STATE, time);
    }

    @Override
    protected int computeFallDamage(float fallDistance, float damageMultiplier) {
        return 0;
    }

    @Override
    public boolean canTakeDamage() {
        return false;
    }


    private <E extends IAnimatable> PlayState predicate2(AnimationEvent<E> event) {
        if(event.getLimbSwingAmount() > 0.01F){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.revenant.move", true));
        }else{
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.revenant.idle", true));
        }
        return PlayState.CONTINUE;
    }
    private <E extends IAnimatable> PlayState predicate3(AnimationEvent<E> event) {
        if(this.dataTracker.get(STATE) == 1){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.revenant.attack", true));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller3", 0, this::predicate3));
        data.addAnimationController(new AnimationController<>(this, "controller2", 10, this::predicate2));
    }



    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public int getAngerTime() {
        return this.dataTracker.get(ANGER_TIME);
    }

    @Override
    public void setAngerTime(int ticks) {
        this.dataTracker.set(ANGER_TIME, ticks);
    }

    @Override
    public UUID getAngryAt() {
        return this.targetUuid;
    }

    @Override
    public void setAngryAt(@Nullable UUID uuid) {
        this.targetUuid = uuid;
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (locate((ServerWorldAccess) world, getBlockPos()) != null) {
            target = null;
        }
        super.setTarget(target);
        dataTracker.set(HAS_TARGET, target != null);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ANGER_TIME, 0);
        this.dataTracker.startTracking(STATE, 0);
        this.dataTracker.startTracking(HAS_TARGET, false);
    }

    @Override
    public void chooseRandomAngerTime() {
        this.setAngerTime(ANGER_TIME_RANGE.get(this.random));
    }
    public class AttackGoal extends MeleeAttackGoal {
        private final UngodlyRevenantEntity entity;

        public AttackGoal(UngodlyRevenantEntity zombieIn, double speedIn, boolean longMemoryIn) {
            super(zombieIn, speedIn, longMemoryIn);
            this.entity = zombieIn;
        }

        public void start() {
            super.start();
        }

        @Override
        public boolean shouldContinue() {
            if (locate((ServerWorldAccess) world, getBlockPos()) != null) {
                return false;
            }
            return super.shouldContinue();
        }
        @Override
        public void stop() {
            super.stop();
            this.entity.setAttacking(false);
            this.entity.setAttackingState(0);
        }


        @Override
        protected void attack(LivingEntity livingentity, double squaredDistance) {
            double d0 = this.getSquaredMaxAttackDistance(livingentity);
            if (squaredDistance <= d0 && this.getCooldown() <= 0) {
                this.resetCooldown();
                this.entity.setAttackingState(1);
                this.mob.tryAttack(livingentity);
            }
        }

        @Override
        protected int getMaxCooldown() {
            return 50;
        }

        @Override
        protected double getSquaredMaxAttackDistance(LivingEntity entity) {
            return (double) (this.mob.getWidth() * 1.0F * this.mob.getWidth() * 1.0F + entity.getWidth());
        }

    }
    @Override
    public boolean canHaveStatusEffect(StatusEffectInstance effect) {
        return false;
    }

    @Override
    public boolean isTouchingWater() {
        return false;
    }

    @Override
    public boolean isInLava() {
        return false;
    }
    private static Pair<BlockPos, Integer> locate(ServerWorldAccess world, BlockPos pos) {
        PhasmoWorldState worldState = PhasmoWorldState.get(world.toServerWorld());
        for (Long longPos : worldState.crucifix) {
            BlockPos crucifixPos = BlockPos.fromLong(longPos);
            double distance = Math.sqrt(crucifixPos.getSquaredDistance(pos));
            if (distance <= Byte.MAX_VALUE) {
                int radius = -1;
                BlockEntity blockEntity = world.getBlockEntity(crucifixPos);
                if (blockEntity instanceof PlaceableBlockEntity placeableBlockEntity) {
                    for (int i = 0; i < placeableBlockEntity.size(); i++) {
                        Item item = placeableBlockEntity.getStack(i).getItem();
                        if(item instanceof CrucifixItem crucifixItem){
                            radius = crucifixItem.radius;
                            System.out.println(radius);
                        }
                    }
                }
                if (distance <= radius) {
                    return new Pair<>(crucifixPos, radius);
                }
            }
        }
        return null;
    }
}
