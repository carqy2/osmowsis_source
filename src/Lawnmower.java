import java.util.*;

/**
 * @author Chang Yang
 */
public class Lawnmower {

    private Direction direction;
    private VirtualLawn virtualLawn;
    private Integer[] mowerPosVirtual = new Integer[]{10,10};
    private Integer cutSquareCount = 0;
    private Integer grassSquareNum = 0;
    private Integer turnCount = 0;
    private Integer mowerNumber;
    // to store all the grass coordinates(key)
    private Set<List<Integer>> grassLocations;
    // to give the mower a target location
    private List<Integer> target = new ArrayList<>(2);


    /**
     * mower moves
     * @param steps
     * @param direction
     */
    public void move(Integer steps, Direction direction) {
        Square[][] squares = virtualLawn.getVirtualLawn();

        // update square state
        for (int step = 0; step < steps; step++) {
            HashMap<Direction, Integer[]> coordMap = Utility.orientationAndCoordinates(1);
            Integer[] coordIncrement = coordMap.get(direction);

            mowerPosVirtual[0] = mowerPosVirtual[0] + coordIncrement[0];
            mowerPosVirtual[1] = mowerPosVirtual[1] + coordIncrement[1];
            if (!Square.CUT_CODE.equals(squares[mowerPosVirtual[0]][mowerPosVirtual[1]])) {
                squares[mowerPosVirtual[0]][mowerPosVirtual[1]] = Square.CUT_CODE;
                List<Integer> temp = new ArrayList<>(2);
                temp.add(0, mowerPosVirtual[0]);
                temp.add(1, mowerPosVirtual[1]);
                boolean i = this.grassLocations.remove(temp);
                this.cutSquareCount++;

                // if the mower reaches a target, clear this target for next use
                if (target.size()>0 && target.get(0).equals(mowerPosVirtual[0])
                        && target.get(1).equals(mowerPosVirtual[1])) {
                    target = new ArrayList<>(2);
                }
            }
        }
    }


    /**
     * mower scans
     * @param scanResult
     */
    public void scanAndUpdateVirtualLawn (String[] scanResult) {
        Square[][] squares = virtualLawn.getVirtualLawn();



        // assign scanned states to the squares in virtual lawn
        HashMap<Integer, Integer[]> coordinateMap = Utility.indexAndCoordinates();
        Integer[] scanFence = new Integer[8];
        for (int i = 0; i < scanResult.length; i++) {
            // get boundary numbers: flag it with 1 if that index contains fence
            if ("fence".equals(scanResult[i])) {
                scanFence[i] = 1;
            }

            Integer[] coord = coordinateMap.get(i);
            int x = mowerPosVirtual[0]+coord[0];
            int y = mowerPosVirtual[1]+coord[1];
            // if the virtual lawn doesn't have enough space, expand
            if (squares.length <= x || x < 0 || squares[0].length <= y || y < 0) {
                // update mower position & grass location map & target
                this.updateCoordinateAfterExpansion(squares);

                // expand the current square dimension
                squares = virtualLawn.updateVirtualLawn();

                // update the new coordinate after expansion
                x = mowerPosVirtual[0]+coord[0];
                y = mowerPosVirtual[1]+coord[1];
            }


            // update if the square state is null, else it's been initiated
            if (null == squares[x][y]) {
                squares[x][y] = Square.setSquareByState(scanResult[i]);
                // if scanned grass, update grass count
                if ("grass".equals(scanResult[i])) {
                    List<Integer> temp = new ArrayList<>(2);
                    temp.add(0, x);
                    temp.add(1, y);
                    this.grassLocations.add(temp);
                    this.grassSquareNum = grassSquareNum++;
                }
            }
        }
        /**
         * index 0,2,4,6 are sufficient enough to determine the north, east, south, west bound
         */
        Boundary bound = this.virtualLawn.getBound();
        // north bound
        if (null != scanFence[0] && 1 == scanFence[0]) {
            // north of current mower position, update that x-axis (horizontal line) into fence
            for (int k = 0; k < squares.length; k++) {
                squares[k][mowerPosVirtual[1]+1] = Square.FENCE_CODE;

            }
            // set boundary
            bound.setBoundary(mowerPosVirtual[1]+1, Direction.North);
        }

        // east bound
        if (null != scanFence[2] && 1 == scanFence[2]) {
            // east of current mower position, update that y-axis (vertical line) into fence
            for (int k = 0; k < squares[mowerPosVirtual[0]+1].length; k++) {
                squares[mowerPosVirtual[0]+1][k] = Square.FENCE_CODE;
            }
            // set boundary
            bound.setBoundary(mowerPosVirtual[0]+1, Direction.East);
        }

        // south bound
        if (null != scanFence[4] && 1 == scanFence[4]) {
            // south of current mower position, update that x-axis (horizontal line) into fence
            for (int k = 0; k < squares[mowerPosVirtual[0]-1].length; k++) {
                squares[k][mowerPosVirtual[1]-1] = Square.FENCE_CODE;
            }
            // set boundary
            bound.setBoundary(mowerPosVirtual[1]-1, Direction.South);
        }

        // west bound
        if (null != scanFence[6] && 1 == scanFence[6]) {
            // west of current mower position, update that y-axis (vertical line) into fence
            for (int k = 0; k < squares.length; k++) {
                squares[mowerPosVirtual[0]-1][k] = Square.FENCE_CODE;
            }
            // set boundary
            bound.setBoundary(mowerPosVirtual[0]-1, Direction.West);
        }

    }


