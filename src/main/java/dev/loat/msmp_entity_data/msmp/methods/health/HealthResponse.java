package dev.loat.msmp_entity_data.msmp.methods.health;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.loat.msmp_entity_data.msmp.components.EntityRef;
import net.minecraft.server.jsonrpc.api.Schema;


/**
 * Response payload for the {@code entity_data:health} method.
 *
 * <p>Example JSON representations:</p>
 * <pre>{@code
 * // Player:
 * {
 *   "entity":     { "id": "069a79f4-44e9-4726-a5be-fca90e38aaf5", "name": "Steve" },
 *   "health":     20.0,
 *   "max_health": 20.0
 * }
 *
 * // Non-player entity:
 * {
 *   "entity":     { "id": "1b3e9f2a-12cd-4b56-a832-ff1234567890" },
 *   "health":     14.0,
 *   "max_health": 20.0
 * }
 * }</pre>
 *
 * @param entity    Reference to the entity; always includes UUID, name only for players
 * @param health    The entity's current health points
 * @param maxHealth The entity's maximum health points
 */
public record HealthResponse(EntityRef entity, double health, double maxHealth) {

    /**
     * Codec for serializing and deserializing {@link HealthResponse} instances.
     */
    public static final Codec<HealthResponse> CODEC = RecordCodecBuilder.create(i -> i.group(
        EntityRef.CODEC.fieldOf("entity").forGetter(HealthResponse::entity),
        Codec.DOUBLE.fieldOf("health").forGetter(HealthResponse::health),
        Codec.DOUBLE.fieldOf("max_health").forGetter(HealthResponse::maxHealth)
    ).apply(i, HealthResponse::new));

    /**
     * MSMP schema for {@link HealthResponse}, used for protocol discovery.
     */
    public static final Schema<HealthResponse> SCHEMA = Schema.record(CODEC)
        .withField("entity", EntityRef.SCHEMA)
        .withField("health", Schema.NUMBER_SCHEMA)
        .withField("max_health", Schema.NUMBER_SCHEMA);
}
