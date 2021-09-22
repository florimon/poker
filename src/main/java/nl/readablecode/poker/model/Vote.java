package nl.readablecode.poker.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.resource.FixedVersionStrategy;

@Getter
@RequiredArgsConstructor
public enum Vote {

    ZERO        ("0"),
    HALF        ("1/2"),
    ONE         ("1"),
    TWO         ("2"),
    THREE       ("3"),
    FIVE        ("5"),
    EIGHT       ("8"),
    THIRTEEN    ("13"),
    TWENTY      ("20"),
    UNKNOWN     ("?");

    private final String label;
}
