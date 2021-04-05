package rest;

import dto.DataForPickupDto;
import dto.DataForSelectDto;
import org.springframework.http.ResponseEntity;

public interface Rest {
    void step(RestResultHandler restResultHandler);

    void status(RestResultHandler restResultHandler);

    void pickup(DataForPickupDto dataForPickupDto, RestResultHandler restResultHandler);

    void select(DataForSelectDto dataForSelectDto, RestResultHandler restResultHandler);
}
