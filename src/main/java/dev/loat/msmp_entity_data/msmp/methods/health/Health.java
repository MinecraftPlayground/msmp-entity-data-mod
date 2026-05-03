package dev.loat.msmp_entity_data.msmp.methods.health;

import java.util.Optional;
import java.util.UUID;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity_data.logging.Logger;
import dev.loat.msmp_entity_data.msmp.components.EntityRef;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;


/**
 * Registers the {@code entity_data:health} MSMP method.
 *
 * <p>Returns the current and maximum health of any online {@link LivingEntity}.
 * Players can be looked up by UUID or name; all other entities require a UUID.</p>
 *
 * <p>Example requests:</p>
 * <pre>{@code
 * // Player by name:
 * { "jsonrpc": "2.0", "id": 1, "method": "entity_data:health",
 *   "params": [{ "name": "Steve" }] }
 *
 * // Any entity by UUID:
 * { "jsonrpc": "2.0", "id": 1, "method": "entity_data:health",
 *   "params": [{ "id": "069a79f4-44e9-4726-a5be-fca90e38aaf5" }] }
 * }</pre>
 *
 * <p>Example responses:</p>
 * <pre>{@code
 * // Player:
 * { "entity": { "id": "069a...", "name": "Steve" }, "health": 20.0, "max_health": 20.0 }
 *
 * // Non-player entity:
 * { "entity": { "id": "1b3e..." }, "health": 14.0, "max_health": 20.0 }
 * }</pre>
 */
public class Health {

    /**
     * Registers the {@code entity_data:health} method on the given {@link MSMPNamespace}.
     *
     * <p>Lookup order:</p>
     * <ol>
     *   <li>By UUID via {@code server.getPlayerList().getPlayer(UUID)} (players only, fast path)</li>
     *   <li>By UUID via level entity lookup (all {@link LivingEntity} types)</li>
     *   <li>By name via {@code server.getPlayerList().getPlayerByName(String)} (players only)</li>
     * </ol>
     *
     * <p>Max health is read from the {@link Attributes#MAX_HEALTH} attribute as a
     * {@code double} to avoid floating-point precision issues with {@code float}.</p>
     *
     * @param namespace The namespace to register this method under
     */
    public static void register(MSMPNamespace namespace) {
        namespace.method("health",
            HealthRequest.SCHEMA,
            HealthResponse.SCHEMA,
            "Returns the current and maximum health of any LivingEntity by UUID, or a player by name",
            (server, params, client) -> {
                if (params.id().isEmpty() && params.name().isEmpty()) {
                    Logger.warning("'entity_data:health' called without 'id' or 'name'");
                }

                LivingEntity living = null;

                if (params.id().isPresent()) {
                    UUID uuid;
                    try {
                        uuid = UUID.fromString(params.id().get());
                    } catch (IllegalArgumentException e) {
                        Logger.warning("'entity_data:health' called with invalid UUID: " + params.id().get());
                        return null;
                    }

                    living = server.getPlayerList().getPlayer(uuid);

                    // Fall back to all loaded levels for non-player entities
                    if (living == null) {
                        for (ServerLevel level : server.getAllLevels()) {
                            Entity entity = level.getEntity(uuid);
                            if (entity instanceof LivingEntity livingEntity) {
                                living = livingEntity;
                                break;
                            } else if (entity != null) {
                                Logger.warning("'entity_data:health' entity %s is not a LivingEntity".formatted(uuid));
                            }
                        }
                    }
                }

                // Name fallback: players only
                if (living == null && params.name().isPresent()) {
                    living = server.getPlayerList().getPlayerByName(params.name().get());
                }

                if (living == null) {
                    String identifier = params.id().orElseGet(() -> params.name().get());
                    Logger.warning("'entity_data:health' entity not found: " + identifier);
                }

                Optional<String> name = living instanceof Player p
                    ? Optional.of(p.getName().getString())
                    : Optional.empty();

                EntityRef entityRef = new EntityRef(living.getUUID().toString(), name);
                double health = living.getHealth();
                double maxHealth = living.getAttributeValue(Attributes.MAX_HEALTH);

                return new HealthResponse(entityRef, health, maxHealth);
            }
        );
    }
}
