package de.weber.util.micro_bus.handler;

import de.weber.util.micro_bus.event.MicroBusEvent;

@FunctionalInterface
public interface ErrorConsumer<E extends MicroBusEvent, H extends MicroBusHandler<?>> {

    void accept(E event, H handler, Throwable th);
}
