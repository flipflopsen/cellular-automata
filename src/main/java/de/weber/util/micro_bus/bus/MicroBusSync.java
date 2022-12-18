package de.weber.util.micro_bus.bus;

import de.weber.util.loggerino.LoggerFactorino;
import de.weber.util.micro_bus.event.EventWrapper;
import de.weber.util.micro_bus.handler.MicroBusHandler;
import de.weber.util.micro_bus.handler.MicroBusHandlerWeakRef;
import de.weber.util.micro_bus.handler.ErrorConsumer;
import de.weber.util.micro_bus.event.MicroBusEvent;

import java.lang.ref.ReferenceQueue;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public class MicroBusSync<E extends MicroBusEvent, H extends MicroBusHandler<?>> implements MicroBus<E, H> {
    private static final LoggerFactorino.Loggerinotho logger = LoggerFactorino.FACTORINO.makeLogger("MicroBusSync").grabLogger("MicroBusSync");

    private final AtomicInteger inside = new AtomicInteger();
    private final ReferenceQueue<? extends MicroBusHandlerWeakRef<?>> dispQueue = new ReferenceQueue<>();

    private final Set<MicroBusHandlerWeakRef<H>> handlers = Collections.newSetFromMap(new ConcurrentHashMap<>());


    protected void fireHandler(H handler, E event) throws Throwable {

        logger.info("Received Event of type \"{0}\" and sending it to Handler of type: \"{1}\"",
                    event.getClass().getSimpleName(),
                    handler.getClass().getSimpleName());

        handler.handleEvent(event);
    }


    @Override
    public void subscribe(H subscriber) {
        handlers.add(new MicroBusHandlerWeakRef<>(subscriber, dispQueue));
    }

    @Override
    public void unsubscribe(H subscriber) {
        handlers.remove(new MicroBusHandlerWeakRef<>(subscriber, dispQueue));
    }

    @Override
    public void publish(E event) {
        publish(event, null, null);
    }

    @Override
    public void publish(E event, BiConsumer<E, H> success, ErrorConsumer<E, H> error) {
        if (event == null) {
            return;
        }

        inside.incrementAndGet();
        try {
            processEvent(new EventWrapper<>(event, success, error));
        } finally {
            inside.decrementAndGet();
        }
    }

    @Override
    public boolean hasPendingEvents() {
        return inside.get() > 0;
    }

    private void processEvent(EventWrapper<E, H> eventWrapper) {
        MicroBusHandlerWeakRef<?> hWeakRef;

        while ((hWeakRef = (MicroBusHandlerWeakRef) dispQueue.poll()) != null) {
            handlers.remove(hWeakRef);
        }

        if (eventWrapper != null) {
            notifySubscribers(eventWrapper);
        }
    }

    private void notifySubscribers(EventWrapper<E, H> eventWrapper) {
        for (MicroBusHandlerWeakRef<H> handler : handlers) {
            H eventHandler = handler.get();
            if (eventHandler == null) {
                continue;
            }

            try {
                if (eventHandler.getConnectedClass() == null) {
                    if (eventHandler.canHandle(eventWrapper.getEvent().getClass())) {
                        runHandlerWrapper(eventHandler, eventWrapper);
                    }
                } else if (eventHandler.getConnectedClass().isAssignableFrom(eventHandler.eventClass)) {
                    runHandlerWrapper(eventHandler, eventWrapper);
                } else {
                    logger.critical("Some error while getting classes. Handler: {0}, Wrapper: {1}, Connected: {2}",
                            eventHandler.eventClass.getSimpleName(),
                            eventWrapper.getEvent().getClass().getSimpleName(),
                            eventHandler.getConnectedClass().getSimpleName());
                }
            } catch (Throwable t) {
                logger.critical("Failed to process Event in MicroBusSync!\n Event: {0} \nError-Message: {1} \nThrowable: {2}",
                        eventWrapper.getEvent().getClass().getSimpleName(),
                        t.getMessage(),
                        t);
            }
        }

    }

    private void runHandlerWrapper(H eventHandler, EventWrapper<E, H> eventWrapper) {
        try {
            fireHandler(eventHandler, eventWrapper.getEvent());
            if (eventWrapper.success != null) {
                eventWrapper.success.accept(eventWrapper.getEvent(), eventHandler);
            }
        } catch (Throwable t) {
            logger.critical("Error in Handler at MicroBusSync!\n Event: {0} \nError-Message: {1} \nThrowable: {2}",
                    eventWrapper.getEvent().getClass().getSimpleName(),
                    t.getMessage(),
                    t);

            if (eventWrapper.error != null) {
                eventWrapper.error.accept(eventWrapper.getEvent(), eventHandler, t);
            }
        }
    }

    public Set<MicroBusHandlerWeakRef<H>> retrieveAllHandlers() {
        return handlers;
    }

    public void removeHandler(MicroBusHandlerWeakRef<H> eventHandler) {
        handlers.remove(eventHandler);
    }
}
