package com.a424appslab.androidboy.cpu;

import android.util.Log;

import com.a424appslab.androidboy.cpu.register.Reg16Bit;
import com.a424appslab.androidboy.memory.MemoryMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shyri on 02/07/17.
 */

public class CPU {
    public static final byte VBLANK_IRQ = 0x01;
    public static final byte LCDC_IRQ = 0x02;
    public static final byte TIMA_OVERFLOW_IRQ = 0x04;
    public static final byte SERIAL_IRQ = 0x08;
    public static final byte INPUT_IRQ = 0x10;

    static final byte FLAG_ZERO = (byte) 0x80;
    static final byte FLAG_NEGATIVE = 0x40;
    static final byte FLAG_HALF = 0x20;
    static final byte FLAG_CARRY = 0x10;
    static final byte FLAG_NONE = 0x00;

    private ALU ALU;
    private LD LD;
    private MemoryMap memoryMap;
    private Timers timers;

    Reg16Bit AF;
    Reg16Bit BC;
    Reg16Bit DE;
    Reg16Bit HL;
    Reg16Bit SP;
    Reg16Bit PC;

    List<Byte> opcodesShown = new ArrayList<>();

    private boolean IME = true;

    private int cycles;

    public CPU() {
        AF = new Reg16Bit();
        BC = new Reg16Bit();
        DE = new Reg16Bit();
        HL = new Reg16Bit();
        SP = new Reg16Bit();
        PC = new Reg16Bit();
    }

    public void init(MemoryMap memoryMap, Timers timers) {
        AF.setValue(0x010B);
        BC.setValue(0x0013);
        DE.setValue(0x00D8);
        HL.setValue(0x014D);

        SP.setValue(0xFFFE);
        PC.setValue(0x0100);

        this.ALU = new ALU(this);
        this.LD = new LD(memoryMap);
        this.timers = timers;
        this.memoryMap = memoryMap;
    }

    public void nextStep() {
        cycles = 0;

        byte opcode = memoryMap.read(PC.getValue());
        PC.inc();
        //        Log.d("CPU", "(" + count + ") Next OPCode " + Integer.toHexString(opCode));

        handleInterrupt();

        int opcodeCycles = runOpCode(opcode);

        cycles = cycles + opcodeCycles;

        timers.update(cycles);

        if (!opcodesShown.contains(opcode)) {
            Log.d("CPU", "opcode: " + String.format("%04X", opcode));
            dumpState();
            opcodesShown.add(opcode);
        }
    }

    private int runOpCode(byte opCode) {
        switch (opCode) {
            case 0x00: {
                // NOP
                return 4;
            }
            case 0x05: {
                //DEC B
                ALU.dec(BC.getHighReg());

                return 4;
            }
            case 0x06: {
                // LD B,n
                LD.addrToReg8bit(PC.getValue(), BC.getHighReg());
                PC.inc();

                return 8;
            }
            case 0x0D: {
                // DEC C
                ALU.dec(BC.getLowReg());
                return 4;
            }
            case 0x0E: {
                // LD C,n
                LD.addrToReg8bit(PC.getValue(), BC.getLowReg());
                PC.inc();

                return 8;
            }
            case 0x20: {
                // JR NZ,n
                if (isFlagSet(FLAG_ZERO)) {
                    PC.inc();
                } else {
                    int n = memoryMap.read(PC.getValue());
                    PC.inc();
                    PC.setValue(PC.getValue() + n);
                }

                return 8;
            }
            case (byte) 0x21: {
                // LD HL,nn
                LD.addrToReg8bit(PC.getValue(), HL.getLowReg());
                PC.inc();
                LD.addrToReg8bit(PC.getValue(), HL.getHighReg());
                PC.inc();

                return 12;
            }
            case (byte) 0x32: {
                // LDD (HL), A
                LD.addrToReg8bit(HL.getValue(), AF.getHighReg());
                HL.dec();

                return 8;
            }
            case 0x3E: {
                LD.addrToReg8bit(PC.getValue(), AF.getHighReg());
                PC.inc();
                return 8;
            }
            case (byte) 0xAF: {
                // XOR AF
                ALU.xor(AF.getHigh());

                return 4;
            }

            case (byte) 0xC3: {
                // JP nn
                byte lowN = memoryMap.read(PC.getValue());
                PC.inc();
                byte highN = memoryMap.read(PC.getValue());
                PC.setLow(lowN);
                PC.setHigh(highN);

                return 12;
            }

            case (byte) 0xE0: {
                int address = 0xFF00 + memoryMap.read(PC.getValue());
                LD.valToAddr(AF.getHighReg(), address);
                PC.inc();

                return 12;
            }

            case (byte) 0xF0: {
                LD.addrToReg8bit(0xFF00 + memoryMap.read(PC.getValue()), AF.getHighReg());
                PC.inc();

                return 12;
            }

            case (byte) 0xF3: {
                IME = false;
                return 4;
            }

            case (byte) 0xFE: {
                ALU.cp(PC.getValue());
                PC.inc();
                return 8;
            }
        }

        throw new IllegalStateException("OpCode not implemented " + Integer.toHexString(opCode));
    }

