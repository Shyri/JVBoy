package es.shyri.jvboy.cpu;

import com.sun.javafx.application.PlatformImpl;

import es.shyri.jvboy.cpu.register.Reg16Bit;

/**
 * Created by shyri on 02/10/2017.
 */
public class CPUDebugger extends CPU {
    private final CPUStatusOutput debugOutput;
    private final DisassemblyOutput disassemblyOutput;
    private String[] disassembledCode = new String[0x10000];

    private int currentPC;
    private Reg16Bit VirtualPC = new Reg16Bit();

    private boolean debugEnabled = false;

    public CPUDebugger(CPUStatusOutput debugOutput, DisassemblyOutput disassemblyOutput) {
        this.debugOutput = debugOutput;
        this.disassemblyOutput = disassemblyOutput;
    }

    @Override
    public int nextStep() {
        currentPC = PC.getValue();
        VirtualPC.setValue(currentPC);

        int cycles = super.nextStep();

        if (debugEnabled) {
            updateStatus();
        }

        return cycles;
    }

    @Override
    protected int runOpCode(int opCode) {
        dumpInstruction(opCode);

        return super.runOpCode(opCode);
    }

    private void dumpInstruction(int opCode) {
        if (disassembledCode[currentPC] != null) {
            return;
        }

        VirtualPC.inc();

        switch (opCode) {
            case 0x00: {
                dumpInstruction("NOP");
                break;
            }

            case 0x01: {
                byte low = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                byte high = memoryMap.read(VirtualPC.getValue());
                int result = (((high << 8) & 0xFF00) | (low & 0xFF));

                dumpInstruction("LD BC," + format4(result) + "h");
                break;
            }

            case 0x02: {
                dumpInstruction("LD (BC),A");
                break;
            }

            case 0x03: {
                dumpInstruction("INC BC");
                break;
            }

            case 0x04: {
                dumpInstruction("INC B");
                break;
            }

            case 0x05: {
                dumpInstruction("DEC B");
                break;
            }

            case 0x06: {
                byte result = memoryMap.read(VirtualPC.getValue());
                dumpInstruction("LD B," + format2(result) + "h");

                break;
            }

            case 0x07: {
                dumpInstruction("RLCA");
                break;
            }

            case 0x08: {
                byte low = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                byte high = memoryMap.read(VirtualPC.getValue());
                int result = (((high << 8) & 0xFF00) | (low & 0xFF));

                dumpInstruction("LD ($" + format4(result) + "),SP");
                break;
            }

            case 0x09: {
                dumpInstruction("ADD HL,BC");
                break;
            }

            case 0x0A: {
                dumpInstruction("LD A,(BC)");
                break;
            }

            case 0x0B: {
                dumpInstruction("DEC BC");
                break;
            }

            case 0x0C: {
                dumpInstruction("INC C");
                break;
            }

            case 0x0D: {
                dumpInstruction("DEC C");
                break;
            }

            case 0x0E: {
                byte result = memoryMap.read(VirtualPC.getValue());
                dumpInstruction("LD C," + format2(result) + "h");
                break;
            }

            case 0x0F: {
                dumpInstruction("RRCA");
                break;
            }

            case 0x11: {
                byte low = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                byte high = memoryMap.read(VirtualPC.getValue());
                int result = (((high << 8) & 0xFF00) | (low & 0xFF));

                dumpInstruction("LD DE," + format4(result) + "h");
                break;
            }

            case 0x12: {
                dumpInstruction("LD (DE),A");
                break;
            }

            case 0x13: {
                dumpInstruction("INC DE");
                break;
            }

            case 0x14: {
                dumpInstruction("INC D");
                break;
            }

            case 0x15: {
                dumpInstruction("DEC D");
                break;
            }

            case 0x16: {
                int result = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                dumpInstruction("LD D," + format2(result) + "h");
                break;
            }

            case 0x17: {
                dumpInstruction("RLA");
                break;
            }

            case 0x18: {
                byte result = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                dumpInstruction("JR Addr_" + format4(VirtualPC.getValue() + result));

                break;
            }

            case 0x19: {
                dumpInstruction("ADD HL,DE");
                break;
            }
            case 0x1A: {
                dumpInstruction("LD A,(DE)");
                break;
            }

            case 0x1B: {
                dumpInstruction("DEC DE");
                break;
            }

            case 0x1D: {
                dumpInstruction("DEC E");
                break;
            }

            case 0x1C: {
                dumpInstruction("INC E");
                break;
            }

            case 0x1E: {
                int result = memoryMap.read(VirtualPC.getValue());
                dumpInstruction("LD E," + format2(result) + "h");

                break;
            }

            case 0x1F: {
                dumpInstruction("RRA");
                break;
            }

            case 0x20: {
                int result = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                dumpInstruction("JR NZ,Addr_" + format4(VirtualPC.getValue() + result));

                break;
            }

            case 0x21: {
                byte low = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                byte high = memoryMap.read(VirtualPC.getValue());
                int result = (((high << 8) & 0xFF00) | (low & 0xFF));

                dumpInstruction("LD HL," + format4(result) + "h");
                break;
            }

            case 0x22: {
                dumpInstruction("LD (HL+), A");
                break;
            }

            case 0x23: {
                dumpInstruction("INC HL");
                break;
            }

            case 0x24: {
                dumpInstruction("INC H");
                break;
            }

            case 0x25: {
                dumpInstruction("DEC H");
                break;
            }

            case 0x26: {
                int result = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                dumpInstruction("LD H," + format2(result) + "h");
                break;
            }

            case 0x27: {
                dumpInstruction("DAA");
                break;
            }

            case 0x28: {
                byte result = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                dumpInstruction("JR Z,Addr_" + format4(VirtualPC.getValue() + result));

                break;
            }

            case 0x29: {
                dumpInstruction("ADD HL,HL");
                break;
            }

            case 0x2A: {
                dumpInstruction("LD A,(HL+)");
                break;
            }

            case 0x2B: {
                dumpInstruction("DEC HL");
                break;
            }

            case 0x2C: {
                dumpInstruction("INC L");
                break;
            }

            case 0x2D: {
                dumpInstruction("DEC L");
                break;
            }

            case 0x2E: {
                byte result = memoryMap.read(VirtualPC.getValue());
                dumpInstruction("LD L," + format2(result) + "h");
                break;
            }

            case 0x2F: {
                dumpInstruction("CPL");
                break;
            }

            case 0x30: {
                int result = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                dumpInstruction("JR NC,Addr_" + format4(VirtualPC.getValue() + result));

                break;
            }
            case 0x31: {
                byte low = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                byte high = memoryMap.read(VirtualPC.getValue());
                int result = (((high << 8) & 0xFF00) | (low & 0xFF));

                dumpInstruction("LD SP," + format4(result) + "h");
                break;
            }

            case 0x32: {
                dumpInstruction("LD (HL-),A");
                break;
            }

            case 0x33: {
                dumpInstruction("INC SP");
                break;
            }

            case 0x34: {
                dumpInstruction("INC (HL)");
                break;
            }

            case 0x35: {
                dumpInstruction("DEC (HL)");
                break;
            }

            case 0x36: {
                int result = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                dumpInstruction("LD (HL)," + format2(result) + "h");
            }

            case 0x37: {
                dumpInstruction("SCF");
            }

            case 0x38: {
                int result = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                dumpInstruction("JR C,Addr_" + format4(VirtualPC.getValue() + result));

                break;
            }

            case 0x39: {
                dumpInstruction("ADD HL,SP");
                break;
            }

            case 0x3A: {
                dumpInstruction("LD A,(HL-)");
                break;
            }

            case 0x3E: {
                int result = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                dumpInstruction("LD A," + format2(result) + "h");
                break;
            }

            case 0x3B: {
                dumpInstruction("DEC SP");
                break;
            }

            case 0x3C: {
                dumpInstruction("INC A");
                break;
            }

            case 0x3D: {
                dumpInstruction("DEC A");
                break;
            }

            case 0x3F: {
                dumpInstruction("CCF");
                break;
            }

            case 0x40: {
                dumpInstruction("LD B,B");
                break;
            }

            case 0x41: {
                dumpInstruction("LD B,C");
                break;
            }

            case 0x42: {
                dumpInstruction("LD B,D");
                break;
            }

            case 0x43: {
                dumpInstruction("LD B,E");
                break;
            }

            case 0x44: {
                dumpInstruction("LD B,H");
                break;
            }

            case 0x45: {
                dumpInstruction("LD B,L");
                break;
            }

            case 0x46: {
                dumpInstruction("LD B,(HL)");
                break;
            }

            case 0x47: {
                dumpInstruction("LD B,A");
                break;
            }

            case 0x48: {
                dumpInstruction("LD C,B");
                break;
            }

            case 0x49: {
                dumpInstruction("LD C,C");
                break;
            }

            case 0x4A: {
                dumpInstruction("LD C,D");
                break;
            }

            case 0x4B: {
                dumpInstruction("LD C,E");
                break;
            }

            case 0x4C: {
                dumpInstruction("LD C,H");
                break;
            }

            case 0x4D: {
                dumpInstruction("LD C,L");
                break;

            }

            case 0x4E: {
                dumpInstruction("LD C,(HL)");
                break;
            }

            case 0x4F: {
                dumpInstruction("LD C,A");
                break;
            }

            case 0x50: {
                dumpInstruction("LD D,B");
                break;
            }

            case 0x51: {
                dumpInstruction("LD D,C");
                break;
            }

            case 0x52: {
                dumpInstruction("LD D,D");
                break;
            }

            case 0x53: {
                dumpInstruction("LD D,E");
                break;
            }

            case 0x54: {
                dumpInstruction("LD D,H");
                break;
            }

            case 0x55: {
                dumpInstruction("LD D,L");
                break;
            }

            case 0x56: {
                dumpInstruction("LD D,(HL)");
                break;
            }

            case 0x57: {
                dumpInstruction("LD D,A");
                break;
            }

            case 0x58: {
                dumpInstruction("LD E,B");
                break;
            }

            case 0x59: {
                dumpInstruction("LD E,C");
                break;
            }

            case 0x5A: {
                dumpInstruction("LD E,D");
                break;
            }

            case 0x5B: {
                dumpInstruction("LD E,E");
                break;
            }

            case 0x5C: {
                dumpInstruction("LD E,H");
                break;
            }

            case 0x5D: {
                dumpInstruction("LD E,L");
                break;
            }

            case 0x5E: {
                dumpInstruction("LD E,(HL)");
                break;
            }

            case 0x5F: {
                dumpInstruction("LD E,A");
                break;
            }

            case 0x60: {
                dumpInstruction("LD H,B");
                break;
            }

            case 0x61: {
                dumpInstruction("LD H,C");
                break;
            }

            case 0x62: {
                dumpInstruction("LD H,D");
                break;
            }

            case 0x63: {
                dumpInstruction("LD H,E");
                break;
            }

            case 0x64: {
                dumpInstruction("LD H,H");
                break;
            }

            case 0x65: {
                dumpInstruction("LD H,L");
                break;
            }

            case 0x66: {
                dumpInstruction("LD H,(HL)");
                break;
            }

            case 0x67: {
                dumpInstruction("LD H,A");
                break;
            }

            case 0x68: {
                dumpInstruction("LD L,B");
                break;
            }

            case 0x69: {
                dumpInstruction("LD L,C");
                break;
            }

            case 0x6A: {
                dumpInstruction("LD L,D");
                break;
            }

            case 0x6B: {
                dumpInstruction("LD L,E");
                break;
            }

            case 0x6C: {
                dumpInstruction("LD L,H");
                break;
            }

            case 0x6D: {
                dumpInstruction("LD L,L");
                break;
            }

            case 0x6E: {
                dumpInstruction("LD L,(HL)");
                break;
            }

            case 0x6F: {
                dumpInstruction("LD L,A");
            }

            case 0x70: {
                dumpInstruction("LD (HL),B");
                break;
            }

            case 0x71: {
                dumpInstruction("LD (HL),C");
                break;
            }

            case 0x72: {
                dumpInstruction("LD (HL),D");
                break;
            }

            case 0x73: {
                dumpInstruction("LD (HL),E");
                break;
            }

            case 0x74: {
                dumpInstruction("LD (HL),H");
                break;
            }

            case 0x75: {
                dumpInstruction("LD (HL),L");
                break;
            }

            case 0x76: {
                dumpInstruction("HALT");

                break;
            }

            case 0x77: {
                dumpInstruction("LD (HL),A");
                break;
            }

            case 0x78: {
                dumpInstruction("LD A,B");
                break;
            }

            case 0x79: {
                dumpInstruction("LD A,C");
                break;
            }

            case 0x7A: {
                dumpInstruction("LD A,D");
                break;
            }

            case 0x7B: {
                dumpInstruction("LD A,E");
                break;
            }

            case 0x7C: {
                dumpInstruction("LD A,H");
                break;
            }

            case 0x7D: {
                dumpInstruction("LD A,L");
                break;
            }

            case 0x7F: {
                dumpInstruction("LD A,A");
                break;
            }

            case 0x80: {
                dumpInstruction("ADD A,B");
                break;
            }

            case 0x81: {
                dumpInstruction("ADD A,C");
                break;
            }

            case 0x82: {
                dumpInstruction("ADD A,D");
                break;
            }
            case 0x83: {
                dumpInstruction("ADD A,E");
                break;
            }

            case 0x84: {
                dumpInstruction("ADD A,H");
                break;
            }

            case 0x85: {
                dumpInstruction("ADD A,L");
                break;
            }

            case 0x86: {
                dumpInstruction("ADD A,(HL)");
                break;
            }

            case 0x87: {
                dumpInstruction("ADD A,A");
                break;
            }

            case 0x88: {
                dumpInstruction("ADC A,B");
                break;
            }

            case 0x89: {
                dumpInstruction("ADC A,C");
                break;
            }

            case 0x8A: {
                dumpInstruction("ADC A,D");
                break;
            }

            case 0x8B: {
                dumpInstruction("ADC A,E");
                break;
            }

            case 0x8C: {
                dumpInstruction("ADC A,H");
                break;
            }

            case 0x8D: {
                dumpInstruction("ADC A,L");
                break;
            }

            case 0x8E: {
                dumpInstruction("ADC A,(HL)");
                break;
            }

            case 0x8F: {
                dumpInstruction("ADC A,A");
                break;
            }

            case 0x90: {
                dumpInstruction("SUB B");
                break;
            }
            case 0x91: {
                dumpInstruction("SUB C");
                break;
            }
            case 0x92: {
                dumpInstruction("SUB D");
                break;
            }

            case 0x93: {
                dumpInstruction("SUB E");
                break;
            }

            case 0x94: {
                dumpInstruction("SUB H");
                break;
            }

            case 0x95: {
                dumpInstruction("SUB L");
                break;
            }

            case 0x96: {
                dumpInstruction("SUB (HL)");
                break;
            }

            case 0x97: {
                dumpInstruction("SUB A");
                break;
            }

            case 0x98: {
                dumpInstruction("SBC B");
                break;
            }

            case 0x99: {
                dumpInstruction("SBC C");
                break;
            }

            case 0x9A: {
                dumpInstruction("SBC D");
                break;
            }

            case 0x9B: {
                dumpInstruction("SBC E");
                break;
            }

            case 0x9C: {
                dumpInstruction("SBC H");
                break;
            }

            case 0x9D: {
                dumpInstruction("SBC L");
                break;
            }

            case 0x9E: {
                dumpInstruction("SBC (HL)");
                break;
            }

            case 0x9F: {
                dumpInstruction("SBC A");
                break;
            }

            case 0xA0: {
                dumpInstruction("AND B");
                break;
            }

            case 0xA1: {
                dumpInstruction("AND C");
                break;
            }

            case 0xA2: {
                dumpInstruction("AND D");
                break;
            }

            case 0xA3: {
                dumpInstruction("AND E");
                break;
            }

            case 0xA4: {
                dumpInstruction("AND H");
                break;
            }

            case 0xA5: {
                dumpInstruction("AND L");
                break;
            }

            case 0xA6: {
                dumpInstruction("AND (HL)");
                break;
            }

            case 0xA7: {
                dumpInstruction("AND A");
                break;
            }

            case 0xA8: {
                dumpInstruction("XOR B");
                break;
            }

            case 0xA9: {
                dumpInstruction("XOR C");
                break;
            }

            case 0xAA: {
                dumpInstruction("XOR D");
                break;
            }

            case 0xAB: {
                dumpInstruction("XOR E");
                break;
            }

            case 0xAC: {
                dumpInstruction("XOR H");
                break;
            }

            case 0xAD: {
                dumpInstruction("XOR L");
                break;
            }

            case 0xAE: {
                dumpInstruction("XOR (HL)");
                break;
            }

            case 0xAF: {
                dumpInstruction("XOR A");
                break;
            }

            case 0xB0: {
                dumpInstruction("OR B");
                break;
            }

            case 0xB1: {
                dumpInstruction("OR C");
                break;
            }

            case 0xB2: {
                dumpInstruction("OR D");
                break;
            }

            case 0xB3: {
                dumpInstruction("OR E");
                break;
            }

            case 0xB4: {
                dumpInstruction("OR H");
                break;
            }

            case 0xB5: {
                dumpInstruction("OR L");
                break;
            }

            case 0xB6: {
                dumpInstruction("OR (HL)");
                break;
            }

            case 0xB7: {
                dumpInstruction("OR A");
                break;
            }

            case 0xB8: {
                dumpInstruction("OR B");
                break;
            }

            case 0xB9: {
                dumpInstruction("OR C");
                break;
            }

            case 0xBA: {
                dumpInstruction("OR D");
                break;
            }

            case 0xBB: {
                dumpInstruction("OR E");
                break;
            }

            case 0xBC: {
                dumpInstruction("OR H");
                break;
            }

            case 0xBD: {
                dumpInstruction("OR L");
                break;
            }

            case 0xBE: {
                dumpInstruction("CP (HL)");
                break;
            }

            case 0xBF: {
                dumpInstruction("CP A");
                break;
            }

            case 0xC0: {
                dumpInstruction("RET NZ");
                break;
            }

            case 0xC1: {
                dumpInstruction("POP BC");
                break;
            }

            case 0xC2: {
                byte low = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                byte high = memoryMap.read(VirtualPC.getValue());
                int result = (((high << 8) & 0xFF00) | (low & 0xFF));

                dumpInstruction("JP NZ,$" + format4(result));
                break;
            }

            case 0xC3: {
                byte low = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                byte high = memoryMap.read(VirtualPC.getValue());
                int result = (((high << 8) & 0xFF00) | (low & 0xFF));

                dumpInstruction("JP $" + format4(result));
                break;
            }

            case 0xC4: {
                byte low = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                byte high = memoryMap.read(VirtualPC.getValue());
                int result = (((high << 8) & 0xFF00) | (low & 0xFF));

                dumpInstruction("CALL NZ,$" + format4(result));
                break;
            }

            case 0xC5: {
                dumpInstruction("PUSH BC");
                break;
            }

            case 0xC6: {
                byte result = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                dumpInstruction("ADD A," + format2(result) + "h");
                return;
            }

            case 0xC7: {
                dumpInstruction("RST 00h");
                return;
            }

            case 0xC8: {
                dumpInstruction("RET Z");
                break;
            }

            case 0xC9: {
                dumpInstruction("RET");
                break;
            }

            case 0xCA: {
                byte low = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                byte high = memoryMap.read(VirtualPC.getValue());
                int result = (((high << 8) & 0xFF00) | (low & 0xFF));

                dumpInstruction("JP Z,$" + format4(result));
                break;
            }

            case 0xCB: {
                // CB opcode
                dumpCBOpcode();
                break;
            }

            case 0xCC: {
                byte low = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                byte high = memoryMap.read(VirtualPC.getValue());
                int result = (((high << 8) & 0xFF00) | (low & 0xFF));

                dumpInstruction("CALL Z,$" + format4(result));
                break;
            }

            case 0xCD: {
                byte low = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                byte high = memoryMap.read(VirtualPC.getValue());
                int result = (((high << 8) & 0xFF00) | (low & 0xFF));

                dumpInstruction("CALL $" + format4(result));
                break;
            }

            case 0xCE: {
                byte result = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                dumpInstruction("ADC A," + format2(result) + "h");
                return;
            }

            case 0xCF: {
                dumpInstruction("RST 08h");
                return;
            }

            case 0xD0: {
                dumpInstruction("RET NC");
                break;
            }

            case 0xD1: {
                dumpInstruction("POP DE");
                break;
            }

            case 0xD2: {
                byte low = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                byte high = memoryMap.read(VirtualPC.getValue());
                int result = (((high << 8) & 0xFF00) | (low & 0xFF));

                dumpInstruction("JP NC,$" + format4(result));
                break;
            }

            case 0xD4: {
                byte low = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                byte high = memoryMap.read(VirtualPC.getValue());
                int result = (((high << 8) & 0xFF00) | (low & 0xFF));

                dumpInstruction("CALL NC,$" + format4(result));
                break;
            }

            case 0xD5: {
                dumpInstruction("PUSH DE");
                break;
            }

            case 0xD6: {
                byte result = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                dumpInstruction("SUB " + format2(result) + "h");
                break;
            }

            case 0xD7: {
                dumpInstruction("RST 10h");
                return;
            }

            case 0xD8: {
                dumpInstruction("RET C");
                break;
            }

            case 0xD9: {
                dumpInstruction("RETI");
                break;
            }

            case 0xDA: {
                byte low = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                byte high = memoryMap.read(VirtualPC.getValue());
                int result = (((high << 8) & 0xFF00) | (low & 0xFF));

                dumpInstruction("JP C,$" + format4(result));
                break;
            }

            case 0xDC: {
                byte low = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                byte high = memoryMap.read(VirtualPC.getValue());
                int result = (((high << 8) & 0xFF00) | (low & 0xFF));

                dumpInstruction("CALL C,$" + format4(result));
                break;
            }

            case 0xDE: {
                byte result = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                dumpInstruction("SBC " + format2(result) + "h");
                return;
            }

            case 0xDF: {
                dumpInstruction("RST 18h");
                return;
            }

            case 0xE0: {
                byte result = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                dumpInstruction("LD ($FF00 + $" + format2(result) + "), A");
                break;
            }

            case 0xE1: {
                dumpInstruction("POP HL");
                break;
            }

            case 0xE2: {
                dumpInstruction("($FF00+C),A");
                break;
            }

            case 0xE5: {
                dumpInstruction("PUSH HL");
                break;
            }

            case 0xE6: {
                byte result = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                dumpInstruction("AND " + format2(result) + "h");
                break;
            }

            case 0xE7: {
                dumpInstruction("RST 20h");
                return;
            }

            case 0xE8: {
                byte result = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                dumpInstruction("ADD SP," + format2(result) + "h");
                break;
            }

            case 0xE9: {
                dumpInstruction("JP (HL)");
                break;
            }

            case 0xEA: {
                byte low = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                byte high = memoryMap.read(VirtualPC.getValue());
                int result = (((high << 8) & 0xFF00) | (low & 0xFF));

                dumpInstruction("LD ($" + format4(result) + "),A");
                break;
            }

            case 0xEE: {
                byte result = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                dumpInstruction("XOR " + format2(result) + "h");
                return;
            }

            case 0xEF: {
                dumpInstruction("RST 28h");
                return;
            }

            case 0xF0: {
                byte result = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                dumpInstruction("LD A,($FF00 + " + format2(result) + ")");
                break;
            }

            case 0xF1: {
                dumpInstruction("POP AF");
                break;
            }

            case 0xF2: {
                dumpInstruction("LD A,($FF00+C)");
                break;
            }

            case 0xF3: {
                dumpInstruction("DI");
                break;
            }

            case 0xF5: {
                dumpInstruction("PUSH AF");
                break;
            }

            case 0xF6: {
                byte result = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                dumpInstruction("OR " + format2(result) + "h");
                return;
            }

            case 0xF7: {
                dumpInstruction("RST 30h");
                return;
            }

            case 0xF8: {
                byte n = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();

                dumpInstruction("LDHL SP," + n + "h");

                break;
            }

            case 0xF9: {
                dumpInstruction("LD SP,HL");
                break;
            }

            case 0xFA: {
                byte low = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                byte high = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                int result = (((high << 8) & 0xFF00) | (low & 0xFF));

                dumpInstruction("LD A,($" + format4(result) + ")");
                break;
            }

            case 0xFB: {
                dumpInstruction("EI");
                break;
            }

            case 0xFE: {
                int result = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();
                dumpInstruction("CP " + format2(result) + "h");
                break;
            }

            case 0xFF: {
                dumpInstruction("RST 38h");
                return;
            }
        }
    }

