package es.shyri.jvboy.cpu;

import es.shyri.jvboy.cpu.register.Reg8Bit;
import es.shyri.jvboy.memory.MemoryMap;

/**
 * Created by shyri on 03/07/17.
 */
public class LD {
    private final MemoryMap memoryMap;

    public LD(MemoryMap memoryMap) {this.memoryMap = memoryMap;}

    void addrToReg8bit(int address, Reg8Bit reg) {
        reg.setValue(memoryMap.read(address));
    }

    void valToAddr(int value, int address) {
        memoryMap.write(address, value);
    }
}
