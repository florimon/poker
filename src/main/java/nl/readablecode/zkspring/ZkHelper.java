package nl.readablecode.zkspring;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.impl.PollingServerPush;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.util.ConventionWires;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ZkHelper {
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

    public static void serverPush(Page page, Runnable runnable) {
        Executions.schedule(page.getDesktop(), event -> runnable.run(), null);
    }

    public static boolean onClick(Component component, EventListener<? extends Event> listener) {
        return component.addEventListener(Events.ON_CLICK, listener);
    }

    public static boolean onClick(Component component, Runnable runnable) {
        return component.addEventListener(Events.ON_CLICK, event -> runnable.run());
    }

    public static boolean onChange(Component component, EventListener<? extends Event> listener) {
        return component.addEventListener(Events.ON_CHANGE, listener);
    }

    public static boolean onChange(Component component, Runnable runnable) {
        return component.addEventListener(Events.ON_CHANGE, event -> runnable.run());
    }

    public static void onEvents(Component component, Runnable runnable, String... eventNames) {
        onEvents(component, event -> runnable.run(), eventNames);
    }

    public static void onEvents(Component component, EventListener<? extends Event> listener, String... eventNames) {
        Arrays.stream(eventNames).forEach(eventName -> component.addEventListener(eventName, listener));
    }

    public static boolean onAll(Component component, Runnable runnable) {
        return onAll(component, event -> runnable.run());
    }

    public static boolean onAll(Component component, EventListener<? extends Event> listener) {
        getAllEventNames().stream().filter(eventName -> !eventName.startsWith("onMouse"))
                        .forEach(eventName -> addOrIgnoreEventListener(component, eventName, listener));
        return true;
    }

    public static boolean addOrIgnoreEventListener(Component component, String eventName, EventListener<? extends Event> listener) {
        try {
            return component.addEventListener(eventName, listener);
        } catch (IllegalArgumentException e) {
            log.error("Failed to add listener for event '{}'", eventName);
            return false;
        }
    }

    public static List<String> getAllEventNames() {
        List<String> eventNames = new ArrayList<>();
        ReflectionUtils.doWithFields(Events.class,
                field -> eventNames.add((String) ReflectionUtils.getField(field, null)),
                field -> field.getName().startsWith("ON_"));
        return eventNames;
    }
}
