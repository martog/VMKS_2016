package com.example.cripz.bluetoothrccarcontroller;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


public class MenuActivity extends AppCompatActivity {
    protected int currentMode;

    protected int getCurrentMode() {
        SharedPreferences modeOption = getSharedPreferences("Mode", 0);
        currentMode = modeOption.getInt("currentMode", 0);
        return currentMode;
    }

    protected void putCurrentMode(int currentMode) {
        SharedPreferences modeOption = getSharedPreferences("Mode", 0);
        SharedPreferences.Editor editor = modeOption.edit();
        editor.putInt("currentMode", currentMode);
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.controls:
                putCurrentMode(0);
                return true;
            case R.id.accelerometer:
                putCurrentMode(1);
                return true;
            case R.id.programming:
                putCurrentMode(2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
