package controller;

import dto.DataForPickupDto;
import dto.DataForSelectDto;
import dto.ElevatorSystemConfigDto;
import dto.StatusDto;
import enums.Direction;
import enums.NoTargetFloor;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.springframework.stereotype.Controller;
import rest.Rest;
import rest.RestImpl;

import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
public class WindowController implements Initializable {

    private Rest rest;

    private int numberOfFloors = 10, numberOfElevators = 0;

    @FXML
    private VBox vBox;

    public static boolean realTimeFlag = false;

    private Button stepButton, confirmButton;

    private ToggleGroup group;

    private  RadioButton stepByStep, realTime;

    public WindowController() {
        rest = new RestImpl();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
      //  localOrAWSHost();
        if (!isElevatorSystemConfigured()) {
            restartSettings();
        } else {
            setAmountOfElevatorsAndFloors();
            setStatus();
        }
        live();
    }


    public void live() {
        Thread t = new Thread(() -> {
            while (realTimeFlag) {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                makeStep();
            }
        });
        t.setDaemon(true);
        t.start();

    }

    public boolean isElevatorSystemConfigured() {
        var res = rest.getNumberOfElevators();
        if (res.getNumberOfElevators() == -1) {
            return false;
        } else {
            numberOfElevators = res.getNumberOfElevators();
            numberOfFloors = res.getNumberOfFloors();
            return true;
        }
    }

    private void setStatus() {
        rest.status((result) -> {
            Platform.runLater(() -> {
               // if (result.)
                HBox hBox;
                List<StatusDto> list = Arrays.asList((StatusDto[]) result.getBody());
                for (int i = 0; i < list.size(); i++) {
                    hBox = (HBox) vBox.getChildren().get(i + 1);
                    setLabels(hBox, list, i);
                }
            });
        });
    }

