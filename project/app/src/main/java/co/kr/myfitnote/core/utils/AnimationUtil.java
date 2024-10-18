package co.kr.myfitnote.core.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;

public class AnimationUtil {

    static public void applyScaleAnimationToView(View view){
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f);
        scaleDownX.setDuration(1000);
        scaleDownY.setDuration(1000);

        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY);
        scaleDown.start();
    }

    static public void applyBounceAnimationToView(View view, int repeatCount) {
        final float bounceScale = 1.2f; // Adjust the bounce factor as needed
        final long duration = 700;

        final ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", bounceScale);
        final ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", bounceScale);

        scaleUpX.setDuration(duration / 2);
        scaleUpY.setDuration(duration / 2);

        scaleUpX.setInterpolator(new OvershootInterpolator()); // Create a bounce effect
        scaleUpY.setInterpolator(new OvershootInterpolator());

        final ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1f);
        final ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f);

        scaleDownX.setDuration(duration / 2);
        scaleDownY.setDuration(duration / 2);

        scaleDownX.setInterpolator(new AnticipateInterpolator()); // Create a bounce-back effect
        scaleDownY.setInterpolator(new AnticipateInterpolator());

        final AnimatorSet bounceAnimation = new AnimatorSet();
        bounceAnimation.play(scaleUpX).with(scaleUpY).before(scaleDownX).before(scaleDownY);

        // Add an AnimatorListener to restart the animation when it ends
        bounceAnimation.addListener(new AnimatorListenerAdapter() {
            private int repeatCounter = 0;

            @Override
            public void onAnimationEnd(Animator animation) {
                if (repeatCounter < repeatCount - 1) {
                    // Increment the repeat counter and restart the animation
                    repeatCounter++;
                    bounceAnimation.start();
                }
            }
        });

        // Start the animation initially
        bounceAnimation.start();
    }
}
