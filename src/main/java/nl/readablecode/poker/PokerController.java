package nl.readablecode.poker;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import nl.readablecode.poker.model.PlayerRow;
import nl.readablecode.poker.model.PlayerSession;
import nl.readablecode.poker.model.TeamSession;
import nl.readablecode.poker.model.TeamSessionFactory;
import nl.readablecode.zkspring.PageController;
import nl.readablecode.zkspring.PageMapping;
import nl.readablecode.zkspring.ZkHelper;
import nl.readablecode.zkspring.scopes.PageScope;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PathVariable;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.Collection;
import java.util.UUID;

import static java.lang.String.format;

@Slf4j
//@PageScope
@PageController
@EnableScheduling
@RequiredArgsConstructor
public class PokerController implements DisposableBean, BeanNameAware {
    private final ConfigurableBeanFactory configurableBeanFactory;

    private final TeamSessionFactory teamSessionFactory;

    @Setter
    private String beanName;

    private Page page;

    @PageMapping("/poker/{team}")
    public void service(Page page, @PathVariable("team") String team) {
        service(page, team, "unknown");
    }

    @PageMapping("/poker/{team}/{player}")
    public void service(Page page, @PathVariable("team") String teamName, @PathVariable("player") String playerName) {
        page.setTitle("Planning Poker");
        TeamSession teamSession = teamSessionFactory.getOrCreateTeamSession(teamName);
        new PlayerSession(page, teamSession, playerName);
//        this.page = page;
    }

//    @Scheduled(fixedRate = 2000)
//    public void update() {
//        log.info("update is called, bean name is {}", beanName);
//        if (page != null) {
//            log.info("Page is {}alive", page.isAlive() ? "" : "not ");
////            Executions.schedule(page.getDesktop(), event -> estimate.setLabel(UUID.randomUUID().toString()), null);
//            if (!page.isAlive()) {
//                page = null;
//                configurableBeanFactory.destroyBean(beanName, this);
//            }
//        }
//    }

    public void destroy() {
        log.info("destroy() is called");
    }
}
