package dev.loat.msmp_entity_data.msmp.methods;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity_data.msmp.methods.health.Health;
import dev.loat.msmp_entity_data.msmp.methods.location.Location;


/**
 * Central registration point for all {@code entity_data} MSMP methods.
 *
 * <p>Each method is implemented in its own sub-package and registered here.
 * Call {@link #register(MSMPNamespace)} once during mod initialization, before
 * the server starts.</p>
 */
public class Methods {

    private Methods() {}

    /**
     * Registers all {@code entity_data} methods on the given {@link MSMPNamespace}.
     *
     * @param namespace The namespace to register all methods under
     */
    public static void register(MSMPNamespace namespace) {
        Health.register(namespace);
        Location.register(namespace);
    }
}
