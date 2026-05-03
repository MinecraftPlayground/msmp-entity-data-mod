package dev.loat.msmp_entity_data;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp.MSMPServer;
import dev.loat.msmp_entity_data.logging.Logger;
import dev.loat.msmp_entity_data.msmp.methods.Methods;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;


public class MSMPEntityData implements ModInitializer {

    private static final MSMPNamespace NS = new MSMPNamespace("entity_data");
    private static MSMPServer msmp;

    @Override
    public void onInitialize() {
        Logger.setLoggerClass(MSMPEntityData.class);

        Methods.register(NS);

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            NS.attach(server);
            msmp = new MSMPServer(server);
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            NS.detach();
            msmp = null;
        });

        Logger.info("MSMP Entity Data initialized.");
    }
}
