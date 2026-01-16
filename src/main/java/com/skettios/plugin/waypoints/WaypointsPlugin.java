package com.skettios.plugin.waypoints;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import javax.annotation.Nonnull;
import java.util.List;

public class WaypointsPlugin extends JavaPlugin {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public WaypointsPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        LOGGER.atInfo().log("Hello from " + this.getName() + " version " + this.getManifest().getVersion().toString());
    }

    @Override
    protected void setup() {
        LOGGER.atInfo().log("Setting up plugin " + this.getName());
        AssetRegistry.getAssetStore(Interaction.class).loadAssets("skettios:Waypoints", List.of(WaypointInteraction.INTERACTION));
        AssetRegistry.getAssetStore(RootInteraction.class).loadAssets("skettios:Waypoints", List.of(WaypointInteraction.ROOT_INTERACTION));
        Interaction.CODEC.register("OpenWaypoint", WaypointInteraction.class, WaypointInteraction.CODEC);
        this.getBlockStateRegistry().registerBlockState(WaypointState.class, "waystone", WaypointState.CODEC);
        this.getCommandRegistry().registerCommand(new ExampleCommand(this.getName(), this.getManifest().getVersion().toString()));
    }
}