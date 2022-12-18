package de.weber.util.micro_bus.handler;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public class MicroBusHandlerWeakRef<H extends MicroBusHandler> extends WeakReference<H> {

    private final int hash;

    private final Class handlerTypeClass;

    public MicroBusHandlerWeakRef(H handler, ReferenceQueue q) {
        super(handler, q);
        hash = handler.hashCode();
        handlerTypeClass = handler.getConnectedClass();
    }

    public Class getHandlerTypeClass() {
        return handlerTypeClass;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof MicroBusHandlerWeakRef)) {
            return false;
        }

        var t = this.get();
        var u = ((MicroBusHandlerWeakRef<?>) obj).get();

        if (t == u) {
            return true;
        }
        if (t == null || u == null) {
            return false;
        }
        return t.equals(u);
    }
}
