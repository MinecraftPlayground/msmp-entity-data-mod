package dev.loat.msmp_entity_data.msmp.methods.location;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.loat.msmp_entity_data.msmp.components.EntityRef;
import net.minecraft.server.jsonrpc.api.Schema;


/**
 * Response payload for the {@code entity_data:location} method.
 *
 * <p>Example JSON representations:</p>
 * <pre>{@code
 * // Player:
 * {
 *   "entity": { "name": "Steve" },
 *   "dimension": "minecraft:overworld",
 *   "x": 128.5,
 *   "y": 64.0,
 *   "z": -32.3,
 *   "yaw": 90.0,
 *   "pitch": -15.0
 * }
 *
 * // Non-player entity:
 * {
 *   "entity": { "id": "1b3e9f2a-12cd-4b56-a832-ff1234567890" },
 *   "dimension": "minecraft:the_nether",
 *   "x": 0.5,
 *   "y": 64.0,
 *   "z": 0.5,
 *   "yaw": 180.0,
 *   "pitch": 0.0
 * }
 * }</pre>
 *
 * @param entity The entity reference; always includes UUID, name only for players
 * @param dimension The resource key of the dimension the entity is in (e.g. {@code minecraft:overworld})
 * @param x The entity's X coordinate
 * @param y The entity's Y coordinate
 * @param z The entity's Z coordinate
 * @param yaw The entity's yaw rotation in degrees (horizontal, -180 to 180)
 * @param pitch The entity's pitch rotation in degrees (vertical, -90 to 90)
 */
public record LocationResponse(EntityRef entity, String dimension, double x, double y, double z, double yaw, double pitch) {

    /**
     * Codec for serializing and deserializing {@link LocationResponse} instances.
     */
    public static final Codec<LocationResponse> CODEC = RecordCodecBuilder.create(i -> i.group(
        EntityRef.CODEC.fieldOf("entity").forGetter(LocationResponse::entity),
        Codec.STRING.fieldOf("dimension").forGetter(LocationResponse::dimension),
        Codec.DOUBLE.fieldOf("x").forGetter(LocationResponse::x),
        Codec.DOUBLE.fieldOf("y").forGetter(LocationResponse::y),
        Codec.DOUBLE.fieldOf("z").forGetter(LocationResponse::z),
        Codec.DOUBLE.fieldOf("yaw").forGetter(LocationResponse::yaw),
        Codec.DOUBLE.fieldOf("pitch").forGetter(LocationResponse::pitch)
    ).apply(i, LocationResponse::new));

    /**
     * MSMP schema for {@link LocationResponse}, used for protocol discovery.
     */
    public static final Schema<LocationResponse> SCHEMA = Schema.record(CODEC)
        .withField("entity", EntityRef.SCHEMA)
        .withField("dimension", Schema.STRING_SCHEMA)
        .withField("x", Schema.NUMBER_SCHEMA)
        .withField("y", Schema.NUMBER_SCHEMA)
        .withField("z", Schema.NUMBER_SCHEMA)
        .withField("yaw", Schema.NUMBER_SCHEMA)
        .withField("pitch", Schema.NUMBER_SCHEMA);
}
