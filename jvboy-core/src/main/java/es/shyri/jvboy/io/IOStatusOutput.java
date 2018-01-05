package es.shyri.jvboy.io;

/**
 * Created by shyri on 11/10/2017.
 */
public interface IOStatusOutput {
    void updateIE(byte IE);

    void updateIF(byte IF);

    void updateDIV(byte DIV);

    void updateTIMA(byte TIMA);

    void updateTMA(byte TMA);

    void updateTAC(byte TAC);

    void updateLCDC(byte LCDC);

    void updateSCY(byte SCY);

    void updateSCX(byte SCX);

    void updateBGP(byte BGP);
}
