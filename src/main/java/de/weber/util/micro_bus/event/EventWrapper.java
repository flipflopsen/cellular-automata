package de.weber.util.micro_bus.event;

import de.weber.util.micro_bus.handler.MicroBusHandler;
import de.weber.util.micro_bus.handler.ErrorConsumer;

import java.util.function.BiConsumer;

public class EventWrapper<E extends MicroBusEvent, H extends MicroBusHandler<?>> {

    protected final E event;
    public final BiConsumer<E, H> success;
    public final ErrorConsumer<E, H> error;

    public EventWrapper(E event, BiConsumer<E, H> success, ErrorConsumer<E, H> failure) {
        this.event = event;
        this.success = success;
        this.error = failure;
    }

    public E getEvent() {
        return this.event;
    }

}
