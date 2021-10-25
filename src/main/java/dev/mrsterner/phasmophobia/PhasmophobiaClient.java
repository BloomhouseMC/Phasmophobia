package dev.mrsterner.phasmophobia;

import dev.mrsterner.phasmophobia.client.renderer.BaseGhostEntityRenderer;
import dev.mrsterner.phasmophobia.client.renderer.PlaceableBlockEntityRenderer;
import dev.mrsterner.phasmophobia.client.shader.FlashlightShader;
import dev.mrsterner.phasmophobia.common.registry.PhasmoEntities;
import dev.mrsterner.phasmophobia.common.registry.PhasmoObjects;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;


public class PhasmophobiaClient implements ClientModInitializer {
    FlashlightShader flashlightShader = new FlashlightShader();

    @Override
    public void onInitializeClient() {

        for(EntityType entityType : PhasmoEntities.ENTITY_TYPES.keySet()){
            EntityRendererRegistry.INSTANCE.register(entityType, BaseGhostEntityRenderer::new);
        }
        BlockEntityRendererRegistry.INSTANCE.register(PhasmoObjects.PLACEABLE_BLOCK_ENTITY, ctx -> new PlaceableBlockEntityRenderer());

        ClientPlayNetworking.registerGlobalReceiver(new Identifier("phasmophobia:flashlight"), (client, handler, buf, responseSender) -> client.execute(() -> {
            World world = client.world;
            if (world != null) {
                flashlightShader.player = client.player;
                flashlightShader.renderingEffect = !flashlightShader.renderingEffect;
            }
        }));
        flashlightShader.registerCallbacks();
    }
}
