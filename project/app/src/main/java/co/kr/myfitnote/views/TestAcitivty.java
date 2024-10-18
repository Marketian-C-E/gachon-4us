package co.kr.myfitnote.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.polar.sdk.api.PolarBleApi;
import com.polar.sdk.api.PolarBleApiCallback;
import com.polar.sdk.api.PolarBleApiDefaultImpl;
import com.polar.sdk.api.errors.PolarInvalidArgument;
import com.polar.sdk.api.model.PolarDeviceInfo;
import com.polar.sdk.api.model.PolarHrData;

import java.util.Set;
import java.util.UUID;

import co.kr.myfitnote.R;
import co.kr.myfitnote.components.LevelProgressView;
import co.kr.myfitnote.components.LevelProgressWithLabelView;
import co.kr.myfitnote.core.utils.Metronome;
import co.kr.myfitnote.core.utils.SoundManager;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;

public class TestAcitivty extends AppCompatActivity {
    private String TAG = "TestAcitivty";

    private SeekBar seekBar;
    private Metronome metronome = null;
    private TextView seekBarValue;

    private Button playSound;
    private ProgressBar progressBar;

    private int maxHR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_acitivty);

        SoundManager.initSounds(this);

        int age = 25;
        maxHR = 220 - age;

        playSound = findViewById(R.id.playSound);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMin(0);
        progressBar.setMax(maxHR);

        seekBarValue = findViewById(R.id.seekBarValue);

        seekBar = findViewById(R.id.seekBar);
        seekBar.setMin(0);
        seekBar.setMax(200);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.e(TAG, "Progress is changed to " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int value = seekBar.getProgress();
                if (metronome != null){
                    metronome.stop();
                    metronome = null;
                }
                metronome = new Metronome(value);
                metronome.start();
//                progressBar.setProgress(value);
                ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", value);
                animation.setDuration(300);
                animation.setInterpolator(new LinearInterpolator());
                animation.start();
                seekBarValue.setText(String.valueOf(value) + " BPM");
            }
        });

        metronome = new Metronome(60); // 60 BPM
        metronome.start(); // Start the metronome

        playSound.setOnClickListener(v -> {
            // Run thread's run method
            new Thread(){
                @Override
                public void run() {
                    Log.e(TAG, "New thread is run!" + " " + this.getName());
                    playSound();
                }
            }.start();
        });

        /**
         * 220 - 나이 * 0.65 => 목표 심박수, 목표 심박수 이후에 5회 걷는 시간을 측정하여, 시간을 5회로 나누어 1회당 시간을 구한다. 이후에, 해당 시간으로 메트로놈 BPM을 계산한다.
         */

    }

    public void playSound(){
        SoundManager.play(R.raw.voice_pose_match_nose, this);
    }
}