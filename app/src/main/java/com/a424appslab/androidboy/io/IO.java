package com.a424appslab.androidboy.io;

import com.a424appslab.androidboy.cpu.Timers;
import com.a424appslab.androidboy.lcd.PPU;

/**
 * Created by shyri on 07/07/17.
 */

public class IO {
    private Timers timers;
    private PPU ppu;

    byte IE = 0x00;
    byte IF = 0x00;

    public void init(Timers timers, PPU ppu) {
        this.timers = timers;
        this.ppu = ppu;
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
            case 0xFF25:
            case 0xFF26:
                // TODO Unimplemented
                return 0x00;

            case 0xFF42:
                // SCY
                return ppu.SCY;
            case 0xFF43:
                // SCX
                return ppu.SCX;

            case 0xFF44:
                // LY
                return (byte) ppu.LY;
            case 0xFF0F:
                // IF
                return IF;
            case 0xFFFF:
                return IE;
            default:
                throw new IllegalStateException("Trying to read unknown IO register " + Integer.toHexString(address));
        }
    }

    public void write(int address, byte value) {
        switch (address) {
            case 0xFF01:
                // SB
                // TODO Unimplemented
                break;
            case 0xFF02:
                // SC
                // TODO Unimplemented
                break;
            case 0xFF04:
                // DIV
                timers.resetDiv();
                break;
            case 0xFF07:
                // TAC
                timers.setTAC(value);
            case 0xFF11:
            case 0xFF12:
            case 0xFF13:
            case 0xFF14:
            case 0xFF24:
            case 0xFF25:
            case 0xFF26:
                // TODO Unimplemented
                break;
            case 0xFF40:
                // LCDC
                ppu.LCDC = value;
                break;
            case 0xFF41:
                // STAT
                ppu.STAT = value;
                break;
            case 0xFF42:
                // SCY
                ppu.SCY = value;
                break;
            case 0xFF43:
                // SCX
                ppu.SCX = value;
                break;
            case 0xFF47:
                // BGP
                ppu.BGP = value;
                break;
            case 0xFF48:
                // OBP0
                ppu.OBP0 = value;
                break;
            case 0xFF49:
                // OBP1
                ppu.OBP1 = value;
                break;
            case 0xFF0F:
                // IF
                IE = value;
                break;
            case 0xFFFF:
                IE = value;
                break;
            default:
                throw new IllegalStateException("Trying to write unknown IO register " + Integer.toHexString(address));
        }
    }
}
