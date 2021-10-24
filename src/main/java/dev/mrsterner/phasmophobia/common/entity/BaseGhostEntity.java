package dev.mrsterner.phasmophobia.common.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class BaseGhostEntity extends HostileEntity implements IAnimatable {
    protected int timeInOverworld;
    AnimationFactory factory = new AnimationFactory(this);
    public static final TrackedData<Boolean> ATTACKING = DataTracker.registerData(BaseGhostEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

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
}
