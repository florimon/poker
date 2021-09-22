package nl.readablecode.poker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.readablecode.poker.model.PlayerSession;
import nl.readablecode.poker.model.TeamSession;
import nl.readablecode.poker.model.TeamSessionFactory;
import nl.readablecode.zkspring.PageController;
import nl.readablecode.zkspring.PageMapping;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.PathVariable;
import org.zkoss.zk.ui.Page;

@Slf4j
@PageController
@EnableScheduling
@RequiredArgsConstructor
public class PokerController {

    private final TeamSessionFactory teamSessionFactory;

    @PageMapping("/poker/{team}")
    public void service(Page page, @PathVariable("team") String team) {
        service(page, team, "Anonymous");
    }

    @PageMapping("/poker/{team}/{player}")
    public void service(Page page, @PathVariable("team") String teamName, @PathVariable("player") String playerName) {
        TeamSession teamSession = teamSessionFactory.getOrCreateTeamSession(teamName);
        new PlayerSession(page, teamSession, playerName);
        log.info("{} joined team session {}", playerName, teamName);
    }
}
