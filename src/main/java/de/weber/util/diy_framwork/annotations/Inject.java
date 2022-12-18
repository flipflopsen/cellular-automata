package de.weber.util.diy_framwork.annotations;

import java.lang.annotation.*;

@Target({ ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Inject {

}
