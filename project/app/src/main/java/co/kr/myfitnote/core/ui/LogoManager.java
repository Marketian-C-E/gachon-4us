package co.kr.myfitnote.core.ui;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import co.kr.myfitnote.core.utils.PreferencesManager;
import io.sentry.Sentry;

public class LogoManager {
    private static String TAG = "LogoManager";

    static private Uri makeUri(String logoPath){
        return Uri.parse("http://58.120.166.106:7575" + logoPath);
    }

    static public void setLogoToImageView(Context context, ImageView imageView){
        /**
         * ImageView에 Key에 존재하는 로고 삽입
         */
        try{
            PreferencesManager preferencesManager = PreferencesManager.getInstance(context);

            if (preferencesManager.getData("LOGO_PATH") == null){
                return;
            }
            Uri uri = LogoManager.makeUri(preferencesManager.getData("LOGO_PATH"));
//            imageView.setImageURI(uri);

            Glide.with(context)
                    .load(uri)
                    .into(imageView);

        } catch (Exception e){
            e.printStackTrace();
            Sentry.captureMessage(e.getMessage());
        }
    }
}
