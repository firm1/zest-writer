package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Accesses file systems on box which is running tests.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface MediumTest {}
