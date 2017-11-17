package es.shyri.jvboy.memory;

import es.shyri.jvboy.io.IO;

/**
 * Created by shyri on 21/09/2017.
 */
public class MemoryMap {

    // Interrupt Enable Register
    // --------------------------- FFFF
    // Internal CPU RAM
    // --------------------------- FF80
    // Empty but unusable for I/O
    // --------------------------- FF4C
    // I/O Registers
    // --------------------------- FF00
    // Unusable
    // --------------------------- FEA0
    // Sprite Attribute Memory (OAM)
    // --------------------------- FE00
    // Echo of 8kB Internal RAM
    // --------------------------- E000
    // 8kB Internal RAM
    // --------------------------- C000
    // 8kB switchable RAM bank
    // --------------------------- A000
    // 8kB Video RAM
    // --------------------------- 8000
    // 16kB switchable ROM bank
    // --------------------------- 4000
    // 16kB ROM bank #0
    // --------------------------- 0000

    protected byte[] rom;
    protected byte[] bios;
    protected byte[] ram = new byte[0x10000];

    private IO io;

    public void init(IO io) {
        this.io = io;
    }

    // TODO proper implementation for boot up
    boolean loadingBios = false; // 0 = bios, 1 = cart

    public void loadBios(byte[] bios) {
        this.bios = bios;
        loadingBios = true;
    }

    public void loadRom(byte[] rom) {
        this.rom = rom;
    }

    public byte read(int address) {
        if (address < 0x8000) {
            if (loadingBios && address < 0x100) {
                return bios[address];
            }

            return rom[address];
        } else if ((address >= 0xFF00) && (address < 0xFF80) || address == 0xFFFF) {
            return io.read(address);
        }

        return ram[address];
    }

    public void write(int address, int value) {
        if (address < 0x8000) {
            new IllegalAccessError("Trying to write to ROM... " + address + " bad boy ").printStackTrace();
        } else if ((address >= 0xFF00) && (address < 0xFF4C) || address == 0xFFFF) {
            io.write(address, (byte) (value & 0xFF));
        } else if (address >= 0xFF4C && address < 0xFF80) {
            // Unused rom
        } else {
            ram[address] = (byte) (value & 0xFF);
        }
    }

    public void logDump(int startAddress, int endAddress) {
        byte[] bytes = new byte[endAddress - startAddress];

        for (int i = startAddress; i < endAddress; i++) {
            bytes[i - startAddress] = read(i);
        }

//        Log.d("Memory",
//              "Memory Dump:[" + String.format("%04X", startAddress) + "-" + String.format("%04X", endAddress) + "]: " +
//              bytesToHex(startAddress, bytes));
    }

    private static String bytesToHex(int address, byte[] in) {
        final StringBuilder builder = new StringBuilder();
        int i = 0;
        int offset = 0;
        builder.append("\n");
        builder.append("\n" + String.format("%02X ", address) + ": ");
        for (byte b : in) {
            builder.append(String.format("%02X ", b));
            if (i == 15) {
                builder.append("\n" + String.format("%02X ", address + offset) + ": ");
                i = 0;
            } else {
                i++;
            }
            offset++;
        }
        return builder.toString();
    }

    public void disableBIOS() {
        loadingBios = false;
    }
}