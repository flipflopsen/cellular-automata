package de.weber.util.micro_bus.handler;

import de.weber.util.micro_bus.event.MicroBusEvent;

import java.lang.reflect.ParameterizedType;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class MicroBusHandler<E extends MicroBusEvent> {

    public Class<E> eventClass;

    private Class<E> getClassGeneric() {
        if (eventClass == null)  {
            eventClass = (Class<E>)((ParameterizedType) getClass()
                    .getGenericSuperclass())
                    .getActualTypeArguments()[0];
        }
        return eventClass;
    }

    public Class<E> getConnectedClass() {
        return getClassGeneric();
    }

    public boolean canHandle(Class<? extends MicroBusEvent> cls) {
        return false;
    }

    public void handleEvent(MicroBusEvent event) throws Throwable {
        this.handle(getClassGeneric().cast(event));
    }

    public abstract void handle(E event) throws Throwable;

}
