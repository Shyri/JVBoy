package es.shyri.jvboy.javafx.cpu;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

/**
 * Created by shyri on 04/11/2017.
 */
public class CodeCellController {
    @FXML
    Text codeText;

    public void setText(String text) {
        codeText.setText(text);
    }
}
