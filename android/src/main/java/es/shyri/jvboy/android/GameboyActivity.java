package es.shyri.jvboy.android;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

import es.shyri.jvboy.GameBoy;
import es.shyri.jvboy.renderer.LCDRenderer;

public class GameboyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameboy);

        final LCDRenderer lcdRenderer = (LCDRenderer) findViewById(R.id.lcd);

        new Thread() {
            @Override
            public void run() {
                super.run();
                GameBoy gameBoy = new GameBoy();
                try {
                    File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    //                    gameBoy.loadBios(new File(downloadsDir, "bios.gb"));
                    //                    gameBoy.loadRom(new File(downloadsDir, "01-special.gb"));
                    //                    gameBoy.loadRom(new File(downloadsDir, "02-interrupts.gb"));
                    //                    gameBoy.loadRom(new File(downloadsDir, "03-op sp,hl.gb"));
                    //                    gameBoy.loadRom(new File(downloadsDir, "04-op r,imm.gb"));
                    //                    gameBoy.loadRom(new File(downloadsDir, "05-op rp.gb"));
//                    gameBoy.loadRom(new File(downloadsDir, "06-ld r,r.gb"));
                    //                    gameBoy.loadRom(new File(downloadsDir, "07-jr,jp,call,ret,rst.gb"));
                    //                    gameBoy.loadRom(new File(downloadsDir, "08-misc instrs.gb"));
                    //                    gameBoy.loadRom(new File(downloadsDir, "09-op r,r.gb"));
                    //                    gameBoy.loadRom(new File(downloadsDir, "10-bit ops.gb"));
                    //                    gameBoy.loadRom(new File(downloadsDir, "11-op a,(hl).gb"));

                    //                    gameBoy.loadRom(new File(downloadsDir, "cpu_registers_initial_dmg.gbc"));
                    gameBoy.loadRom(new File(downloadsDir, "Tetris.gb"));
                } catch (IOException e) {
                    e.printStackTrace();
                    requestPermission(GameboyActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                }
                gameBoy.init(lcdRenderer);
                gameBoy.start();

            }
        }.start();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean requestPermission(Activity activity, String permission) {

        boolean alreadyHas;
        //String locationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
        int hasPermission = activity.checkSelfPermission(permission);
        String[] permissions = new String[] {permission};
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            alreadyHas = false;
            activity.requestPermissions(permissions, 1);
        } else {
            alreadyHas = true;
        }
        return alreadyHas;
    }
}
