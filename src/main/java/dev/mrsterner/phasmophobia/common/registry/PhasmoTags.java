package dev.mrsterner.phasmophobia.common.registry;

import dev.mrsterner.phasmophobia.Phasmophobia;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class PhasmoTags {
    public static final Tag<Item> PLACEABLES = TagRegistry.item(new Identifier(Phasmophobia.MODID, "placeables"));
}
