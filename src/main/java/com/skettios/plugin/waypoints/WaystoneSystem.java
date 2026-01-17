package com.skettios.plugin.waypoints;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class WaystoneSystem extends HolderSystem<EntityStore> {
    private final ComponentType<EntityStore, WaystoneManager> waystoneManagerComponentType;

    public WaystoneSystem(ComponentType<EntityStore, WaystoneManager> type) {
       this.waystoneManagerComponentType = type;
    }

    @Override
    public void onEntityAdd(@NonNullDecl Holder<EntityStore> holder, @NonNullDecl AddReason addReason, @NonNullDecl Store<EntityStore> store) {
        Player player = holder.getComponent(Player.getComponentType());
        if (player != null)
            holder.ensureComponent(waystoneManagerComponentType);
    }

    @Override
    public void onEntityRemoved(@NonNullDecl Holder<EntityStore> holder, @NonNullDecl RemoveReason removeReason, @NonNullDecl Store<EntityStore> store) {
        Player player = holder.getComponent(Player.getComponentType());
        WaystoneManager manager = holder.getComponent(waystoneManagerComponentType);
        if (player != null) {
            WaypointsPlugin.INSTANCE.saveWaystones(player.getUuid(), manager);
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Player.getComponentType();
    }

    public static class PlayerAdded extends RefSystem<EntityStore> {
        @Override
        public void onEntityAdded(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl AddReason addReason, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
            WaystoneManager manager = commandBuffer.getComponent(ref, WaypointsPlugin.INSTANCE.waystoneManagerComponentType);
            PlayerRef playerRef = commandBuffer.getComponent(ref, PlayerRef.getComponentType());

            WaypointsPlugin.INSTANCE.loadWaystones(playerRef.getUuid(), manager);
        }

        @Override
        public void onEntityRemove(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl RemoveReason removeReason, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        }

        @NullableDecl
        @Override
        public Query<EntityStore> getQuery() {
            return Archetype.of(new ComponentType[]{Player.getComponentType(), PlayerRef.getComponentType()});
        }
    }
}
