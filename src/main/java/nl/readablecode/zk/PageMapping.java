package nl.readablecode.zk;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 *
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PageMapping {

    @AliasFor("path")
    String value() default "";

    @AliasFor("value")
    String path() default "";
}
