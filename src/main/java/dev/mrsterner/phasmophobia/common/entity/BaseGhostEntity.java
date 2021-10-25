package dev.mrsterner.phasmophobia.common.entity;

import dev.mrsterner.phasmophobia.common.block.entity.PlaceableBlockEntity;
import dev.mrsterner.phasmophobia.common.item.CrucifixItem;
import dev.mrsterner.phasmophobia.common.world.PhasmoWorldState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.NavigationConditions;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
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
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
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

public class BaseGhostEntity extends HostileEntity implements IAnimatable {
    protected int timeInOverworld;
    private int movementCooldownTicks;
    AnimationFactory factory = new AnimationFactory(this);
    public static final TrackedData<Boolean> ATTACKING = DataTracker.registerData(BaseGhostEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> HAS_TARGET = DataTracker.registerData(BaseGhostEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> ANGER_TIME = DataTracker.registerData(BaseGhostEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public BaseGhostEntity(EntityType<? extends HostileEntity> entityType, World world) {
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

    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return LivingEntity.createLivingAttributes()
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20.0D)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3D)
            .add(EntityAttributes.GENERIC_MAX_HEALTH, Double.MAX_VALUE)
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, Double.MAX_VALUE)
            .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.0D)
            .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, Double.MAX_VALUE);
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
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

    @Override
    public void tickMovement() {
        if (this.movementCooldownTicks > 0) {
            --this.movementCooldownTicks;
        }

        super.tickMovement();
    }

    @Override
    public boolean canHaveStatusEffect(StatusEffectInstance effect) {
        return false;
    }

    @Override
    public boolean canTakeDamage() {
        return false;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("TimeInOverworld", this.timeInOverworld);
    }

    @Override
    public double getHeightOffset() {
        return this.isBaby() ? -0.05D : -0.45D;
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.timeInOverworld = nbt.getInt("TimeInOverworld");
    }

    @Override
    protected void mobTick() {
        super.mobTick();
    }


    @Nullable
    public LivingEntity getTarget() {
        return (LivingEntity)this.brain.getOptionalMemory(MemoryModuleType.ATTACK_TARGET).orElse((LivingEntity) null);
    }

    @Override
    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugInfoSender.sendBrainDebugData(this);
    }


    public String getEntity(BaseGhostEntity ghostEntity){
        return Registry.ENTITY_TYPE.getKey(ghostEntity.getType()).get().getValue().getPath();
    }


    private <E extends IAnimatable> PlayState basicMovement(AnimationEvent<E> event) {
        if(event.getLimbSwingAmount() > 0.01F){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation."+getEntity(this)+".move", true));
        }else{
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation."+getEntity(this)+".idle", true));
        }
        return PlayState.CONTINUE;
    }
    private <E extends IAnimatable> PlayState attackingMovement(AnimationEvent<E> event) {
        if(this.dataTracker.get(ATTACKING)){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation."+getEntity(this)+".attack", true));
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
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ANGER_TIME, 0);
        this.dataTracker.startTracking(ATTACKING, false);
        this.dataTracker.startTracking(HAS_TARGET, false);
    }

    @Override
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
            BaseGhostEntity.locate(world, pos) == null;
    }

    public static Pair<BlockPos, Integer> locate(ServerWorldAccess world, BlockPos pos) {
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
}
