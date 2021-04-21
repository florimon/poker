package nl.readablecode.zk;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Scope(value = ZkScopesConfigurer.WEBAPP_SCOPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public @interface ZkWebAppScope {
}