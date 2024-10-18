package co.kr.myfitnote.core.utils;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Metronome {
    private final int BPM;
    private final Handler handler;
    private final ToneGenerator toneGenerator;
    private final Runnable beeper;
    private Context context;

    public Metronome(int bpm) {
        this.context = context;
        this.BPM = bpm;
        this.handler = new Handler();
        this.toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        this.beeper = new Runnable() {
            @Override
            public void run() {
                toneGenerator.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                handler.postDelayed(this, 60000/BPM);
            }
        };
    }

    public void start() {
        handler.post(beeper);
    }

    public void stop() {
        handler.removeCallbacks(beeper);
        toneGenerator.release();
    }
}