package nl.readablecode.zk.scopes;

import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.sys.ExecutionCtrl;

/**
 * Registers ZK specific Spring scopes.
 *
 * @author florimon
 */
public class ZkScopesConfigurer extends CustomScopeConfigurer {
    public static final String WEBAPP_SCOPE     = "webapp";
    public static final String DESKTOP_SCOPE    = "desktop";
    public static final String PAGE_SCOPE       = "page";
    public static final String EXECUTION_SCOPE  = "execution";

    public ZkScopesConfigurer() {
        addScope(WEBAPP_SCOPE, new ZkScope<>("ZK_SPRING_APP_SCOPE",
                exec -> exec.getDesktop().getWebApp(), WebApp::getAttribute, WebApp::setAttribute, WebApp::getAppName));

        addScope(DESKTOP_SCOPE, new ZkScope<>("ZK_SPRING_DESKTOP_SCOPE",
                Execution::getDesktop, Desktop::getAttribute, Desktop::setAttribute, Desktop::getId));

        addScope(PAGE_SCOPE, new ZkScope<>("ZK_SPRING_PAGE_SCOPE",
                exec -> ((ExecutionCtrl) exec).getCurrentPage(), Page::getAttribute, Page::setAttribute, Page::getId));

        addScope(EXECUTION_SCOPE, new ZkScope<>("ZK_SPRING_EXEC_SCOPE",
                exec -> exec, Execution::getAttribute, Execution::setAttribute, exec -> null));
    }
}