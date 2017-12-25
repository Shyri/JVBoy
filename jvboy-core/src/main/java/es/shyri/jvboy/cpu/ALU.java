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
        int result = (reg.getValue() + 1) & 0xFF;
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

    int incVal(byte value) {
        int result = (value + 1) & 0xFF;

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

        return result;
    }

    void add(Reg8Bit reg, int value) {
        int originalValue = reg.getValue();
        int result = originalValue + (value & 0xFF);
        reg.setValue(result);

        cpu.clearFlags();
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
            cpu.setFlag(FLAG_HALF);
        } else {
            cpu.resetFlag(FLAG_HALF);
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
        int result = (reg.getValue() - 1) & 0xFF;
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

    int decVal(byte value) {
        int result = (value - 1) & 0xFF;

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

        return result;
    }

    void sub(Reg8Bit reg, int value) {
        int originalValue = reg.getValue();
        int result = originalValue - (value & 0xFF);
        reg.setValue(result);

        cpu.clearFlags();

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

    void and(Reg8Bit reg, int value) {
        int result = (reg.getValue() & value) & 0xFF;
        reg.setValue(result);
        cpu.clearFlags();

        if (result == 0) {
            cpu.setFlag(FLAG_ZERO);
        }

        cpu.setFlag(FLAG_HALF);
    }

    void xor(Reg8Bit reg, int value) {
        int result = (reg.getValue() ^ value) & 0xFF;
        reg.setValue(result);
        cpu.clearFlags();
        if (result == 0) {
            cpu.setFlag(FLAG_ZERO);
        }
    }

    void or(Reg8Bit reg, int value) {
        int result = (reg.getValue() | value) & 0xFF;
        reg.setValue(result);
        cpu.clearFlags();
        if (result == 0) {
            cpu.setFlag(FLAG_ZERO);
        }
    }

    void cp(Reg8Bit reg, int value) {
        int originalValue = reg.getValue();
        int result = originalValue - (value & 0xFF);

        cpu.clearFlags();

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

        cpu.clearFlags();

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

        cpu.clearFlags();

        if ((result & 0x100) != 0) {
            cpu.setFlag(FLAG_CARRY);
        }

        if ((result & 0xFF) == 0) {
            cpu.setFlag(FLAG_ZERO);
        }

        reg.setValue(result);
    }

    void rotateLeft(Reg8Bit reg) {
        int currentCarry = (cpu.isFlagSet(FLAG_CARRY) ? 0x01 : 0x00);
        int result = (reg.getValue() << 1) | currentCarry;

        cpu.clearFlags();

        if ((result & 0x100) != 0) {
            cpu.setFlag(FLAG_CARRY);
        }

        if ((result & 0xFF) == 0) {
            cpu.setFlag(FLAG_ZERO);
        }

        reg.setValue(result);
    }

    void rotateRight(Reg8Bit reg) {
        int currentCarry = (cpu.isFlagSet(FLAG_CARRY) ? 0x01 : 0x00);
        int result = (reg.getValue() >> 1) | currentCarry;

        cpu.clearFlags();

        if ((result & 0x100) != 0) {
            cpu.setFlag(FLAG_CARRY);
        }

        if ((result & 0xFF) == 0) {
            cpu.setFlag(FLAG_ZERO);
        }

        reg.setValue(result);
    }

    void rotateLeftC(Reg8Bit reg) {
        int result = (reg.getValue() << 1);

        cpu.clearFlags();

        if ((result & 0x100) != 0) {
            cpu.setFlag(FLAG_CARRY);
            result = result | 0x01;
        }

        if ((result & 0xFF) == 0) {
            cpu.setFlag(FLAG_ZERO);
        }

        reg.setValue(result);
    }

    void rotateRightC(Reg8Bit reg) {
        int result = reg.getValue() >> 1;

        cpu.clearFlags();

        if ((reg.getValue() & 0x01) != 0) {
            cpu.setFlag(FLAG_CARRY);
            result = result | 0x80;
        }

        if ((result & 0xFF) == 0) {
            cpu.setFlag(FLAG_ZERO);
        }

        reg.setValue(result);
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
        reg.setValue(low | high);
    }

    void adc(Reg8Bit reg, int value) {
        int originalValue = reg.getValue();
        int result = originalValue + (value & 0xFF);

        if (cpu.isFlagSet(FLAG_CARRY)) {
            result = result + 1;
        }

        reg.setValue(result);

        cpu.clearFlags();
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