package nl.readablecode.zkspring.scopes;

import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.sys.ExecutionCtrl;

import java.util.Locale;
import java.util.function.Function;

/**
 * Registers ZK specific Spring scopes.
 *
 * @author florimon
 */
public class ZkScopesConfigurer extends CustomScopeConfigurer {
    public static final String WEBAPP    = "webapp";
    public static final String DESKTOP   = "desktop";
    public static final String PAGE      = "page";
    public static final String EXECUTION = "execution";


    public ZkScopesConfigurer() {
        addScope(WEBAPP,    exec -> exec.getDesktop().getWebApp(), WebApp::getAttribute, WebApp::setAttribute, WebApp::getAppName);
        addScope(DESKTOP,   Execution::getDesktop, Desktop::getAttribute, Desktop::setAttribute, Desktop::getId);
        addScope(PAGE,      exec -> ((ExecutionCtrl) exec).getCurrentPage(), Page::getAttribute, Page::setAttribute, Page::getId);
        addScope(EXECUTION, exec -> exec, Execution::getAttribute, Execution::setAttribute, exec -> null);
    }

    private <T> void addScope(String scope,
                              Function<Execution, T> executionFunction,
                              ZkScope.AttributeGetter<T> getter,
                              ZkScope.AttributeSetter<T> setter,
                              Function<T, String> idFunction) {
        addScope(scope, new ZkScope<>(scopeId(scope), Executions::getCurrent, executionFunction, getter, setter, idFunction));
    }

    private String scopeId(String scopeName) {
        return String.format("ZK_SPRING_%s_SCOPE", scopeName.toUpperCase(Locale.ROOT));
    }
}