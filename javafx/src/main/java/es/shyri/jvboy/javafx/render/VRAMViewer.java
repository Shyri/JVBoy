package es.shyri.jvboy.javafx.render;

import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;

/**
 * Created by shyri on 03/11/2017.
 */
public class VRAMViewer {
    WritableImage bgMapImage;
    int[] BGMap = new int[128 * 128];

    public VRAMViewer(HBox parent) {
        bgMapImage = new WritableImage(128, 128);
        ImageView bgMapImageView = new ImageView(bgMapImage);
        parent.getChildren()
              .add(0, bgMapImageView);

    }

    public void onVRAMUpdate(int address, byte[] ramValues) {
        updateBackgroundMap(address, ramValues);
    }

    public void onUpdateWholeVRAM(byte[] ramValues) {
        for (int i = 0x8000; i < 0x9999; i++) {
            updateBackgroundMap(i, ramValues);
        }
    }

    private void updateBackgroundMap(int address, byte[] ramValues) {
        int relAddress = address - 0x8000;
        int tileIndex = (relAddress % 4096) >> 4;
        int tileY = tileIndex >> 4;
        int tileX = tileIndex % 16;

        int byteLine = relAddress - (tileIndex << 4);
        int lineInTile = byteLine >> 1;

        int tile1;
        int tile2;

        if (address % 2 == 0) {
            tile1 = ramValues[address];
            tile2 = ramValues[address + 1];
        } else {
            tile1 = ramValues[address - 1];
            tile2 = ramValues[address];
        }

        for (int i = 0; i < 8; i++) {
            byte pixelIndex = (byte) ((0x80 >> i));
            int pixel1 = (((pixelIndex & tile1) & 0xFF) >> (7 - i));
            int pixel2 = (((pixelIndex & tile2) & 0xFF) >> (7 - i));
            byte pixel = (byte) ((pixel2 << 1) | pixel1);

            BGMap[tileY * 16 * 8 * 8 + tileX * 8 + (lineInTile > 0 ? lineInTile * 128 : 0) + i] = getColorForShade(pixel);
        }

        bgMapImage.getPixelWriter()
                  .setPixels(0, 0, 128, 128, PixelFormat.getIntArgbInstance(), BGMap, 0, 128);
    }

    private int getColorForShade(int shade) {
        if (shade == 0) {
            return 0xffffffff;
        } else if (shade == 1) {
            return 0xffBBBBBB;
        } else if (shade == 2) {
            return 0xff666666;
        } else {
            return 0xFF000000;
        }
    }
}
