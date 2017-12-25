package es.shyri.jvboy.cpu;

import es.shyri.jvboy.cpu.register.Reg16Bit;
import es.shyri.jvboy.cpu.register.Reg8Bit;
import es.shyri.jvboy.memory.MemoryMap;

/**
 * Created by shyri on 02/07/17.
 */
public class CPU {
    public static final byte VBLANK_IRQ = 0x01;
    public static final byte LCDC_IRQ = 0x02;
    public static final byte TIMA_OVERFLOW_IRQ = 0x04;
    public static final byte SERIAL_IRQ = 0x08;
    public static final byte INPUT_IRQ = 0x10;

    protected static final int FLAG_ZERO = 0x80;
    protected static final int FLAG_NEGATIVE = 0x40;
    protected static final int FLAG_HALF = 0x20;
    protected static final int FLAG_CARRY = 0x10;
    protected static final int FLAG_NONE = 0x00;

    private ALU ALU;
    private LD LD;
    protected MemoryMap memoryMap;
    private Timers timers;

    protected Reg16Bit AF;
    protected Reg16Bit BC;
    protected Reg16Bit DE;
    protected Reg16Bit HL;
    protected Reg16Bit SP;
    public Reg16Bit PC;

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
        AF.setValue(0x01B0);
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

    public int nextStep() {
        cycles = 0;

        handleInterrupt();

        int opcode = memoryMap.read(PC.getValue()) & 0xFF;
        PC.inc();
        //        Log.d("CPU", "(" + count + ") Next OPCode " + Integer.toHexString(opCode));

        try {
            int opcodeCycles = runOpCode(opcode);

            cycles = cycles + opcodeCycles;

            timers.update(cycles);

        } catch (Throwable e) {
            //            Log.d("CPU", "PC: " + String.format("%04X", PC.getValue()));
            //            Log.d("CPU", "opcode: " + String.format("%04X", opcode));
            throw e;
        }

        return cycles;
    }

