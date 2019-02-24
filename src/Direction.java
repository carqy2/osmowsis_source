/**
 * @author Chang Yang
 */
public enum Direction {

    /**
     * eight directions of the mower could go
     */
    North("North"),
    Northeast("Northeast"),
    East("East"),
    Southeast("Southeast"),
    South("South"),
    Southwest("Southwest"),
    West("West"),
    Northwest("Northwest");


    private String direction;
    Direction(String direction) {
        this.direction = direction;
    }

    public String getDirectionString() {
        return direction;
    }

    public Direction getDirection() {
        return this;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public static Direction getDirectionFromString (String direction) {
        for (Direction dir : Direction.values()) {
            if (dir.getDirectionString().equals(direction)) {
                return dir;
            }
        }
        return null;
    }

    public static Direction[] directions () {
        return new Direction[]{Direction.North, Direction.Northeast, Direction.East,
                Direction.Southeast, Direction.South, Direction.Southwest, Direction.West, Direction.Northwest};
    }
}
