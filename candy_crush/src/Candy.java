public class Candy {
    private CandyColor color;
    private CandyType type;

    public Candy(CandyColor color) {
        this.color = color;
        this.type  = CandyType.NORMAL;
    }

    public Candy(CandyColor color, CandyType type) {
        this.color = color;
        this.type  = type;
    }

    public CandyColor getColor() { return color; }
    public CandyType getType()    { return type; }

    public boolean isSpecial() {
        return type != CandyType.NORMAL;
    }

    @Override
    public String toString() {
        // First letter of the color
        char c = color.name().charAt(0);
        switch (type) {
            case NORMAL:             return "" + c;
            case STRIPED_HORIZONTAL: return c + "-";
            case STRIPED_VERTICAL:   return c + "|";
            case EXPLOSIVE:          return c + "o";
            case COLOR_BOMB:         return c + "*";
            default:                 return "" + c;
        }
    }
}
