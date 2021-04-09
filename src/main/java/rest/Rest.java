package rest;

import dto.DataForPickupDto;
import dto.DataForSelectDto;
import dto.ElevatorSystemConfigDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface Rest {

    void step();

    void status(RestResultHandler restResultHandler);

    void pickup(List<DataForPickupDto> list, RestResultHandler restResultHandler);

    void select(List<DataForSelectDto> list, RestResultHandler restResultHandler);

    void save(boolean save,RestResultHandler restResultHandler);

    ElevatorSystemConfigDto getNumberOfElevators();

    void setElevatorSystemConfig(ElevatorSystemConfigDto elevatorSystemConfigDto);
}
