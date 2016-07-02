package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This test doesn't interact with any file system or network.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface SmallTest {}
