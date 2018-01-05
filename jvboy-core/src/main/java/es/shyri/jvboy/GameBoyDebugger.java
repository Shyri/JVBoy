package es.shyri.jvboy;

import es.shyri.jvboy.cpu.CPUDebugger;
import es.shyri.jvboy.cpu.CPUStatusOutput;
import es.shyri.jvboy.cpu.DisassemblyOutput;
import es.shyri.jvboy.cpu.TimersDebugger;
import es.shyri.jvboy.io.IODebugger;
import es.shyri.jvboy.io.IOStatusOutput;
import es.shyri.jvboy.lcd.PPU;
import es.shyri.jvboy.memory.MemoryDebugOutput;
import es.shyri.jvboy.memory.MemoryMapDebugger;

/**
 * Created by shyri on 02/10/2017.
 */
public class GameBoyDebugger extends GameBoy {
    public GameBoyDebugger(CPUStatusOutput statusOutput,
                           DisassemblyOutput disassemblyOutput,
                           MemoryDebugOutput memoryDebugOutput,
                           IOStatusOutput ioStatusOutput) {
        memoryMap = new MemoryMapDebugger(memoryDebugOutput);
        cpu = new CPUDebugger(statusOutput, disassemblyOutput);
        io = new IODebugger(ioStatusOutput);
        timers = new TimersDebugger(cpu, (IODebugger) io);
        ppu = new PPU();
    }

    public void setCPUDebugEnabled(boolean debugEnabled) {
        ((CPUDebugger) cpu).setDebugEnabled(debugEnabled);
    }

    public void setIODebugEnabled(boolean debugEnabled) {
        ((IODebugger) io).setDebugEnabled(debugEnabled);
    }

    public void setMemoryDebugEnabled(boolean debugEnabled) {
        ((MemoryMapDebugger) memoryMap).setDebugEnabled(debugEnabled);
    }
}
