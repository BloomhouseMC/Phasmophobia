package dev.mrsterner.phasmophobia.common.world;

import dev.mrsterner.phasmophobia.Phasmophobia;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class PhasmoWorldState extends PersistentState {
    public final List<Long> crucifix = new ArrayList<>();

    public static PhasmoWorldState readNbt(NbtCompound nbt) {
        PhasmoWorldState worldState = new PhasmoWorldState();
        NbtList crucifix = nbt.getList("Crucifix", NbtType.COMPOUND);
        for (int i = 0; i < crucifix.size(); i++) {
            NbtCompound posCompound = crucifix.getCompound(i);
            worldState.crucifix.add(posCompound.getLong("Pos"));
        }
        return worldState;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList crucifix = new NbtList();
        for (long pos : this.crucifix) {
            NbtCompound posCompound = new NbtCompound();
            posCompound.putLong("Pos", pos);
            crucifix.add(posCompound);
        }
        nbt.put("Crucifix", crucifix);
        return nbt;
    }
    public static PhasmoWorldState get(World world) {
        return ((ServerWorld) world).getPersistentStateManager().getOrCreate(PhasmoWorldState::readNbt, PhasmoWorldState::new, Phasmophobia.MODID + "_universal");
    }
}
