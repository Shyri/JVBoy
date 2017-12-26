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
                dumpInstruction("RLAC");
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

            case 0x3C: {
                dumpInstruction("INC A");
                break;
            }

            case 0x3D: {
                dumpInstruction("DEC A");
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
                byte low = memoryMap.read(VirtualPC.getValue());
                VirtualPC.inc();

                dumpInstruction("LD (HL)," + format2(low) + "h");

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

            case 0x82: {
                dumpInstruction("ADD A,D");
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

            case 0x89: {
                dumpInstruction("ADC A,C");
                break;
            }

            case 0x90: {
                dumpInstruction("SUB B");
                break;
            }

            case 0x93: {
                dumpInstruction("SUB E");
                break;
            }

            case 0xA1: {
                dumpInstruction("AND C");
                break;
            }

            case 0xA7: {
                dumpInstruction("AND A");
                break;
            }

            case 0xA9: {
                dumpInstruction("XOR C");
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

            case 0xB6: {
                dumpInstruction("OR (HL)");
                break;
            }

            case 0xB7: {
                dumpInstruction("OR A");
                break;
            }

            case 0xBE: {
                dumpInstruction("CP (HL)");
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

            case 0xD0: {
                dumpInstruction("RET NC");
                break;
            }

            case 0xD1: {
                dumpInstruction("POP DE");
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

            case 0xD8: {
                dumpInstruction("RET C");
                break;
            }

            case 0xD9: {
                dumpInstruction("RETI");
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

            case 0xF8: {
                byte n = memoryMap.read(VirtualPC.getValue());
                PC.inc();

                int value = SP.getValue() + n;

                dumpInstruction("LDHL SP," + value + "h");

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
                dumpInstruction("RST $38");
                return;
            }
        }
    }

    private void dumpCBOpcode() {
        int opcode = memoryMap.read(VirtualPC.getValue());
        switch (opcode) {
            case 0x11: {
                dumpInstruction("RL C");
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

            case 0x27: {
                dumpInstruction("SLA A");
                break;
            }

            case 0x33: {
                dumpInstruction("INC SP");
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

            case 0x40: {
                dumpInstruction("BIT 0,B");
                break;
            }

            case 0x48: {
                dumpInstruction("BIT 1,B");
                break;
            }

            case 0x50: {
                dumpInstruction("BIT 2,B");
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

            case 0x5f: {
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

            case 0x68: {
                dumpInstruction("BIT 5,B");
                break;
            }

            case 0x69: {
                dumpInstruction("BIT 5,C");
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

            case 0x77: {
                dumpInstruction("BIT 6,A");
                break;
            }

            case 0x7C: {
                dumpInstruction("BIT 7,H");
                break;
            }

            case 0x7F: {
                dumpInstruction("BIT 7,A");
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
        }
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
