package com.a424appslab.androidboy.cpu.register;

/**
 * Created by shyri on 02/07/17.
 */

public class Reg8Bit {
    byte value;

    public void setValue(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public void dec() {
        value--;
    }
}
