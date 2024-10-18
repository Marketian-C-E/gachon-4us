/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.kr.myfitnote.core.mlkit.posedetector;

import static java.lang.Math.max;
import static java.lang.Math.min;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.google.mlkit.vision.common.PointF3D;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.util.List;
import java.util.Locale;

import co.kr.myfitnote.R;
import co.kr.myfitnote.core.mlkit.GraphicOverlay;


public class CustomPoseGraphic extends GraphicOverlay.Graphic {

    private static final float STROKE_WIDTH = 10.0f;
    private int weight = 0;
    private float y = 0;
    private float translateY = 0;
    private int type = 0;
    private final Paint paint, startTextPaint, endTextPaint;

    public CustomPoseGraphic(
            GraphicOverlay overlay,
            String color,
            int type,
            float y) {
        /**
         * type 0 : start line
         * type 1 : end line
         */
        super(overlay);
        this.weight = overlay.getWidth();
        this.y = y;
        this.translateY = translateY(y);
        this.type = type;

        paint = new Paint();
        paint.setStrokeWidth(STROKE_WIDTH);
        paint.setColor(Color.parseColor(color));

        startTextPaint = new Paint();
        startTextPaint.setTextSize(50);
        startTextPaint.setColor(Color.parseColor("#000000"));

        endTextPaint = new Paint();
        endTextPaint.setTextSize(50);
        endTextPaint.setColor(Color.parseColor("#ffffff"));

    }

    @Override
    public void draw(Canvas canvas) {
        drawLine(canvas, y, paint);
    }

    public void drawLine(Canvas canvas, float y, Paint paint) {
        // Gets average z for the current body line
        canvas.drawLine(
                0,
                translateY,
                weight,
                translateY,
                paint);
    }

    public float getTranslateY(float y) {
        return translateY(y);
    }

}
