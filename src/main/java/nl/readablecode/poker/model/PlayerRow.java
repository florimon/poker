package nl.readablecode.poker.model;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;

class PlayerRow {
    private static final String CSS_CLASS_VOTE = "vote";
    private static final String CSS_CLASS_HIDDEN = "hidden";

    private Row row = new Row();
    private Label nameLabel = new Label();
    private Label voteLabel = new Label();

    PlayerRow(String name) {
        nameLabel.setValue(name);
        nameLabel.setParent(row);
        voteLabel.setParent(row);
        voteLabel.setSclass(CSS_CLASS_VOTE);
    }

    PlayerRow attach(Component parent) {
        row.setParent(parent);
        return this;
    }

    PlayerRow detach() {
        return attach(null);
    }

    void setName(String name) {
        nameLabel.setValue(name);
    }

    void clearVote() {
        showVote("");
    }

    void showVote(String vote) {
        voteLabel.setValue(vote);
        setVoteHidden(false);
    }

    void setVoteHidden(boolean isVoteHidden) {
        if (isVoteHidden) {
            voteLabel.addSclass(CSS_CLASS_HIDDEN);
        } else {
            voteLabel.removeSclass(CSS_CLASS_HIDDEN);
        }
    }
}
