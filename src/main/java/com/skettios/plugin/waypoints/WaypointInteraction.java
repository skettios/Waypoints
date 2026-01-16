package com.skettios.plugin.waypoints;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.protocol.packets.window.WindowType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.PageManager;
import com.hypixel.hytale.server.core.entity.entities.player.windows.Window;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import javax.annotation.Nonnull;

public class WaypointInteraction extends SimpleBlockInteraction {
    public static final BuilderCodec<WaypointInteraction> CODEC;
    public static final WaypointInteraction INTERACTION;
    public static final RootInteraction ROOT_INTERACTION;

    public WaypointInteraction(String id) {
        super(id);
    }

    protected WaypointInteraction() {

    }

    @Override
    protected void interactWithBlock(@NonNullDecl World world, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NullableDecl ItemStack itemStack, @NonNullDecl Vector3i vector3i, @NonNullDecl CooldownHandler cooldownHandler) {
        Ref<EntityStore> ref = interactionContext.getEntity();
        Store<EntityStore> store = ref.getStore();
        Player player = commandBuffer.getComponent(ref, Player.getComponentType());
        if (player != null) {
            WaypointState state = (WaypointState) world.getState(vector3i.x, vector3i.y, vector3i.z, true);
            PageManager pageManager = player.getPageManager();
            Window[] window = new Window[1];
            window[0] = (Window)new WaypointWindow(WindowType.Container, state);
            pageManager.openCustomPage(ref, store, new WaypointPage(player.getPlayerRef()));
            System.out.println("YES");
        }
    }

    @Override
    protected void simulateInteractWithBlock(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NullableDecl ItemStack itemStack, @NonNullDecl World world, @NonNullDecl Vector3i vector3i) {
    }

    static {
        INTERACTION = new WaypointInteraction("OpenWaypoint");
        ROOT_INTERACTION = new RootInteraction(INTERACTION.getId(), new String[]{INTERACTION.getId()});
        CODEC = BuilderCodec.builder(WaypointInteraction.class, WaypointInteraction::new, SimpleBlockInteraction.CODEC).build();
    }
}
