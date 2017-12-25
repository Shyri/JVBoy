package es.shyri.jvboy.javafx.render;

import com.sun.javafx.application.PlatformImpl;

import es.shyri.jvboy.renderer.LCDRenderer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;

/**
 * Created by shyri on 21/09/2017.
 */
public class LCDRendererFX implements LCDRenderer {
    private static final int HEIGHT = 144;
    private static final int WIDTH = 160;

    int[] frameBuffer = new int[HEIGHT * WIDTH];

    public PixelWriter gameboyScreen;

    public LCDRendererFX(PixelWriter gameboyScreen) {
        this.gameboyScreen = gameboyScreen;
    }

    @Override
    public void updateLine(final int[] colors, final int line) {
        //        new Thread() {
        //            @Override
        //            public void run() {
        for (int i = 0; i < colors.length; i++) {
            frameBuffer[line * WIDTH + i] = getColorForShade(colors[i]);
        }

        PlatformImpl.runAndWait(new Runnable() {
            @Override
            public void run() {
                gameboyScreen.setPixels(0, 0, WIDTH, HEIGHT, PixelFormat.getIntArgbInstance(), frameBuffer, 0, WIDTH);
            }
        });
        //    }
        //        }.start();
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

        //        f (shade == 0) {
        //            return 0xff879603;
        //        } else if (shade == 1) {
        //            return 0xff4d6b03;
        //        } else if (shade == 2) {
        //            return 0xff2b5503;
        //        } else {
        //            return 0xff144403;
        //        }
    }

}
