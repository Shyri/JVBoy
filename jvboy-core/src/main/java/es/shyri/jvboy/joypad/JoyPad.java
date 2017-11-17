package es.shyri.jvboy.joypad;

/**
 * Created by shyri on 20/10/2017.
 */
public class JoyPad {
    int P15 = 1;
    int P14 = 1;
    int P13 = 1;
    int P12 = 1;
    int P11 = 1;
    int P10 = 1;

    public void setP1(int value) {
        P15 = value >> 5 & 0x1;
        P14 = value >> 4 & 0x1;
    }

    public byte getP1() {
        byte value = (byte) 0xC0;
        value = (byte) (value | P15 << 5);
        value = (byte) (value | P14 << 4);
        value = (byte) (value | P13 << 3);
        value = (byte) (value | P12 << 2);
        value = (byte) (value | P11 << 1);
        value = (byte) (value | P10);

        return value;
    }

}
