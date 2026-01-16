package com.skettios.plugin.waypoints;

import com.hypixel.hytale.builtin.crafting.state.BenchState;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.StateData;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;

public class WaypointState extends BlockState {
    public static final BuilderCodec<WaypointState> CODEC;

    public WaypointState() {

    }

    static {
        CODEC = BuilderCodec.builder(WaypointState.class, WaypointState::new, BlockState.BASE_CODEC).build();
    }
}
