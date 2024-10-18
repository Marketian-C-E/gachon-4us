package co.kr.myfitnote.core.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import co.kr.myfitnote.R;

public class GradeView extends View {
    private int grade = 0;
    private int filledColor = getResources().getColor(R.color.GREEN_1);
    private int unfilledColor = getResources().getColor(R.color.e9ebef);
    private final Paint paint = new Paint();
    private final RectF rect = new RectF();
    private final Path path = new Path();
    private float currentFill = 0f;

    public GradeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setGrade(int grade) {
        this.grade = grade;
        // 1 -> 4, 2 -> 3, 3 -> 2, 4 -> 1 change grade reverse
        grade = 5 - grade;

        ValueAnimator animator = ValueAnimator.ofFloat(0f, grade);
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animation -> {
            currentFill = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    public void setColors(int filled, int unfilled) {
        this.filledColor = filled;
        this.unfilledColor = unfilled;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float margin = 10f; // Adjust the margin as needed
        float width = (getWidth() - margin * 3) / 4f; // Subtract the total margin before dividing by 4
        for (int i = 0; i < 4; i++) {
            rect.set(i * (width + margin), 0f, i * (width + margin) + width, getHeight());
            paint.setColor(i < currentFill ? filledColor : unfilledColor);
            path.reset();
            if (i == 0) {
                path.addRoundRect(rect, new float[]{100f, 100f, 0f, 0f, 0f, 0f, 100f, 100f}, Path.Direction.CW);
            } else if (i == 3) {
                path.addRoundRect(rect, new float[]{0f, 0f, 100f, 100f, 100f, 100f, 0f, 0f}, Path.Direction.CW);
            } else {
                path.addRect(rect, Path.Direction.CW);
            }
            canvas.drawPath(path, paint);
        }
    }
}