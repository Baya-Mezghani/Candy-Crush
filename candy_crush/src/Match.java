// Match.java
import java.util.List;

public class Match {
    public final List<Position> positions;
    public final CandyType resultType;     // NORMAL, STRIPED_…, EXPLOSIVE, COLOR_BOMB
    public final Position specialPosition; // where to put the new special (or null)
    public final CandyColor color;         // the matched color

    public Match(List<Position> positions,
                 CandyType resultType,
                 Position specialPosition,
                 CandyColor color) {
        this.positions       = positions;
        this.resultType      = resultType;
        this.specialPosition = specialPosition;
        this.color           = color;
    }
    public boolean isSpecial() {
        return resultType != CandyType.NORMAL;
    }
}
