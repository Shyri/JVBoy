package com.a424appslab.androidboy.cpu;

import com.a424appslab.androidboy.cpu.register.Reg8Bit;
import com.a424appslab.androidboy.memory.MemoryMap;

/**
 * Created by shyri on 03/07/17.
 */

public class LD {
    private final MemoryMap memoryMap;

    public LD(MemoryMap memoryMap) {this.memoryMap = memoryMap;}

    void addrToReg8bit(int address, Reg8Bit reg) {
        reg.setValue(memoryMap.read(address));
    }

    void valToAddr(Reg8Bit reg8Bit, int address) {
        memoryMap.write(address, reg8Bit.getValue());
    }
}
