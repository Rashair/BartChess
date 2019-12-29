package view.promotion;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.stage.Stage;
import model.pieces.*;

public class PromotionWindowController {
    private Class<? extends Piece> promotionChoice = Pawn.class;

    public Class<? extends Piece> getPromotionChoice() {
        return promotionChoice;
    }

    public void onQueenButtonClick(ActionEvent actionEvent) {
        promotionChoice = Queen.class;
        closeWindow(actionEvent);
    }

    public void onRookButtonClick(ActionEvent actionEvent) {
        promotionChoice = Rook.class;
        closeWindow(actionEvent);
    }

    public void onBishopButtonClick(ActionEvent actionEvent) {
        promotionChoice = Bishop.class;
        closeWindow(actionEvent);
    }

    public void onKnightButtonClick(ActionEvent actionEvent) {
        promotionChoice = Knight.class;
        closeWindow(actionEvent);
    }

    public void onPawnButtonClick(ActionEvent actionEvent) {
        promotionChoice = Pawn.class;
        closeWindow(actionEvent);
    }

    private void closeWindow(Event e) {
        Node source = (Node) e.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
