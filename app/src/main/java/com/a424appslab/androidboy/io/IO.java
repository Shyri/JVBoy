package com.a424appslab.androidboy.io;

import com.a424appslab.androidboy.cpu.Timers;

/**
 * Created by shyri on 07/07/17.
 */

public class IO {
    private Timers timers;

    public void init(Timers timers) {
        this.timers = timers;
    }

    public byte read(int address) {
        switch (address) {
            //            case 0xFF00:
            //                // P1 TODO
            //                throw new IllegalStateException("Not implemented");
            //            case 0xFF01:
            //                // SB // TODO
            //                throw new IllegalStateException("Not implemented");
            //            case 0xFF02:
            //                //SC
            //                throw new IllegalStateException("Not implemented");
            case 0xFF04:
                // DIV
                return timers.getDIV();
            //            case 0xFF07:
            //                // TAC
            //                return ram[address];
            //            case 0xFF0F:
            //                // IF
            //                return ram[address];
            default:
                throw new IllegalStateException("Trying to read unknown IO register " + address);
        }
    }

    public void write(int address, byte value) {
        switch (address) {
            case 0xFF04:
                // DIV
                timers.resetDiv();
                break;
            case 0xFF07:
                // TAC
                timers.setTAC(value);
                break;
            default:
                throw new IllegalStateException("Trying to write unknown IO register " + address);
        }
    }
}
