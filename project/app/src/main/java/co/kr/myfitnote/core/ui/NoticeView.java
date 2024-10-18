package co.kr.myfitnote.core.ui;


import android.content.Context;
import android.media.Image;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;

import co.kr.myfitnote.R;

public class NoticeView extends RelativeLayout {
    private TextView noticeTextView;
    private Animation fadeIn, fadeOut;
    private ImageView notice_image;
    private static final int MS_TIME = 2000;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            hideNotice();
        }
    };


    public NoticeView(Context context) {
        super(context);
        init(context);
    }

    public NoticeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NoticeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        // Inflate the layout
        LayoutInflater.from(context).inflate(R.layout.notice_view, this, true);

        // Get the TextView from the layout
        noticeTextView = findViewById(R.id.notice_text);
        notice_image = findViewById(R.id.notice_image);

        // Create the animations
        fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(1000);

        fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setStartOffset(1000);
        fadeOut.setDuration(1000);
    }

    public void showNotice(String noticeText, String imageUrl) {

        // If already textview is visible, do nothing
        if (this.getVisibility() == View.VISIBLE) {
            return;
        }

        this.setImage(imageUrl);

        noticeTextView.setText(noticeText);
        this.setVisibility(View.VISIBLE);
        this.startAnimation(fadeIn);

        handler.postDelayed(runnable, MS_TIME);
    }

    public void showNotice(String noticeText, int resourceId) {

        // If already textview is visible, do nothing
        if (this.getVisibility() == View.VISIBLE) {
            return;
        }

        noticeTextView.setText(noticeText);
        notice_image.setImageResource(resourceId);
        this.setVisibility(View.VISIBLE);
        this.startAnimation(fadeIn);

        handler.postDelayed(runnable, MS_TIME);
    }

    public void showNotice(String noticeText) {
        // If already textview is visible, do nothing
        if (this.getVisibility() == View.VISIBLE) {
            return;
        }

        this.setDefaultImage();

        noticeTextView.setText(noticeText);
        this.setVisibility(View.VISIBLE);
        this.startAnimation(fadeIn);

        handler.postDelayed(runnable, MS_TIME);
    }

    public void fixNotice(String noticeText){
        /**
         * 일정 시간 이후 사라지지 않는 시간을 띄울 때 사용
         */
        noticeTextView.setText(noticeText);
        this.setVisibility(View.VISIBLE);
        this.startAnimation(fadeIn);
    }

    public void hideNotice() {
        // if already textview is gone, do nothing
        if (this.getVisibility() == View.GONE) {
            return;
        }
        this.startAnimation(fadeOut);
        this.setVisibility(View.GONE);
    }

    // set image with url
    public void setImage(String url) {
        Glide.with(getContext()).load(url).into(notice_image);
    }

    // Set image with resource
    public void setDefaultImage(){
        notice_image.setImageResource(R.drawable.ic_guider);
    }

}
