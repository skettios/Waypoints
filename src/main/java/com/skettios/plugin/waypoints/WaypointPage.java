package com.skettios.plugin.waypoints;

import com.hypixel.hytale.builtin.teleport.WarpListPage;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WaypointPage extends InteractiveCustomUIPage<WaypointPage.WaystonePageEventData> {
    private final WaypointState state;
    private final WaystoneManager manager;

    public WaypointPage(@NonNullDecl PlayerRef playerRef, @NonNullDecl WaypointState state, WaystoneManager manager) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, WaystonePageEventData.CODEC);
        this.state = state;
        this.manager = manager;
    }

    @Override
    public void build(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl UICommandBuilder uiCommandBuilder, @NonNullDecl UIEventBuilder uiEventBuilder, @NonNullDecl Store<EntityStore> store) {
        Player player = store.getComponent(ref, Player.getComponentType());
        uiCommandBuilder.append("Pages/WaypointPage.ui");
        buildWaystoneList(player, uiCommandBuilder, uiEventBuilder);
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#WaystoneSave", EventData.of("@WaystoneName", "#WaystoneName.Value"), false);
//        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#WaystoneSave", EventData.of("Waystone", "waystone").append("@WaystoneName", "#WaystoneName.Value"), false);
    }

    private void buildWaystoneList(Player player, UICommandBuilder commandBuilder, UIEventBuilder eventBuilder) {
        commandBuilder.clear("#WaystoneList");

        int i = 0;
        for (Waystone waystone : manager.getRegisteredWaystones().values()) {
            String buttonName = "#WaystoneList[" + i + "]";
            String waystoneName = waystone.getName();
            commandBuilder.append("#WaystoneList", "Pages/WarpEntryButton.ui");
            commandBuilder.set(buttonName + " #Name.Text", waystoneName);
            commandBuilder.set(buttonName + " #World.Text", player.getWorld().getName());
            eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, buttonName, EventData.of("WaystoneAction", waystoneName), false);
            i++;
        }
    }

    @Override
    public void handleDataEvent(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl Store<EntityStore> store, @NonNullDecl WaystonePageEventData data) {
        Player player = store.getComponent(ref, Player.getComponentType());

        if (data.waystoneName != null) {
            if (!manager.hasWaystone(data.waystoneName)) {
                Vector3i blockPosition = this.state.getBlockPosition();
                manager.registerWaystone(data.waystoneName, new Waystone(data.waystoneName, player.getWorld(), blockPosition.getX(), blockPosition.y, blockPosition.getZ()));

                UICommandBuilder commandBuilder = new UICommandBuilder();
                UIEventBuilder eventBuilder = new UIEventBuilder();

                buildWaystoneList(player, commandBuilder, eventBuilder);
                sendUpdate(commandBuilder, eventBuilder, false);
            }
//            if (!WaypointsPlugin.INSTANCE.waystones.containsKey(player.getUuid().toString()))
//                WaypointsPlugin.INSTANCE.waystones.put(player.getUuid().toString(), new HashMap<>());
//
//            HashMap<String, Waystone> list = WaypointsPlugin.INSTANCE.waystones.get(player.getUuid().toString());
//            if (list.keySet().contains(data.waystoneName)) {
//                System.out.println(data.waystoneName+ " is already in the list");
//                return;
//            }
//
//            Vector3i playerPosition = this.state.getBlockPosition();
//            WaypointsPlugin.INSTANCE.waystones.get(player.getUuid().toString()).put(data.waystoneName, new Waystone(data.waystoneName, player.getWorld(), Math.floor(playerPosition.x), Math.floor(playerPosition.y), Math.floor(playerPosition.z)));
//            WaypointsPlugin.INSTANCE.saveWaystones(player.getUuid());
//
//            UICommandBuilder commandBuilder = new UICommandBuilder();
//            UIEventBuilder eventBuilder = new UIEventBuilder();
//
//            buildWaystoneList(player, commandBuilder, eventBuilder);
//            sendUpdate(commandBuilder, eventBuilder, false);
//
//            System.out.println("saved");
        } else if (data.waystoneAction != null) {
            Waystone waystone = manager.getRegisteredWaystones().get(data.waystoneAction);
            Transform transform = waystone.getTransform();

            System.out.printf("Teleport: %s %f %f %f\n", waystone.getName(), transform.getPosition().x, transform.getPosition().y, transform.getPosition().z);
            Teleport teleport = new Teleport(player.getWorld(), transform);
            store.addComponent(ref, Teleport.getComponentType(), teleport);

            System.out.println("teleport");
        }
    }

    public static class WaystonePageEventData {
        public static final BuilderCodec<WaystonePageEventData> CODEC;

        public String waystoneName;
        public String waystoneAction;

        static {
            CODEC = ((BuilderCodec.Builder<WaystonePageEventData>) ((BuilderCodec.Builder<WaystonePageEventData>) BuilderCodec.builder(WaystonePageEventData.class, WaystonePageEventData::new).append(new KeyedCodec("WaystoneAction", Codec.STRING), (entry, s) -> entry.waystoneAction = s, (entry) -> entry.waystoneAction).add()).append(new KeyedCodec("@WaystoneName", Codec.STRING), (entry, s) -> entry.waystoneName = s, (entry) -> entry.waystoneName).add())
                    .build();
        }
    }
}
