package es.shyri.jvboy.cpu;

import es.shyri.jvboy.cpu.register.Reg16Bit;
import es.shyri.jvboy.cpu.register.Reg8Bit;

import static es.shyri.jvboy.cpu.CPU.FLAG_CARRY;
import static es.shyri.jvboy.cpu.CPU.FLAG_HALF;
import static es.shyri.jvboy.cpu.CPU.FLAG_NEGATIVE;
import static es.shyri.jvboy.cpu.CPU.FLAG_ZERO;

/**
 * Created by shyri on 03/07/17.
 */
public class ALU {
    private final CPU cpu;

    public ALU(CPU cpu) {this.cpu = cpu;}

    void inc(Reg8Bit reg) {
        int originalValue = reg.getValue();
        int result = originalValue + 1;

        reg.setValue(result);

        boolean cFlag = cpu.isFlagSet(FLAG_CARRY);

        cpu.resetFlags();

        if ((result & 0xFF) == 0) {
            cpu.setFlag(FLAG_ZERO);
        }

        if (cFlag) {
            cpu.setFlag(FLAG_CARRY);
        }

        if ((originalValue & 0x0F) == 0x0F) {
            cpu.setFlag(FLAG_HALF);
        }

    }

    int incVal(byte value) {
        int result = (value + 1) & 0xFF;

        boolean cFlag = cpu.isFlagSet(FLAG_CARRY);

        if (result == 0) {
            cpu.setFlag(FLAG_ZERO);
        } else {
            cpu.resetFlags();
        }

        if (cFlag) {
            cpu.setFlag(FLAG_CARRY);
        }

        if ((result & 0x0F) == 0x00) {
            cpu.setFlag(FLAG_HALF);
        }

        return result;
    }

    void add(Reg8Bit reg, int value) {
        int originalValue = reg.getValue();
        int result = originalValue + (value & 0xFF);
        reg.setValue(result);

        cpu.resetFlags();
        if ((result & 0xFF) == 0) {
            cpu.setFlag(FLAG_ZERO);
        }

        int carry = (originalValue ^ (value & 0xFF) ^ result);

        if ((carry & 0x10) != 0) {
            cpu.setFlag(FLAG_HALF);
        }

        if ((carry & 0x100) != 0) {
            cpu.setFlag(FLAG_CARRY);
        }
    }

    void add(Reg16Bit reg, int value) {
        int result = reg.getValue() + value;

        cpu.resetFlag(FLAG_NEGATIVE);

        if ((result & 0x10000) != 0) {
            cpu.setFlag(FLAG_CARRY);
        } else {
            cpu.resetFlag(FLAG_CARRY);
        }

        int carry = (reg.getValue() ^ (value & 0xFFFF) ^ result);

        if ((carry & 0x1000) != 0) {
            cpu.setFlag(FLAG_HALF);
        } else {
            cpu.resetFlag(FLAG_HALF);
        }

        reg.setValue(result & 0xFFFF);
    }

    void dec(Reg8Bit reg) {
        int originalValue = reg.getValue();
        int result = originalValue - 1;
        reg.setValue(result);

        boolean cFlag = cpu.isFlagSet(FLAG_CARRY);

        cpu.resetFlags();

        if ((result & 0xFF) == 0) {
            cpu.setFlag(FLAG_ZERO);
        }

        cpu.setFlag(FLAG_NEGATIVE);

        if (cFlag) {
            cpu.setFlag(FLAG_CARRY);
        }

        if ((originalValue & 0x0F) - 1 < 0) {
            cpu.setFlag(FLAG_HALF);
        }
    }

    int decVal(byte value) {
        int result = (value - 1) & 0xFF;

        boolean cFlag = cpu.isFlagSet(FLAG_CARRY);

        if (result == 0) {
            cpu.setFlag(FLAG_ZERO);
        } else {
            cpu.resetFlags();
        }

        cpu.setFlag(FLAG_NEGATIVE);

        if (cFlag) {
            cpu.setFlag(FLAG_CARRY);
        }

        if ((result & 0x0F) == 0x0F) {
            cpu.setFlag(FLAG_HALF);
        }

        return result;
    }

    void sub(Reg8Bit reg, int value) {
        int originalValue = reg.getValue();
        int result = originalValue - (value & 0xFF);
        reg.setValue(result);

        cpu.resetFlags();

        if (result == 0) {
            cpu.setFlag(FLAG_ZERO);
        }

        cpu.setFlag(FLAG_NEGATIVE);

        int carry = (originalValue ^ (value & 0xFF) ^ result);

        if ((carry & 0x10) != 0) {
            cpu.setFlag(FLAG_HALF);
        }

        if ((carry & 0x100) != 0) {
            cpu.setFlag(FLAG_CARRY);
        }
    }

