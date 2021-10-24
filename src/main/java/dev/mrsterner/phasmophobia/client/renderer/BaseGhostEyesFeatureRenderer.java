package dev.mrsterner.phasmophobia.client.renderer;

import dev.mrsterner.phasmophobia.Phasmophobia;
import dev.mrsterner.phasmophobia.common.entity.BaseGhostEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

@Environment(EnvType.CLIENT)
public class BaseGhostEyesFeatureRenderer extends GeoLayerRenderer<BaseGhostEntity> {
    public String getEntity(BaseGhostEntity ghostEntity){
        return Registry.ENTITY_TYPE.getKey(ghostEntity.getType()).get().getValue().getPath();
    }
    private final IGeoRenderer<BaseGhostEntity> renderer;

    public BaseGhostEyesFeatureRenderer(BaseGhostEntityRenderer entityRendererIn, BaseGhostEntityRenderer.BaseGhostEyesRenderer revenantEyesRenderer) {
        super(entityRendererIn);
        this.renderer = revenantEyesRenderer;
    }

    @Override
    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, BaseGhostEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        renderer.render(getEntityModel().getModel(new Identifier(Phasmophobia.MODID, "geo/"+getEntity(entitylivingbaseIn)+".geo.json")),
            entitylivingbaseIn,
            partialTicks,
            RenderLayer.getEyes(new Identifier(Phasmophobia.MODID,"textures/entity/"+getEntity(entitylivingbaseIn)+"_eyes_1.png")),
            matrixStackIn,
            bufferIn,
            bufferIn.getBuffer(RenderLayer.getEyes(new Identifier(Phasmophobia.MODID,"textures/entity/"+getEntity(entitylivingbaseIn)+"_eyes_1.png"))),
            packedLightIn,
            OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);

        renderer.render(getEntityModel().getModel(new Identifier(Phasmophobia.MODID, "geo/"+getEntity(entitylivingbaseIn)+".geo.json")),
            entitylivingbaseIn,
            partialTicks,
            RenderLayer.getEyes(new Identifier(Phasmophobia.MODID,"textures/entity/"+getEntity(entitylivingbaseIn)+"_eyes_2.png")),
            matrixStackIn,
            bufferIn,
            bufferIn.getBuffer(RenderLayer.getEyes(new Identifier(Phasmophobia.MODID,"textures/entity/"+getEntity(entitylivingbaseIn)+"_eyes_2.png"))),
            packedLightIn,
            OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);

    }
}