    private void handleInterrupt() {
        if (!IME) {
            return;
        }

        byte IE = memoryMap.read(0xFFFF);   // Interrupt Enable
        byte IF = memoryMap.read(0xFF0F);   // Interrupt Flag

        byte interrupt = (byte) (IE & IF);

        if (interrupt == 0x00) {
            // No interrupts
            return;
        }

        IME = false;
        stackPush(PC);
        // Follow intrrupt priority
        if ((interrupt & VBLANK_IRQ) > 0) {
            handleVBlankIRQ();
        } else if ((interrupt & LCDC_IRQ) > 0) {
            handleLCDCIRQ();
        } else if ((interrupt & TIMA_OVERFLOW_IRQ) > 0) {
            handleTimerOverflowIRQ();
        } else if ((interrupt & SERIAL_IRQ) > 0) {
            handleSerialIRQ();
        } else if ((interrupt & INPUT_IRQ) > 0) {
            handleInputInt();
        }
        cycles = cycles + 20;
        IME = true;
    }

    private void handleVBlankIRQ() {
        PC.setValue(0x0040);
        memoryMap.write(0xFF0F, (byte) (memoryMap.read(0xFF0F) & 0xFE));
    }

    private void handleLCDCIRQ() {
        PC.setValue(0x0048);
        memoryMap.write(0xFF0F, (byte) (memoryMap.read(0xFF0F) & 0xFD));
    }

    private void handleTimerOverflowIRQ() {
        PC.setValue(0x0050);
        memoryMap.write(0xFF0F, (byte) (memoryMap.read(0xFF0F) & 0xFB));
    }

    private void handleSerialIRQ() {
        PC.setValue(0x0058);
        memoryMap.write(0xFF0F, (byte) (memoryMap.read(0xFF0F) & 0xF7));
    }

    private void handleInputInt() {
        PC.setValue(0x0060);
        memoryMap.write(0xFF0F, (byte) (memoryMap.read(0xFF0F) & 0xEF));
    }

    void stackPush(Reg16Bit reg) {
        SP.dec();
        memoryMap.write(SP.getValue(), reg.getHigh());
        SP.dec();
        memoryMap.write(SP.getValue(), reg.getLow());
    }

    void clearFlags() {
        AF.setLow(FLAG_NONE);
    }

    void clearWithFlag(byte flag) {
        AF.setLow(flag);
    }

    void setFlag(int flag) {
        AF.setLow((byte) (AF.getLow() | flag));
    }

    boolean isFlagSet(byte flag) {
        return (AF.getLow() & flag) != 0;
    }

    public void dumpState() {
        Log.d("CPU", "AF:" + String.format("%04X", AF.getValue()));
        Log.d("CPU", "BC:" + String.format("%04X", BC.getValue()));
        Log.d("CPU", "DE:" + String.format("%04X", DE.getValue()));
        Log.d("CPU", "HL:" + String.format("%04X", HL.getValue()));
        Log.d("CPU", "SP:" + String.format("%04X", SP.getValue()));
        Log.d("CPU", "PC:" + String.format("%04X", PC.getValue()));
        Log.d("CPU", "[PC]:" + String.format("%04X", memoryMap.read(PC.getValue())));
        Log.d("CPU", "==============================================");
    }

    public void requestInterrupt(byte interrupt) {
        byte IF = memoryMap.read(0xFF0F);
        IF = (byte) (IF | interrupt);
        memoryMap.write(0xFF0F, IF);
    }
}
