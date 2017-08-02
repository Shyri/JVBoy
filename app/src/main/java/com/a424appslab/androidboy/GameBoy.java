package com.a424appslab.androidboy;

import com.a424appslab.androidboy.cpu.CPU;
import com.a424appslab.androidboy.cpu.Timers;
import com.a424appslab.androidboy.io.IO;
import com.a424appslab.androidboy.lcd.PPU;
import com.a424appslab.androidboy.memory.MemoryMap;
import com.a424appslab.androidboy.render.LCDRenderer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by shyri on 02/07/17.
 */

public class GameBoy {
    private final MemoryMap memoryMap;
    private final CPU cpu;
    private final PPU ppu;
    private final IO io;
    private final Timers timers;

    public GameBoy() {
        memoryMap = new MemoryMap();
        cpu = new CPU();
        timers = new Timers(cpu);
        io = new IO();
        ppu = new PPU();
    }

    public void loadBios(File file) throws IOException {
        memoryMap.loadBios(readFile(file));
    }

    public void loadRom(File file) throws IOException {
        memoryMap.loadRom(readFile(file));
    }

    public void init(LCDRenderer lcdRenderer) {
        memoryMap.init(io);
        io.init(timers, ppu);
        cpu.init(memoryMap, timers);
        ppu.init(cpu, lcdRenderer);
    }

    public void start() {
        while (true) {
            int cycles = cpu.nextStep();
            ppu.update(cycles);
        }
    }

    private byte[] readFile(File file) throws IOException {
        //        if (file.length() > MAX_FILE_SIZE) {
        //            throw new FileTooBigException(file);
        //        }
        ByteArrayOutputStream ous = null;
        InputStream ios = null;
        try {
            byte[] buffer = new byte[4096];
            ous = new ByteArrayOutputStream();
            ios = new FileInputStream(file);
            int read = 0;
            while ((read = ios.read(buffer)) != -1) {
                ous.write(buffer, 0, read);
            }
        } finally {
            try {
                if (ous != null) {
                    ous.close();
                }
            } catch (IOException e) {
            }

            try {
                if (ios != null) {
                    ios.close();
                }
            } catch (IOException e) {
            }
        }
        return ous.toByteArray();
    }
}

