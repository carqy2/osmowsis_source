import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

/**
 * @author Chang Yang
 */
public class SimulationManager {

    private Integer[] mowerPosition = new Integer[2];
    private Direction direction;
    private MowingReport mowingReport = new MowingReport();
    private Square[][] actualLawn;
    private Lawnmower lawnmower;


    /**
     * process the file info
     * @param fileName
     * @return
     */
    public Square[][] readFile (String fileName) {
        final String DELIMITER = ",";

        try {
            Scanner inputFileInfo = new Scanner(new File(fileName));
            String[] tokens;
            int i, j, k;

            // read in the lawn info
            tokens = inputFileInfo.nextLine().split(DELIMITER);
            int lawnWidth = Integer.parseInt(tokens[0]);
            tokens = inputFileInfo.nextLine().split(DELIMITER);
            int lawnHeight = Integer.parseInt(tokens[0]);

            //generate the actual lawn info
            actualLawn = new Square[lawnWidth][lawnHeight];
            //assign the actual lawn with all grass first
            for (i = 0; i < lawnWidth; i++) {
                for (j = 0; j < lawnHeight; j++) {
                    actualLawn[i][j] = Square.GRASS_CODE;
                }
            }

            // read the mower information
            tokens = inputFileInfo.nextLine().split(DELIMITER);
            int mowerNum = Integer.parseInt(tokens[0]);
            lawnmower = new Lawnmower();
//            mowerPosition = new HashMap<>(mowerNum);
//            direction = new HashMap<>(mowerNum);
            int mowerX, mowerY;
            String mowerDirection;

            for (k = 0; k < mowerNum; k++) {
                tokens = inputFileInfo.nextLine().split(DELIMITER);
                mowerX = Integer.parseInt(tokens[0]);
                mowerY = Integer.parseInt(tokens[1]);
                mowerDirection = tokens[2];

                // assign the mower's initial position as cut
                actualLawn[mowerX][mowerY] = Square.CUT_CODE;

                // initialize all mowers info
                Integer[] mowerPos = new Integer[2];
                mowerPos[0] = mowerX;
                mowerPos[1] = mowerY;

                lawnmower = new Lawnmower(mowerDirection, k);
                mowerPosition = mowerPos;
                direction = Direction.getDirectionFromString(mowerDirection);

//                lawnmowers[k] = mower;

                /* TODO the mower's initial position */
            }

            // read crater information
            tokens = inputFileInfo.nextLine().split(DELIMITER);
            int craterNum = Integer.parseInt(tokens[0]);
            for (k = 0; k < craterNum; k++) {
                tokens = inputFileInfo.nextLine().split(DELIMITER);

                int craterX = Integer.parseInt(tokens[0]);
                int craterY = Integer.parseInt(tokens[1]);

                actualLawn[craterX][craterY] = Square.CRATER_CODE;
            }

            // calculate the total number of grass
            int grassNum = lawnWidth * lawnHeight - craterNum;

            // set the total grass number to mower and mowing report
            lawnmower.setGrassSquareNum(grassNum);
            this.mowingReport.setGrassSquareCount(grassNum);
            this.mowingReport.setSquareTotal(lawnWidth * lawnHeight);

            inputFileInfo.close();
            /* TODO exception, IOException? MovementException?*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return actualLawn;
    }


    /**
     * run the simulation
     * @return
     */
    public void runSimulation () {

        String action = this.lawnmower.validateMowerAction();
        int counter = 0;
        while (null != action && counter <= 1000) {
            // get virtual lawn squares
            Square[][] squares = this.lawnmower.getVirtualLawn().getVirtualLawn();

            switch (action) {
                case "scan":
                    System.out.println("scan");
                    String[] scanResult = this.provideLawnScanResult(mowerPosition);
                    StringBuilder s = new StringBuilder();
                    for (int str = 0; str < scanResult.length; str++) {
                        s.append(scanResult[str]).append(",");
                    }
                    s.deleteCharAt(s.length()-1);
                    System.out.println(s.toString());

                    this.lawnmower.scanAndUpdateVirtualLawn(scanResult);
                    action = this.lawnmower.validateMowerAction();
                    counter++;
                    break;

                case "move":
                    boolean okFlag = this.lawnmower.okToMoveTwoSteps(squares, this.lawnmower.getDirection());
                    int step = 0;
                    if (okFlag) {
                        step = 2;
                    } else {
                        step = 1;
                    }
                    StringBuilder s1 = new StringBuilder("move");
                    s1.append(",").append(step).append(",").append(this.lawnmower.getDirection());
                    System.out.println(s1.toString());

                    // simulation checks this intended move
                    boolean ok = this.validToMove(step, this.lawnmower.getDirection());
                    if (ok) {
                        System.out.println("ok");
                    } else {
                        System.out.println("crash");
                        action = "turnoff";
                        break;
                    }

                    this.lawnmower.move(step, this.lawnmower.getDirection());

                    // update the actual lawn state
                    for (int st = 1; st <= step; st++) {
                        HashMap<Direction, Integer[]> map = Utility.orientationAndCoordinates(st);
                        Integer[] integer = map.get(this.lawnmower.getDirection());
                        if (!Square.CUT_CODE.equals(actualLawn[mowerPosition[0]+integer[0]][mowerPosition[1]+integer[1]])) {
                            actualLawn[mowerPosition[0] + integer[0]][mowerPosition[1] + integer[1]] = Square.CUT_CODE;
                            mowingReport.setCutSquareCount(mowingReport.getCutSquareCount() + 1);
                        }
                    }

                    // update the current mower position
                    HashMap<Direction, Integer[]> coordMap = Utility.orientationAndCoordinates(step);
                    Integer[] coord = coordMap.get(this.lawnmower.getDirection());
                    mowerPosition[0] = mowerPosition[0] + coord[0];
                    mowerPosition[1] = mowerPosition[1] + coord[1];

                    this.renderLawn();
                    this.lawnmower.renderLawn();

                    action = this.lawnmower.validateMowerAction();
                    counter++;
                    break;

                case "turnoff":
                    action = null;
                    this.mowingReport = this.displayReport();
                    counter++;
                    this.mowingReport.setTurnCount(counter);
                    System.out.println("turnoff");
                    StringBuilder s2 = new StringBuilder();
                    s2.append(mowingReport.getSquareTotal()).append(",").append(mowingReport.getGrassSquareCount())
                            .append(",").append(mowingReport.getCutSquareCount()).append(",")
                            .append(mowingReport.getTurnCount());
                    System.out.println(s2.toString());
                    this.renderLawn();
                    break;
            }
        }
        if (counter > 1000) {
            System.out.println("Simulation is turned off because it reached the maximum 300 turns");
        }

    }


    /**
     * get the final results displayed
     * @return
     */
    public MowingReport displayReport() {
        mowingReport.setCutSquareCount(this.lawnmower.getCutSquareCount());
        return mowingReport;
    }


    public String[] provideLawnScanResult(Integer[] mowerPos) {
        // get boundary flags
        // horizontal
        boolean hasYBound = actualLawn[0].length <= mowerPos[1]+1;
        // vertical
        boolean hasXBound = actualLawn.length <= mowerPos[0]+1;

        HashMap<Integer, Integer[]> coordMap = Utility.indexAndCoordinates();
        String[] scanResult = new String[8];

        for (int i = 0; i < scanResult.length; i++) {
            Integer[] coord = coordMap.get(i);

            // left bound
            if (mowerPos[0]-1 < 0) {
                if (i==5 || i==6 || i==7) {
                    scanResult[i] = "fence";
                    continue;
                }
            }
            // right bound
            if (hasXBound) {
                if (i==1 || i==2 || i==3) {
                    scanResult[i] = "fence";
                    continue;
                }
            }
            // lower bound
            if (mowerPos[1]-1 < 0) {
                if (i==3 || i==4 || i==5) {
                    scanResult[i] = "fence";
                    continue;
                }
            }
            // upper bound
            if (hasYBound) {
                if (i==0 || i==1 || i==7) {
                    scanResult[i] = "fence";
                    continue;
                }
            }

            scanResult[i] = Square.getStateByCode(actualLawn[mowerPos[0]+coord[0]][mowerPos[1]+coord[1]]);
        }
        return scanResult;
    }


    private boolean validToMove(Integer step, Direction direction) {
        HashMap<Direction, Integer[]> map = Utility.orientationAndCoordinates(step);
        Integer[] coordIncrement = map.get(direction);

        int x = mowerPosition[0] + coordIncrement[0];
        int y = mowerPosition[1] + coordIncrement[1];

        // only if the intended destination contains grass or empty, then ok to move
        if (Square.CUT_CODE.equals(actualLawn[x][y]) || Square.GRASS_CODE.equals(actualLawn[x][y])) {
            return true;
        }
        return false;
    }



    private void renderHorizontalBar(int size) {
        System.out.print(" ");
        for (int k = 0; k < size; k++) {
            System.out.print("-");
        }
        System.out.println("");
    }


    public void renderLawn() {
        int i, j;
        int charWidth = 2 * actualLawn.length + 2;

        // display the rows of the lawn from top to bottom
        for (j = actualLawn[0].length - 1; j >= 0; j--) {
            renderHorizontalBar(charWidth);

            // display the Y-direction identifier
            System.out.print(j);

            // display the contents of each square on this row
            for (i = 0; i < actualLawn.length; i++) {
                System.out.print("|");

                // the mower overrides all other contents
                if (i == mowerPosition[0] & j == mowerPosition[1]) {
                    System.out.print("M");
                } else {
                    switch (actualLawn[i][j]) {
                        case CUT_CODE:
                            System.out.print(" ");
                            break;
                        case GRASS_CODE:
                            System.out.print("g");
                            break;
                        case CRATER_CODE:
                            System.out.print("c");
                            break;
                        default:
                            break;
                    }
                }
            }
            System.out.println("|");
        }
        renderHorizontalBar(charWidth);

        // display the column X-direction identifiers
        System.out.print(" ");
        for (i = 0; i < actualLawn.length; i++) {
            System.out.print(" " + i);
        }
        System.out.println("");

        // display the mower's direction
        System.out.println("dir: " + this.lawnmower.getDirection());
        System.out.println("");
    }

}
