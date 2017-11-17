package es.shyri.jvboy.cpu.register;

/**
 * Created by shyri on 02/07/17.
 */
public class Reg8Bit {
    int value;

    public void setValue(int value) {
        this.value = value & 0xFF;
    }

    public int getValue() {
        return value;
    }
}