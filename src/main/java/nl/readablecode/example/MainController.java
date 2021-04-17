package nl.readablecode.example;

import lombok.extern.slf4j.Slf4j;
import nl.readablecode.zk.ZkPageScope;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import java.util.UUID;

@EnableScheduling
@Slf4j
@Component
@ZkPageScope
public class MainController {

    Window window = new Window("MainController Test", "normal", false);
    Label label = new Label();
    Button button = new Button("Change");

    Desktop desktop;
    String id;

    public void service(Page page) {
        desktop = page.getDesktop();
        enableServerPush(desktop);
        id = page.getDesktop().getId();
        log.info("\nIn MainController, instance = {}, desktop = {}\n", this, id);
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
        log.info("In update");
        Executions.schedule(desktop, event -> label.setValue(id + " " +
                UUID.randomUUID().toString().substring(0, 8)), null);
    }


    private void enableServerPush(Desktop desktop) {
        ((DesktopCtrl) desktop).enableServerPush(new org.zkoss.zk.ui.impl.PollingServerPush(-1,-1,-1));
    }
}