    private void dumpCBOpcode() {
        int opcode = memoryMap.read(VirtualPC.getValue());
        switch (opcode) {
            case 0x00: {
                dumpInstruction("RLC B");
                break;
            }

            case 0x01: {
                dumpInstruction("RLC C");
                break;
            }

            case 0x02: {
                dumpInstruction("RLC D");
                break;
            }

            case 0x03: {
                dumpInstruction("RLC E");
                break;
            }

            case 0x04: {
                dumpInstruction("RLC H");
                break;
            }

            case 0x05: {
                dumpInstruction("RLC L");
                break;
            }

            case 0x06: {
                dumpInstruction("RLC (HL)");
                break;
            }

            case 0x07: {
                dumpInstruction("RLC A");
                break;
            }

            case 0x08: {
                dumpInstruction("RRC B");
                break;
            }

            case 0x09: {
                dumpInstruction("RRC C");
                break;
            }

            case 0x0A: {
                dumpInstruction("RRC D");
                break;
            }

            case 0x0B: {
                dumpInstruction("RRC E");
                break;
            }

            case 0x0C: {
                dumpInstruction("RRC H");
                break;
            }

            case 0x0D: {
                dumpInstruction("RRC L");
                break;
            }

            case 0x0E: {
                dumpInstruction("RRC (HL)");
                break;
            }

            case 0x0F: {
                dumpInstruction("RRC A");
                break;
            }

            case 0x10: {
                dumpInstruction("RL B");
                break;
            }

            case 0x11: {
                dumpInstruction("RL C");
                break;
            }

            case 0x12: {
                dumpInstruction("RL D");
                break;
            }

            case 0x13: {
                dumpInstruction("RL E");
                break;
            }

            case 0x14: {
                dumpInstruction("RL H");
                break;
            }

            case 0x15: {
                dumpInstruction("RL L");
                break;
            }

            case 0x16: {
                dumpInstruction("RL (HL)");
                break;
            }

            case 0x17: {
                dumpInstruction("RL A");
                break;
            }

            case 0x18: {
                dumpInstruction("RR B");
                break;
            }

            case 0x19: {
                dumpInstruction("RR C");
                break;
            }

            case 0x1A: {
                dumpInstruction("RR D");
                break;
            }

            case 0x1B: {
                dumpInstruction("RR E");
                break;
            }

            case 0x1C: {
                dumpInstruction("RR H");
                break;
            }

            case 0x1D: {
                dumpInstruction("RR L");
                break;
            }

            case 0x1E: {
                dumpInstruction("RR (HL)");
                break;
            }

            case 0x1F: {
                dumpInstruction("RR A");
                break;
            }

            case 0x20: {
                dumpInstruction("SLA B");
                break;
            }

            case 0x21: {
                dumpInstruction("SLA C");
                break;
            }

            case 0x22: {
                dumpInstruction("SLA D");
                break;
            }

            case 0x23: {
                dumpInstruction("SLA E");
                break;
            }

            case 0x24: {
                dumpInstruction("SLA H");
                break;
            }

            case 0x25: {
                dumpInstruction("SLA L");
                break;
            }

            case 0x26: {
                dumpInstruction("SLA (HL)");
                break;
            }

            case 0x27: {
                dumpInstruction("SLA A");
                break;
            }

            case 0x28: {
                dumpInstruction("SRA B");
                break;
            }

            case 0x29: {
                dumpInstruction("SRA C");
                break;
            }

            case 0x2A: {
                dumpInstruction("SRA D");
                break;
            }

            case 0x2B: {
                dumpInstruction("SRA E");
                break;
            }

            case 0x2C: {
                dumpInstruction("SRA H");
                break;
            }

            case 0x2D: {
                dumpInstruction("SRA L");
                break;
            }

            case 0x2E: {
                dumpInstruction("SRA (HL)");
                break;
            }

            case 0x2F: {
                dumpInstruction("SRA A");
                break;
            }

            case 0x30: {
                dumpInstruction("SWAP B");
                break;
            }

            case 0x31: {
                dumpInstruction("SWAP C");
                break;
            }

            case 0x32: {
                dumpInstruction("SWAP D");
                break;
            }

            case 0x33: {
                dumpInstruction("SWAP E");
                break;
            }

            case 0x34: {
                dumpInstruction("SWAP H");
                break;
            }

            case 0x35: {
                dumpInstruction("SWAP L");
                break;
            }

            case 0x36: {
                dumpInstruction("SWAP (HL)");
                break;
            }

            case 0x37: {
                dumpInstruction("SWAP A");
                break;
            }

            case 0x38: {
                dumpInstruction("SRL B");
                break;
            }

            case 0x39: {
                dumpInstruction("SRL C");
                break;
            }

            case 0x3A: {
                dumpInstruction("SRL D");
                break;
            }

            case 0x3B: {
                dumpInstruction("SRL E");
                break;
            }

            case 0x3C: {
                dumpInstruction("SRL H");
                break;
            }

            case 0x3D: {
                dumpInstruction("SRL L");
                break;
            }

            case 0x3E: {
                dumpInstruction("SRL (HL)");
                break;
            }

            case 0x3F: {
                dumpInstruction("SRL A");
                break;
            }

            case 0x40: {
                dumpInstruction("BIT 0,B");
                break;
            }

            case 0x41: {
                dumpInstruction("BIT 0,C");
                break;
            }

            case 0x42: {
                dumpInstruction("BIT 0,D");
                break;
            }

            case 0x43: {
                dumpInstruction("BIT 0,E");
                break;
            }

            case 0x44: {
                dumpInstruction("BIT 0,H");
                break;
            }

            case 0x45: {
                dumpInstruction("BIT 0,L");
                break;
            }

            case 0x46: {
                dumpInstruction("BIT 0,(HL)");
                break;
            }

            case 0x47: {
                dumpInstruction("BIT 0,A");
                break;
            }

            case 0x48: {
                dumpInstruction("BIT 1,B");
                break;
            }

            case 0x49: {
                dumpInstruction("BIT 1,C");
                break;
            }

            case 0x4A: {
                dumpInstruction("BIT 1,D");
                break;
            }

            case 0x4B: {
                dumpInstruction("BIT 1,E");
                break;
            }

            case 0x4C: {
                dumpInstruction("BIT 1,H");
                break;
            }

            case 0x4D: {
                dumpInstruction("BIT 1,L");
                break;
            }

            case 0x4E: {
                dumpInstruction("BIT 1,(HL)");
                break;
            }

            case 0x4F: {
                dumpInstruction("BIT 1,A");
                break;
            }

            case 0x50: {
                dumpInstruction("BIT 2,B");
                break;
            }

            case 0x51: {
                dumpInstruction("BIT 2,C");
                break;
            }

            case 0x52: {
                dumpInstruction("BIT 2,D");
                break;
            }

            case 0x53: {
                dumpInstruction("BIT 2,E");
                break;
            }

            case 0x54: {
                dumpInstruction("BIT 2,H");
                break;
            }

            case 0x55: {
                dumpInstruction("BIT 2,L");
                break;
            }

            case 0x56: {
                dumpInstruction("BIT 2,(HL)");
                break;
            }

            case 0x57: {
                dumpInstruction("BIT 2,A");
                break;
            }

            case 0x58: {
                dumpInstruction("BIT 3,B");
                break;
            }

            case 0x59: {
                dumpInstruction("BIT 3,C");
                break;
            }

            case 0x5A: {
                dumpInstruction("BIT 3,D");
                break;
            }

            case 0x5B: {
                dumpInstruction("BIT 3,E");
                break;
            }

            case 0x5C: {
                dumpInstruction("BIT 3,H");
                break;
            }

            case 0x5D: {
                dumpInstruction("BIT 3,L");
                break;
            }

            case 0x5F: {
                dumpInstruction("BIT 3,A");
                break;
            }

            case 0x60: {
                dumpInstruction("BIT 4,B");
                break;
            }

            case 0x61: {
                dumpInstruction("BIT 4,C");
                break;
            }

            case 0x62: {
                dumpInstruction("BIT 4,D");
                break;
            }

            case 0x63: {
                dumpInstruction("BIT 4,E");
                break;
            }

            case 0x64: {
                dumpInstruction("BIT 4,H");
                break;
            }

            case 0x65: {
                dumpInstruction("BIT 4,L");
                break;
            }

            case 0x67: {
                dumpInstruction("BIT 4,A");
                break;
            }

            case 0x68: {
                dumpInstruction("BIT 5,B");
                break;
            }

            case 0x69: {
                dumpInstruction("BIT 5,C");
                break;
            }

            case 0x6A: {
                dumpInstruction("BIT 5,D");
                break;
            }

            case 0x6B: {
                dumpInstruction("BIT 5,E");
                break;
            }

            case 0x6C: {
                dumpInstruction("BIT 5,H");
                break;
            }

            case 0x6D: {
                dumpInstruction("BIT 5,L");
                break;
            }

            case 0x6E: {
                dumpInstruction("BIT 7,(HL)");
                break;
            }

            case 0x6F: {
                dumpInstruction("BIT 5,A");
                break;
            }

            case 0x70: {
                dumpInstruction("BIT 6,B");
                break;
            }

            case 0x71: {
                dumpInstruction("BIT 6,C");
                break;
            }

            case 0x72: {
                dumpInstruction("BIT 6,D");
                break;
            }

            case 0x73: {
                dumpInstruction("BIT 6,E");
                break;
            }

            case 0x74: {
                dumpInstruction("BIT 6,H");
                break;
            }

            case 0x75: {
                dumpInstruction("BIT 6,L");
                break;
            }

            case 0x76: {
                dumpInstruction("BIT 6,(HL)");
                break;
            }

            case 0x77: {
                dumpInstruction("BIT 6,A");
                break;
            }

            case 0x78: {
                dumpInstruction("BIT 7,B");
                break;
            }

            case 0x79: {
                dumpInstruction("BIT 7,C");
                break;
            }

            case 0x7A: {
                dumpInstruction("BIT 7,D");
                break;
            }

            case 0x7B: {
                dumpInstruction("BIT 7,E");
                break;
            }

            case 0x7C: {
                dumpInstruction("BIT 7,H");
                break;
            }

            case 0x7D: {
                dumpInstruction("BIT 7,L");
                break;
            }

            case 0x7E: {
                dumpInstruction("BIT 7,(HL)");
                break;
            }

            case 0x7F: {
                dumpInstruction("BIT 7,A");
                break;
            }

            case 0x80: {
                dumpInstruction("RES 0,B");
                break;
            }

            case 0x81: {
                dumpInstruction("RES 0,C");
                break;
            }

            case 0x82: {
                dumpInstruction("RES 0,D");
                break;
            }

            case 0x83: {
                dumpInstruction("RES 0,E");
                break;
            }

            case 0x84: {
                dumpInstruction("RES 0,H");
                break;
            }

            case 0x85: {
                dumpInstruction("RES 0,L");
                break;
            }

            case 0x86: {
                dumpInstruction("RES 0,(HL)");
                break;
            }

            case 0x87: {
                dumpInstruction("RES 0,A");
                break;
            }

            case 0x88: {
                dumpInstruction("RES 1,B");
                break;
            }

            case 0x89: {
                dumpInstruction("RES 1,C");
                break;
            }

            case 0x8A: {
                dumpInstruction("RES 1,D");
                break;
            }

            case 0x8B: {
                dumpInstruction("RES 1,E");
                break;
            }

            case 0x8C: {
                dumpInstruction("RES 1,H");
                break;
            }

            case 0x8D: {
                dumpInstruction("RES 1,L");
                break;
            }

            case 0x8E: {
                dumpInstruction("RES 1,(HL)");
                break;
            }

            case 0x8F: {
                dumpInstruction("RES 1,A");
                break;
            }

            case 0x90: {
                dumpInstruction("RES 2,B");
                break;
            }

            case 0x91: {
                dumpInstruction("RES 2,C");
                break;
            }

            case 0x92: {
                dumpInstruction("RES 2,D");
                break;
            }

            case 0x93: {
                dumpInstruction("RES 2,E");
                break;
            }

            case 0x94: {
                dumpInstruction("RES 2,H");
                break;
            }

            case 0x95: {
                dumpInstruction("RES 2,L");
                break;
            }

            case 0x96: {
                dumpInstruction("RES 2,(HL)");
                break;
            }

            case 0x97: {
                dumpInstruction("RES 2,A");
                break;
            }

            case 0x98: {
                dumpInstruction("RES 3,B");
                break;
            }

            case 0x99: {
                dumpInstruction("RES 3,C");
                break;
            }

            case 0x9A: {
                dumpInstruction("RES 3,D");
                break;
            }

            case 0x9B: {
                dumpInstruction("RES 3,E");
                break;
            }

            case 0x9C: {
                dumpInstruction("RES 3,H");
                break;
            }

            case 0x9D: {
                dumpInstruction("RES 3,L");
                break;
            }

            case 0x9E: {
                dumpInstruction("RES 3,(HL)");
                break;
            }

            case 0x9F: {
                dumpInstruction("RES 3,A");
                break;
            }

            case 0xA0: {
                dumpInstruction("RES 4,B");
                break;
            }

            case 0xA1: {
                dumpInstruction("RES 4,C");
                break;
            }

            case 0xA2: {
                dumpInstruction("RES 4,D");
                break;
            }

            case 0xA3: {
                dumpInstruction("RES 4,E");
                break;
            }

            case 0xA4: {
                dumpInstruction("RES 4,H");
                break;
            }

            case 0xA5: {
                dumpInstruction("RES 4,L");
                break;
            }

            case 0xA6: {
                dumpInstruction("RES 4,(HL)");
                break;
            }

            case 0xA7: {
                dumpInstruction("RES 4,A");
                break;
            }

            case 0xA8: {
                dumpInstruction("RES 5,B");
                break;
            }

            case 0xA9: {
                dumpInstruction("RES 5,C");
                break;
            }

            case 0xAA: {
                dumpInstruction("RES 5,D");
                break;
            }

            case 0xAB: {
                dumpInstruction("RES 5,E");
                break;
            }

            case 0xAC: {
                dumpInstruction("RES 5,H");
                break;
            }

            case 0xAD: {
                dumpInstruction("RES 5,L");
                break;
            }

            case 0xAE: {
                dumpInstruction("RES 5,(HL)");
                break;
            }

            case 0xAF: {
                dumpInstruction("RES 5,A");
                break;
            }

            case 0xB0: {
                dumpInstruction("RES 6,B");
                break;
            }

            case 0xB1: {
                dumpInstruction("RES 6,C");
                break;
            }

            case 0xB2: {
                dumpInstruction("RES 6,D");
                break;
            }

            case 0xB3: {
                dumpInstruction("RES 6,E");
                break;
            }

            case 0xB4: {
                dumpInstruction("RES 6,H");
                break;
            }

            case 0xB5: {
                dumpInstruction("RES 6,L");
                break;
            }

            case 0xB6: {
                dumpInstruction("RES 6,(HL)");
                break;
            }

            case 0xB7: {
                dumpInstruction("RES 6,A");
                break;
            }

            case 0xB8: {
                dumpInstruction("RES 7,B");
                break;
            }

            case 0xB9: {
                dumpInstruction("RES 7,C");
                break;
            }

            case 0xBA: {
                dumpInstruction("RES 7,D");
                break;
            }

            case 0xBB: {
                dumpInstruction("RES 7,E");
                break;
            }

            case 0xBC: {
                dumpInstruction("RES 7,H");
                break;
            }

            case 0xBD: {
                dumpInstruction("RES 7,L");
                break;
            }

            case 0xBE: {
                dumpInstruction("RES 7,(HL)");
                break;
            }

            case 0xBF: {
                dumpInstruction("RES 7,A");
                break;
            }

            case 0xC0: {
                dumpInstruction("SET 0,B");
                break;
            }

            case 0xC1: {
                dumpInstruction("SET 0,C");
                break;
            }

            case 0xC2: {
                dumpInstruction("SET 0,D");
                break;
            }

            case 0xC3: {
                dumpInstruction("SET 0,E");
                break;
            }

            case 0xC4: {
                dumpInstruction("SET 0,H");
                break;
            }

            case 0xC5: {
                dumpInstruction("SET 0,L");
                break;
            }

            case 0xC6: {
                dumpInstruction("SET 0,(HL)");
                break;
            }

            case 0xC7: {
                dumpInstruction("SET 0,A");
                break;
            }

            case 0xC8: {
                dumpInstruction("SET 1,B");
                break;
            }

            case 0xC9: {
                dumpInstruction("SET 1,C");
                break;
            }

            case 0xCA: {
                dumpInstruction("SET 1,D");
                break;
            }

            case 0xCB: {
                dumpInstruction("SET 1,E");
                break;
            }

            case 0xCC: {
                dumpInstruction("SET 1,H");
                break;
            }

            case 0xCD: {
                dumpInstruction("SET 1,L");
                break;
            }

            case 0xCE: {
                dumpInstruction("SET 1,(HL)");
                break;
            }

            case 0xCF: {
                dumpInstruction("SET 1,A");
                break;
            }

            case 0xD0: {
                dumpInstruction("SET 2,B");
                break;
            }

            case 0xD1: {
                dumpInstruction("SET 2,C");
                break;
            }

            case 0xD2: {
                dumpInstruction("SET 2,D");
                break;
            }

            case 0xD3: {
                dumpInstruction("SET 2,E");
                break;
            }

            case 0xD4: {
                dumpInstruction("SET 2,H");
                break;
            }

            case 0xD5: {
                dumpInstruction("SET 2,L");
                break;
            }

            case 0xD6: {
                dumpInstruction("SET 2,(HL)");
                break;
            }

            case 0xD7: {
                dumpInstruction("SET 2,A");
                break;
            }

            case 0xD8: {
                dumpInstruction("SET 3,B");
                break;
            }

            case 0xD9: {
                dumpInstruction("SET 3,C");
                break;
            }

            case 0xDA: {
                dumpInstruction("SET 3,D");
                break;
            }

            case 0xDB: {
                dumpInstruction("SET 3,E");
                break;
            }

            case 0xDC: {
                dumpInstruction("SET 3,H");
                break;
            }

            case 0xDD: {
                dumpInstruction("SET 3,L");
                break;
            }

            case 0xDE: {
                dumpInstruction("SET 3,(HL)");
                break;
            }

            case 0xDF: {
                dumpInstruction("SET 3,A");
                break;
            }

            case 0xE0: {
                dumpInstruction("SET 4,B");
                break;
            }

            case 0xE1: {
                dumpInstruction("SET 4,C");
                break;
            }

            case 0xE2: {
                dumpInstruction("SET 4,D");
                break;
            }

            case 0xE3: {
                dumpInstruction("SET 4,E");
                break;
            }

            case 0xE4: {
                dumpInstruction("SET 4,H");
                break;
            }

            case 0xE5: {
                dumpInstruction("SET 4,L");
                break;
            }

            case 0xE6: {
                dumpInstruction("SET 4,(HL)");
                break;
            }

            case 0xE7: {
                dumpInstruction("SET 4,A");
                break;
            }

            case 0xE8: {
                dumpInstruction("SET 5,B");
                break;
            }

            case 0xE9: {
                dumpInstruction("SET 5,C");
                break;
            }

            case 0xEA: {
                dumpInstruction("SET 5,D");
                break;
            }

            case 0xEB: {
                dumpInstruction("SET 5,E");
                break;
            }

            case 0xEC: {
                dumpInstruction("SET 5,H");
                break;
            }

            case 0xED: {
                dumpInstruction("SET 5,L");
                break;
            }

            case 0xEE: {
                dumpInstruction("SET 5,(HL)");
                break;
            }

            case 0xEF: {
                dumpInstruction("SET 5,A");
                break;
            }

            case 0xF0: {
                dumpInstruction("SET 6,B");
                break;
            }

            case 0xF1: {
                dumpInstruction("SET 6,C");
                break;
            }

            case 0xF2: {
                dumpInstruction("SET 6,D");
                break;
            }

            case 0xF3: {
                dumpInstruction("SET 6,E");
                break;
            }

            case 0xF4: {
                dumpInstruction("SET 6,H");
                break;
            }

            case 0xF5: {
                dumpInstruction("SET 6,L");
                break;
            }

            case 0xF6: {
                dumpInstruction("SET 6,(HL)");
                break;
            }

            case 0xF7: {
                dumpInstruction("SET 6,A");
                break;
            }

            case 0xF8: {
                dumpInstruction("SET 7,B");
                break;
            }

            case 0xF9: {
                dumpInstruction("SET 7,C");
                break;
            }

            case 0xFA: {
                dumpInstruction("SET 7,D");
                break;
            }

            case 0xFB: {
                dumpInstruction("SET 7,E");
                break;
            }

            case 0xFC: {
                dumpInstruction("SET 7,H");
                break;
            }

            case 0xFD: {
                dumpInstruction("SET 7,L");
                break;
            }

            case 0xFE: {
                dumpInstruction("SET 7,(HL)");
                break;
            }

            case 0xFF: {
                dumpInstruction("SET 7,A");
                break;
            }
        }
    }

