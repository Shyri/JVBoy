package es.shyri.jvboy.cpu.register;

/**
 * Created by shyri on 02/07/17.
 */

public class Reg16Bit {
    Reg8Bit high;
    Reg8Bit low;

    public Reg16Bit() {
        low = new Reg8Bit();
        high = new Reg8Bit();
    }

    public void setValue(int value) {
        high.setValue(((value & 0xFF00) >> 8));
        low.setValue((value & 0x00FF));
    }

    public int getValue() {
        return (((high.getValue() << 8) & 0xFF00) | (low.getValue() & 0xFF));
    }

    public void setLow(int value) {
        low.setValue(value & 0xFF);
    }

    public int getLow() {
        return low.getValue();
    }

    public Reg8Bit getLowReg() {
        return low;
    }

    public void setHigh(int value) {
        high.setValue(value & 0xFF);
    }

    public int getHigh() {
        return high.getValue();
    }

    public Reg8Bit getHighReg() {
        return high;
    }

    public void inc() {
        setValue(getValue() + 1);
    }

    public void dec() {
        setValue(getValue() - 1);
    }
}
