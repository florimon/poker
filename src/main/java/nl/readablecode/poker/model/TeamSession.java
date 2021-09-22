package nl.readablecode.poker.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.function.Consumer;

import static java.util.Collections.*;

@RequiredArgsConstructor
public class TeamSession {
    private final Set<PlayerSession> players = synchronizedSet(new LinkedHashSet<>());
    private final Map<PlayerSession, Vote> votesByPlayer = synchronizedMap(new LinkedHashMap<>());

    private boolean areVotesShown;

    @Getter
    private final String teamName;


    public void join(PlayerSession player) {
        players.add(player);
        if (areVotesShown) {
            player.onJoinedWithVotesShown(players, votesByPlayer);
        } else {
            player.onJoinedWithVotesHidden(players, votesByPlayer.keySet());
        }
        notifyAllExcept(player, recipient -> recipient.onPlayerJoined(player));
    }

    public void leave(PlayerSession player) {
        players.remove(player);
        notifyAll(recipient -> recipient.onPlayerLeft(player));
    }

    public void changeName(PlayerSession player) {
        notifyAll(recipient -> recipient.onNameChanged(player));
    }

    public void changeTitle(String title) {
        notifyAll(recipient -> recipient.onTitleChanged(title));
    }


    public void vote(PlayerSession player, Vote vote) {
        votesByPlayer.put(player, vote);
        notifyAllExcept(player, recipient -> recipient.onPlayerVoted(player));
    }

    public void clearVotes() {
        votesByPlayer.clear();
        notifyAll(PlayerSession::onVotesCleared);
        areVotesShown = false;
    }

    public void showVotes() {
        notifyAll(playerSession -> playerSession.onVotesShown(votesByPlayer));
        areVotesShown = true;
    }


    private void notifyAll(Consumer<PlayerSession> notification) {
        new HashSet<>(players).forEach(notification);
    }

    private void notifyAllExcept(PlayerSession originator, Consumer<PlayerSession> notification) {
        new HashSet<>(players).stream().filter(player -> player != originator).forEach(notification);
    }

    public void checkLiveness() {
        players.forEach(PlayerSession::checkLiveness);
    }
}
