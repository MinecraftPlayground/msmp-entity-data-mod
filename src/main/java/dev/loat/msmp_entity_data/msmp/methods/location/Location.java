package dev.loat.msmp_entity_data.msmp.methods.location;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity_data.logging.Logger;
import dev.loat.msmp_entity_data.msmp.components.EntityRequest;
import dev.loat.msmp_entity_data.msmp.components.EntityResolver;
import net.minecraft.world.entity.Entity;


/**
 * Registers the {@code entity_data:location} MSMP method.
 *
 * <p>Returns the current position, rotation and dimension of any loaded entity.
 * Players can be looked up by UUID or name; all other entities require a UUID.</p>
 *
 * <p>Example request:</p>
 * <pre>{@code
 * {
 *   "jsonrpc": "2.0", "id": 1, "method": "entity_data:location",
 *   "params": [{ "name": "Steve" }]
 * }
 * }</pre>
 *
 * <p>Example response:</p>
 * <pre>{@code
 * {
 *   "entity": { "id": "069a...", "name": "Steve" },
 *   "dimension": "minecraft:overworld",
 *   "x": 128.5,
 *   "y": 64.0,
 *   "z": -32.3,
 *   "yaw": 90.0,
 *   "pitch": -15.0
 * }
 * }</pre>
 */
public class Location {

    /**
     * Registers the {@code entity_data:location} method on the given {@link MSMPNamespace}.
     *
     * <p>Entity lookup is delegated to {@link EntityResolver#resolveEntity}.
     * The dimension is returned as a resource key string via {@code identifier().toString()},
     * e.g. {@code minecraft:overworld}.</p>
     *
     * @param namespace The namespace to register this method under
     */
    public static void register(MSMPNamespace namespace) {
        namespace.method("location",
            EntityRequest.SCHEMA,
            LocationResponse.SCHEMA,
            "Returns the current position, rotation and dimension of any loaded entity by UUID, or a player by name",
            (server, params, client) -> {
                try {
                    Entity entity = EntityResolver.resolveEntity(server, params);
                    String dimension = entity.level().dimension().identifier().toString();
                    return new LocationResponse(
                        EntityResolver.toEntityRef(entity),
                        dimension,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        entity.getYRot(),
                        entity.getXRot()
                    );
                } catch (IllegalArgumentException e) {
                    Logger.warning("entity_data:location - " + e.getMessage());
                    return null;
                }
            }
        );
    }
}
