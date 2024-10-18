package co.kr.myfitnote.core.ui;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.TextView;

public class AnimationManager {
    /**
     * 애니메이션 매니저 클래스
     */

    public void animateTextView(int initialValue, int finalValue, final TextView textview) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(initialValue, finalValue);
        valueAnimator.setDuration(1000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                textview.setText(String.format("%,d", valueAnimator.getAnimatedValue()));
            }
        });
        valueAnimator.start();
    }

    public void fadeIn(final View view){
        view.setVisibility(View.VISIBLE);
        view.setAlpha(0f);

        ViewPropertyAnimator animator = view.animate();
        animator.alpha(1f);
        animator.setDuration(500); // Adjust the duration as needed
        animator.start();
    }

    public void fadeOut(final View view){
        ViewPropertyAnimator animator = view.animate();
        animator.alpha(0f);
        animator.setDuration(500); // Adjust the duration as needed
        animator.withEndAction(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.GONE);
            }
        });
        animator.start();
    }
}
