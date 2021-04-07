package rest;

import dto.DataForPickupDto;
import dto.DataForSelectDto;
import dto.ElevatorSystemConfigDto;
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

    private static final String SAVE_URL = "http://localhost:8080/save";

    private static final String NUMBER_OF_ELEVATORS_AND_FLOORS_URL = "http://localhost:8080/number";

    private Thread stepThread, setElevatorSystemConfigThread;

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
            if (stepThread != null) {
                stepThread.join();
            }
            if(setElevatorSystemConfigThread != null){
                setElevatorSystemConfigThread.join();
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
            processPickup(dataForPickupDto, restResultHandler);
        };
        Thread pickupThread = new Thread(pickupTask);
        pickupThread.setDaemon(true);
        pickupThread.start();
    }

    private void processPickup(DataForPickupDto dataForPickupDto, RestResultHandler restResultHandler) {
        ResponseEntity<?> responseEntity = restTemplate.postForEntity(PICKUP_URL, dataForPickupDto, null);
        ;
        restResultHandler.handle(responseEntity);
    }

    @Override
    public void select(DataForSelectDto dataForSelectDto, RestResultHandler restResultHandler) {
        Runnable selectTask = () -> {
            processSelect(dataForSelectDto, restResultHandler);
        };
        Thread selectThread = new Thread(selectTask);
        selectThread.setDaemon(true);
        selectThread.start();
    }

    private void processSelect(DataForSelectDto dataForSelectDto, RestResultHandler restResultHandler) {
        ResponseEntity<?> responseEntity = restTemplate.postForEntity(SELECT_URL, dataForSelectDto, null);
        restResultHandler.handle(responseEntity);
    }

    @Override
    public void save(boolean save, RestResultHandler restResultHandler) {
        Runnable selectTask = () -> {
            processSave(save, restResultHandler);
        };
        Thread saveThread = new Thread(selectTask);
        saveThread.setDaemon(true);
        saveThread.start();
    }

    @Override
    public void setNumberOfElevators(int number) {
        ResponseEntity<?> responseEntity = restTemplate.postForEntity(NUMBER_OF_ELEVATORS_AND_FLOORS_URL, number, null);
    }

    private void processSave(boolean save, RestResultHandler restResultHandler) {
        ResponseEntity<?> responseEntity = restTemplate.postForEntity(SAVE_URL, save, null);
        restResultHandler.handle(responseEntity);
    }

    @Override
    public ElevatorSystemConfigDto getNumberOfElevators(RestResultHandler restResultHandler) {
    //    Runnable statusTask = () -> {
            return processGetNumberOfElevators(restResultHandler);
       // };
//        Thread thread = new Thread(statusTask);
//        thread.setDaemon(true);
//        thread.start();
    }


    private ElevatorSystemConfigDto processGetNumberOfElevators(RestResultHandler restResultHandler) {
        ResponseEntity<?> responseEntity = restTemplate.getForEntity(NUMBER_OF_ELEVATORS_AND_FLOORS_URL, ElevatorSystemConfigDto.class);
        restResultHandler.handle(responseEntity);
        return (ElevatorSystemConfigDto)responseEntity.getBody();
    }

    @Override
    public void setElevatorSystemConfig(ElevatorSystemConfigDto elevatorSystemConfigDto) {
        Runnable selectTask = () -> {
            ResponseEntity<?> responseEntity = restTemplate.postForEntity(NUMBER_OF_ELEVATORS_AND_FLOORS_URL, elevatorSystemConfigDto, null);
        };
        setElevatorSystemConfigThread = new Thread(selectTask);
        setElevatorSystemConfigThread.setDaemon(true);
        setElevatorSystemConfigThread.start();
    }


}

