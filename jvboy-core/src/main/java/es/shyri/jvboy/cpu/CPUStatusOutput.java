package es.shyri.jvboy.cpu;

import es.shyri.jvboy.cpu.register.Reg16Bit;

/**
 * Created by shyri on 02/10/2017.
 */
public interface CPUStatusOutput {
    void updateAF(Reg16Bit reg16Bit);
    void updateBC(Reg16Bit reg16Bit);
    void updateDE(Reg16Bit reg16Bit);
    void updateHL(Reg16Bit reg16Bit);
    void updateSP(Reg16Bit reg16Bit);
    void updatePC(Reg16Bit reg16Bit);
    void updateZ(boolean zFlag);
    void updateN(boolean nFlag);
    void updateH(boolean hFlag);
    void updateC(boolean cFlag);


}
