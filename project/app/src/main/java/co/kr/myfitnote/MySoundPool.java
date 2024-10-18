package co.kr.myfitnote;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

public class MySoundPool {
    private SoundPool soundPool;
    private int soundId ;
    public MySoundPool(Context context, int resId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .build();
        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
        }

        soundId = soundPool.load(context, resId, 1);
    }

    public void play(){
        soundPool.play(soundId, 1, 1, 1, 0, 1);
    }

    public void stop(){
        soundPool.stop(0);
    }
}
