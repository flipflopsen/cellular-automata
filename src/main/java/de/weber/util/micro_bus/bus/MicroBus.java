package de.weber.util.micro_bus.bus;

import de.weber.util.micro_bus.handler.MicroBusHandler;
import de.weber.util.micro_bus.handler.ErrorConsumer;
import de.weber.util.micro_bus.event.MicroBusEvent;

import java.util.function.BiConsumer;

public interface MicroBus<E extends MicroBusEvent, H extends MicroBusHandler<?>> {
    void subscribe(H subscriber);
    void unsubscribe(H subscriber);
    void publish(E event);
    void publish(
            E event,
            BiConsumer<E, H> success,
            ErrorConsumer<E, H> failure
    );
    boolean hasPendingEvents();
}
