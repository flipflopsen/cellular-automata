package de.weber.services;

import de.weber.services.interfaces.IMicroBusDistributor;
import de.weber.util.diy_framwork.annotations.Inject;
import de.weber.util.diy_framwork.annotations.Vessel;

@Vessel
public class InjectionRepoJavaFX {

    @Inject
    public IMicroBusDistributor busDistributor;

    public InjectionRepoJavaFX() {

    }

    public IMicroBusDistributor getBusDistributor() {
        return this.busDistributor;
    }
}
