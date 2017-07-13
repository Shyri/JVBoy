package com.a424appslab.androidboy.memory;

import com.a424appslab.androidboy.io.IO;

/**
 * Created by shyri on 02/07/17.
 */

public class MemoryMap {

    // Interrupt Enable Register
    // --------------------------- FFFF
    // Internal RAM
    // --------------------------- FF80
    // Empty but unusable for I/O
    // --------------------------- FF4C
    // I/O ports
    // --------------------------- FF00
    // Empty but unusable for I/O
    // --------------------------- FEA0
    // Sprite Attrib Memory (OAM)
    // --------------------------- FE00
    // Echo of 8kB Internal RAM
    // --------------------------- E000
    // 8kBInternal RAM
    // --------------------------- C000
    // 8kBswitchable RAMbank
    // --------------------------- A000
    // 8kB Video RAM
    // --------------------------- 8000
    // 16kB switchable ROMbank
    // --------------------------- 4000
    // 16kB ROMbank #0
    // --------------------------- 0000

    byte[] rom;
    byte[] ram = new byte[0x10000];

    private IO io;

    public void init(IO io) {
        this.io = io;
    }

    public void loadRom(byte[] rom) {
        this.rom = rom;
    }

    public byte read(int address) {
        if (address < 0x8000) {
            return rom[address];
        } else if ((address >= 0xFF000)) {
            return io.read(address);
        }

        return ram[address];
    }

    public void write(int address, byte value) {
        if (address < 0x8000) {
            throw new IllegalAccessError("Trying to write to ROM... bad boy");
        } else if ((address >= 0xFF000)) {
            io.write(address, value);
        } else {
            ram[address] = value;
        }
    }
}
