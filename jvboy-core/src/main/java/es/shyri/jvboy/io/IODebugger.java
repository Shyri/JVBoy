package es.shyri.jvboy.io;//package es.shyri.jvboy.debug.io;

/**
 * Created by shyri on 11/10/2017.
 */
public class IODebugger extends IO {
    private final IOStatusOutput ioStatusOutput;

    private boolean debugEnabled;

    public IODebugger(IOStatusOutput ioStatusOutput) {this.ioStatusOutput = ioStatusOutput;}

    @Override
    public void write(int address, byte value) {
        super.write(address, value);

        if (!debugEnabled) {
            return;
        }

    }

    private void updateAll() {
        for (int address = 0xFF00; address < 0xFF4C; address++) {
            update(address);
        }
    }

    private void update(int address) {
        switch (address) {
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
        }
    }

    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
        if (debugEnabled) {
            updateAll();
        }
    }
}