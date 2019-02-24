/**
 * @author Chang Yang
 */
public enum Square {

    CUT_CODE(0),
    GRASS_CODE(1),
    CRATER_CODE(2),
    FENCE_CODE(3),
    MOWER_CODE(4),
    UNINITIALIZED_CODE(-1);

    private Integer square;
    Square(Integer s) {
        this.square = s;
    }


    public Integer getSquare() {
        return square;
    }

    public void setSquare(Integer s) {
        this.square = s;
    }

    public static String getStateByCode(Square s) {
        switch (s) {
            case CUT_CODE:
                return "empty";
            case GRASS_CODE:
                return "grass";
            case FENCE_CODE:
                return "fence";
            case CRATER_CODE:
                return "crater";
            case MOWER_CODE:
            case UNINITIALIZED_CODE:
                return "";
        }
        return "";
    }


    public static Square setSquareByState(String state) {
        switch (state) {
            case "empty":
                return Square.CUT_CODE;
            case "grass":
                return Square.GRASS_CODE;
            case "fence":
                return Square.FENCE_CODE;
            case "crater":
                return Square.CRATER_CODE;
            default:
                return null;
        }
    }

}
