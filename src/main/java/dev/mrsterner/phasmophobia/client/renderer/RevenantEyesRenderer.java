package dev.mrsterner.phasmophobia.client.renderer;

import dev.mrsterner.phasmophobia.common.entity.RevenantEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class RevenantEyesRenderer extends GeoEntityRenderer<RevenantEntity> {
    protected RevenantEyesRenderer(EntityRendererFactory.Context ctx, AnimatedGeoModel<RevenantEntity> modelProvider) {
        super(ctx, modelProvider);
    }
}
