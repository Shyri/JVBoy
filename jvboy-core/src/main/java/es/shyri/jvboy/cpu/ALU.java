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
        reg.setValue(inc(reg.getValue()));
    }

    int inc(int originalValue) {
        int result = originalValue + 1;

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

    void addSP(int value) {
        int result = cpu.SP.getValue() + value;

        cpu.resetFlags();

        int carry = (cpu.SP.getValue() ^ value ^ (result & 0xFFFF));

        if ((carry & 0x100) == 0x100) {
            cpu.setFlag(FLAG_CARRY);
        }

        if ((carry & 0x10) == 0x10) {
            cpu.setFlag(FLAG_HALF);
        }

        cpu.SP.setValue(result);
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
        reg.setValue(dec(reg.getValue()));
    }

    int dec(int originalValue) {
        int result = originalValue - 1;

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

        int result = originalValue - (value & 0xFF) - cFlagValue;
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

    void shiftLeftArithmetically(Reg8Bit reg) {
        reg.setValue(shiftLeftArithmetically(reg.getValue()));
    }

    int shiftLeftArithmetically(int value) {
        int result = (value << 1);

        cpu.resetFlags();

        if ((result & 0x100) != 0) {
            cpu.setFlag(FLAG_CARRY);
        }

        if ((result & 0xFF) == 0) {
            cpu.setFlag(FLAG_ZERO);
        }

        return result;
    }

    void shiftRightArithmetically(Reg8Bit reg) {
        reg.setValue(shiftRightArithmetically(reg.getValue()));
    }

    int shiftRightArithmetically(int value) {
        int result = (value >> 1);
        result = result | (value & 0x80);

        cpu.resetFlags();

        if ((value & 0x1) != 0) {
            cpu.setFlag(FLAG_CARRY);
        }

        if ((result & 0xFF) == 0) {
            cpu.setFlag(FLAG_ZERO);
        }

        return result;
    }

    void shiftRightLogically(Reg8Bit reg) {
        reg.setValue(shiftRightLogically(reg.getValue()));
    }

    int shiftRightLogically(int value) {
        int result = (value >> 1);

        cpu.resetFlags();

        if ((value & 0x01) == 0x01) {
            cpu.setFlag(FLAG_CARRY);
        }

        if ((result & 0xFF) == 0) {
            cpu.setFlag(FLAG_ZERO);
        }

        return result;
    }

    void rla(Reg8Bit reg) {
        reg.setValue(rotateLeft(reg.getValue()));
    }

    void rl(Reg8Bit reg) {
        reg.setValue(rl(reg.getValue()));
    }

    int rl(int value) {
        int result = rotateLeft(value);

        if ((result & 0xFF) == 0) {
            cpu.setFlag(FLAG_ZERO);
        }

        return result;
    }

    private int rotateLeft(int value) {
        int currentCarry = (cpu.isFlagSet(FLAG_CARRY) ? 0x01 : 0x00);
        int result = (value << 1) | currentCarry;

        cpu.resetFlags();

        if ((result & 0x100) != 0) {
            cpu.setFlag(FLAG_CARRY);
        }

        return result;
    }

    void rra(Reg8Bit reg) {
        reg.setValue(rotateRight(reg.getValue()));
    }

    void rr(Reg8Bit reg) {
        reg.setValue(rr(reg.getValue()));
    }

    int rr(int value) {
        int result = rotateRight(value);

        if ((result & 0xFF) == 0) {
            cpu.setFlag(FLAG_ZERO);
        }

        return result;
    }

    private int rotateRight(int value) {
        int currentCarry = (cpu.isFlagSet(FLAG_CARRY) ? 0x01 : 0x00);
        int result = (value >> 1) | (currentCarry << 7);

        cpu.resetFlags();

        if ((value & 0x01) == 0x01) {
            cpu.setFlag(FLAG_CARRY);
        }

        return result;
    }

    public void rlca(Reg8Bit reg) {
        reg.setValue(rotateLeftCircular(reg.getValue()));
    }

    public void rlc(Reg8Bit reg) {
        reg.setValue(rlc(reg.getValue()));
    }

    public int rlc(int value) {
        int result = rotateLeftCircular(value);

        if ((result & 0xFF) == 0) {
            cpu.setFlag(FLAG_ZERO);
        }

        return result;
    }

    int rotateLeftCircular(int value) {
        int result = (value << 1);

        cpu.resetFlags();

        if ((result & 0x100) != 0) {
            cpu.setFlag(FLAG_CARRY);
            result = result | 0x01;
        }

        return result;
    }

    public void rrca(Reg8Bit reg) {
        reg.setValue(rotateRightCircular(reg.getValue()));
    }

    public void rrc(Reg8Bit reg) {
        reg.setValue(rrc(reg.getValue()));
    }

    public int rrc(int value) {
        int result = rotateRightCircular(value);

        if ((result & 0xFF) == 0) {
            cpu.setFlag(FLAG_ZERO);
        }

        return result;
    }

    private int rotateRightCircular(int value) {
        int result = value >> 1;

        cpu.resetFlags();

        if ((value & 0x01) != 0) {
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

    void setBit(Reg8Bit reg, int bit) {
        reg.setValue(reg.getValue() | (0x01 << bit));
    }

    int setBit(int value, int bit) {
        return value | (0x01 << bit);
    }

    void resetBit(Reg8Bit reg, int bit) {
        reg.setValue(reg.getValue() & ~(0x01 << bit));
    }

    int resetBit(int value, int bit) {
        return value & ~(0x01 << bit);
    }

    void swap(Reg8Bit reg) {
        reg.setValue(swap(reg.getValue()));
    }

    int swap(int value) {
        int low = (value >> 4) & 0x0F;
        int high = (value << 4) & 0xF0;

        int result = low | high;

        cpu.resetFlags();
        if ((result & 0xFF) == 0) {
            cpu.setFlag(FLAG_ZERO);
        }

        return low | high;
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