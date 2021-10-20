package dev.mrsterner.phasmophobia;

import dev.mrsterner.phasmophobia.client.shader.FlashlightShader;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;


public class PhasmophobiaClient implements ClientModInitializer {
    FlashlightShader flashlightShader = new FlashlightShader();

    @Override
    public void onInitializeClient() {
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
