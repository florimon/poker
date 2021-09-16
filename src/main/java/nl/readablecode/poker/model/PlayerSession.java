package nl.readablecode.poker.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nl.readablecode.zkspring.ZkHelper;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Rows;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class PlayerSession implements PlayerId {
    private final String id = UUID.randomUUID().toString();
    private final Map<PlayerId, PlayerRow> playersById = new HashMap<>();

    private Page page;
    private TeamSession teamSession;
    private String name;

    private Label teamId;
    private Rows players;
    private Button estimate;

    public PlayerSession(Page page, TeamSession teamSession, String name) {
        this.page = page;
        this.teamSession = teamSession;
        this.name = name;
        ZkHelper.wireView("/zul/poker.zul", this);
        ZkHelper.enableServerPush(page);
        teamId.setValue(teamSession.getTeamName());
        teamSession.join(this);
    }

    public void onSelfJoined(Collection<? extends PlayerId> players) {
        players.forEach(this::newPlayer);
    }

    private void newPlayer(PlayerId playerId) {
        PlayerRow playerRow = new PlayerRow(playerId.getName());
        playersById.put(playerId, playerRow);
        playerRow.getRow().setParent(players);
    }

    public void onPlayerJoined(PlayerId player) {
        onUiThread(() -> newPlayer(player));
    }

    public void onPlayerLeft(PlayerId player) {
        onUiThread(() -> {
            players.removeChild(playersById.get(player).getRow());
            playersById.remove(player);
        });
    }

    public void onPlayerVoted(PlayerId player, Vote vote) {

    }

    public void onVotesCleared() {

    }

    public void onVotesShown() {

    }

    private void onUiThread(Runnable runnable) {
        if (page.isAlive()) {
            Executions.schedule(page.getDesktop(), event -> runnable.run(), null);
        } else {
            teamSession.leave(this);
            teamSession = null;
            playersById.clear();
            page = null;
        }
    }
}
