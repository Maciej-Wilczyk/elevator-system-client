package rest;

import controller.WindowController;
import dto.DataForPickupDto;
import dto.DataForSelectDto;
import dto.ElevatorSystemConfigDto;
import dto.StatusDto;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.ConnectException;
import java.rmi.ConnectIOException;
import java.util.List;

public class RestImpl implements Rest {


    private final static  String STEP_URL = "http://localhost:8080/step";

    private final static  String STATUS_URL = "http://localhost:8080/status";

    private final static  String SELECT_URL = "http://localhost:8080/select";

    private final static  String PICKUP_URL = "http://localhost:8080/pickup";

    private final static  String SAVE_URL = "http://localhost:8080/save";

    private final static  String NUMBER_OF_ELEVATORS_AND_FLOORS_URL = "http://localhost:8080/number";

    private Thread stepThread, setElevatorSystemConfigThread;

    private final RestTemplate restTemplate;


    public RestImpl() {
        restTemplate = new RestTemplate();
    }

    @Override
    public void step() {
        Runnable stepTask = () -> {
            processStep();
        };
        stepThread = new Thread(stepTask);
        stepThread.setDaemon(true);
        stepThread.start();
    }


    private void processStep() {
        try {
            restTemplate.postForEntity(STEP_URL, null, null);
        } catch (IllegalStateException | ResourceAccessException e){
            serverError();

        }
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
        ResponseEntity<?> responseEntity = null;
        try {
             responseEntity = restTemplate.getForEntity(STATUS_URL, StatusDto[].class);
            restResultHandler.handle(responseEntity);
        }catch (IllegalStateException | ResourceAccessException e ){
            serverError();

        }

    }



    @Override
    public void pickup(List<DataForPickupDto> list, RestResultHandler restResultHandler) {
        Runnable pickupTask = () -> {
            processPickup(list, restResultHandler);
        };
        Thread pickupThread = new Thread(pickupTask);
        pickupThread.setDaemon(true);
        pickupThread.start();
    }

    private void processPickup(List<DataForPickupDto> list, RestResultHandler restResultHandler) {
        try {
            ResponseEntity<?> responseEntity = restTemplate.postForEntity(PICKUP_URL, list, null);
            restResultHandler.handle(responseEntity);
        } catch (IllegalStateException | ResourceAccessException e){
            serverError();
        }
    }

    @Override
    public void select(List<DataForSelectDto> list, RestResultHandler restResultHandler) {
        Runnable selectTask = () -> {
            processSelect(list, restResultHandler);
        };
        Thread selectThread = new Thread(selectTask);
        selectThread.setDaemon(true);
        selectThread.start();
    }

    private void processSelect(List<DataForSelectDto> list, RestResultHandler restResultHandler) {
        try {
            ResponseEntity<?> responseEntity = restTemplate.postForEntity(SELECT_URL, list, null);
            restResultHandler.handle(responseEntity);
        } catch (IllegalStateException | ResourceAccessException e){
            serverError();
        }
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

    private void processSave(boolean save, RestResultHandler restResultHandler) {
        try {
            ResponseEntity<?> responseEntity = restTemplate.postForEntity(SAVE_URL, save, null);restResultHandler.handle(responseEntity);

        } catch (IllegalStateException | ResourceAccessException e) {
         serverError();
        }
    }

    @Override
    public ElevatorSystemConfigDto getNumberOfElevators() {
            return processGetNumberOfElevators();
    }



    private ElevatorSystemConfigDto processGetNumberOfElevators() {
        try {
            ResponseEntity<?> responseEntity = restTemplate.getForEntity(NUMBER_OF_ELEVATORS_AND_FLOORS_URL, ElevatorSystemConfigDto.class);

            return (ElevatorSystemConfigDto) responseEntity.getBody();
        } catch (ResourceAccessException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Connection error");
            alert.setContentText("Please, start server");
            alert.showAndWait();
            System.exit(0);
        }
        return null;
    }

    @Override
    public void setElevatorSystemConfig(ElevatorSystemConfigDto elevatorSystemConfigDto) {
        Runnable setElevatorSystemConfigTask = () -> {
            try {
                ResponseEntity<?> responseEntity = restTemplate.postForEntity(NUMBER_OF_ELEVATORS_AND_FLOORS_URL, elevatorSystemConfigDto, null);
            } catch (IllegalStateException | ResourceAccessException e){
                serverError();
            }
        };
        setElevatorSystemConfigThread = new Thread(setElevatorSystemConfigTask);
        setElevatorSystemConfigThread.setDaemon(true);
        setElevatorSystemConfigThread.start();
    }

    private void serverError() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Server Connection Error");
            alert.setContentText("");
            alert.showAndWait();
        });
    }

}

