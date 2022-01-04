package nl.readablecode.poker.model;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.synchronizedMap;

/**
 *
 */
@Service
@EnableScheduling
public class TeamSessionFactory {
    private final Map<String, TeamSession> teamSessions = synchronizedMap(new HashMap<>());

    public TeamSession getOrCreateTeamSession(String teamName) {
        return teamSessions.computeIfAbsent(teamName, TeamSession::new);
    }

    @Scheduled(fixedRate = 2000)
    public void checkLiveness() {
        teamSessions.values().forEach(TeamSession::removeAbandonedPlayerSessions);
    }
}
