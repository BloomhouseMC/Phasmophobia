package dev.mrsterner.phasmophobia.common.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import dev.mrsterner.phasmophobia.common.block.entity.PlaceableBlockEntity;
import dev.mrsterner.phasmophobia.common.item.CrucifixItem;
import dev.mrsterner.phasmophobia.common.registry.PhasmoBrains;
import dev.mrsterner.phasmophobia.common.world.PhasmoWorldState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.*;
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
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
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

import java.util.Random;

public class RevenantEntity extends BaseGhostEntity implements IAnimatable {
    private int movementCooldownTicks;
    public static final TrackedData<Boolean> ATTACKING = DataTracker.registerData(RevenantEntity.class,
                                                                                  TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> HAS_TARGET = DataTracker.registerData(RevenantEntity.class,
                                                                                   TrackedDataHandlerRegistry.BOOLEAN);
    protected static final ImmutableList<SensorType<? extends Sensor<? super RevenantEntity>>> SENSORS;
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES;
    private static final TrackedData<Integer> ANGER_TIME = DataTracker.registerData(RevenantEntity.class,
                                                                                    TrackedDataHandlerRegistry.INTEGER);

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

    AnimationFactory factory = new AnimationFactory(this);

    public RevenantEntity(EntityType<? extends BaseGhostEntity> entityType, World world) {
        super(entityType, world);
        this.ignoreCameraFrustum = true;
    }

    public boolean tryAttack(Entity target) {
        if (!(target instanceof LivingEntity)) {
            return false;
        } else {
            this.movementCooldownTicks = 10;
            this.world.sendEntityStatus(this, (byte)4);
            return tryAttack(this, (LivingEntity)target);
        }
    }

    static boolean tryAttack(LivingEntity attacker, LivingEntity target) {
        float f = (float)attacker.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        float h;
        if (!attacker.isBaby() && (int)f > 0) {
            h = f / 2.0F + (float)attacker.world.random.nextInt((int)f);
        } else {
            h = f;
        }

        boolean bl = target.damage(DamageSource.mob(attacker), h);
        if (bl) {
            attacker.applyDamageEffects(attacker, target);
            if (!attacker.isBaby()) {
                knockback(attacker, target);
            }
        }

        return bl;
    }

    static void knockback(LivingEntity attacker, LivingEntity target) {
        double d = attacker.getAttributeValue(EntityAttributes.GENERIC_ATTACK_KNOCKBACK);
        double e = target.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE);
        double f = d - e;
        if (!(f <= 0.0D)) {
            double g = target.getX() - attacker.getX();
            double h = target.getZ() - attacker.getZ();
            float i = (float)(attacker.world.random.nextInt(21) - 10);
            double j = f * (double)(attacker.world.random.nextFloat() * 0.5F + 0.2F);
            Vec3d vec3d = (new Vec3d(g, 0.0D, h)).normalize().multiply(j).rotateY(i);
            double k = f * (double)attacker.world.random.nextFloat() * 0.5D;
            target.addVelocity(vec3d.x, k, vec3d.z);
            target.velocityModified = true;
        }
    }

    public static boolean canSpawnInDark(EntityType<? extends HostileEntity> type,
                                         ServerWorldAccess world,
                                         SpawnReason spawnReason,
                                         BlockPos pos,
                                         Random random) {
        return world.getDifficulty() != Difficulty.PEACEFUL &&
            isSpawnDark(world, pos, random) &&
            canMobSpawn(type, world, spawnReason, pos, random) &&
            locate(world, pos) == null;
    }

    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return LivingEntity.createLivingAttributes()
                           .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20.0D)
                           .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3D)
                           .add(EntityAttributes.GENERIC_MAX_HEALTH, Double.MAX_VALUE)
                           .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, Double.MAX_VALUE)
                           .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.0D)
                           .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, Double.MAX_VALUE);
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
                        if (item instanceof CrucifixItem crucifixItem) {
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

    @Override
    public boolean canHurt() {
        return super.canHurt();
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world,
                                 LocalDifficulty difficulty,
                                 SpawnReason spawnReason,
                                 @Nullable EntityData entityData,
                                 @Nullable NbtCompound entityNbt) {
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    public void tick() {
        super.tick();
        if (world.isDay()) this.kill();
    }

    public void tickMovement() {
        if (this.movementCooldownTicks > 0) {
            --this.movementCooldownTicks;
        }

        super.tickMovement();
    }

    @Override
    public boolean isPushable() {
        return false;
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
    public boolean canTakeDamage() {
        return false;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ANGER_TIME, 0);
        this.dataTracker.startTracking(ATTACKING, false);
        this.dataTracker.startTracking(HAS_TARGET, false);
    }

    private <E extends IAnimatable> PlayState basicMovement(AnimationEvent<E> event) {
        if (event.getLimbSwingAmount() > 0.01F) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.revenant.move", true));
        } else {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.revenant.idle", true));
        }
        return PlayState.CONTINUE;
    }

    public int getMovementCooldownTicks() {
        return this.movementCooldownTicks;
    }

    private <E extends IAnimatable> PlayState attackingMovement(AnimationEvent<E> event) {
        if (this.dataTracker.get(ATTACKING)) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.revenant.attack", true));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "Attacking", 0, this::attackingMovement));
        data.addAnimationController(new AnimationController<>(this, "BasicMovement", 10, this::basicMovement));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugInfoSender.sendBrainDebugData(this);
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        return new RevenantNavigation(this, world);
    }

    @Override
    public boolean canHaveStatusEffect(StatusEffectInstance effect) {
        return false;
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
}
