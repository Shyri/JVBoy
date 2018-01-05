package es.shyri.jvboy.io;

import es.shyri.jvboy.cpu.Timers;
import es.shyri.jvboy.joypad.JoyPad;
import es.shyri.jvboy.lcd.PPU;

/**
 * Created by shyri on 07/07/17.
 */

public class IO {
    protected Timers timers;
    protected PPU ppu;
    protected JoyPad joyPad;

    byte IE = 0x00;
    byte IF = 0x00;

    public void init(Timers timers, PPU ppu, JoyPad joyPad) {
        this.timers = timers;
        this.ppu = ppu;
        this.joyPad = joyPad;
    }

    public byte read(int address) {
        switch (address) {
            case 0xFF00:
                return joyPad.getP1();
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
                // TODO Sound Unimplemented
                return 0x00;

            case 0xFF40:
                // LCDC
                return ppu.LCDC;
            case 0xFF41:
                // STAT
                return ppu.STAT;
            case 0xFF42:
                // SCY
                return ppu.SCY;
            case 0xFF43:
                // SCX
                return ppu.SCX;

            case 0xFF44:
                // LY
                return (byte) ppu.LY;

            case 0xFF4A:
                // WY
                return (byte) ppu.WY;

            case 0xFF4B:
                // WX
                return (byte) ppu.WX;
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
            case 0xFF00:
                joyPad.setP1(value);
                break;
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
            case 0xFF05:
                // TIMA
                timers.setTIMA(value);
                break;
            case 0xFF06:
                // TMA
                timers.setTMA(value);
                break;
            case 0xFF07:
                // TAC
                timers.setTAC(value);
                break;
            case 0xFF10:
            case 0xFF11:
            case 0xFF12:
            case 0xFF13:
            case 0xFF14:
            case 0xFF15:
            case 0xFF16:
            case 0xFF17:
            case 0xFF18:
            case 0xFF19:
            case 0xFF1A:
            case 0xFF1B:
            case 0xFF1C:
            case 0xFF1D:
            case 0xFF1E:
            case 0xFF1F:
            case 0xFF20:
            case 0xFF21:
            case 0xFF22:
            case 0xFF23:
            case 0xFF24:
            case 0xFF25:
            case 0xFF26:
            case 0xFF30:
            case 0xFF31:
            case 0xFF32:
            case 0xFF33:
            case 0xFF34:
            case 0xFF35:
            case 0xFF36:
            case 0xFF37:
            case 0xFF38:
            case 0xFF39:
            case 0xFF3A:
            case 0xFF3B:
            case 0xFF3C:
            case 0xFF3D:
            case 0xFF3E:
            case 0xFF3F:
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
            case 0xFF46:
                // DMA
                ppu.DMA = value;
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

            case 0xFF4A:
                // WY
                ppu.WY = value;
                break;
            case 0xFF4B:
                // WX
                ppu.WX = value;
                break;
            case 0xFF0F:
                // IF
                IF = value;
                break;
            case 0xFFFF:
                IE = value;
                break;
            default:
                throw new IllegalStateException("Trying to write unknown IO register " + Integer.toHexString(address));
        }
    }
}
