package nl.readablecode.zkspring;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>This annotation is to be used in combination with the {@link PageController} annotation, and indicates
 * for which request path a page handler method in the PageController-annotated class should be called.</p>
 *
 * <p>When this annotation is applied to a PageController-annotated class, its value will be used as a prefix
 * for all the PageMapping-annotated Page handler methods in the class. Note that the annotation does not
 * have to be present on the PageController class itself, it may also be applied on any superclass or on any
 * interface that it (or any of its superclasses) implements.</p>
 *
 * <p>As for the method level, this annotation may be applied directly on a Page handler method itself, or
 * on any method that it overrides or implements, anywhere in the hierarchy of the class.</p>
 *
 * @author florimon
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PageMapping {

    /**
     * <p>The (partial) request path for which this PageMapping applies.
     * Like Spring {@link RequestMapping}, this annotation too supports the use of path variables of the
     * form "{name}", which can then be referred to from {@link PathVariable} annotations on parameters of
     * the Page handler method.</p>
     *
     * @return (partial) request path
     */
    String value() default "";
}
