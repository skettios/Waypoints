package com.skettios.plugin.waypoints;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.function.BsonFunctionCodec;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.universe.world.World;

public class Waystone {
    public static final Codec<Waystone> CODEC;
    public static final ArrayCodec<Waystone> ARRAY_CODEC;

    private String name;
    private String world;
    private Transform transform;

    public Waystone() {

    }

    public Waystone(String name, World world,double x, double y, double z) {
        this.name = name;
        this.world = world.getName();
        this.transform = new Transform(x, y, z);
    }

    public String getName() { return this.name; }

    public String getWorld() { return this.world; }

    public Transform getTransform() { return this.transform; }

    static {
        CODEC = new BsonFunctionCodec<Waystone>(((BuilderCodec.Builder<Waystone>)((BuilderCodec.Builder<Waystone>)BuilderCodec.builder(Waystone.class, Waystone::new)
                .addField(new KeyedCodec("Name", Codec.STRING), (waystone, s) -> waystone.name = s, (waystone) -> waystone.name))
                .addField(new KeyedCodec("World", Codec.STRING), (waystone, s) -> waystone.world = s, (waystone) -> waystone.world))
                .build(),
                (waystone, value) -> {
            waystone.transform = Transform.CODEC.decode(value);
            return waystone;
        }, (value, waystone) -> {
            value.asDocument().putAll(Transform.CODEC.encode(waystone.transform).asDocument());
            return value;
        });
        ARRAY_CODEC = new ArrayCodec(CODEC, (x) -> new Waystone[x]);
    }
}
