package dev.mrsterner.phasmophobia.client.renderer;

import dev.mrsterner.phasmophobia.client.model.RevenantEntityModel;
import dev.mrsterner.phasmophobia.common.entity.UngodlyRevenantEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class RevenantEntityRenderer extends GeoEntityRenderer<UngodlyRevenantEntity> {
    public RevenantEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new RevenantEntityModel());
        this.addLayer(new RevenantEyesFeatureRenderer(this, new RevenantEyesRenderer(ctx, new RevenantEntityModel())));
    }

    @Override
    public RenderLayer getRenderType(UngodlyRevenantEntity animatable, float partialTicks, MatrixStack stack, @Nullable VertexConsumerProvider renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
        return RenderLayer.getEntityTranslucent(this.getTextureLocation(animatable));
    }

}
