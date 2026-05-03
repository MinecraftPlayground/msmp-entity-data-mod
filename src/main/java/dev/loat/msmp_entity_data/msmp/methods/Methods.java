package dev.loat.msmp_entity_data.msmp.methods;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity_data.msmp.methods.health.Health;

public class Methods {

    public static void register(MSMPNamespace namespace) {
        Health.register(namespace);
    }
}
