package de.weber.util.diy_framwork;

import de.weber.util.diy_framwork.annotations.Vessel;
import de.weber.util.diy_framwork.exception.ImplementationClassNotFoundException;
import de.weber.util.class_duties.ClassPredator;
import de.weber.util.loggerino.LoggerFactorino;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DInjector {

    private static final LoggerFactorino.Loggerinotho logger = LoggerFactorino.FACTORINO.makeLogger("DInjector").grabLogger("DInjector");
    private final Map<Class<?>, Class<?>> diMap;
    private final Map<Class<?>, Object> appScope;

    private static DInjector injector;

    private DInjector() {
        super();
        diMap = new HashMap<>();
        appScope = new HashMap<>();
    }

    public static void boot(Class<?> mainClass) {
        try {
            synchronized (DInjector.class) {
                if (injector == null) {
                    injector = new DInjector();
                    injector.initializeDiy(mainClass);
                }
            }
        }
        catch (InvocationTargetException e) {
            logger.warn("InvocationTargetException occurred because of the JavaFX Platform.runLater()" +
                " in the constructor of the MainController.");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            logger.fine("DI-Y is running!");
        }
    }

    private void initializeDiy(Class<?> mainClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ImplementationClassNotFoundException, IOException {
        ClassPredator.scanCompleteClasspath(mainClass.getPackageName().replace(".", "/"));
        ClassPredator.loadClasses();

        var cla = ClassPredator.retrieveClasses();
        var clazzes = cla.toArray(new Class[0]);

        ClassPredator.loadAnnotatedClasses(Vessel.class);

        var types = ClassPredator.retrieveLoadedAnnotatedClasses();

        for (Class<?> impl : types) {
            Class<?>[] interfaces = impl.getInterfaces();
            if (interfaces.length == 0) {
                //Map to itself if no interfaces
                diMap.put(impl, impl);
            } else {
                for (Class<?> anInterface : interfaces) {
                    diMap.put(impl, anInterface);
                }
            }
        }

        //Here happens the actual DI
        for (Class<?> clazz : clazzes) {
            if (clazz.isAnnotationPresent(Vessel.class)) {
                var inst = clazz.getDeclaredConstructor().newInstance();
                appScope.put(clazz, inst);
                DInjection.vessel(injector, clazz, inst);
            }
        }
    }


    public static <T> T getService(Class<T> clazz) {
        try {
            return injector.getBeany(clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private <T> T getBeany(Class<T> interfaceClass) throws InstantiationException, IllegalAccessException, ImplementationClassNotFoundException, InvocationTargetException, NoSuchMethodException {
        return (T) getBeany(interfaceClass, null, null);
    }


    public <T> Object getBeany(Class<T> ifaceClazz, String fieldName, String nickname) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ImplementationClassNotFoundException {
        var implementation = getImplOfIface(ifaceClazz, fieldName, nickname);

        if (appScope.containsKey(implementation)) {
            return appScope.get(implementation);
        }

        synchronized (appScope) {
            var serv = implementation.getDeclaredConstructor().newInstance();
            appScope.put(implementation, serv);
            return serv;
        }

    }

    private Class<?> getImplOfIface(Class<?> ifaceClazz, final String fieldName, final String nickname) throws ImplementationClassNotFoundException {
        var implClazzes = diMap.entrySet().stream()
                .filter(e -> e.getValue() == ifaceClazz)
                .collect(Collectors.toSet());

        if (implClazzes.isEmpty()) {
            throw new ImplementationClassNotFoundException("Failed to find implementation for interface: " + ifaceClazz.getSimpleName(), new Throwable());
        } else if (implClazzes.size() == 1) {
            var opt = implClazzes.stream().findFirst();

            //Sonarlint complains here for actually no reason, because the else if condition is only met when there exists exactly one implementation class.
            return opt.get().getKey();
        } else  {
            var opt = implClazzes.stream()
                    .filter( e -> e.getKey().getSimpleName().equalsIgnoreCase(
                            (nickname == null || nickname.trim().length() == 0) ? fieldName : nickname))
                    .findAny();

            if (opt.isPresent()) {
                return opt.get().getKey();
            } else {
                throw new ImplementationClassNotFoundException("Found multiple Implementations (" + implClazzes.size() + ") for interface: " + ifaceClazz.getSimpleName(), new Throwable());
            }
        }
    }
}
