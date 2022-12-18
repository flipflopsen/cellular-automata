package de.weber.simulation;

import de.weber.model.Automaton;

public interface ISimulationThread {
    void pauseSimulation();
    void stopSimulation();
    void startSimulation();
    void setSimulationPace(long pace);
    void setAutomaton(Automaton automaton);
    boolean isRunning();
}
