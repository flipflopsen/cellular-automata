package de.weber.util.class_duties;

import de.weber.util.loggerino.LoggerFactorino;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClassPredator {

    private static final List<URL> foundClasses = new CopyOnWriteArrayList<>();
    private static final List<String> foundClassNames = new CopyOnWriteArrayList<>();
    private static final List<Class<?>> classInstancesAnnotated = new CopyOnWriteArrayList<>();
    private static final List<Class<?>> classInstances = new CopyOnWriteArrayList<>();
    private static final LoggerFactorino.Loggerinotho logger = LoggerFactorino.FACTORINO.makeLogger("ClassPredator").grabLogger("ClassPredator");


    private ClassPredator() {
        //Default private constructor
    }

    public static Collection<Field> retrieveAllFieldsWithAnnotationAndMakeAccessible(Class<?> target, Class<? extends Annotation> annotationClazz) {
        Collection<Field> fields = new java.util.ArrayList<>(Collections.emptyList());

        for (Field field : target.getFields()) {
            logger.info("Found \"{0}\"-Annotated Field: \"{1}\" in \"{2}\"...", annotationClazz.getSimpleName(), field.getName(), target.getSimpleName());
            if (field.isAnnotationPresent(annotationClazz)) {
                field.setAccessible(true);
                fields.add(field);
            }
        }
        return fields;
    }

    public static void setFieldValue(Object target, Field field, Object value) throws IllegalAccessException {
        field.setAccessible(true);
        field.set(target, value);
    }

    public static Collection<Class<?>> retrieveLoadedAnnotatedClasses() {
        return classInstancesAnnotated;
    }

    public static Collection<Class<?>> retrieveClasses() {
        return classInstances;
    }

    public static void scanCompleteClasspath(String packageName) throws IOException {
        var workingDir = System.getProperty("user.dir");
        var classpath = workingDir + "\\target\\classes\\" + packageName + "\\";
        var start = Instant.now();
        try (var files = Files.find(Paths.get(classpath), 500, (p, bfa) -> bfa.isRegularFile())) {
            files.forEach(filePath -> {
                try {
                    if (String.valueOf(filePath.getFileName()).contains(".class")) {
                        var fileStr = String.valueOf(filePath);

                        var pkg = packageName.replace("/", "\\");
                        var fullQualifiedPath = String.valueOf(filePath).substring(fileStr.indexOf(pkg));

                        fullQualifiedPath = fullQualifiedPath.split("\\.")[0];
                        fullQualifiedPath = fullQualifiedPath.replace("\\", ".");

                        foundClassNames.add(fullQualifiedPath);
                        foundClasses.add(new URL( "file:///" + fileStr));
                    }
                } catch (MalformedURLException e) {
                    logger.error("Failed to parse the path to the classpath correctly...");
                }
            });
        }
        var end = Instant.now();
        logger.debug("[ClassPredator] It took {0}ms for scanning the classpath.", Duration.between(start, end).toMillis());
    }

    public static void loadAnnotatedClasses(Class<? extends Annotation> annotation) throws IOException {
        try (URLClassLoader loader = new URLClassLoader(foundClasses.toArray(new URL[0]))) {
            foundClassNames.forEach(file -> {
                try {
                    var clazz = loader.loadClass(file);
                    if (clazz.isAnnotationPresent(annotation)) {
                        classInstancesAnnotated.add(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public static void loadClasses() throws IOException {
        try (URLClassLoader loader = new URLClassLoader(foundClasses.toArray(new URL[0]))) {
            foundClassNames.forEach(file -> {
                try {
                    var clazz = loader.loadClass(file);
                    classInstances.add(clazz);
                } catch (ClassNotFoundException e) {
                    logger.error("Failed to load class: {0} -> weren't able to find it.", file);
                }
            });
        }
    }

    public static Optional<Class<?>> getClassByName(String name) {
        return classInstances.stream()
                .filter(c -> c.getSimpleName().contains(name))
                .findFirst();
    }

    public static List<Class<?>> getClassesInDirectory(String dir) {
        return classInstances.stream()
                .filter(c -> c.getName().contains(dir))
                .toList();
    }
}
