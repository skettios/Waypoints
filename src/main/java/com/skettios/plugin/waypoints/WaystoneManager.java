package com.skettios.plugin.waypoints;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.component.system.ISystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.HashMap;

public class WaystoneManager implements Component<EntityStore> {
    public WaystoneManager() {}

    private final HashMap<String, Waystone> registeredWaystones = new HashMap<>();

    public void registerWaystone(String name, Waystone waystone) {
        registeredWaystones.put(name, waystone);
    }

    public boolean hasWaystone(String waystone) {
        return registeredWaystones.containsKey(waystone);
    }

    public HashMap<String, Waystone> getRegisteredWaystones() { return registeredWaystones; }

    public void removeWaystone(String waystone) {
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return null;
    }
}
