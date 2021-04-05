package enums;

public enum Direction {
    UP("UP"),
    DOWN("DOWN"),
    STANDING("-");
    String directionAsString;

    Direction(String directionAsString) {
        this.directionAsString = directionAsString;
    }

    public String getDirectionAsString() {
        return directionAsString;
    }
}