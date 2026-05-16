package dev.loat.msmp_entity_data.msmp.methods.location;

import java.util.Optional;
import java.util.UUID;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity_data.logging.Logger;
import dev.loat.msmp_entity_data.msmp.components.EntityRef;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;


/**
 * Registers the {@code entity_data:location} MSMP method.
 *
 * <p>Returns the current position and dimension of any loaded entity.
 * Players can be looked up by UUID or name; all other entities require a UUID.</p>
 *
 * <p>Example requests:</p>
 * <pre>{@code
 * // Player by name:
 * { "jsonrpc": "2.0", "id": 1, "method": "entity_data:location",
 *   "params": [{ "name": "Steve" }] }
 *
 * // Any entity by UUID:
 * { "jsonrpc": "2.0", "id": 1, "method": "entity_data:location",
 *   "params": [{ "id": "069a79f4-44e9-4726-a5be-fca90e38aaf5" }] }
 * }</pre>
 *
 * <p>Example responses:</p>
 * <pre>{@code
 * // Player:
 * { "entity": { "id": "069a...", "name": "Steve" },
 *   "dimension": "minecraft:overworld", "x": 128.5, "y": 64.0, "z": -32.3 }
 *
 * // Non-player entity:
 * { "entity": { "id": "1b3e..." },
 *   "dimension": "minecraft:the_nether", "x": 0.5, "y": 64.0, "z": 0.5 }
 * }</pre>
 */
public class Location {

    /**
     * Registers the {@code entity_data:location} method on the given {@link MSMPNamespace}.
     *
     * <p>Lookup order:</p>
     * <ol>
     *   <li>By UUID via {@code server.getPlayerList().getPlayer(UUID)} (players only, fast path)</li>
     *   <li>By UUID via level entity lookup across all loaded levels</li>
     *   <li>By name via {@code server.getPlayerList().getPlayerByName(String)} (players only)</li>
     * </ol>
     *
     * <p>The dimension is returned as a resource key string, e.g. {@code minecraft:overworld},
     * {@code minecraft:the_nether}, or {@code minecraft:the_end}.</p>
     *
     * @param namespace The namespace to register this method under
     */
    public static void register(MSMPNamespace namespace) {
        namespace.method("location",
            LocationRequest.SCHEMA,
            LocationResponse.SCHEMA,
            "Returns the current position and dimension of any loaded entity by UUID, or a player by name",
            (server, params, client) -> {
                if (params.id().isEmpty() && params.name().isEmpty()) {
                    Logger.warning("entity_data:location called without 'id' or 'name'");
                    throw new IllegalArgumentException("Either 'id' or 'name' must be provided");
                }

                Entity entity = null;

                if (params.id().isPresent()) {
                    UUID uuid;
                    try {
                        uuid = UUID.fromString(params.id().get());
                    } catch (IllegalArgumentException e) {
                        Logger.warning("entity_data:location called with invalid UUID: " + params.id().get());
                        throw new IllegalArgumentException("Invalid UUID: " + params.id().get());
                    }

                    // Fast path: try player list first
                    entity = server.getPlayerList().getPlayer(uuid);

                    // Fall back to all loaded levels for non-player entities
                    if (entity == null) {
                        for (ServerLevel level : server.getAllLevels()) {
                            Entity found = level.getEntity(uuid);
                            if (found != null) {
                                entity = found;
                                break;
                            }
                        }
                    }
                }

                // Name fallback: players only
                if (entity == null && params.name().isPresent()) {
                    entity = server.getPlayerList().getPlayerByName(params.name().get());
                }

                if (entity == null) {
                    String identifier = params.id().orElseGet(() -> params.name().get());
                    Logger.warning("entity_data:location entity not found: " + identifier);
                    throw new IllegalArgumentException("Entity not found: " + identifier);
                }

                Optional<String> name = entity instanceof Player p
                    ? Optional.of(p.getName().getString())
                    : Optional.empty();

                EntityRef entityRef = new EntityRef(entity.getUUID().toString(), name);
                String dimension = entity.level().dimension().identifier().toString();

                return new LocationResponse(entityRef, dimension, entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
            }
        );
    }
}
