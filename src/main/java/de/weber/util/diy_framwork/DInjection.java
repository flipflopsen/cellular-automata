package de.weber.util.diy_framwork;

import de.weber.util.diy_framwork.annotations.Inject;
import de.weber.util.diy_framwork.annotations.Nickname;
import de.weber.util.diy_framwork.exception.ImplementationClassNotFoundException;
import de.weber.util.class_duties.ClassPredator;
import de.weber.util.loggerino.LoggerFactorino;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public class DInjection {

    private static final LoggerFactorino.Loggerinotho logger = LoggerFactorino.FACTORINO.makeLogger("DInjecton").grabLogger("DInjecton");

    private DInjection() {
        super();
    }

    public static void vessel(DInjector injector, Class<?> clazz, Object instance) throws ImplementationClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Collection<Field> fields = ClassPredator.retrieveAllFieldsWithAnnotationAndMakeAccessible(clazz, Inject.class);

        for (Field field : fields) {
            String nickname;

            if (field.isAnnotationPresent(Nickname.class)) {
                nickname = field.getAnnotation(Nickname.class).val();
            } else {
                nickname = null;
            }

            Object fieldInstance = injector.getBeany(field.getType(), field.getName(), nickname);
            ClassPredator.setFieldValue(instance, field, fieldInstance);

            logger.info("[INJECTING] \"{0}\" -> \"{1}\"", fieldInstance.getClass().getSimpleName(), instance.getClass().getSimpleName());

            vessel(injector, fieldInstance.getClass(), fieldInstance);
        }
    }
}
