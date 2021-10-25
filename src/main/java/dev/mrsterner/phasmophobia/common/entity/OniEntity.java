package dev.mrsterner.phasmophobia.common.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;

public class OniEntity extends BaseGhostEntity implements IAnimatable {
    public OniEntity(EntityType<? extends BaseGhostEntity> entityType, World world) {
        super(entityType, world);
        this.ignoreCameraFrustum = true;
    }
}