           private void setAmountOfElevatorsAndFloors() {
            HBox hBox;
            for (int i = 0; i < numberOfElevators; i++) {
                hBox = new HBox();
                hBox.setSpacing(25);
                hBox.setAlignment(Pos.CENTER);
                Label elevatorIdLabel = new Label(String.valueOf(i));
                elevatorIdLabel.getStyleClass().add("label-2");
                Label currentFloorLabel = new Label("1");
                currentFloorLabel.getStyleClass().add("label-2");
                Label directionLabel = new Label("-");
                directionLabel.getStyleClass().add("label-2");
                Label targetFloorLabel = new Label("-");
                targetFloorLabel.getStyleClass().add("label-2");
                ChoiceBox<Integer> selectFloorChoiceBox = new ChoiceBox<>();
                selectFloorChoiceBox.setItems(numberOfFloorsList(numberOfFloors));
                selectFloorChoiceBox.getStyleClass().add("choice-box-1");
                ChoiceBox<Integer> pickupFloorChoiceBox = new ChoiceBox<>();
                pickupFloorChoiceBox.setItems(numberOfFloorsList(numberOfFloors));
                pickupFloorChoiceBox.getStyleClass().add("choice-box-1");
                ChoiceBox<Direction> pickupFloorButtonChoiceBox = new ChoiceBox<>();
                pickupFloorButtonChoiceBox.getItems().addAll(Direction.UP, Direction.DOWN);
                pickupFloorButtonChoiceBox.getStyleClass().add("choice-box-1");
                hBox.getChildren().addAll(elevatorIdLabel, currentFloorLabel, directionLabel, targetFloorLabel, selectFloorChoiceBox,  pickupFloorChoiceBox, pickupFloorButtonChoiceBox);
                vBox.getChildren().add(hBox);
            }
            hBox = new HBox();
            hBox.setSpacing(25);
            hBox.setAlignment(Pos.CENTER);
            hBox.setMinHeight(15);
            vBox.getChildren().add(hBox);
            hBox = new HBox();
            hBox.setSpacing(25);
            hBox.setAlignment(Pos.CENTER);
            stepButton = new Button("Step");
            stepButton.getStyleClass().add("button-1");
            stepButton.setOnMouseClicked(e -> {
                makeStep();
            });
            Button restartSettings = new Button("Restart Settings");
            restartSettings.getStyleClass().add("button-1");
            restartSettings.setOnMouseClicked(e -> {
                restartSettings();
            });
            group = new ToggleGroup();

            stepByStep = new RadioButton("Step by Step");
            stepByStep.setToggleGroup(group);
            stepByStep.setSelected(true);
            stepByStep.getStyleClass().add("radio-button");
            realTime = new RadioButton("Real time");
            realTime.setToggleGroup(group);
            realTime.getStyleClass().add("radio-button");

            group.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) -> {
                if (stepByStep == group.getSelectedToggle()) {
                    realTimeFlag = false;
                    stepButton.setDisable(false);
                }
                else if (realTime == group.getSelectedToggle()) {
                    stepButton.setDisable(true);
                    realTimeFlag = true;
                    live();
                }
            });
            confirmButton = new Button("Confirm Data");
            confirmButton.getStyleClass().add("button-1");
            confirmButton.setOnMouseClicked(e -> {selectEvent(); pickupEvent(); }
            );
            hBox.getChildren().addAll(stepButton,restartSettings,stepByStep,realTime, confirmButton);
            vBox.getChildren().add(hBox);


    }

    private ObservableList<Integer> numberOfFloorsList(int numberOfFloors) {
        ObservableList<Integer> list = FXCollections.observableArrayList();
        for (int i = 1; i <= numberOfFloors; i++) {
            list.add(i);
        }
        return list;
    }


    private void setLabels(HBox hBox, List<StatusDto> list, int i) {
        Label elevatorIdLabel, currentFloorLabel, directionLabel, nearestTargetFloorLabel;
        elevatorIdLabel = ((Label) hBox.getChildren().get(0));
        elevatorIdLabel.setText(String.valueOf(list.get(i).getElevatorId()));
        currentFloorLabel = ((Label) hBox.getChildren().get(1));
        currentFloorLabel.setText(String.valueOf(list.get(i).getCurrentFloor()));
        directionLabel = ((Label) hBox.getChildren().get(2));
        directionLabel.setText(String.valueOf(list.get(i).getDirection().getDirectionAsString()));
        nearestTargetFloorLabel = (Label) hBox.getChildren().get(3);
        if (list.get(i).getNearestTargetFloor() == NoTargetFloor.NO_TARGET_FLOOR.noTargetFloorAsInt) {
            nearestTargetFloorLabel.setText(NoTargetFloor.NO_TARGET_FLOOR.noTargetFloorAsString);
        } else {
            nearestTargetFloorLabel.setText(String.valueOf(list.get(i).getNearestTargetFloor()));
        }
        if (list.get(i).isIfReachedTargetFloor()) {
            currentFloorLabel.setTextFill(Color.GREEN);
            nearestTargetFloorLabel.setTextFill(Color.GREEN);
        } else {
            currentFloorLabel.setTextFill(Color.BLACK);
            nearestTargetFloorLabel.setTextFill(Color.BLACK);
        }
    }

    public void makeStep() {
        rest.step();
        setStatus();
    }

    public void restartSettings() {
        ElevatorSystemConfigDto elevatorSystemConfigDto = new ElevatorSystemConfigDto();
        int resultElevators = getNumberOfElevatorsDialogResult();
        int resultFloors = getNumberOfFloorsDialogResult();
        elevatorSystemConfigDto.setNumberOfElevators(resultElevators);
        elevatorSystemConfigDto.setNumberOfFloors(resultFloors);
        numberOfElevators = resultElevators;
        rest.setElevatorSystemConfig(elevatorSystemConfigDto);
        int size = vBox.getChildren().size();
        for (int i = 1; i < size; i++) {
            vBox.getChildren().remove(1);
        }
        setAmountOfElevatorsAndFloors();
        setStatus();
    }

    public int getNumberOfElevatorsDialogResult() {
        List<Integer> choices = new ArrayList<>();
        for (int i = 1; i <= 16; i++) {
            choices.add(i);
        }
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(1, choices);
        dialog.setTitle("");
        dialog.setHeaderText("");
        dialog.setContentText("Please enter number of elevator:");
        dialog.setGraphic(null);
        Optional<Integer> result = dialog.showAndWait();
        return result.orElseThrow();
    }

    public int getNumberOfFloorsDialogResult() {
        TextInputDialog dialog = new TextInputDialog("10");
        dialog.setTitle("");
        dialog.setHeaderText("");
        dialog.setContentText("Please enter number of floors:");
        dialog.setGraphic(null);
        Optional<String> result = dialog.showAndWait();
        numberOfFloors = Integer.parseInt(result.orElseThrow());
        return numberOfFloors;
    }

    public void selectEvent() {
        List<DataForSelectDto> list = new ArrayList<>();
        DataForSelectDto dataForSelectDto;
        for (Node i : vBox.getChildren()) {
            if (skipIsFirstHBox(vBox, i)) {
                continue;
            }
            HBox hBox = (HBox) i;

            if (isCorrectHbox(hBox)) {
                ChoiceBox<Integer> selectFloorChoiceBox = getSelectFloorChoiceBox(hBox);
                if (selectFloorChoiceBox.getValue() == null) {
                    continue;
                }
                dataForSelectDto = new DataForSelectDto();
                dataForSelectDto.setElevatorId(getElevatorId(hBox));
                dataForSelectDto.setSelectedFloor(selectFloorChoiceBox.getValue());
                selectFloorChoiceBox.setValue(null);
                list.add(dataForSelectDto);
            } else {
                break;
            }
        }
        if(!list.isEmpty()) {
            rest.select(list, (result) -> {
            });
        }
    }

    private boolean isCorrectHbox(HBox hBox) {
        return hBox.getChildren().size() == 7;
    }

    public void pickupEvent() {
        List<DataForPickupDto> list = new ArrayList<>();
        DataForPickupDto dataForPickupDto;
        for (Node i : vBox.getChildren()) {
            if (skipIsFirstHBox(vBox, i)) {
                continue;
            }
            HBox hBox = (HBox) i;

            if (isCorrectHbox(hBox)) {
                    ChoiceBox<Integer> pickupFloorChoiceBox = getPickupFloorChoiceBox(hBox);
                    ChoiceBox<Direction> pickupButtonChoiceBox = getPickupButtonChoiceBox(hBox);
                    if (pickupFloorChoiceBox.getValue() == null || pickupButtonChoiceBox.getValue() == null) {
                        continue;
                    }
                    dataForPickupDto = new DataForPickupDto();
                    dataForPickupDto.setElevatorId(getElevatorId(hBox));
                    dataForPickupDto.setRequestedFloor(pickupFloorChoiceBox.getValue());
                    dataForPickupDto.setDirection(pickupButtonChoiceBox.getValue());
                    pickupFloorChoiceBox.setValue(null);
                    pickupButtonChoiceBox.setValue(null);
                    list.add(dataForPickupDto);
            } else {
                break;
            }
        }
        if(!list.isEmpty()) {
            rest.pickup(list, (result) -> {
            });
        }
    }

    private ChoiceBox<Integer> getPickupFloorChoiceBox(HBox hBox) {
        return (ChoiceBox<Integer>) hBox.getChildren().get(5);
    }

    private ChoiceBox<Direction> getPickupButtonChoiceBox(HBox hBox) {
        return (ChoiceBox<Direction>) hBox.getChildren().get(6);
    }

    private int getElevatorId(HBox hBox) {
        return Integer.parseInt(((Label) hBox.getChildren().get(0)).getText());
    }


    private boolean skipIsFirstHBox(VBox vBox, Node node) {
        if (vBox.getChildren().indexOf(node) == 0) {
            return true;
        }
        return false;
    }


    private ChoiceBox getSelectFloorChoiceBox(HBox hBox) {
        return (ChoiceBox) hBox.getChildren().get(4);
    }

//    private void localOrAWSHost(){
//        List<String> choices = new ArrayList<>();
//            choices.add("LOCAL");
//            choices.add("AWS");
//        ChoiceDialog<String> dialog = new ChoiceDialog<>("AWS", choices);
//        dialog.setHeaderText("");
//        dialog.setTitle("");
//        dialog.setContentText("Please choose host:");
//        dialog.setGraphic(null);
//        Optional<String> result = dialog.showAndWait();
//        if(result.orElseThrow().equals("LOCAL")) {
//            rest.setLocalAwsHost("http://localhost:8080");
//        } else {
//            RestImpl.LOCAL_AWS_HOST = "aws";
//        }
//    }

}
