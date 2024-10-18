package co.kr.myfitnote.views.view;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import co.kr.myfitnote.R;

public class ProgressCircleView extends View {
    private Boolean filled;
    public ProgressCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundResource(R.drawable.question_progress_circle);
        // attrs를 직접 사용하지 않음 (아래 문제)
        // 1. 스타일이 적용되지 않음.
        // 2. 속성 값 내의 리소스 참조가 결정되지 않음
        // -> TypedArray를 사용 (미리 declare-styleable을 색인시켜두어 상수를 사용할 수 있게 해 줌)
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.ProgressCircleView, 0, 0);

        try{
            filled = a.getBoolean(R.styleable.ProgressCircleView_filled, false);
            if (filled) setFilled(true);
        }finally {
            a.recycle();
        }
    }

    public Boolean getFilled() {
        return filled;
    }


    public void setFilled(Boolean filled) {
        /**
         * 동적으로 속성 및 동작을 변경하려면 getter, setter를 사용해야 함.
         */
        this.filled = filled;
        int colorFrom, colorTo;
        if (filled) {
            colorFrom = getResources().getColor(R.color.grey);
            colorTo = getResources().getColor(R.color.orange);
//            this.getBackground().setColorFilter(getResources().getColor(R.color.orange),
//                        PorterDuff.Mode.SRC_ATOP);
        }else{
            colorFrom = getResources().getColor(R.color.orange);
            colorTo = getResources().getColor(R.color.grey);
        }

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(250); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                ProgressCircleView.this.getBackground()
                        .setTint((int) animator.getAnimatedValue());
            }
        });
        colorAnimation.start();

        // 값 변경 후 다시 그려야 한다는 것을 시스템에 인식하도록 아래 코드 추가
        invalidate();
        requestLayout();
    }
}
