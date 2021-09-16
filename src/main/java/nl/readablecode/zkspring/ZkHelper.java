package nl.readablecode.zkspring;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.impl.PollingServerPush;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.util.ConventionWires;

import java.util.Map;

import static java.util.Collections.emptyMap;

public class ZkHelper {
    private static final String ZUL_PREFIX = "~.";

    public static Component wireView(String zulFilename, Object target) {
        return wireView(zulFilename, target, null, emptyMap());
    }

    public static Component wireView(String zulFilename, Object target, Component root) {
        return wireView(zulFilename, target, root, emptyMap());
    }

    public static Component wireView(String zulFilename, Object target, Map<?,?> parameters) {
        return wireView(zulFilename, target, null, parameters);
    }

    public static Component wireView(String zulFilename, Object target, Component root, Map<?, ?> parameters) {
        Component component = Executions.createComponents(ZUL_PREFIX + zulFilename, root, parameters);
        ConventionWires.wireVariables(component, target);
        return component;
    }

    public static void enableServerPush(Page page) {
        ((DesktopCtrl) page.getDesktop()).enableServerPush(new PollingServerPush(-1,-1,-1));
    }
}
