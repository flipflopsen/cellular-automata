package de.weber.util.micro_bus.bus;

import de.weber.util.loggerino.LoggerFactorino;
import de.weber.util.micro_bus.event.EventWrapper;
import de.weber.util.micro_bus.handler.MicroBusHandler;
import de.weber.util.micro_bus.handler.MicroBusHandlerWeakRef;
import de.weber.util.micro_bus.handler.ErrorConsumer;
import de.weber.util.micro_bus.event.MicroBusEvent;

import java.lang.ref.ReferenceQueue;
import java.util.Collections;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

public class MicroBusAsync<E extends MicroBusEvent, H extends MicroBusHandler<?>> implements MicroBus<E, H> {

    private static final LoggerFactorino.Loggerinotho logger = LoggerFactorino.FACTORINO.makeLogger("MicroBusAsync").grabLogger("MicroBusAsync");

    private final Thread evQueueThread;

    public final Queue<EventWrapper<E, H>> eventsQueue = new ConcurrentLinkedQueue<>();

    private final ReferenceQueue<?> dispQueue = new ReferenceQueue();

    private final Map<Class<?>, Set<MicroBusHandlerWeakRef<H>>> handlerClazzes = new ConcurrentHashMap<>();

    private final Set<MicroBusHandlerWeakRef<H>> handlers = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private final ExecutorService handlersExecutor;
    private final AtomicBoolean interrupted = new AtomicBoolean(false);

    private final int sleepMs;


    protected void submitHandler(H h, EventWrapper<E, H> ew) {
        handlersExecutor.submit(() -> runHandlerWrapper(h, ew));
    }

    protected void fireHandler(H h, E e) throws Throwable {
        h.handleEvent(e);
    }

    public MicroBusAsync() {
        this(Executors.newCachedThreadPool());
    }

    /**
     * Create instance with customer ExecutorService for event handlers.
     *
     * @param handlersExecutor Will be used to run event handler processing for each event
     */
    public MicroBusAsync(ExecutorService handlersExecutor) {
        this.handlersExecutor = handlersExecutor;
        evQueueThread = new Thread(this::eventsQueue, "EV-Handler-Thread");
        evQueueThread.setDaemon(true);
        evQueueThread.start();

        this.sleepMs = 5;
    }


    @Override
    public void subscribe(H subscriber) {
        Class<? extends MicroBusEvent> clazz = subscriber.getConnectedClass();
        if (clazz == null) {
            handlers.add(new MicroBusHandlerWeakRef(subscriber, dispQueue));
        } else {
            synchronized (this) {
                Set<MicroBusHandlerWeakRef<H>> handlerSet = handlerClazzes.get(clazz);
                if (handlerSet == null) {
                    handlerSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
                    handlerClazzes.put(clazz, handlerSet);
                }
                handlerSet.add(new MicroBusHandlerWeakRef<>(subscriber, dispQueue));
            }
        }
    }

    @Override
    public void unsubscribe(H sub) {
        Class<?> clazz = sub.getConnectedClass();
        if (clazz == null) {
            handlers.remove(new MicroBusHandlerWeakRef<>(sub, dispQueue));
        } else {
            Set<MicroBusHandlerWeakRef<H>> set = handlerClazzes.get(clazz);
            if (set != null) {
                set.remove(new MicroBusHandlerWeakRef<>(sub, dispQueue));
            }
        }
    }

    @Override
    public void publish(E event) {
        publish(event, null, null);
    }

    @Override
    public void publish(E event, BiConsumer<E, H> success, ErrorConsumer<E, H> failure) {
        if (event == null) {
            return;
        }
        eventsQueue.add(new EventWrapper<>(event, success, failure));
    }

    @Override
    public boolean hasPendingEvents() {
        return false;
    }

    private void eventsQueue() {
        while (!interrupted.get()) {
            MicroBusHandlerWeakRef<?> hWeakRef;
            while ((hWeakRef = (MicroBusHandlerWeakRef<?>) dispQueue.poll()) != null) {
                Class<?> clazz = hWeakRef.getHandlerTypeClass();
                if (clazz == null) {
                    handlers.remove(hWeakRef);
                } else {
                    Set<MicroBusHandlerWeakRef<H>> set = handlerClazzes.get(clazz);
                    if (set != null) {
                        set.remove(hWeakRef);
                    }
                }
            }

            if (eventsQueue.isEmpty()) {
                try {
                    Thread.sleep(sleepMs);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
            }

            EventWrapper<E, H> ew = eventsQueue.poll();
            if (ew != null && ew.getEvent() != null) {
                notifySubscribers(ew);
            }
        }
    }

    private void notifySubscribers(EventWrapper<E, H> eventWrapper) {
        try {
            Set<MicroBusHandlerWeakRef<H>> hClazz = handlerClazzes.get(eventWrapper.getEvent().getClass());
            if (hClazz != null) {
                for (MicroBusHandlerWeakRef<H> hWeakRef : hClazz) {
                    H eventHandler = hWeakRef.get();
                    if (eventHandler != null) {
                        submitHandler(eventHandler, eventWrapper);
                    }
                }
            }

            for (MicroBusHandlerWeakRef<H> hWeakRef : handlers) {
                H eventHandler = hWeakRef.get();
                if (eventHandler.canHandle(eventWrapper.getEvent().getClass())) {
                    submitHandler(eventHandler, eventWrapper);
                }
            }
        } catch (Throwable t) {
            logger.critical("Failed to process Event in MicroBusSync!\n Event: {0} \nError-Message: {1} \nThrowable: {2}",
                    eventWrapper.getEvent().getClass().getSimpleName(),
                    t.getMessage(),
                    t);
        }
    }

    private void runHandlerWrapper(H handler, EventWrapper<E, H> eventWrapper) {
        try {
            fireHandler(handler, eventWrapper.getEvent());
            if (eventWrapper.success != null) {
                eventWrapper.success.accept(eventWrapper.getEvent(), handler);
            }
        } catch (Throwable t) {
            logger.critical("Error in Handler at MicroBusSync!\n Event: {0} \nError-Message: {1} \nThrowable: {2}",
                    eventWrapper.getEvent().getClass().getSimpleName(),
                    t.getMessage(),
                    t);

            if (eventWrapper.error != null) {
                eventWrapper.error.accept(eventWrapper.getEvent(), handler, t);
            }
        }
    }

    public void shutdownBus() {
        interrupted.set(true);
    }
}
