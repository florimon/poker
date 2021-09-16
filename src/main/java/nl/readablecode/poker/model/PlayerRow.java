package nl.readablecode.poker.model;

import lombok.Getter;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;

public class PlayerRow {
    @Getter
    private Row row = new Row();

    private Label nameLabel = new Label();
    private Label pointLabel = new Label();

    public PlayerRow(String name) {
        nameLabel.setValue(name);
        nameLabel.setParent(row);
        pointLabel.setParent(row);
    }
}
