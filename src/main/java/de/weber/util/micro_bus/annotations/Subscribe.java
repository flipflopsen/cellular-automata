package de.weber.util.micro_bus.annotations;

import java.lang.annotation.*;


@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Subscribe {
    String to() default "";
}