    /**
     * get the next valid movement
     * @return
     */
    public String validateMowerAction () {
        Square[][] squares = virtualLawn.getVirtualLawn();

        if (null == this.direction) {
            this.direction = Direction.North;
        }

        // if boundary is set
        Boundary bound = virtualLawn.getBound();
        boolean hasBoundary = null != bound && null != bound.getEast() && null != bound.getNorth() && null != bound.getWest()
                && null != bound.getSouth();
        // if grass are cut, boundary are set, then turn off
        if (this.grassSquareNum.equals(this.cutSquareCount) && hasBoundary) {
            return "turnoff";
        }


        // if north of current mower position is not empty, determine next action else scan
        if (squares.length <= mowerPosVirtual[0]+1 || mowerPosVirtual[0]-1 < 0 ||
                squares[0].length <= mowerPosVirtual[1]+1 || mowerPosVirtual[1]-1 < 0) {
            return "scan";
        }

        // get the current mower position & direction's coordinate increment
        HashMap<Direction, Integer[]> coordMap = Utility.orientationAndCoordinates();
        Integer[] coordIncr = coordMap.get(this.direction);

        // add the coord increments to have the new attempt position
        int xCoordinate = mowerPosVirtual[0] + coordIncr[0];
        int yCoordinate = mowerPosVirtual[1] + coordIncr[1];

        // if ahead of mower position is not initialized, then scan
        if (null == squares[xCoordinate][yCoordinate]) {
            return "scan";
        }

        Direction newDirection = this.direction;
        for (int i = 0; i < 7; i++) {
            newDirection = this.newDirection(newDirection);
            Integer[] newCoord = coordMap.get(newDirection);

            // if one of the surrounding squares is not initialized
            if (null == squares[mowerPosVirtual[0]+newCoord[0]][mowerPosVirtual[1]+newCoord[1]]) {
                return "scan";
            }
        }


        // if ahead of mower position is initialized and contains grass, then move
        if (Square.GRASS_CODE.equals(squares[xCoordinate][yCoordinate])) {
            return "move";
        }

        // ahead of mower position is initialized and contain crater, then change direction
        if (Square.CRATER_CODE.equals(squares[xCoordinate][yCoordinate]) ||
                Square.FENCE_CODE.equals(squares[xCoordinate][yCoordinate])
                || Square.CUT_CODE.equals(squares[xCoordinate][yCoordinate])
                ) {
            HashMap<Direction, Integer[]> map = Utility.orientationAndCoordinates();
            newDirection = this.direction;
            for (int i = 0; i < 7; i++) {
                newDirection = this.newDirection(newDirection);
                Integer[] newCoord = map.get(newDirection);

                // if the square contains grass in new direction
                if (Square.GRASS_CODE.equals(
                        squares[mowerPosVirtual[0]+newCoord[0]][mowerPosVirtual[1]+newCoord[1]])) {
                    this.direction = newDirection;
                    return "move";
                }

                // if one of the surrounding squares is not initialized
                if (null == squares[mowerPosVirtual[0]+newCoord[0]][mowerPosVirtual[1]+newCoord[1]]) {
                    return "scan";
                }

                // didn't find any surrounding grass
                if (i == 6) {
                    return this.setTarget();
                }
            }
        }

        // if any cases that were not catched above, should turn off mower
        return "turnoff";
    }


