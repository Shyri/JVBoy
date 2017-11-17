package es.shyri.jvboy.javafx.cpu;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import es.shyri.jvboy.cpu.DisassemblyOutput;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/**
 * Created by shyri on 03/11/2017.
 */
public class CodeListController implements DisassemblyOutput, Initializable {
    @FXML
    ListView disassembledCodeListView;

    final ObservableList<String> disassembledCodeList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        VBox.setVgrow(disassembledCodeListView, Priority.ALWAYS);
        disassembledCodeListView.setItems(disassembledCodeList);
        disassembledCodeListView.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                try {
                    return new CodeCell();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        });
    }

    @Override
    public void updateDecompiled(int PC, String[] decompiledCode) {
        disassembledCodeList.clear();

        for (String line : decompiledCode) {
            if (line != null) {
                disassembledCodeList.add(line);
            }
        }

        //            @Override
        //            public void updateDecompiled(int PC, String[] decompiledCode) {
        //                //                if (disassembledCodeList.size() == 0) {
        //                //                    disassembledCodeList.add(PC, decompiledCode[PC]);
        //                //                } else {
        //                //                    disassembledCodeList.set(PC, decompiledCode[PC]);
        //                //                }
        //                disassembledCodeList.clear();
        //
        //                for (String line : decompiledCode) {
        //                    if (line != null) {
        //                        disassembledCodeList.add(line);
        //                    }
        //                }

        int index = disassembledCodeList.indexOf(decompiledCode[PC]);
        disassembledCodeListView.getSelectionModel()
                                .clearSelection();
        disassembledCodeListView.getSelectionModel()
                                .select(index);
        if (index - 6 < 0) {
            disassembledCodeListView.scrollTo(0);
        } else {
            disassembledCodeListView.scrollTo(index - 6);
        }
        //            }
    }

}
