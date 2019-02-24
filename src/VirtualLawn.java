import java.util.HashMap;

/**
 * @author Chang Yang
 */
public class VirtualLawn {

    /**
     * Each mower contains a virtual lawn, that has an initial size of 100x100.
     * The mower updates this virtual lawn information as it scans and mows the grass.
     */

    private static final int DEFAULT_WIDTH = 20;
    private static final int DEFAULT_HEIGHT = 20;

    private Square[][] virtualLawn;
    private Boundary bound;


    /**
     * update virtual lawn once the mower scans new information
     */
    public Square[][] updateVirtualLawn() {
        int oldWidth = virtualLawn.length;
        int oldHeight = virtualLawn[0].length;
        // new virtual lawn has twice the size
        Square[][] newVirtualLawn = new Square[oldWidth*2][oldHeight*2];

        // update boundary
        if (null != bound) {
            Integer[] bounds = new Integer[4];

            if (null != this.bound.getNorth()) {
                bounds[0] = this.bound.getNorth() + oldHeight/2;
                for (int i = 0; i < newVirtualLawn[0].length; i++) {
                    newVirtualLawn[i][bounds[0]] = Square.FENCE_CODE;
                }
            } else {
                bounds[0] = null;
            }

            if (null != this.bound.getEast()) {
                bounds[1] = this.bound.getEast() + oldWidth/2;
                for (int i = 0; i < newVirtualLawn.length; i++) {
                    newVirtualLawn[bounds[1]][i] = Square.FENCE_CODE;
                }
            } else {
                bounds[1] = null;
            }

            if (null != this.bound.getSouth()) {
                bounds[2] = this.bound.getSouth() + oldHeight/2;
                for (int i = 0; i < newVirtualLawn[0].length; i++) {
                    newVirtualLawn[i][bounds[2]] = Square.FENCE_CODE;
                }
            } else {
                bounds[2] = null;
            }

            if (null != this.bound.getWest()) {
                bounds[3] = this.bound.getWest() + oldWidth/2;
                for (int i = 0; i < newVirtualLawn.length; i++) {
                    newVirtualLawn[bounds[3]][i] = Square.FENCE_CODE;
                }
            } else {
                bounds[3] = null;
            }
//            bounds[0] = null == this.bound.getNorth() ? null : this.bound.getNorth() + oldHeight/2;
//            bounds[1] = null == this.bound.getEast() ? null : this.bound.getEast() + oldWidth/2;
//            bounds[2] = null == this.bound.getSouth() ? null : this.bound.getSouth() + oldHeight/2;
//            bounds[3] = null == this.bound.getWest() ? null : this.bound.getWest() + oldWidth/2;
            this.bound.updateBoundary(bounds);
        }

        // copy the old lawn info to new one and place the old lawn in the middle of the new one
        for (int i = 0; i < virtualLawn.length; i++) {
            for (int j = 0; j < virtualLawn[i].length; j++) {
                if (Square.FENCE_CODE.equals(virtualLawn[i][j])) {
                    continue;
                }
                newVirtualLawn[oldWidth/2+i][oldHeight/2+j] = virtualLawn[i][j];
            }
        }

        this.setVirtualLawn(newVirtualLawn);
        return newVirtualLawn;
    }



    public VirtualLawn () {
        this.virtualLawn = new Square[DEFAULT_WIDTH][DEFAULT_HEIGHT];
        this.bound = new Boundary();
    }

    public VirtualLawn (Integer width, Integer height) {
        this.virtualLawn = new Square[width][height];
    }

    public VirtualLawn (Square[][] squares, Boundary boundary) {
        this.virtualLawn = squares;
        this.bound = boundary;
    }


    public Square[][] getVirtualLawn() {
        return virtualLawn;
    }

    public void setVirtualLawn(Square[][] virtualLawn) {
        this.virtualLawn = virtualLawn;
    }

    public Boundary getBound() {
        return bound;
    }

    public void setBound(Boundary bound) {
        this.bound = bound;
    }
}
