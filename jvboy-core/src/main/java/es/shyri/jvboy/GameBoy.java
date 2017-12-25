package es.shyri.jvboy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import es.shyri.jvboy.cpu.CPU;
import es.shyri.jvboy.cpu.Timers;
import es.shyri.jvboy.io.IO;
import es.shyri.jvboy.joypad.JoyPad;
import es.shyri.jvboy.lcd.PPU;
import es.shyri.jvboy.memory.MemoryMap;
import es.shyri.jvboy.renderer.LCDRenderer;

/**
 * Created by shyri on 02/07/17.
 */

public class GameBoy {
    protected MemoryMap memoryMap;
    protected CPU cpu;
    protected PPU ppu;
    protected IO io;
    protected Timers timers;
    protected JoyPad joyPad;

    public GameBoy() {
        memoryMap = new MemoryMap();
        cpu = new CPU();
        timers = new Timers(cpu);
        io = new IO();
        ppu = new PPU();
        joyPad = new JoyPad();
    }

    public void loadBios(File file) throws IOException {
        memoryMap.loadBios(readFile(file));
    }

    public void loadRom(File file) throws IOException {
        memoryMap.loadRom(readFile(file));
    }

    public void init(LCDRenderer lcdRenderer) {
        memoryMap.init(io);
        io.init(timers, ppu, joyPad);
        cpu.init(memoryMap, timers);
        ppu.init(cpu, memoryMap, lcdRenderer);
    }

    public void start() {
        while (true) {
            int cycles = cpu.nextStep();
            ppu.update(cycles);
        }
    }

    public void runToAddress(int address) {
        while (cpu.PC.getValue() != address) {
            int cycles = cpu.nextStep();
            ppu.update(cycles);
        }
    }

    public void runInstructions(int amount) {
        for (int i = 0; i < amount; i++) {
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
