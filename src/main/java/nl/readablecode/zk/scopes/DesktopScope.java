package nl.readablecode.zk.scopes;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import java.lang.annotation.*;

/**
 *
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Scope(value = ZkScopesConfigurer.DESKTOP_SCOPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public @interface DesktopScope {
}
