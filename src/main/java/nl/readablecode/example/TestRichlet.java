package nl.readablecode.example;

import lombok.extern.slf4j.Slf4j;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.GenericRichlet;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

@Slf4j
public class TestRichlet extends GenericRichlet {

    private HelloService helloService = SpringUtil.getBean("helloService");

    public void service(Page page) {

        page.setTitle("Richlet Test");
        log.info("Requestpath = {}", page.getRequestPath());
        Executions.getCurrent().getParameterMap().forEach((string, object) -> log.info("{} = {}", string, object));

        log.info("\nIn TestRichlet, instance = {}, desktop = {}\n", this, page.getDesktop().getId());

        final Window w = new Window("Richlet Test", "normal", false);
        new Label("Hello World!").setParent(w);
        final Label l = new Label();
        l.setParent(w);

        final Button b = new Button("Change");
        b.addEventListener(Events.ON_CLICK,
                new EventListener() {
                    int count;
                    public void onEvent(Event evt) {
                        l.setValue(helloService.sayHello() + ++count);
//                        l.setValue("" + ++count);
                    }
                });
        b.setParent(w);

        w.setPage(page);
    }
}