package nl.readablecode.poker.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

import static java.util.Collections.synchronizedSet;

@RequiredArgsConstructor
public class TeamSession {
    private final Set<PlayerSession> players = synchronizedSet(new LinkedHashSet<>());

    @Getter
    private final String teamName;


    public void join(PlayerSession player) {
        player.onSelfJoined(players);
        players.add(player);
        notifyAll(recipient -> recipient.onPlayerJoined(player));
    }

    public void leave(PlayerSession player) {
        players.remove(player);
        notifyAll(recipient -> recipient.onPlayerLeft(player));
    }

    public void vote(PlayerSession player, Vote vote) {
        notifyAll(recipient -> recipient.onPlayerVoted(player, vote));
    }

    public void clearVotes() {
        notifyAll(PlayerSession::onVotesCleared);
    }

    public void showVotes() {
        notifyAll(PlayerSession::onVotesShown);
    }

    private void notifyAll(Consumer<PlayerSession> notification) {
        players.stream().forEach(notification);
    }
}
