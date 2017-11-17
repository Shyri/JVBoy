package es.shyri.jvboy.memory;

/**
 * Created by shyri on 11/10/2017.
 */
public class MemoryMapDebugger extends MemoryMap {
    private final MemoryDebugOutput memoryDebugOutput;
    private String[] dumpedRam = new String[0x10000];

    private boolean debugEnabled = false;

    public MemoryMapDebugger(MemoryDebugOutput memoryDebugOutput) {
        this.memoryDebugOutput = memoryDebugOutput;

        for (int i = ram.length - 1; i > 0x8000; i--) {
            dumpedRam[ram.length - i] = getRegionPrefix(i) + ":" + format4(i) + "  00";
        }
    }

    @Override
    public void write(int address, int value) {
        super.write(address, value);
        //        if ((address >= 0xFF00) && (address < 0xFF80)) {
        //            io.write(address, (byte) (value & 0xFF));
        if (address >= 0x8000) {
            dumpedRam[ram.length - address] = getRegionPrefix(address) + ":" + format4(address) + "  " + format2(value);
        }

        if (debugEnabled) {
            memoryDebugOutput.onWriteToRAM(ram.length - address, dumpedRam, ram);
        }
    }

    private String format2(int val) {
        return String.format("%02X", val & 0xFF);
    }

    private String format4(int val) {
        return String.format("%04X", val & 0xFFFF);
    }

    private String getRegionPrefix(int address) {
        String region = "";

        if (address >= 0x8000 && address < 0xA000) {
            region = "VRAM";
        } else if (address >= 0xA000 && address < 0xC000) {
            region = "SRAM";
        } else if (address >= 0xC000 && address < 0xD000) {
            region = "WRAM0";
        } else if (address >= 0xE000 && address < 0xFE00) {
            region = "ECHO";
        } else if (address >= 0xFF80 && address < 0xFFFF) {
            region = "HRAM";
        }

        return region;
    }

    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
        if (debugEnabled) {
            memoryDebugOutput.onUpdateWholeRAM(dumpedRam, ram);
        }
    }
}
