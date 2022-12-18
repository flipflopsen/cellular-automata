package de.weber;

import de.weber.services.interfaces.IAutomataService;
import de.weber.util.jfx_peculiarities.interfaces.IJFXManagerino;
import de.weber.util.jfx_peculiarities.JFXManagerino;
import de.weber.services.interfaces.IMicroBusDistributor;
import de.weber.util.diy_framwork.DInjector;
import de.weber.util.loggerino.LoggerFactorino;
import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {
    public IJFXManagerino jfxManagerino;
    private static final LoggerFactorino.Loggerinotho logger = LoggerFactorino.FACTORINO.makeLogger("Main").grabLogger("Main");

    public static void main(String[] args) {
        initializeDependencyInjection();
        initializeMicroBus();
        initializeServices();
        prepareShutdownHook();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        jfxManagerino = DInjector.getService(IJFXManagerino.class);
        jfxManagerino.initializeService(primaryStage);

        jfxManagerino.initMainStage("Willkommen zur Simulation diverser Dinge.");

        jfxManagerino.showStage(JFXManagerino.JFXStageLevel.ROOT, "main");
    }

    private static void initializeDependencyInjection() {
        DInjector.boot(Main.class);
    }

    private static void initializeMicroBus() {
        DInjector.getService(IMicroBusDistributor.class).createNewSyncBus("PopulationResizeDiag");
        //DInjector.getService(IMicroBusDistributor.class).addSyncHandlerAndSubscribe("PopulationResizeDiag", (MicroBusHandler<?>) new Object());
    }

    private static void initializeServices() {
        DInjector.getService(IJFXManagerino.class);
        DInjector.getService(IAutomataService.class);
        //DInjector.getService(ISimulationThread.class);
        //DInjector.getService(IPopulationPanelView.class);
        //DInjector.getService(IPopulationController.class);
    }

    private static void prepareShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            DInjector.getService(IAutomataService.class).clearAutomataFolder();
        }));
    }

}