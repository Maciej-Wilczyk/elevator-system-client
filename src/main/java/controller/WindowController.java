package controller;

import dto.DataForPickupDto;
import dto.DataForSelectDto;
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
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rest.Rest;
import rest.RestImpl;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class WindowController implements Initializable {

    private Rest rest;

    @FXML
    VBox vBox;

    @FXML
    AnchorPane anchorPane;

    public WindowController() {
        rest = new RestImpl();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setAmountOfElevatorsAndFloors();
        setStatus();
        selectEvent();
        pickupEvent();
    }

    private void setStatus() {
        rest.status((result) -> {
            Platform.runLater(() -> {
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
        for (int i = 0; i < 16; i++) {
            hBox = new HBox();
            hBox.setSpacing(25);
            hBox.setAlignment(Pos.CENTER);
            Label elevatorIdLabel = new Label(String.valueOf(i));
            elevatorIdLabel.getStyleClass().add("label-2");
            Label currentFloorLabel= new Label("1");
            currentFloorLabel.getStyleClass().add("label-2");
            Label directionLabel = new Label("-");
            directionLabel.getStyleClass().add("label-2");
            Label targetFloorLabel = new Label("-");
            targetFloorLabel.getStyleClass().add("label-2");
            ChoiceBox<Integer> selectFloorChoiceBox = new ChoiceBox<>();
            selectFloorChoiceBox.setItems(numberOfFloorsList(10));
            selectFloorChoiceBox.getStyleClass().add("choice-box-1");
            Button selectConfirmButton = new Button("Confirm");
            selectConfirmButton.getStyleClass().add("button-1");
            ChoiceBox<Integer> pickupFloorChoiceBox = new ChoiceBox<>();
            pickupFloorChoiceBox.setItems(numberOfFloorsList(10));
            pickupFloorChoiceBox.getStyleClass().add("choice-box-1");
            ChoiceBox<Direction> pickupFloorButtonChoiceBox = new ChoiceBox<>();
            pickupFloorButtonChoiceBox.getItems().addAll(Direction.values());
            pickupFloorButtonChoiceBox.getStyleClass().add("choice-box-1");
            Button pickupConfirmButton = new Button("Confirm");
            pickupConfirmButton.getStyleClass().add("button-1");
            hBox.getChildren().addAll(elevatorIdLabel,currentFloorLabel,directionLabel,targetFloorLabel,selectFloorChoiceBox,selectConfirmButton,pickupFloorChoiceBox,pickupFloorButtonChoiceBox,pickupConfirmButton);
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
        Button stepButton = new Button("Step");
        stepButton.getStyleClass().add("button-1");
        stepButton.setOnMouseClicked( e ->{
            makeStep();
        });
        hBox.getChildren().add(stepButton);
        vBox.getChildren().add(hBox);
    }

    private ObservableList<Integer> numberOfFloorsList(int numberOfFloors){
        ObservableList<Integer> list = FXCollections.observableArrayList();
        for(int i = 1; i <= numberOfFloors; i ++){
            list.add(i);
        }
        return list;
    }


    private void setLabels(HBox hBox, List<StatusDto> list, int i) {
        Label label;
        ((Label) hBox.getChildren().get(0)).setText(String.valueOf(list.get(i).getElevatorId()));
        ((Label) hBox.getChildren().get(1)).setText(String.valueOf(list.get(i).getCurrentFloor()));
        ((Label) hBox.getChildren().get(2)).setText(String.valueOf(list.get(i).getDirection().getDirectionAsString()));
        label = (Label) hBox.getChildren().get(3);
        if (list.get(i).getNearestTargetFloor() == NoTargetFloor.NO_TARGET_FLOOR.noTargetFloorAsInt) {
            label.setText(NoTargetFloor.NO_TARGET_FLOOR.noTargetFloorAsString);
        } else {
            label.setText(String.valueOf(list.get(i).getNearestTargetFloor()));
        }
    }

    public void makeStep() {
        rest.step((result) -> {
        });
        setStatus();

    }


    public void selectEvent() {
        for (Node i : vBox.getChildren()) {
            if (skipIsFirstHBox(vBox, i)) {
                continue;
            }
            vBox.getChildren().indexOf(i);
            HBox hBox = (HBox) i;
            if (isCorrectHbox(hBox)) {
                getSelectConfirmButton(hBox).setOnMouseClicked(e -> {
                    ChoiceBox<Integer> choiceBox = getSelectFloorChoiceBox(hBox);
                    if (choiceBox.getValue() == null) {
                        return;
                    }
                    DataForSelectDto dataForSelectDto = new DataForSelectDto();
                    dataForSelectDto.setElevatorId(getElevatorId(hBox));
                    dataForSelectDto.setSelectedFloor(choiceBox.getValue());
                    choiceBox.setValue(null);
                    rest.select(dataForSelectDto, (result) -> {
                    });

                });
            }
        }
    }

    private boolean isCorrectHbox(HBox hBox) {
        return hBox.getChildren().size() == 9;
    }


    public void pickupEvent() {
        for (Node i : vBox.getChildren()) {
            if (skipIsFirstHBox(vBox, i)) {
                continue;
            }
            vBox.getChildren().indexOf(i);
            HBox hBox = (HBox) i;

            if (isCorrectHbox(hBox)) {
                getPickupConfirmButton(hBox).setOnMouseClicked(e -> {

                    ChoiceBox<Integer> pickupFloorChoiceBox = getPickupFloorChoiceBox(hBox);
                    ChoiceBox<Direction> pickupButtonChoiceBox = getPickupButtonChoiceBox(hBox);
                    if (pickupFloorChoiceBox.getValue() == null || pickupButtonChoiceBox == null) {
                        return;
                    }

                    DataForPickupDto dataForPickupDto = new DataForPickupDto();
                    dataForPickupDto.setElevatorId(getElevatorId(hBox));
                    dataForPickupDto.setRequestedFloor(pickupFloorChoiceBox.getValue());
                    dataForPickupDto.setDirection(pickupButtonChoiceBox.getValue());
                    pickupFloorChoiceBox.setValue(null);
                    pickupButtonChoiceBox.setValue(null);
                    rest.pickup(dataForPickupDto, (result) -> {
                    });

                });
            }
        }
    }

    private ChoiceBox<Integer> getPickupFloorChoiceBox(HBox hBox) {
        return (ChoiceBox<Integer>) hBox.getChildren().get(6);
    }

    private ChoiceBox<Direction> getPickupButtonChoiceBox(HBox hBox) {
        return (ChoiceBox<Direction>) hBox.getChildren().get(7);
    }

    private Node getPickupConfirmButton(HBox hBox) {
        return hBox.getChildren().get(8);
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

    private Node getSelectConfirmButton(HBox hBox) {
        return hBox.getChildren().get(5);
    }

}
