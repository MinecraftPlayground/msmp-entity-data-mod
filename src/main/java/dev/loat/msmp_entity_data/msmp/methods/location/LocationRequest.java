package dev.loat.msmp_entity_data.msmp.methods.location;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.jsonrpc.api.Schema;

import java.util.Optional;


/**
 * Request payload for the {@code entity_data:location} method.
 *
 * <p>At least one of {@code id} or {@code name} must be present.
 * UUID lookup works for all entity types; name lookup is limited to online players.</p>
 *
 * <p>Example JSON representations:</p>
 * <pre>{@code
 * { "id": "069a79f4-44e9-4726-a5be-fca90e38aaf5" } // any entity by UUID
 * { "name": "Steve" } // player by name
 * }</pre>
 *
 * @param id   The entity's UUID as a string, if provided
 * @param name The player's in-game name, if provided (only works for online players)
 */
public record LocationRequest(Optional<String> id, Optional<String> name) {

    /**
     * Codec for serializing and deserializing {@link LocationRequest} instances.
     * Both fields are optional.
     */
    public static final Codec<LocationRequest> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.STRING.optionalFieldOf("id").forGetter(LocationRequest::id),
        Codec.STRING.optionalFieldOf("name").forGetter(LocationRequest::name)
    ).apply(i, LocationRequest::new));

    /**
     * MSMP schema for {@link LocationRequest}, used for protocol discovery.
     */
    public static final Schema<LocationRequest> SCHEMA = Schema.record(CODEC)
        .withField("id", Schema.STRING_SCHEMA)
        .withField("name", Schema.STRING_SCHEMA);
}
