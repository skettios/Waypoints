package com.skettios.plugin.waypoints;

import com.google.gson.JsonObject;
import com.hypixel.hytale.protocol.packets.window.WindowType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.entities.player.windows.BlockWindow;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class WaypointWindow extends BlockWindow {
    protected final JsonObject windowData = new JsonObject();

    public WaypointWindow(@NonNullDecl WindowType windowType, WaypointState state) {
        super(windowType, state.getBlockX(), state.getBlockY(), state.getBlockZ(), state.getRotationIndex(), state.getBlockType());
        Item item = blockType.getItem();
        windowData.addProperty("name", item.getTranslationKey());
        windowData.addProperty("blockItemId", item.getId());
    }

    @NonNullDecl
    @Override
    public JsonObject getData() {
        return windowData;
    }

    @Override
    protected boolean onOpen0() {
        return true;
    }

    @Override
    protected void onClose0() {

    }
}
