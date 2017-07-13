package com.a424appslab.androidboy;

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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GameBoy gameBoy = new GameBoy();
        try {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            gameBoy.loadRom(new File(downloadsDir, "Tetris.gb"));
        } catch (IOException e) {
            e.printStackTrace();
            requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        gameBoy.init();
        gameBoy.start();

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
