package co.kr.myfitnote.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class LevelProgressView extends View {
    private int level;
    private Paint paint;
    private RectF[] rects;

    private String activeColor = "#00b0a6";
    private String inactiveColor = "#efefef";

    public LevelProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        level = 1; // Default level
        paint = new Paint();
        paint.setAntiAlias(true);
        rects = new RectF[4];
        for (int i = 0; i < rects.length; i++) {
            rects[i] = new RectF();
        }
    }

    public void setLevel(int level) {
        if (level >= 1 && level <= 4) {
            this.level = level;
            invalidate(); // Redraw the view with the new level
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        // Calculate the width of each rectangle based on the level
        int rectWidth = width / 4;

        // Set the radius for the first and last rectangles
        float radius = rectWidth / 2;

        // Draw the rectangles based on the current level
        for (int i = 0; i < 4; i++) {
            paint.setColor(Color.parseColor(i < level ? activeColor : inactiveColor)); // Change the color as needed
            rects[i].set(i * rectWidth, 0, (i + 1) * rectWidth - 1, height);

            // Create a path to define rounded corners on the left side of the first rectangle
            Path path = new Path();
            if (i == 0) {
                path.addRoundRect(rects[i], new float[]{radius, radius, 0, 0, 0, 0, radius, radius}, Path.Direction.CW);
            } else if (i == 3) {
                path.addRoundRect(rects[i], new float[]{0, 0, radius, radius, radius, radius, 0, 0}, Path.Direction.CW);
            } else {
                path.addRect(rects[i], Path.Direction.CW); // No radius for inner rectangles
            }

            canvas.drawPath(path, paint);
        }
    }
}