    void sbc(Reg8Bit reg, int value) {
        int originalValue = reg.getValue();
        int cFlagValue = cpu.isFlagSet(FLAG_CARRY) ? 1 : 0;

        int result = originalValue - value - cFlagValue;
        reg.setValue(result);

        cpu.resetFlags();

        if ((result & 0xFF) == 0) {
            cpu.setFlag(FLAG_ZERO);
        }

        cpu.setFlag(FLAG_NEGATIVE);

        if (result < 0) {
            cpu.setFlag(FLAG_CARRY);
        }

        if ((originalValue & 0x0F) - (value & 0x0F) - cFlagValue < 0) {
            cpu.setFlag(FLAG_HALF);
        }
    }

    void and(Reg8Bit reg, int value) {
        int result = (reg.getValue() & value) & 0xFF;
        reg.setValue(result);
        cpu.resetFlags();

        if (result == 0) {
            cpu.setFlag(FLAG_ZERO);
        }

        cpu.setFlag(FLAG_HALF);
    }

    void xor(Reg8Bit reg, int value) {
        int result = (reg.getValue() ^ value) & 0xFF;
        reg.setValue(result);
        cpu.resetFlags();
        if (result == 0) {
            cpu.setFlag(FLAG_ZERO);
        }
    }

    void or(Reg8Bit reg, int value) {
        int result = (reg.getValue() | value) & 0xFF;
        reg.setValue(result);
        cpu.resetFlags();
        if (result == 0) {
            cpu.setFlag(FLAG_ZERO);
        }
    }

    void cp(Reg8Bit reg, int value) {
        int originalValue = reg.getValue();
        int result = originalValue - (value & 0xFF);

        cpu.resetFlags();

        if (result == 0) {
            cpu.setFlag(FLAG_ZERO);
        }

        cpu.setFlag(FLAG_NEGATIVE);

        int carry = (originalValue ^ (value & 0xFF) ^ result);

        if ((carry & 0x10) != 0) {
            cpu.setFlag(FLAG_HALF);
        }

        if ((carry & 0x100) != 0) {
            cpu.setFlag(FLAG_CARRY);
        }
    }

    void shiftLeft(Reg8Bit reg) {
        int result = (reg.getValue() << 1);

        cpu.resetFlags();

        if ((result & 0x100) != 0) {
            cpu.setFlag(FLAG_CARRY);
        }

        if ((result & 0xFF) == 0) {
            cpu.setFlag(FLAG_ZERO);
        }

        reg.setValue(result);
    }

    void shiftRight(Reg8Bit reg) {
        int result = (reg.getValue() >> 1);
        result = result | (reg.getValue() & 0x80);

        cpu.resetFlags();

        if ((reg.getValue() & 0x1) != 0) {
            cpu.setFlag(FLAG_CARRY);
        }

        if ((result & 0xFF) == 0) {
            cpu.setFlag(FLAG_ZERO);
        }

        reg.setValue(result);
    }

    void shiftRightLogically(Reg8Bit reg) {
        int result = (reg.getValue() >> 1);

        cpu.resetFlags();

        if ((reg.getValue() & 0x01) == 0x01) {
            cpu.setFlag(FLAG_CARRY);
        }

        if ((result & 0xFF) == 0) {
            cpu.setFlag(FLAG_ZERO);
        }

        reg.setValue(result);
    }

    void rla(Reg8Bit reg) {
        reg.setValue(rotateLeft(reg));
    }

    void rl(Reg8Bit reg) {
        int result = rotateLeft(reg);

        reg.setValue(result);

        if ((result & 0xFF) == 0) {
            cpu.setFlag(FLAG_ZERO);
        }
    }

    private int rotateLeft(Reg8Bit reg) {
        int currentCarry = (cpu.isFlagSet(FLAG_CARRY) ? 0x01 : 0x00);
        int result = (reg.getValue() << 1) | currentCarry;

        cpu.resetFlags();

        if ((result & 0x100) != 0) {
            cpu.setFlag(FLAG_CARRY);
        }

        return result;
    }

    void rra(Reg8Bit reg) {
        reg.setValue(rotateRight(reg));
    }

