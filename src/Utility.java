import java.util.HashMap;

/**
 * @author Chang Yang
 */
public class Utility {

    public static HashMap<Integer, Integer[]> indexAndCoordinates() {
        HashMap<Integer, Integer[]> map = new HashMap<>(8);
        map.put(0, new Integer[]{0,1});
        map.put(1, new Integer[]{1,1});
        map.put(2, new Integer[]{1,0});
        map.put(3, new Integer[]{1,-1});
        map.put(4, new Integer[]{0,-1});
        map.put(5, new Integer[]{-1,-1});
        map.put(6, new Integer[]{-1,0});
        map.put(7, new Integer[]{-1,1});
        return map;
    }

    public static HashMap<Direction, Integer[]> orientationAndCoordinates(Integer steps) {
        HashMap<Direction, Integer[]> map = new HashMap<>(8);
        map.put(Direction.North, new Integer[]{0,steps});
        map.put(Direction.Northeast, new Integer[]{steps,steps});
        map.put(Direction.East, new Integer[]{steps,0});
        map.put(Direction.Southeast, new Integer[]{steps,-steps});
        map.put(Direction.South, new Integer[]{0,-steps});
        map.put(Direction.Southwest, new Integer[]{-steps,-steps});
        map.put(Direction.West, new Integer[]{-steps,0});
        map.put(Direction.Northwest, new Integer[]{-steps,steps});
        return map;
    }


    public static HashMap<Direction, Integer[]> orientationAndCoordinates() {
        HashMap<Direction, Integer[]> map = new HashMap<>(8);
        map.put(Direction.North, new Integer[]{0,1});
        map.put(Direction.Northeast, new Integer[]{1,1});
        map.put(Direction.East, new Integer[]{1,0});
        map.put(Direction.Southeast, new Integer[]{1,-1});
        map.put(Direction.South, new Integer[]{0,-1});
        map.put(Direction.Southwest, new Integer[]{-1,-1});
        map.put(Direction.West, new Integer[]{-1,0});
        map.put(Direction.Northwest, new Integer[]{-1,1});
        return map;
    }


    public static Direction getTargetLocation(int x, int y) {
        // north
        if (x == 0 && y > 0) {
            return Direction.North;
        }
        // northeast
        if (x > 0 && y > 0) {
            return Direction.Northeast;
        }
        // east
        if (x > 0 && y == 0) {
            return Direction.East;
        }
        // southeast
        if (x > 0 && y < 0) {
            return Direction.Southeast;
        }
        // south
        if (x == 0 && y < 0) {
            return Direction.South;
        }
        // southwest
        if (x < 0 && y < 0) {
            return Direction.Southwest;
        }
        // west
        if (x < 0 && y == 0) {
            return Direction.West;
        }
        // northwest
        if (x < 0 && y > 0) {
            return Direction.Northwest;
        }
        return null;
    }
}
