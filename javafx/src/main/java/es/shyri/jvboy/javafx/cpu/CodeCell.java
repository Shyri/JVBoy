package es.shyri.jvboy.javafx.cpu;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListCell;

/**
 * Created by shyri on 09/10/2017.
 */
public class CodeCell extends ListCell<String> {
    private final Node graphic;
    private final CodeCellController controller;

    public CodeCell() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/list_cell_code.fxml"));
        graphic = loader.load();
        controller = loader.getController();
    }

    @Override
    protected void updateItem(String string, boolean empty) {
        super.updateItem(string, empty);
        //        setFont(Font.font("Verdana"));
        //        if (empty || string == null) {
        //            setText(null);
        //            setGraphic(null);
        //            setPrefHeight(0);
        //        } else {
        //            setText(string);
        //            setGraphic(new Text());
        //            setPrefHeight(Region.USE_COMPUTED_SIZE);
        //        }
        if (string != null) {
            controller.setText(string);
            setGraphic(graphic);
        } else {
            controller.setText("");
            setGraphic(null);
        }
    }
}
