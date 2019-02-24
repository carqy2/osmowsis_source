/**
 * @author Chang Yang
 */
public class Boundary {

    private Integer north;
    private Integer east;
    private Integer south;
    private Integer west;

    /**
     * when mower scans a fence, then set the boundary
     * @param num
     * @param direction
     */
    public void setBoundary (Integer num, Direction direction) {
        switch (direction) {
            case North:
                this.setNorth(num);
                break;
            case East:
                this.setEast(num);
                break;
            case South:
                this.setSouth(num);
                break;
            case West:
                this.setWest(num);
                break;
        }
    }


    /**
     * update boundary
     * @param coord
     */
    public void updateBoundary (Integer[] coord) {
        this.setNorth(coord[0]);
        this.setEast(coord[1]);
        this.setSouth(coord[2]);
        this.setWest(coord[3]);
    }



    public Integer getNorth() {
        return north;
    }

    public void setNorth(Integer north) {
        this.north = north;
    }

    public Integer getEast() {
        return east;
    }

    public void setEast(Integer east) {
        this.east = east;
    }

    public Integer getSouth() {
        return south;
    }

    public void setSouth(Integer south) {
        this.south = south;
    }

    public Integer getWest() {
        return west;
    }

    public void setWest(Integer west) {
        this.west = west;
    }
}
