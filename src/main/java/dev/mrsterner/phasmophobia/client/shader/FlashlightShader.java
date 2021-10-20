package dev.mrsterner.phasmophobia.client.shader;

import dev.mrsterner.phasmophobia.common.registry.PhasmoObjects;
import ladysnake.satin.api.event.PostWorldRenderCallback;
import ladysnake.satin.api.experimental.ReadableDepthFramebuffer;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class FlashlightShader implements PostWorldRenderCallback, ClientTickEvents.StartTick, ServerTickEvents.StartWorldTick {
    public boolean renderingEffect = false;
    public @Nullable PlayerEntity player = null;
    private ManagedShaderEffect shader = ShaderEffectManager.getInstance().manage(new Identifier("phasmophobia:shaders/post/flashlight.json"), shader -> {
        MinecraftClient mc = MinecraftClient.getInstance();
        shader.setSamplerUniform("DiffuseDepthSampler", ((ReadableDepthFramebuffer) mc.getFramebuffer()).getStillDepthMap());
    });

    public void registerCallbacks() {
        PostWorldRenderCallback.EVENT.register(this);
        ClientTickEvents.START_CLIENT_TICK.register(this);
        ServerTickEvents.START_WORLD_TICK.register(this);
    }

    @Override
    public void onWorldRendered(Camera camera, float tickDelta, long nanoTime) {
        if(renderingEffect) shader.render(tickDelta);
    }

    @Override
    public void onStartTick(MinecraftClient client) {

    }


    @Override
    public void onStartTick(ServerWorld world) {
        world.getPlayers().stream().forEach(serverPlayerEntity -> {
            if(renderingEffect && !serverPlayerEntity.getInventory().contains(new ItemStack(PhasmoObjects.FLASHLIGHT))){
                renderingEffect = false;
            }
        });
    }
}
