package es.shyri.jvboy.javafx.memory;

import com.sun.javafx.application.PlatformImpl;

import java.net.URL;
import java.util.ResourceBundle;

import es.shyri.jvboy.javafx.render.VRAMViewer;
import es.shyri.jvboy.memory.MemoryDebugOutput;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

/**
 * Created by shyri on 03/11/2017.
 */
public class MemoryDebugController implements MemoryDebugOutput, Initializable {
    @FXML
    ListView ramListView;

    final ObservableList<String> ramList = FXCollections.observableArrayList();

    private VRAMViewer vramViewer;

    private boolean vramViewerEnabled = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ramListView.setItems(ramList);
    }

    public void setRamViewer(VRAMViewer vramViewer) {
        this.vramViewer = vramViewer;
    }

    @Override
    public void onWriteToRAM(final int position, final String[] ram, final byte[] ramValues) {
        PlatformImpl.runAndWait(new Runnable() {
            @Override
            public void run() {

                if (ramList.size() == 0) {
                    ramList.addAll(ram);
                } else {
                    ramList.set(position, ram[position]);
                }

                ramListView.getSelectionModel()
                           .clearSelection();
                ramListView.getSelectionModel()
                           .select(position);
                if (position - 6 < 0) {
                    ramListView.scrollTo(0);
                } else {
                    ramListView.scrollTo(position - 6);
                }

                if (vramViewerEnabled && vramViewer != null) {
                    int address = ram.length - position;
                    if (address >= 0x8000 && address < 0x9000) {
                        vramViewer.onVRAMUpdate(address, ramValues);
                    }
                }
            }
        });
    }

    @Override
    public void onUpdateWholeRAM(String[] ram, final byte[] ramValues) {
        ramList.clear();
        ramList.addAll(ram);

        if (vramViewerEnabled && vramViewer != null) {
            vramViewer.onUpdateWholeVRAM(ramValues);
        }
    }

    public void setVramViewerEnabled(boolean vramViewerEnabled) {
        this.vramViewerEnabled = vramViewerEnabled;
    }
}
