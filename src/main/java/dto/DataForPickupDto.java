package dto;

import enums.Direction;
import lombok.Data;

@Data
public class DataForPickupDto {
    private int elevatorId;
    private int requestedFloor;
    private Direction direction;

}
