public class Candy {
    private int color; // 0 to 5 (or use enum for color too)
    private CandyType type;

    public Candy(int color) {
        this.color = color;
        this.type = CandyType.NORMAL;
    }

    public int getColor() {
        return color;
    }

    public CandyType getType() {
        return type;
    }

    public void setType(CandyType type) {
        this.type = type;
    }

    public boolean isSpecial() {
        return type != CandyType.NORMAL;
    }
}
