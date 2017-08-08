package com.a424appslab.androidboy.cpu;

import com.a424appslab.androidboy.cpu.register.Reg8Bit;

import static com.a424appslab.androidboy.cpu.CPU.FLAG_CARRY;
import static com.a424appslab.androidboy.cpu.CPU.FLAG_HALF;
import static com.a424appslab.androidboy.cpu.CPU.FLAG_NEGATIVE;
import static com.a424appslab.androidboy.cpu.CPU.FLAG_ZERO;

/**
 * Created by shyri on 03/07/17.
 */

public class ALU {
    private final CPU cpu;

    public ALU(CPU cpu) {this.cpu = cpu;}

    void inc(Reg8Bit reg) {
        byte result = (byte) (reg.getValue() + 1);
        reg.setValue(result);

        boolean cFlag = cpu.isFlagSet(FLAG_CARRY);

        if (result == 0) {
            cpu.setFlag(FLAG_ZERO);
        } else {
            cpu.clearFlags();
        }

        if (cFlag) {
            cpu.setFlag(FLAG_CARRY);
        }

        if ((result & 0x0F) == 0x00) {
            cpu.setFlag(FLAG_HALF);
        }

    }

    void add(Reg8Bit reg, byte value) {
        int originalValue = uint(reg.getValue());
        byte result = (byte) ((byte) (originalValue + value) & 0xFF);
        reg.setValue(result);

        cpu.clearFlags();
        if (result == 0) {
            cpu.setFlag(FLAG_ZERO);
        }

        byte carry = (byte) (originalValue & value);

        if ((carry & 0x08) == 0x08) {
            cpu.setFlag(FLAG_HALF);
        }

        if ((carry & 0x80) == 0x80) {
            cpu.setFlag(FLAG_HALF);
        }
    }

    void dec(Reg8Bit reg) {
        byte result = (byte) (reg.getValue() - 1);
        reg.setValue(result);

        boolean cFlag = cpu.isFlagSet(FLAG_CARRY);

        if (result == 0) {
            cpu.setFlag(FLAG_ZERO);
        } else {
            cpu.clearFlags();
        }

        cpu.setFlag(FLAG_NEGATIVE);

        if (cFlag) {
            cpu.setFlag(FLAG_CARRY);
        }

        if ((result & 0x0F) == 0x0F) {
            cpu.setFlag(FLAG_HALF);
        }

    }

    void sub(Reg8Bit reg, byte value) {
        byte originalValue = reg.getValue();
        byte result = (byte) (originalValue - value);
        reg.setValue(result);

        cpu.clearFlags();

        if (result == 0) {
            cpu.setFlag(FLAG_ZERO);
        }

        cpu.setFlag(FLAG_NEGATIVE);

        byte borrow = (byte) ((~originalValue) & 0xFF & value);

        if ((borrow & 0x80) != 0x80) {
            cpu.setFlag(FLAG_CARRY);
        }

        if ((borrow & 0x08) != 0x08) {
            cpu.setFlag(FLAG_HALF);
        }
    }

    void xor(byte value) {
        byte result = (byte) (cpu.AF.getHigh() ^ value);
        cpu.AF.setHigh(result);
        cpu.clearFlags();
        if (result == 0) {
            cpu.setFlag(FLAG_ZERO);
        }
    }

    void cp(int value) {
        byte result = (byte) (cpu.AF.getHigh() - value);

        cpu.clearFlags();

        if (result == 0) {
            cpu.setFlag(FLAG_ZERO);
        } else if (cpu.AF.getHigh() < value) {
            cpu.setFlag(FLAG_CARRY);
        }

        cpu.setFlag(FLAG_NEGATIVE);

        if ((result & 0x0F) == 0x0F) {
            cpu.setFlag(FLAG_HALF);
        }
    }

    void rotate(Reg8Bit reg) {
        byte currentCarry = (byte) (cpu.isFlagSet(FLAG_CARRY) ? 0x01 : 0x00);
        byte result = (byte) ((byte) (reg.getValue() << 1) | currentCarry);

        cpu.clearFlags();
        if ((reg.getValue() & 0x80) > 0) {
            cpu.setFlag(FLAG_CARRY);
        }

        if (result == 0) {
            cpu.setFlag(FLAG_ZERO);
        }
        reg.setValue(result);
    }

    private static int uint(byte value) {
        return value & 0xff;
    }
}
