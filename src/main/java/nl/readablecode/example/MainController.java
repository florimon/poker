package nl.readablecode.example;

import lombok.extern.slf4j.Slf4j;
import nl.readablecode.zk.PageController;
import nl.readablecode.zk.PageMapping;
import nl.readablecode.zk.scopes.PageScope;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PathVariable;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import java.util.UUID;

@Slf4j
@PageScope
@PageController
@EnableScheduling
public class MainController {

    Window window = new Window("MainController Test", "normal", false);
    Label label = new Label();
    Button button = new Button("Change");

    Desktop desktop;
    String id;

    @PageMapping("/test/{name}")
    public void service(Page page, @PathVariable("name") String name) {
        log.info("Name = " + name);
        Executions.getCurrent().getParameterMap().forEach((string, object) -> log.info("{} = {}", string, object));
        desktop = page.getDesktop();
        enableServerPush(desktop);
        id = page.getDesktop().getId();
        page.setTitle("MainController Test");
        new Label("Hello World!").setParent(window);
        label.setParent(window);
        button.addEventListener(Events.ON_CLICK, event ->
                label.setValue(UUID.randomUUID().toString().substring(0, 8)));
        button.setParent(window);
        window.setPage(page);
    }

    @Scheduled(fixedRate = 2000)
    public void update() {
        if (desktop != null) {
            Executions.schedule(desktop, event -> label.setValue(id + " " +
                    UUID.randomUUID().toString().substring(0, 8)), null);
        }
    }

    private void enableServerPush(Desktop desktop) {
        ((DesktopCtrl) desktop).enableServerPush(new org.zkoss.zk.ui.impl.PollingServerPush(-1,-1,-1));
    }
}
