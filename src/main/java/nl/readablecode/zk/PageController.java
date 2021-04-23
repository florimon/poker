package nl.readablecode.zk;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import org.zkoss.zk.ui.Page;

import java.lang.annotation.*;

/**
 * Indicates that an annotated class is a ZK {@link Page} 'controller', i.e. a Spring bean
 * that has one or more handler methods that can service a Page instance, analogous to
 * the Spring {@link RestController} for REST requests.
 * The handler methods are to be annotated with the {@link PageMapping} annotation.
 *
 * @author florimon
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface PageController {

    /**
     * The value may indicate a suggestion for a logical component name,
     * to be turned into a Spring bean in case of an autodetected component.
     * @return the suggested component name, if any (or empty String otherwise)
     */
    @AliasFor(annotation = Component.class)
    String value() default "";
}
