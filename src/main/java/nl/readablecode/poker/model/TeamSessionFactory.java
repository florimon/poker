package nl.readablecode.poker.model;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.synchronizedMap;

/**
 *
 */
@Service
public class TeamSessionFactory {
    private final Map<String, TeamSession> teamSessions = synchronizedMap(new HashMap<>());

    public TeamSession getOrCreateTeamSession(String teamName) {
        return teamSessions.computeIfAbsent(teamName, TeamSession::new);
    }
}
