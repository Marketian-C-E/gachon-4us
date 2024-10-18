package co.kr.myfitnote.core.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import co.kr.myfitnote.R;

public class SoundManager {
    private static final String TAG = "SoundManager";
    public static final int GUIDE_START = R.raw.voice_guide_start;
    public static final int SHOW_FULL_BODY = R.raw.voice_show_full_body;
    public static final int SHOW_FULL_BODY_SEATING = R.raw.voice_show_full_body_with_seating;
    public static final int VOICE_NUMBER_1 = R.raw.voice_number_1;
    public static final int VOICE_NUMBER_2 = R.raw.voice_number_2;
    public static final int VOICE_NUMBER_3 = R.raw.voice_number_3;
    public static final int VOICE_NUMBER_4 = R.raw.voice_number_4;
    public static final int VOICE_NUMBER_5 = R.raw.voice_number_5;
    public static final int START_AFTER_FIVE = R.raw.voice_start_after_five;
    public static final int START_AFTER_FIVE_SHOW_SIDE = R.raw.voice_start_after_five_show_side;
    public static final int CHECK_RESULT = R.raw.voice_check_result;
    public static final int POSE_MATCH_NOSE = R.raw.voice_pose_match_nose;
    public static final int POSE_MATCH_SHOULDER = R.raw.voice_pose_match_shoulder;
    public static final int ROM_MAINTAIN_FIVE_SEC = R.raw.voice_rom_maintain_five_sec;

    private static SoundPool soundpool;
    private static HashMap<Integer, Integer> soundpoolMap;

    private static MediaPlayer mediaPlayer;

    private static boolean isPlaying = false;

    public static void initSounds(Context context){
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundpool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();
        soundpoolMap = new HashMap(20);
        soundpoolMap.put(GUIDE_START, GUIDE_START);
        soundpoolMap.put(SHOW_FULL_BODY, SHOW_FULL_BODY);
        soundpoolMap.put(SHOW_FULL_BODY_SEATING, SHOW_FULL_BODY_SEATING);
        soundpoolMap.put(VOICE_NUMBER_1, VOICE_NUMBER_2);
        soundpoolMap.put(VOICE_NUMBER_2, VOICE_NUMBER_2);
        soundpoolMap.put(VOICE_NUMBER_3, VOICE_NUMBER_3);
        soundpoolMap.put(VOICE_NUMBER_4, VOICE_NUMBER_4);
        soundpoolMap.put(VOICE_NUMBER_5, VOICE_NUMBER_5);
        soundpoolMap.put(START_AFTER_FIVE, START_AFTER_FIVE);
        soundpoolMap.put(CHECK_RESULT, CHECK_RESULT);
        soundpoolMap.put(POSE_MATCH_NOSE, POSE_MATCH_NOSE);
        soundpoolMap.put(POSE_MATCH_SHOULDER, POSE_MATCH_SHOULDER);
        soundpoolMap.put(START_AFTER_FIVE_SHOW_SIDE, START_AFTER_FIVE_SHOW_SIDE);
        soundpoolMap.put(ROM_MAINTAIN_FIVE_SEC, ROM_MAINTAIN_FIVE_SEC);
    }

    // play second sound file with n second parameter
    public static void playSecond(int second, Context context){
        if (second == 5){
            play(VOICE_NUMBER_5, context);
        } else if (second == 4){
            play(VOICE_NUMBER_4, context);
        } else if (second == 3){
            play(VOICE_NUMBER_3, context);
        } else if (second == 2){
            play(VOICE_NUMBER_2, context);
        } else if (second == 1){
            play(VOICE_NUMBER_1, context);
        }
    }


    public static void play(int raw_id, Context context){
        if (soundpoolMap.containsKey(raw_id)) {
            if (!isPlaying){
                isPlaying = true;
                mediaPlayer = MediaPlayer.create(context, raw_id);
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        isPlaying = false;
                    }
                });
            }
        }
    }

    public static void stop(){
        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static boolean isPlaying(){
        return isPlaying;
    }

    public static void cleanUp(){
        Log.e(TAG, "Called SoundManager cleanUp");
        if (soundpool != null){
            soundpool.release();
            soundpool = null;
        }

        if (mediaPlayer != null){
            if (mediaPlayer.isPlaying()){
                mediaPlayer.stop();
                isPlaying = false;
            }
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


}