    void rr(Reg8Bit reg) {

        int result = rotateRight(reg);

        reg.setValue(result);

        if ((result & 0xFF) == 0) {
            cpu.setFlag(FLAG_ZERO);
        }
    }

    private int rotateRight(Reg8Bit reg) {
        int currentCarry = (cpu.isFlagSet(FLAG_CARRY) ? 0x01 : 0x00);
        int result = (reg.getValue() >> 1) | (currentCarry << 7);

        cpu.resetFlags();

        if ((reg.getValue() & 0x01) == 0x01) {
            cpu.setFlag(FLAG_CARRY);
        }

        return result;
    }

    public void rlca(Reg8Bit reg) {
        reg.setValue(rotateLeftCircular(reg));
    }

    public void rlc(Reg8Bit reg) {
        int result = rotateLeftCircular(reg);

        reg.setValue(result);

        if ((result & 0xFF) == 0) {
            cpu.setFlag(FLAG_ZERO);
        }
    }

    int rotateLeftCircular(Reg8Bit reg) {
        int result = (reg.getValue() << 1);

        cpu.resetFlags();

        if ((result & 0x100) != 0) {
            cpu.setFlag(FLAG_CARRY);
            result = result | 0x01;
        }

        return result;
    }

    public void rrca(Reg8Bit reg) {
        reg.setValue(rotateRightCircular(reg));
    }

    public void rrc(Reg8Bit reg) {
        int result = rotateRightCircular(reg);

        reg.setValue(result);

        if ((result & 0xFF) == 0) {
            cpu.setFlag(FLAG_ZERO);
        }
    }

    private int rotateRightCircular(Reg8Bit reg) {
        int result = reg.getValue() >> 1;

        cpu.resetFlags();

        if ((reg.getValue() & 0x01) != 0) {
            cpu.setFlag(FLAG_CARRY);
            result = result | 0x80;
        }

        return result;
    }

    void toBCD(Reg8Bit reg) {
        int hexValue = reg.getValue();

        if (cpu.isFlagSet(FLAG_NEGATIVE)) {
            if (cpu.isFlagSet(FLAG_HALF) || (hexValue & 0xF) > 0x09) {
                hexValue = hexValue + 0x06;
            }

            if (cpu.isFlagSet(FLAG_CARRY) || hexValue > 0x9F) {
                hexValue = hexValue + 0x60;
            }
        } else {
            if (cpu.isFlagSet(FLAG_HALF)) {
                hexValue = (hexValue - 0x60) & 0xFF;
            }

            if (cpu.isFlagSet(FLAG_CARRY)) {
                hexValue -= 0x60;
            }
        }

        cpu.resetFlag(FLAG_HALF);
        cpu.resetFlag(FLAG_ZERO);

        if ((hexValue & 0x100) == 0x100) {
            cpu.setFlag(FLAG_CARRY);
        }

        hexValue = hexValue & 0xFF;

        if (hexValue == 0) {
            cpu.setFlag(FLAG_ZERO);
        }

        reg.setValue(hexValue);
    }

    void flipBits(Reg8Bit reg) {
        reg.setValue((~reg.getValue()) & 0xFF);
        cpu.setFlag(FLAG_HALF);
        cpu.setFlag(FLAG_NEGATIVE);
    }

    void resetBit(Reg8Bit reg, int bit) {
        reg.setValue(reg.getValue() & ~(0x01 << bit));
    }

    int resetBit(int value, int bit) {
        return value & ~(0x01 << bit);
    }

    void swap(Reg8Bit reg) {
        int low = (reg.getValue() >> 4) & 0x0F;
        int high = (reg.getValue() << 4) & 0xF0;

        int result = low | high;

        cpu.resetFlags();
        if ((result & 0xFF) == 0) {
            cpu.setFlag(FLAG_ZERO);
        }

        reg.setValue(low | high);
    }

    void adc(Reg8Bit reg, int value) {
        int originalValue = reg.getValue();
        int result = originalValue + (value & 0xFF);

        if (cpu.isFlagSet(FLAG_CARRY)) {
            result = result + 1;
        }

        reg.setValue(result);

        cpu.resetFlags();
        if ((result & 0xFF) == 0) {
            cpu.setFlag(FLAG_ZERO);
        }

        int carry = (originalValue ^ (value & 0xFF) ^ result);

        if ((carry & 0x10) != 0) {
            cpu.setFlag(FLAG_HALF);
        }

        if ((carry & 0x100) != 0) {
            cpu.setFlag(FLAG_CARRY);
        }
    }
}