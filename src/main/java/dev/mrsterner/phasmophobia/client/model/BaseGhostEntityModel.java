package dev.mrsterner.phasmophobia.client.model;

import dev.mrsterner.phasmophobia.Phasmophobia;
import dev.mrsterner.phasmophobia.common.entity.BaseGhostEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
public class BaseGhostEntityModel extends AnimatedGeoModel<BaseGhostEntity> {
    public String getEntity(BaseGhostEntity ghostEntity){
        return Registry.ENTITY_TYPE.getKey(ghostEntity.getType()).get().getValue().getPath();
    }

    @Override
    public Identifier getAnimationFileLocation(BaseGhostEntity entity) {
        return new Identifier(Phasmophobia.MODID, "animations/"+getEntity(entity)+".animation.json");
    }

    @Override
    public Identifier getModelLocation(BaseGhostEntity entity) {
        return new Identifier(Phasmophobia.MODID, "geo/"+getEntity(entity)+".geo.json");
    }

    @Override
    public Identifier getTextureLocation(BaseGhostEntity entity) {
        return new Identifier(Phasmophobia.MODID, "textures/entity/"+getEntity(entity)+".png");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void setLivingAnimations(BaseGhostEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");

        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null) {
            head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
            head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
        }
    }
}
