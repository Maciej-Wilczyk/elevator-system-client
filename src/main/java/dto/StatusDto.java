package dto;

import enums.Direction;
import lombok.Data;

@Data
public class StatusDto {

    private int elevatorId;
    private int currentFloor;
    private int nearestTargetFloor;
    private Direction direction;
    private boolean ifReachedTargetFloor;

    public StatusDto() {
    }

}