    @Override
    protected void handleVBlankIRQ() {
        currentPC = 0x0040;
        VirtualPC.setValue(0x0040);
        super.handleVBlankIRQ();
    }

    @Override
    protected void handleLCDCIRQ() {
        currentPC = 0x0048;
        VirtualPC.setValue(0x0048);
        super.handleLCDCIRQ();
    }

    @Override
    protected void handleTimerOverflowIRQ() {
        currentPC = 0x0050;
        VirtualPC.setValue(0x0050);
        super.handleTimerOverflowIRQ();
    }

    @Override
    protected void handleSerialIRQ() {
        currentPC = 0x0058;
        VirtualPC.setValue(0x0058);
        super.handleSerialIRQ();
    }

    @Override
    protected void handleInputInt() {
        currentPC = 0x0060;
        VirtualPC.setValue(0x0060);
        super.handleInputInt();
    }

    private void dumpInstruction(String instr) {
        disassembledCode[currentPC] = format4(currentPC) + ": " + instr;
    }

    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
        if (debugEnabled) {
            updateStatus();
        }
    }

    private String format4(int val) {
        return String.format("%04X", val & 0xFFFF);
    }

    private String format2(int val) {
        return String.format("%02X", val & 0xFF);
    }

    public void updateStatus() {
        PlatformImpl.runAndWait(new Runnable() {
            @Override
            public void run() {
                disassemblyOutput.updateDecompiled(currentPC, disassembledCode);

                debugOutput.updateAF(AF);
                debugOutput.updateBC(BC);
                debugOutput.updateDE(DE);
                debugOutput.updateHL(HL);
                debugOutput.updatePC(PC);
                debugOutput.updateSP(SP);

                debugOutput.updateZ(isFlagSet(FLAG_ZERO));
                debugOutput.updateN(isFlagSet(FLAG_NEGATIVE));
                debugOutput.updateH(isFlagSet(FLAG_HALF));
                debugOutput.updateC(isFlagSet(FLAG_CARRY));
            }
        });
    }
}
