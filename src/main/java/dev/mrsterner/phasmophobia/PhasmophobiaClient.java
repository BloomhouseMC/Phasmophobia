package dev.mrsterner.phasmophobia;

import dev.mrsterner.phasmophobia.common.block.PlaceableBlockEntityRenderer;
import dev.mrsterner.phasmophobia.common.registry.PhasmoObjects;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;

public class PhasmophobiaClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(PhasmoObjects.PLACEABLE_BLOCK_ENTITY, ctx -> new PlaceableBlockEntityRenderer());
    }
}