    /**
     * turn off the mower
     * @param mowerPower
     */
    public void turnOff (Boolean mowerPower) {

    }


    public boolean okToMoveTwoSteps(Square[][] squares, Direction direction) {
        // one step is ok, test for 2 steps
        // get coordinate increment map
        HashMap<Direction, Integer[]> map = Utility.orientationAndCoordinates(2);
        // get coordinate increment
        Integer[] coordIncrement = map.get(direction);
        // if square at that coordinate contains grass
        int x = mowerPosVirtual[0] + coordIncrement[0];
        int y = mowerPosVirtual[1] + coordIncrement[1];
        if (x >= squares.length || x < 0 || y >= squares[0].length || y < 0) {
            return false;
        }
        if (Square.GRASS_CODE.equals(squares[x][y]) || Square.CUT_CODE.equals(squares[x][y])) {
            return true;
        }
        return false;
    }



    private String setTarget () {
        // if the mower doesn't have a target yet
        if (target.size()==0) {
            target = grassLocations.iterator().next();
        }

        int x = target.get(0) - mowerPosVirtual[0];
        int y = target.get(1) - mowerPosVirtual[1];
        this.direction = Utility.getTargetLocation(x, y);

        System.out.println("target is:"+target.get(0)+","+target.get(1));

        Square[][] squares = this.virtualLawn.getVirtualLawn();
        Direction newDirection = this.direction;
        HashMap<Direction, Integer[]> coordMap = Utility.orientationAndCoordinates(1);
        Integer[] coord = coordMap.get(this.direction);

        // if that direction has fence/crater, change direction
        if (Square.CRATER_CODE.equals(squares[mowerPosVirtual[0]+coord[0]][mowerPosVirtual[1]+coord[1]])
            || Square.FENCE_CODE.equals(squares[mowerPosVirtual[0]+coord[0]][mowerPosVirtual[1]+coord[1]])) {
            HashMap<Direction, Integer[]> map = Utility.orientationAndCoordinates();
            for (int i = 0; i < 8; i++) {
                Integer[] newCoord = map.get(newDirection);
                // if the square contains grass or empty in new direction
                if (Square.GRASS_CODE.equals(squares[mowerPosVirtual[0]+newCoord[0]][mowerPosVirtual[1]+newCoord[1]])
                        || Square.CUT_CODE.equals(
                                squares[mowerPosVirtual[0]+newCoord[0]][mowerPosVirtual[1]+newCoord[1]])) {
                    this.direction = newDirection;
                    return "move";
                }
                newDirection = this.newDirection(newDirection);
            }
        }

        return "move";
    }


    private void updateCoordinateAfterExpansion(Square[][] squares) {
        int oldWidth = squares.length;
        int oldHeight = squares[0].length;

        // update the current mower position
        mowerPosVirtual[0] = mowerPosVirtual[0] + oldWidth/2;
        mowerPosVirtual[1] = mowerPosVirtual[1] + oldHeight/2;

        // update grass location map
        Iterator<List<Integer>> it = this.grassLocations.iterator();
        Set<List<Integer>> newGrassLocations = new HashSet<>(grassLocations.size());
        while (it.hasNext()) {
            List<Integer> oldCoord = it.next();
            List<Integer> temp = new ArrayList<>(2);
            temp.add(0, oldCoord.get(0)+oldWidth/2);
            temp.add(1, oldCoord.get(1)+oldHeight/2);
            newGrassLocations.add(temp);
        }

        // update the target
        if (target.size() > 0) {
            target.set(0, target.get(0)+oldWidth/2);
            target.set(1, target.get(1)+oldHeight/2);
        }
        this.grassLocations = newGrassLocations;
    }


