package co.kr.myfitnote.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import co.kr.myfitnote.R;

public class SurfaceDrawingView extends SurfaceView implements SurfaceHolder.Callback {
    private String TAG = "SurfaceDrawingView";
    private Paint paint;
    private int width, height;

    private int strokeWidth = 50;

    public SurfaceDrawingView(Context context) {
        super(context);
        getHolder().addCallback(this);
        init();
    }

    public SurfaceDrawingView(Context context, AttributeSet attrs){
        super(context, attrs);
        getHolder().addCallback(this);
        init();
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;

        if (this.paint != null){
            this.paint.setStrokeWidth(strokeWidth);
        }
    }

    private void init(){
        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.GREEN_1));
        paint.setStyle(Paint.Style.STROKE);
        paint.setAlpha(128);
        paint.setStrokeWidth(strokeWidth);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        width = getWidth();
        height = getHeight();
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }

    public void drawlines(ArrayList<PointF> pointList, ArrayList<Integer> indices, ArrayList<String> colorList){
        /**
         * drawlines when set line colors
         */
        Canvas canvas = getHolder().lockCanvas();
        for (int i=0; i<indices.size(); i+=2) {
            paint.setColor(Color.parseColor(colorList.get(i / 2)));
            canvas.drawLine(pointList.get(indices.get(i)).x * width,
                    pointList.get(indices.get(i)).y * height,
                    pointList.get(indices.get(i+1)).x * width,
                    pointList.get(indices.get(i+1)).y * height,
                    paint);
        }
        // Unlock the canvas to show the drawing
        getHolder().unlockCanvasAndPost(canvas);
    }

    public void drawlines(ArrayList<PointF> pointList, ArrayList<Integer> indices){
        /**
         * Draw multiple line between Point with steb by 2. (i, i+1)
         * e.g) Pointlist, [0, 1, 0, 2] means that drawline(0, 1) and drawline(0, 2)
         */
        Canvas canvas = getHolder().lockCanvas();
        for (int i=0; i<indices.size(); i+=2) {
            canvas.drawLine(pointList.get(indices.get(i)).x * width,
                    pointList.get(indices.get(i)).y * height,
                    pointList.get(indices.get(i+1)).x * width,
                    pointList.get(indices.get(i+1)).y * height,
                    paint);
        }
        // Unlock the canvas to show the drawing
        getHolder().unlockCanvasAndPost(canvas);
    }

    public void drawline(ArrayList<PointF> pointList, int i1, int i2, boolean isInfinite){
        Canvas canvas = getHolder().lockCanvas();
        float x1 = pointList.get(i1).x * width;
        float y1 = pointList.get(i1).y * height;
        float x2 = pointList.get(i2).x * width;
        float y2 = pointList.get(i2).y * height;

        float A = y1 - y2;
        float B = x2 - x1;
        float C = (x1 * y2) - (x2 * y1);

        float leftEdge = 0;
        float rightEdge = width;
        float topEdge = 0;
        float bottomEdge = height;

        // Calculate the intersection points of the line with the edges of the view
        float leftIntersectionY = (-C - A * leftEdge) / B;
        float rightIntersectionY = (-C - A * rightEdge) / B;
        float topIntersectionX = (-C - B * topEdge) / A;
        float bottomIntersectionX = (-C - B * bottomEdge) / A;

        // Define the line coordinates
        float startX = 0, startY = 0, endX = 0, endY = 0;

        if (leftIntersectionY >= topEdge && leftIntersectionY <= bottomEdge) {
            startX = leftEdge;
            startY = leftIntersectionY;
        } else if (topIntersectionX >= leftEdge && topIntersectionX <= rightEdge) {
            startX = topIntersectionX;
            startY = topEdge;
        }

        if (rightIntersectionY >= topEdge && rightIntersectionY <= bottomEdge) {
            endX = rightEdge;
            endY = rightIntersectionY;
        } else if (bottomIntersectionX >= leftEdge && bottomIntersectionX <= rightEdge) {
            endX = bottomIntersectionX;
            endY = bottomEdge;
        }

        // Draw the line
        canvas.drawLine(startX, startY, endX, endY, paint);
        getHolder().unlockCanvasAndPost(canvas);
    }

    public void drawline(ArrayList<PointF> pointList, int i1, int i2){
        /**
         * Draw line between PointF1, PointF2
         * i1 is first index for connection
         * i2 is second index for conneciton
         */
        Canvas canvas = getHolder().lockCanvas();
        canvas.drawLine(pointList.get(i1).x * width,
                pointList.get(i1).y * height,
                pointList.get(i2).x * width,
                pointList.get(i2).y * height,
                paint);
        // Unlock the canvas to show the drawing
        getHolder().unlockCanvasAndPost(canvas);
    }

    public void cleanCanvas(){
        Canvas canvas = getHolder().lockCanvas();
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        getHolder().unlockCanvasAndPost(canvas);
    }

    public void drawDot(float x, float y) {
        // Draw a dot at the specified position (x, y)
        Canvas canvas = getHolder().lockCanvas();
        canvas.drawCircle(x , y, 10f, paint);
        getHolder().unlockCanvasAndPost(canvas);
    }
}
