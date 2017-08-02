package com.a424appslab.androidboy.lcd;

import android.graphics.Color;

import com.a424appslab.androidboy.cpu.CPU;
import com.a424appslab.androidboy.memory.MemoryMap;
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
    private MemoryMap memoryMap;

    /**
     * LCDC $FF40
     * Bit 7 - LCD Display Enable (0=Off, 1=On)
     * Bit 6 - Window Tile Map Display Select (0=9800-9BFF, 1=9C00-9FFF)
     * Bit 5 - Window Display Enable (0=Off, 1=On)
     * Bit 4 - BG & Window Tile Data Select (0=8800-97FF, 1=8000-8FFF)
     * Bit 3 - BG Tile Map Display Select (0=9800-9BFF, 1=9C00-9FFF)
     * Bit 2 - OBJ (Sprite) Size (0=8x8, 1=8x16)
     * Bit 1 - OBJ (Sprite) Display Enable (0=Off, 1=On)
     * Bit 0 - BG Display (for CGB see below) (0=Off, 1=On)
     */
    public byte LCDC = 0x00;

    public int LY = 0x00;

    /**
     * STAT $FF41
     * Bit 6 - LYC=LY Coincidence (Selectable)
     * Bit 5 - Mode 10
     * Bit 4 - Mode 01
     * Bit 3 - Mode 00 (0=Non Selection) (1=Selection)
     * Bit 2 - Coincidence Flag (0=LYC not equal to LCDC LY) (1=LYC = LCDC LY)
     * Bit 1-0 - Mode Flag
     * 00: During H-Blank
     * 01: During V-Blank
     * 10: During Searching OAM-RAM
     * 11: During Transfering Data to LCD Driver
     */
    public byte STAT = 0x00;

    /**
     * Bit 7-6 - Data for Dot Data 11 (Normally darkest color)
     * Bit 5-4 - Data for Dot Data 10
     * Bit 3-2 - Data for Dot Data 01
     * Bit 1-0 - Data for Dot Data 00 (Normally lightest color)
     */
    public byte BGP = 0x00;

    public byte SCY = 0x00;
    public byte SCX = 0x00;

    private int state;
    private int lyCounter = 0;

    public void init(CPU cpu, MemoryMap memoryMap, LCDRenderer lcdRenderer) {
        this.cpu = cpu;
        this.lcdRenderer = lcdRenderer;
        this.memoryMap = memoryMap;
    }

    public void update(int cycles) {
        if (!isLCDEnabled()) {
            return;
        }

        lyCounter = lyCounter + cycles;
        switch (state) {
            case STATE_OAM_SEARCH:
                if (lyCounter >= 20) {
                    // TODO Fetch Tiles + Sprites
                    state = STATE_PIXEL_TRANSFER;
                }
                break;
            case STATE_PIXEL_TRANSFER:
                if (lyCounter >= 63) {
                    // TODO Transfer pixels to framebuffer
                    state = STATE_H_BLANK;
                }
                break;
            case STATE_H_BLANK:
                // Do nothing
                break;
            case STATE_V_BLANK:
                // Do nothing
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

    private void getBackground() {
//        int tileDisp = 0x08 & LCDC;
        //        int tileDispAddr = tileDisp == 0 ? 0x9800 : 0x9C00;
        //        int tileMap = 0x10 & LCDC;
        //        int tileMapAddr = tileMap == 0 ? 0x8800 : 0x8000;
        //
        //        int tileIdAddr = LY << 3;
        //        for (int i = 0; i < 32; i++) {
        //            byte tileId = memoryMap.read(tileDisp + tileIdAddr + i);
        //            byte tile = memoryMap.read(tileMapAddr + tileId);
        //
        //        }
    }

    private int[] generateRandomColors() {
        int[] colors = new int[160];

        for (int i = 0; i < 160; i++) {
            colors[i] = Color.RED;
        }

        return colors;
    }

    private boolean isLCDEnabled() {
        return (LCDC & 0x80) > 0;
    }

}
