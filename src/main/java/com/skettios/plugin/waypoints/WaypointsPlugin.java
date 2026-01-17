package com.skettios.plugin.waypoints;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.BsonUtil;
import org.bson.BsonDocument;
import org.bson.BsonValue;
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

    public ComponentType<EntityStore, WaystoneManager> waystoneManagerComponentType;
    public ComponentType<ChunkStore, WaypointComponent> waystoneComponentType;

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

        ComponentRegistryProxy<EntityStore> entityStoreRegistry = this.getEntityStoreRegistry();
        this.waystoneManagerComponentType = entityStoreRegistry.registerComponent(WaystoneManager.class, WaystoneManager::new);
        entityStoreRegistry.registerSystem(new WaystoneSystem(waystoneManagerComponentType));
        entityStoreRegistry.registerSystem(new WaystoneSystem.PlayerAdded());

        ComponentRegistryProxy<ChunkStore> chunkStoreRegistry = this.getChunkStoreRegistry();
        this.waystoneComponentType = chunkStoreRegistry.registerComponent(WaypointComponent.class, WaypointComponent::new);

        Interaction.CODEC.register("OpenWaypoint", WaypointInteraction.class, WaypointInteraction.CODEC);
        this.getBlockStateRegistry().registerBlockState(WaypointState.class, "waystone", WaypointState.CODEC);
        this.getCommandRegistry().registerCommand(new ExampleCommand(this.getName(), this.getManifest().getVersion().toString()));
    }

    // TODO(skettios): load from file
    public void saveWaystones(UUID uuid, WaystoneManager manager) {
        if (saveLock.tryLock()) {
            try {
                Waystone[] array = manager.getRegisteredWaystones().values().toArray((x) -> new Waystone[x]);
                BsonDocument document = new BsonDocument("Waystones", Waystone.ARRAY_CODEC.encode(array));
                Path path = Universe.get().getPath().resolve("waystones/" + uuid.toString() + ".json");
                BsonUtil.writeDocument(path, document).join();
            } catch (Throwable e) {

            } finally {
                saveLock.unlock();
            }

            if (postSaveRedo.getAndSet(false))
                saveWaystones(uuid, manager);
        } else {
            postSaveRedo.set(true);
        }
    }

    public void loadWaystones(UUID uuid, WaystoneManager manager) {
        Path path = Universe.get().getPath().resolve("waystones/" + uuid.toString() + ".json");
        BsonDocument document = BsonUtil.readDocument(path).join();
        Waystone[] array = (Waystone[])Waystone.ARRAY_CODEC.decode(document.get("Waystones"));
        for (Waystone w : array)
            manager.registerWaystone(w.getName(), w);
    }
}