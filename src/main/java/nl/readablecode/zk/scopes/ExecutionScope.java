package nl.readablecode.zk.scopes;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import java.lang.annotation.*;

/**
 * A {@link Scope}-derived meta-annotation to indicate that the annotated bean should be created in/taken from
 * the ZK-specific 'execution' scope.
 *
 * @author florimon
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Scope(value = ZkScopesConfigurer.EXECUTION_SCOPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public @interface ExecutionScope {
}
