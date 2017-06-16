package c2w.hla;

import java.lang.annotation.*;

/**
 * Annotation to indicate a parameter of a federate config.
 */
@Documented
@Target(ElementType.FIELD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface FederateParameter  {}
