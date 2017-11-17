package es.shyri.jvboy.lcd;

import es.shyri.jvboy.cpu.CPU;
import es.shyri.jvboy.memory.MemoryMap;
import es.shyri.jvboy.renderer.LCDRenderer;

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
     * LCDC $FF40 Bit 7 - LCD Display Enable (0=Off, 1=On) Bit 6 - Window Tile Map Display Select (0=9800-9BFF, 1=9C00-9FFF)
     * Bit 5 - Window Display Enable (0=Off, 1=On) Bit 4 - BG & Window Tile Data Select (0=8800-97FF, 1=8000-8FFF) Bit 3 - BG
     * Tile Map Display Select (0=9800-9BFF, 1=9C00-9FFF) Bit 2 - OBJ (Sprite) Size (0=8x8, 1=8x16) Bit 1 - OBJ (Sprite)
     * Display Enable (0=Off, 1=On) Bit 0 - BG Display (for CGB see below) (0=Off, 1=On)
     */
    public byte LCDC = 0x00;

    public int LY = 0x00;

    public int LYC = 0x00;
    /**
     * STAT $FF41 Bit 6 - LYC=LY Coincidence (Selectable) Bit 5 - Mode 10 Bit 4 - Mode 01 Bit 3 - Mode 00 (0=Non Selection)
     * (1=Selection) Bit 2 - Coincidence Flag (0=LYC not equal to LCDC LY) (1=LYC = LCDC LY) Bit 1-0 - Mode Flag 00: During
     * H-Blank 01: During V-Blank 10: During Searching OAM-RAM 11: During Transfering Data to LCD Driver
     */
    public byte STAT = 0x00;

    /**
     * Bit 7-6 - Data for Dot Data 11 (Normally darkest color) Bit 5-4 - Data for Dot Data 10 Bit 3-2 - Data for Dot Data 01
     * Bit 1-0 - Data for Dot Data 00 (Normally lightest color)
     */
    public byte BGP = 0x00;

    public byte SCY = 0x00;
    public byte SCX = 0x00;

    public byte WY = 0x00;

    public byte WX = 0x00;

    public byte OBP0 = 0x00;
    public byte OBP1 = 0x00;

    public byte DMA = 0x00;

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
                    state = STATE_PIXEL_TRANSFER;
                    updateSTATStatus();
                }
                break;
            case STATE_PIXEL_TRANSFER:
                if (lyCounter >= 63) {
                    state = STATE_H_BLANK;
                    updateSTATStatus();
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
                lcdRenderer.updateLine(getBackgroundLine(), LY);
            } else if (LY == 144) {
                state = STATE_V_BLANK;
                updateSTATStatus();
                cpu.requestInterrupt(CPU.VBLANK_IRQ);
            }

            LY++;
            checkLYC();

            lyCounter = 0;
        }
    }

    private int[] getBackgroundLine() {
        int[] pixels = new int[160];

        int tileMap = 0x08 & LCDC;
        int tileMapAddr = tileMap == 0 ? 0x9800 : 0x9C00;
        int tileDisp = 0x10 & LCDC;
        int tileDispAddr = tileDisp == 0 ? 0x8800 : 0x8000;

        int tileIndex = LY >> 3;
        int lineInTile = LY - (tileIndex << 3);

        int z = 0;
        for (int i = 0; i < 20; i++) {
            int tileId = memoryMap.read(tileMapAddr + tileIndex * 32 + i) & 0xFF;

            byte tile1 = memoryMap.read(tileDispAddr + tileId * 16 + (lineInTile << 1));
            byte tile2 = memoryMap.read(tileDispAddr + tileId * 16 + (lineInTile << 1) + 1);

            for (int j = 0; j < 8; j++) {
                byte pixelIndex = (byte) ((0x80 >> j));
                int pixel1 = (((pixelIndex & tile1) & 0xFF) >> (7 - j));
                int pixel2 = (((pixelIndex & tile2) & 0xFF) >> (7 - j));
                byte pixel = (byte) ((pixel2 << 1) | pixel1);
                //                byte pixelIndex = (byte) ((0x80 >> j) & 0xFF);
                //                byte pixel1 = (byte) ((pixelIndex & tile1) >> (7 - j));
                //                byte pixel2 = (byte) ((pixelIndex & tile2) >> (7 - j));
                //                byte pixel = (byte) ((pixel1 << 1) | pixel2);
                pixels[z] = shadeFor(pixel);
                z++;
            }

            if (z == 160) {
                break;
            }
        }

        return pixels;
    }

    private boolean isLCDEnabled() {
        return (LCDC & 0x80) > 0;
    }

    private void checkLYC() {
        if (LY == LYC) {
            STAT = (byte) (STAT & 0xFB);
        } else {
            STAT = (byte) (STAT | 0x04);
        }
    }

    private void updateSTATStatus() {
        STAT = (byte) (STAT & 0xFC);
        switch (state) {
            case STATE_OAM_SEARCH:
                STAT = (byte) (STAT & 0xFE);
                break;
            case STATE_PIXEL_TRANSFER:
                STAT = (byte) (STAT & 0xFF);
                break;
            case STATE_H_BLANK:
                // Leave it like it is
                break;
            case STATE_V_BLANK:
                STAT = (byte) (STAT & 0xFD);
                break;
        }
    }

    private int shadeFor(int colorNumber) {
        return (BGP >> (colorNumber * 2)) & 0x3;
    }

}