package rest;

import dto.DataForPickupDto;
import dto.DataForSelectDto;
import dto.ElevatorSystemConfigDto;
import org.springframework.http.ResponseEntity;

public interface Rest {
    void step(RestResultHandler restResultHandler);

    void status(RestResultHandler restResultHandler);

    void pickup(DataForPickupDto dataForPickupDto, RestResultHandler restResultHandler);

    void select(DataForSelectDto dataForSelectDto, RestResultHandler restResultHandler);

    void save(boolean save,RestResultHandler restResultHandler);

    void setNumberOfElevators(int number);

    ElevatorSystemConfigDto getNumberOfElevators(RestResultHandler restResultHandler);

    void setElevatorSystemConfig(ElevatorSystemConfigDto elevatorSystemConfigDto);
}
