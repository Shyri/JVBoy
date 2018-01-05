package es.shyri.jvboy.cpu;

import es.shyri.jvboy.io.IODebugger;

/**
 * Created by shyri on 05/01/2018.
 */
public class TimersDebugger extends Timers {
    private IODebugger ioDebugger;

    public TimersDebugger(CPU cpu, IODebugger ioDebugger) {
        super(cpu);
        this.ioDebugger = ioDebugger;
    }

    @Override
    public void update(int cycles) {
        int prevDIV = DIV;
        int prevTIMA = TIMA;
        int prevTMA = TMA;
        int prevTAC = TAC;

        super.update(cycles);

        if (prevDIV != DIV) {
            ioDebugger.updateAddress(DIV_ADDRESS);
        }

        if (prevTIMA != TIMA) {
            ioDebugger.updateAddress(TIMA_ADDRESS);
        }

        if (prevTMA != TMA) {
            ioDebugger.updateAddress(TMA_ADDRESS);
        }

        if (prevTAC != TAC) {
            ioDebugger.updateAddress(TAC_ADDRESS);
        }
    }
}
