package de.weber.services.interfaces;

import de.weber.util.micro_bus.bus.MicroBusAsync;
import de.weber.util.micro_bus.bus.MicroBusSync;
import de.weber.util.micro_bus.event.MicroBusEvent;
import de.weber.util.micro_bus.handler.MicroBusHandler;

public interface IMicroBusDistributor {

    void createNewSyncBus(String identifier);

    MicroBusSync<MicroBusEvent, MicroBusHandler<?>> addSyncHandlerAndSubscribe(String busIdentifier, MicroBusHandler<?> handler);

    MicroBusSync<MicroBusEvent, MicroBusHandler<?>> getSyncBus(String busIdentifier);

    void removeSyncHandling(String busIdentifier, MicroBusHandler<?> handler);

    void clearAllSyncBusHandlings(String busIdentifier);

    void createNewAsyncBus(String identifier);

    MicroBusAsync<MicroBusEvent, MicroBusHandler<?>> addAsyncHandlerAndSubscribe(String busIdentifier, MicroBusHandler<?> handler);

    MicroBusAsync<MicroBusEvent, MicroBusHandler<?>> getAsyncBus(String busIdentifier);

    void removeAsyncHandling(String busIdentifier, MicroBusHandler<?> handler);

    int countSyncBusses();
}
