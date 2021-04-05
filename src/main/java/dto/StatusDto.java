package dto;

import enums.Direction;
import lombok.Data;

@Data
public class StatusDto {

    private int elevatorId;
    private int currentFloor;
    private int nearestTargetFloor;
    private Direction direction;

    public StatusDto() {
    }

    public StatusDto(int elevatorId, int currentFloor, int nearestTargetFloor, Direction direction) {
        this.elevatorId = elevatorId;
        this.currentFloor = currentFloor;
        this.nearestTargetFloor = nearestTargetFloor;
        this.direction = direction;
    }
}