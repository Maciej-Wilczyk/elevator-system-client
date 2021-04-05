package rest;

import dto.DataForPickupDto;
import dto.DataForSelectDto;
import dto.StatusDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class RestImpl implements Rest {

    private static final String STEP_URL = "http://localhost:8080/step";

    private static final String STATUS_URL = "http://localhost:8080/status";

    private static final String SELECT_URL = "http://localhost:8080/select";

    private static final String PICKUP_URL = "http://localhost:8080/pickup";

    private Thread stepThread;

    private final RestTemplate restTemplate;

    public RestImpl() {
        restTemplate = new RestTemplate();
    }

    @Override
    public void step(RestResultHandler restResultHandler) {

        Runnable stepTask = () -> {
            processStep(restResultHandler);
        };
        stepThread = new Thread(stepTask);
        stepThread.setDaemon(true);
        stepThread.start();
    }


    private void processStep(RestResultHandler restResultHandler) {
        ResponseEntity<?> responseEntity = restTemplate.postForEntity(STEP_URL, null, null);
        restResultHandler.handle(responseEntity);

    }

    @Override
    public void status(RestResultHandler restResultHandler) {

        Runnable statusTask = () -> {
            processStatus(restResultHandler);
        };
        Thread statusThread = new Thread(statusTask);
        statusThread.setDaemon(true);
        try {
            if(stepThread != null) {
                stepThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        statusThread.start();
    }


    private void processStatus(RestResultHandler restResultHandler) {
        ResponseEntity<?> responseEntity = restTemplate.getForEntity(STATUS_URL, StatusDto[].class);
        restResultHandler.handle(responseEntity);
    }

    @Override
    public void pickup(DataForPickupDto dataForPickupDto, RestResultHandler restResultHandler) {
        Runnable pickupTask = () -> {
            processPickup(dataForPickupDto,restResultHandler);
        };
        Thread pickupThread = new Thread(pickupTask);
        pickupThread.setDaemon(true);
        pickupThread.start();
    }

    private void processPickup(DataForPickupDto dataForPickupDto, RestResultHandler restResultHandler) {
        ResponseEntity<?> responseEntity = restTemplate.postForEntity(PICKUP_URL, dataForPickupDto, null);;
        restResultHandler.handle(responseEntity);
    }

    @Override
    public void select(DataForSelectDto dataForSelectDto, RestResultHandler restResultHandler) {
        Runnable selectTask = () -> {
            processSelect(dataForSelectDto,restResultHandler);
        };
        Thread selectThread = new Thread(selectTask);
        selectThread.setDaemon(true);
        selectThread.start();
    }

    private void processSelect(DataForSelectDto dataForSelectDto, RestResultHandler restResultHandler){
        ResponseEntity<?> responseEntity = restTemplate.postForEntity(SELECT_URL, dataForSelectDto, null);
        restResultHandler.handle(responseEntity);
    }

}

