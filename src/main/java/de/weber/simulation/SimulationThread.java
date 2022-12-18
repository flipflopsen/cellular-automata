package de.weber.simulation;

import de.weber.model.Automaton;
import de.weber.view.interfaces.IPopulationPanelView;
import de.weber.util.diy_framwork.annotations.Vessel;
import de.weber.util.loggerino.LoggerFactorino;

import java.util.concurrent.TimeUnit;

@Vessel
public class SimulationThread extends Thread implements ISimulationThread {
    private final LoggerFactorino.Loggerinotho logger = LoggerFactorino.FACTORINO.makeLogger("SimulationThread").grabLogger("SimulationThread");

    private volatile Object syncObj = new Object();
    private Automaton automaton;
    //public IPopulationPanelView populationPanel = DInjector.getService(IPopulationPanelView.class);
    public IPopulationPanelView populationPanel;
    private volatile long simulationPace;
    private volatile boolean isStopped = false;


    public SimulationThread() {
        //Default Constructor
    }

    public SimulationThread(IPopulationPanelView populationPanel) {
        this.populationPanel = populationPanel;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                try {
                    automaton.nextGeneration();
                    populationPanel.draw();
                    while (isStopped) {
                        synchronized (syncObj) {
                            syncObj.wait();
                        }
                    }
                    TimeUnit.MILLISECONDS.sleep(simulationPace);
                } catch (InterruptedException e) {
                    interrupt();
                }
            } catch (Throwable e) {
                logger.error("Simulation-Thread crashed! Trying to restart it...");
                restartSimulation();
            }
        }
    }

    @Override
    public void pauseSimulation() {
        isStopped = true;
    }

    @Override
    public void stopSimulation() {
        interrupt();
    }

    private void restartSimulation() {
        interrupt();
        startSimulation();
    }

    @Override
    public void startSimulation() {
        if (!isStopped) {
            start();
        } else{
            isStopped = false;
            synchronized (syncObj) {
                syncObj.notify();
            }
        }
    }

    @Override
    public void setSimulationPace(long pace) {
        this.simulationPace = pace;
    }

    @Override
    public void setAutomaton(Automaton automaton) {
        this.automaton = automaton;
    }

    @Override
    public boolean isRunning() {
        return isAlive();
    }

}
