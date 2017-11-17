package es.shyri.jvboy.io;

/**
 * Created by shyri on 11/10/2017.
 */
public interface IOStatusOutput {
    void updateLCDC(byte LCDC);

    void updateSCY(byte SCY);

    void updateSCX(byte SCX);

    void updateBGP(byte BGP);
}
