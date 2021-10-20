package dev.mrsterner.phasmophobia.common.item;

import net.minecraft.item.Item;

public class CrucifixItem extends Item {
    public final byte radius;

    public CrucifixItem(Settings settings, byte radius) {
        super(settings);
        this.radius = radius;

    }
}
