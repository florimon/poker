package nl.readablecode.poker.model;

import lombok.Getter;
import nl.readablecode.zkspring.ZkHelper;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static java.util.Optional.ofNullable;
import static nl.readablecode.zkspring.ZkHelper.onClick;
import static nl.readablecode.zkspring.ZkHelper.onEvents;
import static nl.readablecode.zkspring.ZkHelper.serverPush;

public class PlayerSession implements PlayerId {
    @Getter
    private final String id = UUID.randomUUID().toString();

    private final Map<PlayerId, PlayerRow> playerRowsById = new HashMap<>();

    private Page page;
    private TeamSession teamSession;

    // Zk component fields assigned by call to wireView()
    private Label teamNameLabel;
    private Textbox playerNameInput;
    private Textbox storyTitleInput;
    private Div voteButtons;
    private Button showVotesButton;
    private Button clearVotesButton;
    private Rows playerRows;

    public PlayerSession(Page page, TeamSession teamSession, String playerName) {
        this.page = page;
        this.teamSession = teamSession;
        createUiFor(playerName);
    }

    private void createUiFor(String playerName) {
        page.setTitle("Planning Poker");
        ZkHelper.wireView("/zul/poker.zul", this);
        ZkHelper.enableServerPush(page);
        teamNameLabel.setValue(teamSession.getTeamName());
        playerNameInput.setValue(playerName);
        onEvents(playerNameInput, () -> teamSession.changeName(this), Events.ON_CHANGE, Events.ON_OK);
        onEvents(storyTitleInput, () -> teamSession.changeTitle(storyTitleInput.getValue()), Events.ON_CHANGE, Events.ON_OK);
        onClick(showVotesButton, teamSession::showVotes);
        onClick(clearVotesButton, teamSession::clearVotes);
        Arrays.stream(Vote.values()).forEach(this::createVoteButton);
        teamSession.join(this);
    }

    private void createVoteButton(Vote vote) {
        Button voteButton = new Button(vote.getLabel());
        voteButton.setParent(voteButtons);
        onClick(voteButton, () -> {
            playerRowsById.get(this).showVote(vote.getLabel());
            teamSession.vote(this, vote);
        });
    }

    private void setVoteButtonsEnabled(boolean isEnabled) {
        voteButtons.getChildren().forEach(component -> ((Button) component).setDisabled(!isEnabled));
    }

    @Override
    public String getName() {
        return playerNameInput.getValue();
    }

    // no need to do in UI thread, since it's called as a result of PlayerSession calling TeamSession.join()
    public void onJoinedWithVotesHidden(Set<? extends PlayerId> allPlayers, Set<? extends PlayerId> votedPlayers) {
        allPlayers.forEach(this::newPlayerRow);
        votedPlayers.forEach(playerId -> playerRowsById.get(playerId).setVoteHidden(true));
    }

    // no need to do in UI thread, since it's called as a result of PlayerSession calling TeamSession.join()
    public void onJoinedWithVotesShown(Set<? extends PlayerId> allPlayers, Map<? extends PlayerId, Vote> votesByPlayer) {
        setVoteButtonsEnabled(false);
        allPlayers.forEach(this::newPlayerRow);
        votesByPlayer.forEach((playerId, vote) -> playerRowsById.get(playerId).showVote(vote.getLabel()));
    }

    public void onTitleChanged(String title) {
        onUiThread(() -> storyTitleInput.setValue(title));
    }

    public void onPlayerJoined(PlayerId player) {
        onUiThread(() -> newPlayerRow(player));
    }

    private PlayerRow newPlayerRow(PlayerId playerId) {
        return playerRowsById.put(playerId, new PlayerRow(playerId.getName()).attach(playerRows));
    }

    public void onPlayerLeft(PlayerId player) {
        onUiThread(() -> ofNullable(playerRowsById.remove(player)).ifPresent(PlayerRow::detach));
    }

    public void onNameChanged(PlayerId playerId) {
        onUiThread(() -> playerRowsById.get(playerId).setName(playerId.getName()));
    }

    public void onPlayerVoted(PlayerId player) {
        onUiThread(() -> playerRowsById.get(player).setVoteHidden(true));
    }

    public void onVotesCleared() {
        onUiThread(() -> {
            playerRowsById.values().forEach(PlayerRow::clearVote);
            setVoteButtonsEnabled(true);
        });
    }

    public void onVotesShown(Map<? extends PlayerId,Vote> votesByPlayer) {
        onUiThread(() -> {
            setVoteButtonsEnabled(false);
            votesByPlayer.forEach((playerId, vote) -> playerRowsById.get(playerId).showVote(vote.getLabel()));
        });
    }

    private void onUiThread(Runnable runnable) {
        if (isLive()) {
            serverPush(page, runnable);
        } else {
            destroy();
        }
    }

    public boolean isLive() {
        return page != null && page.isAlive();
    }

    void destroy() {
        if (teamSession != null) {
            teamSession.leave(this);
        }
        teamSession = null;
        playerRowsById.clear();
        page = null;
    }
}
