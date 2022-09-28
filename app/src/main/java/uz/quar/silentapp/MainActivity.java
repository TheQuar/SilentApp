package uz.quar.silentapp;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import uz.quar.silentapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private NotificationManager mNotificationManager;
    private AudioManager audioManager;
    private boolean permission;
    private boolean isMute;
    private boolean isPauseApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        checkPermission();

        if (permission) {
            binding.switch1.setChecked(isMute);
            binding.switch1.setOnClickListener(v -> {
                if (isMute) {
                    mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                } else {
                    mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
                }
                isMute = binding.switch1.isChecked();
                audioControl(isMute);
            });
        }
    }

    private void checkPermission() {
        try {
            permission = mNotificationManager.isNotificationPolicyAccessGranted();

            if (!permission) {
                showDialog("Dasturga ruxsat bering: " + getResources().getString(R.string.app_name));
            } else {
                isMute = (mNotificationManager.getCurrentInterruptionFilter() == NotificationManager.INTERRUPTION_FILTER_NONE);
            }
        } catch (Exception e) {
            showDialog(e.getMessage());
        }
    }

    private void showDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Alert")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    if (!permission) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void audioControl(boolean b) {
        audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, b);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPauseApp = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPauseApp) {
            checkPermission();
            isPauseApp = false;
        }

    }
}