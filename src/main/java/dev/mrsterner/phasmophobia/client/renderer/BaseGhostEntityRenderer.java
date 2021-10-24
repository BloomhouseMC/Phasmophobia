package dev.mrsterner.phasmophobia.client.renderer;

import dev.mrsterner.phasmophobia.client.model.BaseGhostEntityModel;
import dev.mrsterner.phasmophobia.common.entity.BaseGhostEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class BaseGhostEntityRenderer extends GeoEntityRenderer<BaseGhostEntity> {
    public BaseGhostEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new BaseGhostEntityModel());
        this.addLayer(new BaseGhostEyesFeatureRenderer(this, new BaseGhostEyesRenderer(ctx, new BaseGhostEntityModel())));
    }

    @Override
    public RenderLayer getRenderType(BaseGhostEntity animatable, float partialTicks, MatrixStack stack, @Nullable VertexConsumerProvider renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
        return RenderLayer.getEntityTranslucent(this.getTextureLocation(animatable));
    }

    public static class BaseGhostEyesRenderer extends GeoEntityRenderer<BaseGhostEntity> {
        protected BaseGhostEyesRenderer(EntityRendererFactory.Context ctx, AnimatedGeoModel<BaseGhostEntity> modelProvider) {
            super(ctx, modelProvider);
        }
    }

}
