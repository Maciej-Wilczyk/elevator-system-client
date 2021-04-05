package controller;

import dto.DataForPickupDto;
import dto.DataForSelectDto;
import dto.StatusDto;
import enums.NoTargetFloor;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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
        setStatus();
//        for (Node i : vBox.getChildren()) {
//            // if (i instanceof Button) {
//
//            System.out.println(i.getClass().getName());
//            if(i instanceof  Button) {
//
//                System.out.println("Button");
//                i.setOnMouseClicked(e -> {
//                    HBox hBox = (HBox) i.getParent();
//                    System.out.println(hBox.getId());
//                });
//
//            }
//        }
        selectEvent();
//        AnchorPane.setBottomAnchor(anchorPane,0.0);
//        AnchorPane.setLeftAnchor(anchorPane,0.0);
//        AnchorPane.setRightAnchor(anchorPane,0.0);
//        AnchorPane.setTopAnchor(anchorPane,0.0);
//
    }

    private void setStatus() {

        rest.status((result) -> {
            Platform.runLater(() ->{
            HBox hBox;
            Label label;
            List<StatusDto> list = Arrays.asList((StatusDto[]) result.getBody());
            for (int i = 0; i < list.size(); i++) {
                hBox = (HBox) vBox.getChildren().get(i + 1);
                label = (Label) hBox.getChildren().get(0);
                label.setText(String.valueOf(list.get(i).getElevatorId()));
                label = (Label) hBox.getChildren().get(1);
                label.setText(String.valueOf(list.get(i).getCurrentFloor()));
                label = (Label) hBox.getChildren().get(2);
                label.setText(list.get(i).getDirection().getDirectionAsString());
                label = (Label) hBox.getChildren().get(3);
                if (list.get(i).getNearestTargetFloor() == NoTargetFloor.NO_TARGET_FLOOR.noTargetFloorAsInt) {
                    label.setText(NoTargetFloor.NO_TARGET_FLOOR.noTargetFloorAsString);
                } else {
                    label.setText(String.valueOf(list.get(i).getNearestTargetFloor()));
                }
            }
            });
        });

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
            if (hBox.getChildren().size() == 9) {
                getSelectConfirmButton(hBox).setOnMouseClicked(e -> {
                    ChoiceBox<Integer> choiceBox = getSelectFloorChoiceBox(hBox);
                    if(choiceBox.getValue() == null){
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


    public void pickupEvent() {
        for (Node i : vBox.getChildren()) {
            if (skipIsFirstHBox(vBox, i)) {
                continue;
            }
            vBox.getChildren().indexOf(i);
            HBox hBox = (HBox) i;

            if (hBox.getChildren().size() == 9) {
                getPickupConfirmButton(hBox).setOnMouseClicked(e -> {

                    ChoiceBox<Integer> pickupFloorChoiceBox = getPickupFloorChoiceBox(hBox);
                    ChoiceBox<Integer> pickupButtonChoiceBox = getPickupButtonChoiceBox(hBox);
                    if(pickupFloorChoiceBox.getValue() == null || pickupButtonChoiceBox == null){
                        return;
                    }

                    DataForPickupDto dataForPickupDto = new DataForPickupDto();
//                    dataForPickupDto.setElevatorId(getElevatorId(hBox));
//                    dataForPickupDto.setRequestedFloor(pickupFloorChoiceBox.getValue());
//                    dataForPickupDto.setDirection(pickupButtonChoiceBox.getValue());
//
//
//                    DataForSelectDto dataForSelectDto = new DataForSelectDto();
//                    dataForSelectDto.setElevatorId(getElevatorId(hBox));
//                    dataForSelectDto.setSelectedFloor(choiceBox.getValue());
//                    choiceBox.setValue(null);
//                    rest.select(dataForSelectDto, (result) -> {
//                    });

                });
            }
        }
    }

    private ChoiceBox<Integer> getPickupFloorChoiceBox(HBox hBox) {
        return (ChoiceBox<Integer>) hBox.getChildren().get(6);
    }

    private ChoiceBox<Integer> getPickupButtonChoiceBox(HBox hBox) {
        return (ChoiceBox<Integer>) hBox.getChildren().get(7);
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
        return  (ChoiceBox) hBox.getChildren().get(4);
    }

    private Node getSelectConfirmButton(HBox hBox) {
        return hBox.getChildren().get(5);
    }

}
