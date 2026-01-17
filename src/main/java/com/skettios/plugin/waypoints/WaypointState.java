package com.skettios.plugin.waypoints;

import com.hypixel.hytale.builtin.crafting.state.BenchState;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.StateData;
import com.hypixel.hytale.server.core.modules.collision.BlockData;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import com.hypixel.hytale.server.core.universe.world.meta.state.DestroyableBlockState;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class WaypointState extends BlockState implements DestroyableBlockState {
    public static final BuilderCodec<WaypointState> CODEC;

    public WaypointState() {
    }

    @Override
    public boolean initialize(BlockType blockType) {
        return true;
    }

    @Override
    public void onDestroy() {
    }

    static {
        CODEC = BuilderCodec.builder(WaypointState.class, WaypointState::new, BlockState.BASE_CODEC).build();
    }
}
