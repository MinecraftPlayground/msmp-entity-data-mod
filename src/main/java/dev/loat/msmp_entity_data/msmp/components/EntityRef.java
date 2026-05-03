package dev.loat.msmp_entity_data.msmp.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.jsonrpc.api.Schema;

import java.util.Optional;


/**
 * Represents an entity reference used in responses.
 *
 * <p>{@code id} (UUID) is always present. {@code name} is only included
 * if the entity is a player.</p>
 *
 * <p>Example JSON representations:</p>
 * <pre>{@code
 * // Player:
 * { "id": "069a79f4-44e9-4726-a5be-fca90e38aaf5", "name": "Steve" }
 *
 * // Non-player entity:
 * { "id": "1b3e9f2a-12cd-4b56-a832-ff1234567890" }
 * }</pre>
 *
 * @param id   The entity's UUID as a string, always present
 * @param name The player's in-game name, only present if the entity is a player
 */
public record EntityRef(String id, Optional<String> name) {

    /**
     * Codec for serializing and deserializing {@link EntityRef} instances.
     */
    public static final Codec<EntityRef> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.STRING.fieldOf("id").forGetter(EntityRef::id),
        Codec.STRING.optionalFieldOf("name").forGetter(EntityRef::name)
    ).apply(i, EntityRef::new));

    /**
     * MSMP schema for {@link EntityRef}, used for protocol discovery.
     */
    public static final Schema<EntityRef> SCHEMA = Schema.record(CODEC)
        .withField("id", Schema.STRING_SCHEMA)
        .withField("name", Schema.STRING_SCHEMA);
}
