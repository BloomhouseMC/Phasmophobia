package dev.mrsterner.phasmophobia;

import dev.mrsterner.phasmophobia.client.renderer.RevenantEntityRenderer;
import dev.mrsterner.phasmophobia.client.shader.FlashlightShader;
import dev.mrsterner.phasmophobia.common.registry.PhasmoObjects;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;


public class PhasmophobiaClient implements ClientModInitializer {
    FlashlightShader flashlightShader = new FlashlightShader();

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(PhasmoObjects.REVENANT, RevenantEntityRenderer::new);

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
