package dev.loat.msmp_entity_data.msmp.components;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;
import java.util.UUID;

import dev.loat.msmp_entity_data.logging.Logger;


/**
 * Utility class for resolving entities from MSMP request parameters.
 *
 * <p>Centralizes the common lookup logic shared across multiple methods,
 * avoiding duplication between e.g. {@code entity_data:health} and
 * {@code entity_data:location}.</p>
 */
public final class EntityResolver {

    private EntityResolver() {}

    /**
     * Resolves any {@link Entity} from the given {@link EntityRequest}.
     *
     * @param server The running {@link MinecraftServer} instance
     * @param request The request containing optional {@code id} and/or {@code name}
     * 
     * @return The resolved {@link Entity}
     * 
     * @throws IllegalArgumentException if neither field is provided, the UUID is malformed,
     * or no matching entity is found
     */
    public static Entity resolveEntity(MinecraftServer server, EntityRequest request) {
        if (request.id().isEmpty() && request.name().isEmpty()) {
            Logger.warning("Either 'id' or 'name' must be provided");
            return null;
        }

        Entity entity = null;

        if (request.id().isPresent()) {
            UUID uuid;
            try {
                uuid = UUID.fromString(request.id().get());
            } catch (IllegalArgumentException e) {
                Logger.warning("Invalid UUID: " + request.id().get());
                return null;
            }

            entity = server.getPlayerList().getPlayer(uuid);

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

        if (entity == null && request.name().isPresent()) {
            entity = server.getPlayerList().getPlayerByName(request.name().get());
        }

        if (entity == null) {
            String identifier = request.id().orElseGet(() -> request.name().get());
            Logger.warning("Entity not found: " + identifier);
            return null;
        }

        return entity;
    }

    /**
     * Resolves a {@link LivingEntity} from the given {@link EntityRequest}.
     *
     * <p>Same lookup order as {@link #resolveEntity(MinecraftServer, EntityRequest)},
     * but additionally throws if the found entity is not a {@link LivingEntity}.</p>
     *
     * @param server  The running {@link MinecraftServer} instance
     * @param request The request containing optional {@code id} and/or {@code name}
     * @return The resolved {@link LivingEntity}
     * @throws IllegalArgumentException if the entity is found but is not a {@link LivingEntity}
     */
    public static LivingEntity resolveLivingEntity(MinecraftServer server, EntityRequest request) {
        Entity entity = resolveEntity(server, request);
        if (!(entity instanceof LivingEntity living)) {
            throw new IllegalArgumentException(
                "Entity %s is not a LivingEntity".formatted(entity.getUUID())
            );
        }
        return living;
    }

    /**
     * Resolves a {@link Player} from the given {@link EntityRequest}.
     *
     * <p>Same lookup order as {@link #resolveEntity(MinecraftServer, EntityRequest)},
     * but additionally throws if the found entity is not a {@link Player}.</p>
     *
     * @param server  The running {@link MinecraftServer} instance
     * @param request The request containing optional {@code id} and/or {@code name}
     * @return The resolved {@link Player}
     * @throws IllegalArgumentException if the entity is found but is not a {@link Player}
     */
    public static Player resolvePlayer(MinecraftServer server, EntityRequest request) {
        Entity entity = resolveEntity(server, request);
        if (!(entity instanceof Player player)) {
            throw new IllegalArgumentException(
                "Entity %s is not a Player".formatted(entity.getUUID())
            );
        }
        return player;
    }

    /**
     * Builds an {@link EntityRef} from a resolved {@link Entity}.
     * Includes the player name if the entity is a {@link Player}.
     *
     * @param entity The resolved entity
     * @return The corresponding {@link EntityRef}
     */
    public static EntityRef toEntityRef(Entity entity) {
        Optional<String> name = entity instanceof Player p
            ? Optional.of(p.getName().getString())
            : Optional.empty();
        return new EntityRef(entity.getUUID().toString(), name);
    }
}