    protected int runOpCode(int opCode) {
        switch (opCode) {
            case 0x00: {
                // NOP
                return 4;
            }

            case 0x01: {
                // LD BC,nn
                LD.addrToReg8bit(PC.getValue(), BC.getLowReg());
                PC.inc();
                LD.addrToReg8bit(PC.getValue(), BC.getHighReg());
                PC.inc();

                return 12;
            }

            case 0x02: {
                // LD (BC),A
                LD.valToAddr(AF.getHigh(), BC.getValue());

                return 8;
            }

            case 0x03: {
                // INC BC
                BC.inc();
                return 8;
            }

            case 0x04: {
                // INC B
                ALU.inc(BC.getHighReg());
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

            case 0x07: {
                // RLAC
                ALU.rotateLeftC(AF.getHighReg());
                return 4;
            }

            case 0x08: {
                // LD (nn), SP

                byte lowN = memoryMap.read(PC.getValue());
                PC.inc();
                byte highN = memoryMap.read(PC.getValue());
                PC.inc();

                int address = (((highN << 8) & 0xFF00) | (lowN & 0xFF));
                LD.valToAddr(memoryMap.read(SP.getValue()), address);
                return 20;
            }

            case 0x09: {
                // ADD HL,BC
                ALU.add(HL, BC.getValue());

                return 8;
            }

            case 0x0A: {
                // LD A,(BC)
                LD.addrToReg8bit(BC.getValue(), AF.getHighReg());
                return 8;
            }

            case 0x0B: {
                // DEC BC
                BC.dec();
                return 8;
            }

            case 0x0C: {
                // INC C
                ALU.inc(BC.getLowReg());
                return 4;
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

            case 0x0F: {
                // RRCA
                ALU.rotateRightC(AF.getHighReg());

                return 4;
            }

            case 0x11: {
                // LD DE, nn
                LD.addrToReg8bit(PC.getValue(), DE.getLowReg());
                PC.inc();
                LD.addrToReg8bit(PC.getValue(), DE.getHighReg());
                PC.inc();

                return 12;
            }

            case 0x12: {
                // LD (DE),A
                LD.valToAddr(AF.getHigh(), DE.getValue());

                return 8;
            }

            case 0x13: {
                // INC DE
                DE.inc();

                return 8;
            }

            case 0x14: {
                // INC D
                ALU.inc(DE.getHighReg());
                return 4;
            }

            case 0x15: {
                // DEC D
                ALU.dec(DE.getHighReg());
                return 4;
            }

            case 0x16: {
                // LD D,n
                LD.addrToReg8bit(PC.getValue(), DE.getHighReg());
                PC.inc();

                return 8;
            }

            case 0x17: {
                // RLA
                ALU.rotateLeft(AF.getHighReg());
                return 4;
            }

            case 0x18: {
                // JR n
                byte n = memoryMap.read(PC.getValue());
                PC.inc();
                int jpAddress = PC.getValue() + n;

                PC.setValue(jpAddress);
                return 8;
            }

            case 0x19: {
                // ADD HL,DE
                ALU.add(HL, DE.getValue());

                return 8;
            }

            case 0x1A: {
                // LD A,(DE)
                LD.addrToReg8bit(DE.getValue(), AF.getHighReg());
                return 8;
            }

            case 0x1B: {
                // DEC DE
                DE.dec();
                return 8;
            }

            case 0x1C: {
                // INC E
                ALU.inc(DE.getLowReg());
                return 4;
            }

            case 0x1D: {
                // DEC E
                ALU.dec(DE.getLowReg());
                return 4;
            }

            case 0x1F: {
                // RRA
                ALU.rotateRight(AF.getHighReg());
                return 4;
            }

            case 0x1E: {
                // LD E, n
                LD.addrToReg8bit(PC.getValue(), DE.getLowReg());
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

            case 0x21: {
                // LD HL,nn
                LD.addrToReg8bit(PC.getValue(), HL.getLowReg());
                PC.inc();
                LD.addrToReg8bit(PC.getValue(), HL.getHighReg());
                PC.inc();

                return 12;
            }

            case 0x22: {
                // LD (HL+),A
                LD.valToAddr(AF.getHigh(), HL.getValue());
                HL.inc();

                return 8;
            }

            case 0x23: {
                // INC HL
                HL.inc();

                return 8;
            }

            case 0x24: {
                // INC H
                ALU.inc(HL.getHighReg());
                return 4;
            }

            case 0x25: {
                // DEC H
                ALU.dec(HL.getHighReg());
                return 4;
            }

            case 0x26: {
                // LD H,n
                LD.addrToReg8bit(PC.getValue(), HL.getHighReg());
                PC.inc();

                return 8;
            }

            case 0x27: {
                // DAA
                ALU.toBCD(AF.getHighReg());
                return 4;
            }

            case 0x28: {
                // JR Z,n
                if (isFlagSet(FLAG_ZERO)) {
                    int n = memoryMap.read(PC.getValue());
                    PC.inc();
                    PC.setValue(PC.getValue() + n);
                } else {
                    PC.inc();
                }

                return 8;
            }

            case 0x29: {
                // ADD HL,HL
                ALU.add(HL, HL.getValue());

                return 8;
            }

            case 0x2A: {
                // LD A,(HL+)
                LD.addrToReg8bit(HL.getValue(), AF.getHighReg());
                HL.inc();

                return 8;
            }

            case 0x2C: {
                // INC L
                ALU.inc(HL.getLowReg());
                return 4;
            }

            case 0x2D: {
                //DEC L
                ALU.dec(HL.getLowReg());

                return 4;
            }

            case 0x2E: {
                // LD L,n
                LD.addrToReg8bit(PC.getValue(), HL.getLowReg());
                PC.inc();
                return 8;
            }

            case 0x2F: {
                // CPL
                ALU.flipBits(AF.getHighReg());
                return 4;
            }

            case 0x30: {
                // JR NC,n
                if (isFlagSet(FLAG_CARRY)) {
                    PC.inc();
                } else {
                    int n = memoryMap.read(PC.getValue());
                    PC.inc();
                    PC.setValue(PC.getValue() + n);
                }

                return 8;
            }

            case 0x31: {
                // LD SP,nn
                LD.addrToReg8bit(PC.getValue(), SP.getLowReg());
                PC.inc();
                LD.addrToReg8bit(PC.getValue(), SP.getHighReg());
                PC.inc();

                return 12;
            }

            case 0x32: {
                // LD (HL-), A
                LD.valToAddr(AF.getHigh(), HL.getValue());
                HL.dec();

                return 8;
            }

            case 0x33: {
                // INC SP
                SP.inc();

                return 8;
            }

            case 0x34: {
                // INC (HL)
                int result = ALU.incVal(memoryMap.read(HL.getValue()));
                memoryMap.write(HL.getValue(), result);
                return 12;
            }

            case 0x35: {
                // DEC (HL)
                int result = ALU.decVal(memoryMap.read(HL.getValue()));
                memoryMap.write(HL.getValue(), result);
                return 12;
            }

            case 0x36: {
                // LD (HL),n
                memoryMap.write(HL.getValue(), memoryMap.read(PC.getValue()));
                PC.inc();

                return 12;
            }

            case 0x38: {
                // JR C,n
                if (!isFlagSet(FLAG_CARRY)) {
                    PC.inc();
                } else {
                    int n = memoryMap.read(PC.getValue());
                    PC.inc();
                    PC.setValue(PC.getValue() + n);
                }

                return 8;
            }

            case 0x39: {
                // ADD HL,SP
                ALU.add(HL, SP.getValue());

                return 8;
            }

            case 0x3A: {
                // LD A,(HL-)
                LD.addrToReg8bit(HL.getValue(), AF.getHighReg());
                HL.dec();

                return 8;
            }

            case 0x3E: {
                // LD A, #
                LD.addrToReg8bit(PC.getValue(), AF.getHighReg());
                PC.inc();
                return 8;
            }

            case 0x3C: {
                // INC A
                ALU.inc(AF.getHighReg());
                return 4;
            }

            case 0x3D: {
                // DEC A
                ALU.dec(AF.getHighReg());
                return 4;
            }

            case 0x40: {
                // LD B,B
                BC.setHigh(BC.getHigh());
                return 4;
            }

            case 0x41: {
                // LD B,B
                BC.setHigh(BC.getLow());
                return 4;
            }

            case 0x42: {
                // LD B,D
                BC.setHigh(DE.getHigh());
                return 4;
            }

            case 0x43: {
                // LD B,E
                BC.setHigh(DE.getLow());
                return 4;
            }

            case 0x44: {
                // LD B,H
                BC.setHigh(HL.getHigh());
                return 4;
            }

            case 0x45: {
                // LD B,L
                BC.setHigh(HL.getLow());
                return 4;
            }

            case 0x46: {
                // LD B,(HL)
                LD.addrToReg8bit(HL.getValue(), BC.getHighReg());

                return 8;
            }

            case 0x47: {
                // LD B,A
                BC.setHigh(AF.getHigh());
                return 4;
            }

            case 0x48: {
                // LD C,B
                BC.setLow(BC.getHigh());
                return 4;
            }

            case 0x49: {
                // LD C,C
                BC.setLow(BC.getLow());
                return 4;
            }

            case 0x4A: {
                // LD C,D
                BC.setLow(DE.getHigh());
                return 4;
            }

            case 0x4B: {
                // LD C,E
                BC.setLow(DE.getLow());
                return 4;
            }

            case 0x4C: {
                // LD C,H
                BC.setLow(HL.getHigh());
                return 4;
            }

            case 0x4D: {
                // LD C,L
                BC.setLow(HL.getLow());
                return 4;
            }

            case 0x4E: {
                // LD C,(HL)
                LD.addrToReg8bit(HL.getValue(), BC.getLowReg());

                return 8;
            }

            case 0x4F: {
                // LD C,A
                BC.setLow(AF.getHigh());
                return 4;
            }

            case 0x50: {
                // LD D,B
                DE.setHigh(BC.getHigh());
                return 4;
            }

            case 0x51: {
                // LD D,C
                DE.setHigh(BC.getLow());
                return 4;
            }

            case 0x52: {
                // LD D,D
                DE.setHigh(DE.getHigh());
                return 4;
            }

            case 0x53: {
                // LD D,E
                DE.setHigh(DE.getLow());
                return 4;
            }

            case 0x54: {
                // LD D,H
                DE.setHigh(HL.getHigh());
                return 4;
            }

            case 0x55: {
                // LD D,L
                DE.setHigh(HL.getLow());
                return 4;
            }

            case 0x56: {
                // LD D,(HL)
                LD.addrToReg8bit(HL.getValue(), DE.getHighReg());

                return 8;
            }

            case 0x57: {
                // LD D,A
                DE.setHigh(AF.getHigh());
                return 4;
            }

            case 0x58: {
                // LD E,B
                DE.setLow(BC.getHigh());
                return 4;
            }

            case 0x59: {
                // LD E,C
                DE.setLow(BC.getLow());
                return 4;
            }

            case 0x5A: {
                // LD E,D
                DE.setLow(DE.getHigh());
                return 4;
            }

            case 0x5B: {
                // LD E,E
                DE.setLow(DE.getLow());
                return 4;
            }

            case 0x5C: {
                // LD E,H
                DE.setLow(HL.getHigh());
                return 4;
            }

            case 0x5D: {
                // LD E,L
                DE.setLow(HL.getLow());
                return 4;
            }

            case 0x5E: {
                // LD E,(HL)
                LD.addrToReg8bit(HL.getValue(), DE.getLowReg());

                return 8;
            }

            case 0x5F: {
                // LD E,A
                DE.setLow(AF.getHigh());
                return 4;
            }

            case 0x60: {
                // LD H,B
                HL.setHigh(BC.getHigh());
                return 4;
            }

            case 0x61: {
                // LD H,C
                HL.setHigh(BC.getLow());
                return 4;
            }

            case 0x62: {
                // LD H,D
                HL.setHigh(DE.getHigh());
                return 4;
            }

            case 0x63: {
                // LD H,E
                HL.setHigh(DE.getLow());
                return 4;
            }

            case 0x64: {
                // LD H,H
                HL.setHigh(HL.getHigh());
                return 4;
            }

            case 0x65: {
                // LD H,L
                HL.setHigh(HL.getLow());
                return 4;
            }

            case 0x66: {
                // LD H,(HL)
                LD.addrToReg8bit(HL.getValue(), HL.getHighReg());
                return 8;
            }

            case 0x67: {
                // LD H,A
                HL.setHigh(AF.getHigh());
                return 4;
            }

            case 0x68: {
                // LD L,B
                HL.setLow(BC.getHigh());
                return 4;
            }

            case 0x69: {
                // LD L,C
                HL.setLow(BC.getLow());
                return 4;
            }

            case 0x6A: {
                // LD L,D
                HL.setLow(DE.getHigh());
                return 4;
            }

            case 0x6B: {
                // LD L,E
                HL.setLow(DE.getLow());
                return 4;
            }

            case 0x6C: {
                // LD L,H
                HL.setLow(HL.getHigh());
                return 4;
            }

            case 0x6D: {
                // LD L,L
                HL.setLow(HL.getLow());
                return 4;
            }

            case 0x6E: {
                // LD L,(HL)
                LD.addrToReg8bit(HL.getValue(), HL.getLowReg());

                return 8;
            }

            case 0x6F: {
                // LD L,A
                HL.setLow(AF.getHigh());
                return 4;
            }

            case 0x70: {
                // LD (HL), B
                LD.valToAddr(BC.getHigh(), HL.getValue());
                return 8;
            }

            case 0x71: {
                // LD (HL), C
                LD.valToAddr(BC.getLow(), HL.getValue());
                return 8;
            }

            case 0x72: {
                // LD (HL), D
                LD.valToAddr(DE.getHigh(), HL.getValue());
                return 8;
            }

            case 0x73: {
                // LD (HL), E
                LD.valToAddr(DE.getLow(), HL.getValue());
                return 8;
            }

            case 0x74: {
                // LD (HL), H
                LD.valToAddr(HL.getHigh(), HL.getValue());
                return 8;
            }

            case 0x75: {
                // LD (HL), L
                LD.valToAddr(HL.getLow(), HL.getValue());
                return 8;
            }

            case 0x76: {
                // LD (HL), n
                byte n = memoryMap.read(PC.getValue());
                PC.inc();
                LD.valToAddr(n, HL.getValue());

                return 12;
            }

            case 0x77: {
                // LD (HL), A
                LD.valToAddr(AF.getHigh(), HL.getValue());
                return 8;
            }

            case 0x78: {
                // LD A, B
                AF.setHigh(BC.getHigh());
                return 4;
            }

            case 0x79: {
                // LD A, C
                AF.setHigh(BC.getLow());
                return 4;
            }

            case 0x7A: {
                // LD A, D
                AF.setHigh(DE.getHigh());
                return 4;
            }

            case 0x7B: {
                // LD A, E
                AF.setHigh(DE.getLow());

                return 4;
            }

            case 0x7C: {
                // LD A, H
                AF.setHigh(HL.getHigh());

                return 4;
            }

            case 0x7D: {
                // LD A, L
                AF.setHigh(HL.getLow());
                return 4;
            }

            case 0x7E: {
                // LD A,(HL)
                LD.addrToReg8bit(HL.getValue(), AF.getHighReg());

                return 8;
            }

            case 0x7F: {
                // LD A, A
                AF.setHigh(AF.getHigh());
                return 4;
            }

            case 0x80: {
                // ADD A,B
                ALU.add(AF.getHighReg(), BC.getHigh());
                return 4;
            }

            case 0x82: {
                // ADD A,D
                ALU.add(AF.getHighReg(), DE.getHigh());
                return 4;
            }

            case 0x85: {
                // ADD A,L
                ALU.add(AF.getHighReg(), HL.getLow());
                return 4;
            }

            case 0x86: {
                // ADD A,(HL)
                ALU.add(AF.getHighReg(), memoryMap.read(HL.getValue()));
                return 8;
            }

            case 0x87: {
                // ADD A,A
                ALU.add(AF.getHighReg(), AF.getHigh());
                return 4;
            }

            case 0x89: {
                // ADC A,C
                ALU.adc(AF.getHighReg(), BC.getLow());
                return 4;
            }

            case 0x90: {
                // SUB B
                ALU.sub(AF.getHighReg(), BC.getHigh());
                return 4;
            }

            case 0x93: {
                // SUB E
                ALU.sub(AF.getHighReg(), DE.getLow());
                return 4;
            }

            case 0xA1: {
                // AND C
                ALU.and(AF.getHighReg(), BC.getLow());

                return 4;
            }

            case 0xA7: {
                // AND A
                ALU.and(AF.getHighReg(), AF.getHigh());

                return 4;
            }

            case 0xA9: {
                // XOR C
                ALU.xor(AF.getHighReg(), BC.getLow());

                return 4;
            }

            case 0xAD: {
                // XOR L
                ALU.xor(AF.getHighReg(), HL.getLow());

                return 4;
            }

            case 0xAF: {
                // XOR A
                ALU.xor(AF.getHighReg(), AF.getHigh());

                return 4;
            }

            case 0xAE: {
                // XOR (HL)
                ALU.xor(AF.getHighReg(), memoryMap.read(HL.getValue()));

                return 8;
            }

            case 0xB0: {
                // OR B
                ALU.or(AF.getHighReg(), BC.getHigh());

                return 4;
            }

            case 0xB1: {
                // OR C
                ALU.or(AF.getHighReg(), BC.getLow());

                return 4;
            }

            case 0xB6: {
                // OR (HL)
                ALU.or(AF.getHighReg(), memoryMap.read(HL.getValue()));

                return 4;
            }

            case 0xB7: {
                // OR A
                ALU.or(AF.getHighReg(), AF.getHigh());

                return 4;
            }

            case 0xBE: {
                // CP (HL)
                ALU.cp(AF.getHighReg(), memoryMap.read(HL.getValue()));

                return 8;
            }

            case 0xC0: {
                // RET NZ
                if (!isFlagSet(FLAG_ZERO)) {
                    stackPop(PC);
                }

                return 8;
            }

            case 0xC1: {
                // POP BC
                stackPop(BC);
                return 12;
            }

            case 0xC2: {
                // JP NZ,nn
                if (!isFlagSet(FLAG_ZERO)) {
                    byte lowN = memoryMap.read(PC.getValue());
                    PC.inc();
                    byte highN = memoryMap.read(PC.getValue());
                    PC.setLow(lowN);
                    PC.setHigh(highN);
                } else {
                    PC.inc();
                    PC.inc();
                }

                return 12;
            }

            case 0xC3: {
                // JP nn
                byte lowN = memoryMap.read(PC.getValue());
                PC.inc();
                byte highN = memoryMap.read(PC.getValue());
                PC.setLow(lowN);
                PC.setHigh(highN);

                return 12;
            }

            case 0xC4: {
                // CALL NZ,nn
                if (!isFlagSet(FLAG_ZERO)) {
                    byte lowN = memoryMap.read(PC.getValue());
                    PC.inc();
                    byte highN = memoryMap.read(PC.getValue());
                    PC.inc();

                    stackPush(PC);

                    PC.setLow(lowN);
                    PC.setHigh(highN);
                }

                return 12;
            }

            case 0xC5: {
                // PUSH BC
                stackPush(BC);

                return 16;
            }

            case 0xC6: {
                // ADD A, #
                ALU.add(AF.getHighReg(), memoryMap.read(PC.getValue()));
                PC.inc();

                return 8;
            }

            case 0xC7: {
                // RST $00h
                stackPush(PC);
                PC.setHigh(0x00);
                PC.setLow(0x00);
                return 32;
            }

            case 0xC8: {
                // RET Z
                if (isFlagSet(FLAG_ZERO)) {
                    stackPop(PC);
                }

                return 8;
            }

            case 0xC9: {
                // RET
                stackPop(PC);

                return 8;
            }

            case 0xCA: {
                // JP Z,nn
                if (isFlagSet(FLAG_ZERO)) {
                    byte lowN = memoryMap.read(PC.getValue());
                    PC.inc();
                    byte highN = memoryMap.read(PC.getValue());
                    PC.setLow(lowN);
                    PC.setHigh(highN);
                } else {
                    PC.inc();
                    PC.inc();
                }

                return 12;
            }

            case 0xCB: {
                // CB opcode
                return handleCBopcode(memoryMap.read(PC.getValue()) & 0xFF);
            }

            case 0xCD: {
                // CALL nn
                byte lowN = memoryMap.read(PC.getValue());
                PC.inc();
                byte highN = memoryMap.read(PC.getValue());
                PC.inc();

                stackPush(PC);

                PC.setLow(lowN);
                PC.setHigh(highN);

                return 12;
            }

            case 0xCE: {
                // ADC A, #
                ALU.adc(AF.getHighReg(), memoryMap.read(PC.getValue()));
                PC.inc();

                return 8;
            }

            case 0xD0: {
                // RET NC
                if (!isFlagSet(FLAG_CARRY)) {
                    stackPop(PC);
                }

                return 8;
            }
            case 0xD1: {
                // POP DE
                stackPop(DE);
                return 12;
            }

            case 0xD5: {
                // PUSH DE
                stackPush(DE);

                return 16;
            }

            case 0xD6: {
                // SUB #
                ALU.sub(AF.getHighReg(), memoryMap.read(PC.getValue()));
                PC.inc();
                return 8;
            }

            case 0xD8: {
                // RET C
                if (isFlagSet(FLAG_CARRY)) {
                    stackPop(PC);
                }

                return 8;
            }

            case 0xD9: {
                // RETI
                stackPop(PC);
                IME = true;

                return 8;
            }

            case 0xDF: {
                // RST $18h
                stackPush(PC);
                PC.setHigh(0x00);
                PC.setLow(0x18);
                return 32;
            }

            case 0xE0: {
                // LD ($FF00 + n), A
                if (memoryMap.read(PC.getValue()) == 0x50) { // TODO fix this unperformant way of doing it
                    memoryMap.disableBIOS();
                    PC.setValue(0x0100);
                } else {
                    int address = 0xFF00 + (memoryMap.read(PC.getValue()) & 0xFF);
                    LD.valToAddr(AF.getHigh(), address);
                    PC.inc();
                }
                return 12;
            }

            case 0xE1: {
                // POP HL
                stackPop(HL);
                return 12;
            }

            case 0xE2: {
                // LD ($FF00+C),A
                memoryMap.write(0xFF00 + BC.getLow(), AF.getHigh());
                return 8;
            }

            case 0xE5: {
                // PUSH HL
                stackPush(HL);

                return 16;
            }

            case 0xE6: {
                // AND #
                ALU.and(AF.getHighReg(), memoryMap.read(PC.getValue()));
                PC.inc();
                return 8;
            }

            case 0xE9: {
                // JP (HL)
                PC.setValue(HL.getValue());

                return 4;
            }
            case 0xEA: {
                // LD (nn), A
                byte lowN = memoryMap.read(PC.getValue());
                PC.inc();
                byte highN = memoryMap.read(PC.getValue());
                PC.inc();

                int address = (((highN << 8) & 0xFF00) | (lowN & 0xFF));
                LD.valToAddr(AF.getHigh(), address);
                return 16;
            }

            case 0xEE: {
                // XOR #
                ALU.xor(AF.getHighReg(), memoryMap.read(PC.getValue()));
                PC.inc();

                return 8;
            }

            case 0xEF: {
                // RST $28h
                stackPush(PC);
                PC.setHigh(0x00);
                PC.setLow(0x28);
                return 32;
            }

            case 0xF0: {
                // LD A,($FF00 + n)
                LD.addrToReg8bit(0xFF00 + (memoryMap.read(PC.getValue()) & 0xFF), AF.getHighReg());
                PC.inc();

                return 12;
            }

            case 0xF1: {
                stackPop(AF);

                return 12;
            }

            case 0xF3: {
                // DI
                IME = false;
                return 4;
            }

            case 0xF5: {
                // PUSH AF
                stackPush(AF);

                return 16;
            }

            case 0xF6: {
                // OR #
                ALU.or(AF.getHighReg(), memoryMap.read(PC.getValue()));
                PC.inc();

                return 8;
            }

            case 0xF8: {
                // LDHL,SP,n

                byte n = memoryMap.read(PC.getValue());
                PC.inc();

                int value = SP.getValue() + n;

                HL.setHigh(((value & 0xFF00) >> 8));
                HL.setLow((value & 0x00FF));

                return 12;
            }

            case 0xF9: {
                // LD SP,HL
                SP.setLow(HL.getLow());
                SP.setHigh(HL.getHigh());

                return 8;
            }

            case 0xFA: {
                // LD A,(nn)
                byte lowN = memoryMap.read(PC.getValue());
                PC.inc();
                byte highN = memoryMap.read(PC.getValue());
                PC.inc();

                int address = (((highN << 8) & 0xFF00) | (lowN & 0xFF));
                LD.addrToReg8bit(address, AF.getHighReg());

                return 16;
            }

            case 0xFB: {
                // EI
                IME = true;
                return 4;
            }

            case 0xFE: {
                // CP
                ALU.cp(AF.getHighReg(), memoryMap.read(PC.getValue()));
                PC.inc();
                return 8;
            }

            case 0xFF: {
                // RST $38h
                stackPush(PC);
                PC.setHigh(0x00);
                PC.setLow(0x38);
                return 32;
            }
        }

        throw new IllegalStateException("OpCode not implemented " + Integer.toHexString(opCode));
    }

    private int handleCBopcode(int opcode) {
        PC.inc();

        switch (opcode) {
            case 0x11: {
                // RL C
                ALU.rotateLeft(BC.getLowReg());
                return 8;
            }

            case 0x19: {
                // RR C
                ALU.rotateRight(BC.getLowReg());
                return 8;
            }

            case 0x1A: {
                // RR D
                ALU.rotateRight(DE.getHighReg());
                return 8;
            }

            case 0x1B: {
                // RR E
                ALU.rotateRight(DE.getLowReg());
                return 8;
            }

            case 0x27: {
                // SLA A
                ALU.shiftLeft(AF.getHighReg());
                return 8;
            }

            case 0x37: {
                // SWAP A
                ALU.swap(AF.getHighReg());
                return 8;
            }

            case 0x38: {
                // SRL B
                ALU.shiftRightLogically(BC.getHighReg());
                return 8;
            }

            case 0x40: {
                // BIT 0, B
                testBitInReg(0, BC.getHighReg());
                return 8;
            }

            case 0x48: {
                // BIT 1, B
                testBitInReg(1, BC.getHighReg());
                return 8;
            }

            case 0x50: {
                // BIT 2, B
                testBitInReg(2, BC.getHighReg());
                return 8;
            }

            case 0x57: {
                // BIT 2, A
                testBitInReg(2, AF.getHighReg());
                return 8;
            }

            case 0x58: {
                // BIT 3, B
                testBitInReg(3, BC.getHighReg());
                return 8;
            }

            case 0x5f: {
                // BIT 3, A
                testBitInReg(3, AF.getHighReg());
                return 8;
            }

            case 0x60: {
                // BIT 4, B
                testBitInReg(4, BC.getHighReg());
                return 8;
            }

            case 0x61: {
                // BIT 4, C
                testBitInReg(4, BC.getLowReg());
                return 8;
            }

            case 0x68: {
                // BIT 5, B
                testBitInReg(5, BC.getHighReg());
                return 8;
            }

            case 0x69: {
                // BIT 5, C
                testBitInReg(5, BC.getLowReg());
                return 8;
            }

            case 0x6F: {
                // BIT 5, A
                testBitInReg(5, AF.getHighReg());
                return 8;
            }

            case 0x77: {
                // BIT 6, A
                testBitInReg(6, AF.getHighReg());
                return 8;
            }

            case 0x7C: {
                // BIT 7, H
                testBitInReg(7, HL.getHighReg());
                return 8;
            }

            case 0x7E: {
                // RES 7,(HL)
                int value = memoryMap.read(HL.getValue());
                memoryMap.write(HL.getValue(), ALU.resetBit(value, 7));
                PC.inc();
                return 16;
            }

            case 0x7F: {
                // BIT 7, A
                testBitInReg(7, AF.getHighReg());
                return 8;
            }

            case 0x86: {
                // RES 0,(HL)
                int value = memoryMap.read(HL.getValue());
                memoryMap.write(HL.getValue(), ALU.resetBit(value, 0));
                PC.inc();
                return 16;
            }

            case 0x87: {
                // RES 0, A
                ALU.resetBit(AF.getHighReg(), 0);
                return 8;
            }
        }

        throw new IllegalStateException("OpCode not implemented CB " + Integer.toHexString(opcode));
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
        //        IME = true; TODO This seems not to be ok, RET or RETI should decide wether to enable interrupts again or
        // not
    }

    private void handleVBlankIRQ() {
        PC.setValue(0x0040);
        memoryMap.write(0xFF0F, (memoryMap.read(0xFF0F) & 0xFE));
    }

    private void handleLCDCIRQ() {
        PC.setValue(0x0048);
        memoryMap.write(0xFF0F, (memoryMap.read(0xFF0F) & 0xFD));
    }

    private void handleTimerOverflowIRQ() {
        PC.setValue(0x0050);
        memoryMap.write(0xFF0F, (memoryMap.read(0xFF0F) & 0xFB));
    }

    private void handleSerialIRQ() {
        PC.setValue(0x0058);
        memoryMap.write(0xFF0F, (memoryMap.read(0xFF0F) & 0xF7));
    }

    private void handleInputInt() {
        PC.setValue(0x0060);
        memoryMap.write(0xFF0F, (memoryMap.read(0xFF0F) & 0xEF));
    }

    void stackPush(Reg16Bit reg) {
        SP.dec();
        memoryMap.write(SP.getValue(), reg.getHigh());
        SP.dec();
        memoryMap.write(SP.getValue(), reg.getLow());
    }

    void stackPop(Reg16Bit reg) {
        reg.setLow(memoryMap.read(SP.getValue()));
        SP.inc();
        reg.setHigh(memoryMap.read(SP.getValue()));
        SP.inc();
    }

    void clearFlags() {
        AF.setLow(FLAG_NONE);
    }

    void setFlag(int flag) {
        AF.setLow((AF.getLow() | flag) & 0xFF);
    }

    void resetFlag(int flag) {
        AF.setLow((AF.getLow() & ~flag) & 0xFF);
    }

    protected boolean isFlagSet(int flag) {
        return (AF.getLow() & flag & 0xFF) != 0;
    }

    void testBitInReg(int bitNum, Reg8Bit reg) {
        byte bit = (byte) (0x01 << bitNum);
        int result = bit & reg.getValue();

        boolean cFlag = isFlagSet(FLAG_CARRY);

        clearFlags();

        if (result == 0) {
            setFlag(FLAG_ZERO);
        }

        setFlag(FLAG_HALF);

        if (cFlag) {
            setFlag(FLAG_CARRY);
        }

    }

    public void dumpState() {
        //        Log.d("CPU", "AF:" + String.format("%04X", AF.getValue()));
        //        Log.d("CPU", "BC:" + String.format("%04X", BC.getValue()));
        //        Log.d("CPU", "DE:" + String.format("%04X", DE.getValue()));
        //        Log.d("CPU", "HL:" + String.format("%04X", HL.getValue()));
        //        Log.d("CPU", "SP:" + String.format("%04X", SP.getValue()));
        //        Log.d("CPU", "PC:" + String.format("%04X", PC.getValue()));
        //        Log.d("CPU", "[PC]:" + String.format("%04X", memoryMap.read(PC.getValue())));
        //        Log.d("CPU", "==============================================");
    }

    public void requestInterrupt(byte interrupt) {
        byte IF = memoryMap.read(0xFF0F);
        IF = (byte) (IF | interrupt);
        memoryMap.write(0xFF0F, IF);
    }
}