package com.skettios.plugin.waypoints;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class WaypointComponent implements Component<ChunkStore> {
   public WaypointComponent() {

   }

   private WaypointComponent(WaypointComponent other) {
   }

    @NullableDecl
    @Override
    public Component<ChunkStore> clone() {
        return null;
    }
}