    /**
     * determine the next direction
     * @param currentDir
     * @return
     */
    public Direction newDirection (Direction currentDir) {
        Direction nextDir;
        switch (currentDir) {
            case North:
                nextDir = Direction.Northeast;
                break;
            case Northeast:
                nextDir = Direction.East;
                break;
            case East:
                nextDir  = Direction.Southeast;
                break;
            case Southeast:
                nextDir = Direction.South;
                break;
            case South:
                nextDir = Direction.Southwest;
                break;
            case Southwest:
                nextDir = Direction.West;
                break;
            case West:
                nextDir = Direction.Northwest;
                break;
            case Northwest:
                nextDir = Direction.North;
                break;
            default:
                nextDir = currentDir;
                break;
        }
        return nextDir;
    }


    private void renderHorizontalBar(int size) {
        System.out.print(" ");
        for (int k = 0; k < size; k++) {
            System.out.print("-");
        }
        System.out.println("");
    }


    public void renderLawn() {
        Square[][] squares = this.virtualLawn.getVirtualLawn();
        int i, j;
        int charWidth = 2 * squares.length + 2;

        // display the rows of the lawn from top to bottom
        for (j = squares[0].length - 1; j >= 0; j--) {
            renderHorizontalBar(charWidth);

            // display the Y-direction identifier
            System.out.print(j);

            // display the contents of each square on this row
            for (i = 0; i < squares.length; i++) {
                System.out.print("|");

                // the mower overrides all other contents
                if (i == mowerPosVirtual[0] & j == mowerPosVirtual[1]) {
                    System.out.print("M");
                } else {
                    if (null == squares[i][j]) {
                        System.out.print("x");
                    } else {
                        switch (squares[i][j]) {
                            case CUT_CODE:
                                System.out.print(" ");
                                break;
                            case GRASS_CODE:
                                System.out.print("g");
                                break;
                            case CRATER_CODE:
                                System.out.print("c");
                                break;
                            case FENCE_CODE:
                                System.out.print("f");
                            default:
                                break;
                        }
                    }
                }
            }
            System.out.println("|");
        }
        renderHorizontalBar(charWidth);

        // display the column X-direction identifiers
        System.out.print(" ");
        for (i = 0; i < squares.length; i++) {
            System.out.print(" " + i);
        }
        System.out.println("");

        // display the mower's direction
        System.out.println("dir: " + this.direction);
        System.out.println("");
    }



    public Lawnmower() {}

    public Lawnmower (String direction, Integer num) {
        this.direction = Direction.getDirectionFromString(direction);
        this.virtualLawn = new VirtualLawn();
        this.cutSquareCount = 1;
        this.turnCount = 0;
        this.grassSquareNum = 0;
        this.mowerNumber = num;
        this.grassLocations = new HashSet<>();
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public VirtualLawn getVirtualLawn() {
        return virtualLawn;
    }

    public void setVirtualLawn(VirtualLawn virtualLawn) {
        this.virtualLawn = virtualLawn;
    }

    public Integer[] getMowerPosVirtual() {
        return mowerPosVirtual;
    }

    public void setMowerPosVirtual(Integer[] mowerPosVirtual) {
        this.mowerPosVirtual = mowerPosVirtual;
    }

    public Integer getCutSquareCount() {
        return cutSquareCount;
    }

    public void setCutSquareCount(Integer cutSquareCount) {
        this.cutSquareCount = cutSquareCount;
    }

    public Integer getGrassSquareNum() {
        return grassSquareNum;
    }

    public void setGrassSquareNum(Integer grassSquareNum) {
        this.grassSquareNum = grassSquareNum;
    }

    public Integer getTurnCount() {
        return turnCount;
    }

    public void setTurnCount(Integer turnCount) {
        this.turnCount = turnCount;
    }

    public Integer getMowerNumber() {
        return mowerNumber;
    }

    public void setMowerNumber(Integer mowerNumber) {
        this.mowerNumber = mowerNumber;
    }

    public Set<List<Integer>> getGrassLocations() {
        return grassLocations;
    }

    public void setGrassLocations(Set<List<Integer>> grassLocations) {
        this.grassLocations = grassLocations;
    }

    public List<Integer> getTarget() {
        return target;
    }

    public void setTarget(List<Integer> target) {
        this.target = target;
    }
}
