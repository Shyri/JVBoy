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
}
