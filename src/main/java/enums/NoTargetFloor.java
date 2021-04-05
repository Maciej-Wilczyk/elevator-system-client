package enums;

public enum NoTargetFloor {
    NO_TARGET_FLOOR("-", 0);

    public String noTargetFloorAsString;

    public int noTargetFloorAsInt;

    NoTargetFloor(String noTargetFloorAsString, int noTargetFloorAsInt) {
        this.noTargetFloorAsString = noTargetFloorAsString;
        this.noTargetFloorAsInt = noTargetFloorAsInt;
    }
}
