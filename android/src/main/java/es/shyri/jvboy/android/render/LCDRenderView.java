package es.shyri.jvboy.android.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import es.shyri.jvboy.renderer.LCDRenderer;

/**
 * Created by shyri on 19/07/17.
 */

public class LCDRenderView extends View implements LCDRenderer {
    private static final int HEIGHT = 144;
    private static final int WIDTH = 160;

    int[] frameBuffer = new int[HEIGHT * WIDTH];
    Bitmap bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);

    public LCDRenderView(Context context) {
        super(context);
    }

    public LCDRenderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LCDRenderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LCDRenderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        bitmap.setPixels(frameBuffer, 0, WIDTH, 0, 0, WIDTH, HEIGHT);
        canvas.drawBitmap(bitmap, 0, 0, null);
    }

    public void updateLine(int[] colors, int line) {
        for (int i = 0; i < colors.length; i++) {
            frameBuffer[line * WIDTH + i] = colors[i];
        }
        post(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        });
    }
}