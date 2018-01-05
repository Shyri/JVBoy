package es.shyri.jvboy.javafx.io;

import com.sun.javafx.application.PlatformImpl;

import es.shyri.jvboy.io.IOStatusOutput;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.text.Text;

/**
 * Created by shyri on 03/11/2017.
 */
public class IODebugController implements IOStatusOutput {
    @FXML
    Text LCDCText;
    @FXML
    Text SCYText;
    @FXML
    Text SCXText;
    @FXML
    Text BGPText;
    @FXML
    Text IEText;
    @FXML
    Text IFText;
    @FXML
    Text DIVText;
    @FXML
    Text TIMAText;
    @FXML
    Text TMAText;
    @FXML
    Text TACText;

    @FXML
    CheckBox LCDStatusCheck;
    @FXML
    CheckBox WinTileMapCheck;
    @FXML
    CheckBox BGWindowTileDataCheck;
    @FXML
    CheckBox BGTileMapCheck;
    @FXML
    CheckBox ObjectSizeCheck;
    @FXML
    CheckBox ObjectStatusCheck;
    @FXML
    CheckBox BGWindowCheck;

    @Override
    public void updateIE(final byte IE) {
        IEText.setText("(FFFF) IE: " + format2(IE));
    }

    @Override
    public void updateIF(final byte IF) {
        IFText.setText("(FF0F) IF: " + format2(IF));
    }

    @Override
    public void updateDIV(byte DIV) {
        DIVText.setText("(FF04) DIV: " + format2(DIV));
    }

    @Override
    public void updateTIMA(byte TIMA) {
        TIMAText.setText("(FF05) TIMA: " + format2(TIMA));
    }

    @Override
    public void updateTMA(byte TMA) {
        TMAText.setText("(FF06) TMA: " + format2(TMA));
    }

    @Override
    public void updateTAC(byte TAC) {
        TACText.setText("(FF07) TAC: " + format2(TAC));
    }

    @Override
    public void updateLCDC(final byte LCDC) {
        LCDCText.setText("(FF40) LCDC: " + format2(LCDC));

        boolean lcdEnabled = (LCDC & 0x80) > 0;
        LCDStatusCheck.setSelected(lcdEnabled);

        if (lcdEnabled) {
            LCDStatusCheck.setText("LCD: on");
        } else {
            LCDStatusCheck.setText("LCD: off");
        }

        boolean tileMap = (0x08 & LCDC) > 0;
        String tileMapAddr = tileMap ? "$9C00-$9FFF" : "$9800-$9BFF";

        BGTileMapCheck.setSelected(tileMap);
        BGTileMapCheck.setText("BG_MAP: " + tileMapAddr);

        boolean tileDataSelect = (0x10 & LCDC) > 0;
        String tileDataAddr = tileDataSelect ? "$8000-$8FFF" : "$8800-$97FF";
        BGWindowTileDataCheck.setSelected(tileDataSelect);
        BGWindowTileDataCheck.setText("BG_W_DAT: " + tileDataAddr);

    }

    @Override
    public void updateSCY(final byte SCY) {
        SCYText.setText("(FF42) SCY: " + format2(SCY));
    }

    @Override
    public void updateSCX(final byte SCX) {

        PlatformImpl.runAndWait(new Runnable() {
            @Override
            public void run() {
                SCYText.setText("(FF43) SCX: " + format2(SCX));
            }
        });

    }

    @Override
    public void updateBGP(final byte BGP) {

        PlatformImpl.runAndWait(new Runnable() {
            @Override
            public void run() {
                BGPText.setText("(FF47) BGP: " + format2(BGP));
            }
        });

    }

    private String format2(int val) {
        return String.format("%02X", val & 0xFF);
    }

}
