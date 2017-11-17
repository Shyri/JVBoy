package es.shyri.jvboy.javafx.cpu;

import es.shyri.jvboy.cpu.CPUStatusOutput;
import es.shyri.jvboy.cpu.register.Reg16Bit;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.text.Text;

/**
 * Created by shyri on 03/11/2017.
 */
public class CPUDebugController implements CPUStatusOutput {
    @FXML
    Text AFText;
    @FXML
    Text BCText;
    @FXML
    Text DEText;
    @FXML
    Text HLText;
    @FXML
    Text SPText;
    @FXML
    Text PCText;

    @FXML
    CheckBox ZCheckBox;
    @FXML
    CheckBox NCheckBox;
    @FXML
    CheckBox HCheckBox;
    @FXML
    CheckBox CCheckBox;

    @Override
    public void updateAF(Reg16Bit reg16Bit) {
        AFText.setText("AF: " + String.format("%04X", reg16Bit.getValue()));
    }

    @Override
    public void updateBC(Reg16Bit reg16Bit) {
        BCText.setText("BC: " + String.format("%04X", reg16Bit.getValue()));
    }

    @Override
    public void updateDE(Reg16Bit reg16Bit) {
        DEText.setText("DE: " + String.format("%04X", reg16Bit.getValue()));
    }

    @Override
    public void updateHL(Reg16Bit reg16Bit) {
        HLText.setText("HL: " + String.format("%04X", reg16Bit.getValue()));
    }

    @Override
    public void updateSP(Reg16Bit reg16Bit) {
        SPText.setText("SP: " + String.format("%04X", reg16Bit.getValue()));
    }

    @Override
    public void updatePC(Reg16Bit reg16Bit) {
        PCText.setText("PC: " + String.format("%04X", reg16Bit.getValue()));
    }

    @Override
    public void updateZ(boolean zFlag) {
        ZCheckBox.setSelected(zFlag);
    }

    @Override
    public void updateN(boolean nFlag) {
        NCheckBox.setSelected(nFlag);
    }

    @Override
    public void updateH(boolean hFlag) {
        HCheckBox.setSelected(hFlag);
    }

    @Override
    public void updateC(boolean cFlag) {
        CCheckBox.setSelected(cFlag);
    }
}
