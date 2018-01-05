package es.shyri.jvboy.io;//package es.shyri.jvboy.debug.io;

import com.sun.javafx.application.PlatformImpl;

import es.shyri.jvboy.cpu.Timers;
import es.shyri.jvboy.joypad.JoyPad;
import es.shyri.jvboy.lcd.PPU;

/**
 * Created by shyri on 11/10/2017.
 */
public class IODebugger extends IO {
    private final IOStatusOutput ioStatusOutput;

    private boolean debugEnabled;

    public IODebugger(IOStatusOutput ioStatusOutput) {this.ioStatusOutput = ioStatusOutput;}

    @Override
    public void init(Timers timers, PPU ppu, JoyPad joyPad) {
        super.init(timers, ppu, joyPad);
    }

    @Override
    public void write(final int address, byte value) {
        super.write(address, value);

        updateAddress(address);
    }

    public void updateAddress(final int address) {
        if (debugEnabled) {

            PlatformImpl.runAndWait(new Runnable() {
                @Override
                public void run() {
                    update(address);
                }
            });
        }
    }

    private void updateAll() {
        PlatformImpl.runAndWait(new Runnable() {
            @Override
            public void run() {
                for (int address = 0xFF00; address < 0xFFFF; address++) {
                    update(address);
                }
            }
        });
    }

    private void update(int address) {
        switch (address) {
            case 0xFF04:
                // DIV
                ioStatusOutput.updateDIV(timers.getDIV());
                break;
            case 0xFF05:
                // TIMA
                ioStatusOutput.updateTIMA(timers.getTIMA());
                break;
            case 0xFF06:
                // TMA
                ioStatusOutput.updateTMA(timers.getTMA());
                break;
            case 0xFF07:
                // TMA
                ioStatusOutput.updateTAC(timers.getTAC());
                break;
            case 0xFF0F:
                // IF
                ioStatusOutput.updateIF(IF);
                break;
            case 0xFF40:
                // LCDC
                ioStatusOutput.updateLCDC(ppu.LCDC);
                break;
            case 0xFF42:
                // SCY
                ioStatusOutput.updateSCY(ppu.SCY);
                break;
            case 0xFF43:
                // SCX
                ioStatusOutput.updateSCX(ppu.SCX);
                break;
            case 0xFF47:
                // BGP
                ioStatusOutput.updateBGP(ppu.BGP);
                break;
            case 0xFFFF:
                // IE
                ioStatusOutput.updateIE(IE);
                break;
        }
    }

    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
        if (debugEnabled) {
            updateAll();
        }
    }
}