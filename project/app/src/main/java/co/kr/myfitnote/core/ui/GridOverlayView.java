package co.kr.myfitnote.core.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GridOverlayView extends View {
    private int numberOfLines;
    private Paint linePaint;

    public GridOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        numberOfLines = 10; // You can adjust this value as per your requirement

        linePaint = new Paint();
        linePaint.setColor(Color.WHITE);
        linePaint.setStrokeWidth(5f);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAlpha(128); // 50% transparent
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();

        // Draw vertical lines
        for (int i = 0; i < numberOfLines; i++) {
            float x = width * i / numberOfLines;
            if (i == numberOfLines / 2){
                linePaint.setColor(Color.RED);
            }
            canvas.drawLine(x, 0, x, height, linePaint);
            linePaint.setColor(Color.WHITE);
        }

        // Draw horizontal lines
        for (int i = 0; i < numberOfLines; i++) {
            float y = height * i / numberOfLines;
            canvas.drawLine(0, y, width, y, linePaint);
        }
    }
}