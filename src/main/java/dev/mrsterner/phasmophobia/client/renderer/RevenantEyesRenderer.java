package dev.mrsterner.phasmophobia.client.renderer;

import dev.mrsterner.phasmophobia.common.entity.UngodlyRevenantEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class RevenantEyesRenderer extends GeoEntityRenderer<UngodlyRevenantEntity> {
    protected RevenantEyesRenderer(EntityRendererFactory.Context ctx, AnimatedGeoModel<UngodlyRevenantEntity> modelProvider) {
        super(ctx, modelProvider);
    }
}
