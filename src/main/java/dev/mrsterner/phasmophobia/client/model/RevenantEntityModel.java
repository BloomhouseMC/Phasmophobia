package dev.mrsterner.phasmophobia.client.model;

import dev.mrsterner.phasmophobia.Phasmophobia;
import dev.mrsterner.phasmophobia.common.entity.RevenantEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
//<T extends LivingEntity> extends BipedEntityModel<T>
public class RevenantEntityModel extends AnimatedGeoModel<RevenantEntity> {
    @Override
    public Identifier getAnimationFileLocation(RevenantEntity entity) {
        return new Identifier(Phasmophobia.MODID, "animations/revenant.animation.json");
    }

    @Override
    public Identifier getModelLocation(RevenantEntity entity) {
        return new Identifier(Phasmophobia.MODID, "geo/revenant.geo.json");
    }

    @Override
    public Identifier getTextureLocation(RevenantEntity entity) {
        return new Identifier(Phasmophobia.MODID, "textures/entity/revenant.png");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void setLivingAnimations(RevenantEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");

        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null) {
            head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
            head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
        }
    }
}
