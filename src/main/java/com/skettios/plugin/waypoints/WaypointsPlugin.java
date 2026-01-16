package com.skettios.plugin.waypoints;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.util.BsonUtil;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class WaypointsPlugin extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    public static WaypointsPlugin INSTANCE;

    private final ReentrantLock saveLock = new ReentrantLock();
    private final AtomicBoolean postSaveRedo = new AtomicBoolean(false);

    public final HashMap<String, HashMap<String, Waystone>> waystones = new HashMap<>();

    public WaypointsPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        LOGGER.atInfo().log("Hello from " + this.getName() + " version " + this.getManifest().getVersion().toString());
    }

    @Override
    protected void setup() {
        INSTANCE = this;

        LOGGER.atInfo().log("Setting up plugin " + this.getName());
        AssetRegistry.getAssetStore(Interaction.class).loadAssets("skettios:Waypoints", List.of(WaypointInteraction.INTERACTION));
        AssetRegistry.getAssetStore(RootInteraction.class).loadAssets("skettios:Waypoints", List.of(WaypointInteraction.ROOT_INTERACTION));
        Interaction.CODEC.register("OpenWaypoint", WaypointInteraction.class, WaypointInteraction.CODEC);
        this.getBlockStateRegistry().registerBlockState(WaypointState.class, "waystone", WaypointState.CODEC);
        this.getCommandRegistry().registerCommand(new ExampleCommand(this.getName(), this.getManifest().getVersion().toString()));
    }

    // TODO(skettios): load from file
    public void saveWaystones(UUID uuid) {
        if (saveLock.tryLock()) {
            try {
                Waystone[] array = waystones.get(uuid).values().toArray((x) -> new Waystone[x]);
                BsonDocument document = new BsonDocument("Waystones", Waystone.ARRAY_CODEC.encode(array));
                Path path = Universe.get().getPath().resolve("waystones/" + uuid.toString() + ".json");
                BsonUtil.writeDocument(path, document).join();
            } catch (Throwable e) {

            } finally {
                saveLock.unlock();
            }

            if (postSaveRedo.getAndSet(false))
                saveWaystones(uuid);
        } else {
            postSaveRedo.set(true);
        }
    }
}