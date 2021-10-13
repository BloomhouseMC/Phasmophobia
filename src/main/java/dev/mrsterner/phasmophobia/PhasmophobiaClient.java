package dev.mrsterner.phasmophobia;

import dev.mrsterner.phasmophobia.client.renderer.PlaceableBlockEntityRenderer;
import dev.mrsterner.phasmophobia.common.registry.PhasmoObjects;
import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.minecraft.util.Identifier;

public class PhasmophobiaClient implements ClientModInitializer {
    private static final ManagedShaderEffect GREYSCALE_SHADER = ShaderEffectManager.getInstance()
        .manage(new Identifier(Phasmophobia.MODID, "shaders/post/transparency.json"));
    private static boolean enabled = true;  // can be disabled whenever you want

    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(PhasmoObjects.PLACEABLE_BLOCK_ENTITY, ctx -> new PlaceableBlockEntityRenderer());
        ShaderEffectRenderCallback.EVENT.register(tickDelta -> {
            if (enabled) {
                GREYSCALE_SHADER.render(tickDelta);
            }
        });
    }
}
