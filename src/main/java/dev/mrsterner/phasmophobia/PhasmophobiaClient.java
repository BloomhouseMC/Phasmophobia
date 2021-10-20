package dev.mrsterner.phasmophobia;

import dev.mrsterner.phasmophobia.client.renderer.PlaceableBlockEntityRenderer;
import dev.mrsterner.phasmophobia.common.registry.PhasmoObjects;
import ladysnake.satin.api.event.PostWorldRenderCallback;
import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.client.gl.GlDebug;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.UUID;


public class PhasmophobiaClient implements ClientModInitializer {
    private static final ManagedShaderEffect GREYSCALE_SHADER = ShaderEffectManager.getInstance()
        .manage(new Identifier(Phasmophobia.MODID, "shaders/post/flashlight.json"));
    private static boolean enabled = true;  // can be disabled whenever you want
    FlashlightShader flashlightShader = new FlashlightShader();

    @Override
    public void onInitializeClient() {


        ClientTickEvents.START_CLIENT_TICK.register(client -> {

        });
        BlockEntityRendererRegistry.INSTANCE.register(PhasmoObjects.PLACEABLE_BLOCK_ENTITY, ctx -> new PlaceableBlockEntityRenderer());
        /*
        ShaderEffectRenderCallback.EVENT.register(tickDelta -> {
            if (enabled) {
                GREYSCALE_SHADER.render(tickDelta);
            }
        });

         */
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
