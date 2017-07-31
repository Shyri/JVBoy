package com.a424appslab.androidboy.lcd;

import android.graphics.Color;

import com.a424appslab.androidboy.cpu.CPU;
import com.a424appslab.androidboy.render.LCDRenderer;

/**
 * Created by shyri on 19/07/17.
 */

public class PPU {
    private final static int STATE_OAM_SEARCH = 0;
    private final static int STATE_PIXEL_TRANSFER = 1;
    private final static int STATE_H_BLANK = 2;
    private final static int STATE_V_BLANK = 3;

    private LCDRenderer lcdRenderer;

    private CPU cpu;

    private byte LCDC = 0x00;
    private int LY = 0x00;
    private byte STAT = 0x00;

    private int state;
    private int lyCounter = 0;

    public void init(CPU cpu, LCDRenderer lcdRenderer) {
        this.cpu = cpu;
        this.lcdRenderer = lcdRenderer;
    }

    public void update(int cycles) {
        lyCounter = lyCounter + cycles;
        switch (state) {
            case STATE_OAM_SEARCH:
                // TODO Set State
                // TODO Fetch Tiles + Sprites
                if (lyCounter >= 20) {
                    state = STATE_PIXEL_TRANSFER;
                }
                break;
            case STATE_PIXEL_TRANSFER:
                // TODO Set State
                // TODO Transfer pixels to framebuffer
                if (lyCounter >= 63) {
                    state = STATE_H_BLANK;
                }
                break;
            case STATE_H_BLANK:
                // TODO Set State
                break;
            case STATE_V_BLANK:
                // do nothing
                break;
        }

        if (lyCounter >= 144) {
            if (LY == 153) {
                LY = 0;
                lyCounter = 0;
                return;
            }

            if (LY < 144) {
                lcdRenderer.updateLine(generateRandomColors(), LY);
            } else if (LY == 144) {
                state = STATE_V_BLANK;
                cpu.requestInterrupt(CPU.VBLANK_IRQ);
            }

            LY++;

            lyCounter = 0;
        }
    }

    private int[] generateRandomColors() {
        int[] colors = new int[160];

        for (int i = 0; i < 160; i++) {
            colors[i] = Color.RED;
        }

        return colors;
    }
}
