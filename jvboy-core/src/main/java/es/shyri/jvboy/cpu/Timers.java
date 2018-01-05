package es.shyri.jvboy.cpu;

/**
 * Created by shyri on 05/07/17.
 */

public class Timers {
    public final int DIV_ADDRESS = 0xFF04;
    public final int TIMA_ADDRESS = 0xFF05;
    public final int TMA_ADDRESS = 0xFF06;
    public final int TAC_ADDRESS = 0xFF07;

    private final CPU cpu;

    private int divCycles = 0;
    private int timaCycles = 0;

    protected byte DIV;
    protected byte TIMA;
    protected byte TMA;
    protected byte TAC;

    private byte frequency = 0;
    private int[] frequencyClocks = {64, 4096, 1024, 256};
    //    private int[] frequencyClocks = {1024, 16, 64, 256};

    public Timers(CPU cpu) {this.cpu = cpu;}

    public void update(int cycles) {
        divCycles = divCycles + cycles;
        if (divCycles >= 256) {
            divCycles = divCycles - 256;
            DIV = (byte) (DIV + 1);
        }

        if ((TAC & 0x04) == 0x00) {
            // Timer is disabled
            return;
        }

        //        timaCycles++;
        timaCycles = timaCycles + cycles;

        if (timaCycles >= frequencyClocks[frequency]) {
            timaCycles = timaCycles - frequencyClocks[frequency];

            TIMA++;

            if ((TIMA & 0x100) == 0x00) {
                // TIMA Overflow
                TIMA = TMA;
                cpu.requestInterrupt(CPU.TIMA_OVERFLOW_IRQ);
            }
        }
    }

    public void resetDiv() {
        divCycles = 0;
        DIV = 0;
    }

    public byte getDIV() {
        return DIV;
    }

    public byte getTIMA() {
        return TIMA;
    }

    public byte getTMA() {
        return TMA;
    }

    public byte getTAC() {
        return TAC;
    }

    public void setTAC(byte value) {
        if ((value & 0x03) != (TAC & 0x03)) { // change only if new frequency is different
            timaCycles = 0;
            TAC = (byte) (value & 0x07); // Only 3 first bits are usable
            frequency = (byte) (TAC & 0x03);
            TIMA = TMA; // TODO why?
        }
    }

    public void setTMA(byte value) {
        TMA = value;
    }

    public void setTIMA(byte value) {
        TIMA = value;
    }
}