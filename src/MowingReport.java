/**
 * @author Chang Yang
 */
public class MowingReport {

    private Integer cutSquareCount = 0;
    private Integer grassSquareCount;
    private Integer turnCount = 0;
    private Integer squareTotal;
    private Direction direction;


    /**
     * update the number of square of grass cut when mower moves
     * @param step
     */
    public void updateGrassCut(Integer step) {
        this.cutSquareCount = cutSquareCount + step;
    }


    /**
     * update number of turns as mower takes turns
     */
    public void updateTurnCount() {
        this.turnCount++;
    }


    public Integer getCutSquareCount() {
        return cutSquareCount;
    }

    public void setCutSquareCount(Integer cutSquareCount) {
        this.cutSquareCount = cutSquareCount;
    }

    public Integer getGrassSquareCount() {
        return grassSquareCount;
    }

    public void setGrassSquareCount(Integer grassSquareCount) {
        this.grassSquareCount = grassSquareCount;
    }

    public Integer getTurnCount() {
        return turnCount;
    }

    public void setTurnCount(Integer turnCount) {
        this.turnCount = turnCount;
    }

    public Integer getSquareTotal() {
        return squareTotal;
    }

    public void setSquareTotal(Integer squareTotal) {
        this.squareTotal = squareTotal;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
