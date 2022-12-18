package de.weber.services;

import de.weber.services.interfaces.IMicroBusDistributor;
import de.weber.util.diy_framwork.annotations.Vessel;
import de.weber.util.micro_bus.bus.MicroBusAsync;
import de.weber.util.micro_bus.event.MicroBusEvent;
import de.weber.util.micro_bus.handler.MicroBusHandler;
import de.weber.util.micro_bus.bus.MicroBusSync;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Vessel
public class MicroBusDistributor implements IMicroBusDistributor {
    //private static final MicroBusDistributor INSTANCE = new MicroBusDistributor();
    private final Map<String, MicroBusSync<MicroBusEvent, MicroBusHandler<?>>> syncBusMap = new ConcurrentHashMap<>();
    private final Map<String, MicroBusAsync<MicroBusEvent, MicroBusHandler<?>>> asyncBusMap = new ConcurrentHashMap<>();
    private final Set<MicroBusHandler<?>> handlers = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public MicroBusDistributor() { }

    /*

    public static MicroBusDistributor operate() {
        return INSTANCE;
    }

     */

    @Override
    public void createNewSyncBus(String identifier) {
        this.syncBusMap.putIfAbsent(identifier, new MicroBusSync<>());
    }

    @Override
    public MicroBusSync<MicroBusEvent, MicroBusHandler<?>> addSyncHandlerAndSubscribe(String busIdentifier, MicroBusHandler<?> handler) {
        this.handlers.add(handler);
        this.syncBusMap.get(busIdentifier).subscribe(handler);
        return this.syncBusMap.get(busIdentifier);
    }

    @Override
    public MicroBusSync<MicroBusEvent, MicroBusHandler<?>> getSyncBus(String busIdentifier) {
        return this.syncBusMap.get(busIdentifier);
    }

    @Override
    public void removeSyncHandling(String busIdentifier, MicroBusHandler<?> handler) {
        this.handlers.remove(handler);
        this.syncBusMap.get(busIdentifier).unsubscribe(handler);
    }

    @Override
    public void clearAllSyncBusHandlings(String busIdentifier) {
        var syncBusHandlings = this.syncBusMap.get(busIdentifier).retrieveAllHandlers();
        syncBusHandlings.forEach(handler -> {
            if (this.handlers.contains(handler)) {
                this.handlers.remove(handler);
            }
            this.syncBusMap.get(busIdentifier).removeHandler(handler);
        });
    }

    @Override
    public void createNewAsyncBus(String identifier) {
        this.asyncBusMap.putIfAbsent(identifier, new MicroBusAsync<>());
    }

    @Override
    public MicroBusAsync<MicroBusEvent, MicroBusHandler<?>> addAsyncHandlerAndSubscribe(String busIdentifier, MicroBusHandler<?> handler) {
        this.asyncBusMap.get(busIdentifier).subscribe(handler);
        return this.asyncBusMap.get(busIdentifier);
    }

    @Override
    public MicroBusAsync<MicroBusEvent, MicroBusHandler<?>> getAsyncBus(String busIdentifier) {
        return this.asyncBusMap.get(busIdentifier);
    }

    @Override
    public void removeAsyncHandling(String busIdentifier, MicroBusHandler<?> handler) {
        this.asyncBusMap.get(busIdentifier).unsubscribe(handler);
    }

    @Override
    public int countSyncBusses() {
        return this.syncBusMap.size();
    }
}
