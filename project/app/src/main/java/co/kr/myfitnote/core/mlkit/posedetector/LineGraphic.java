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

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import co.kr.myfitnote.core.mlkit.GraphicOverlay;


public class LineGraphic extends GraphicOverlay.Graphic {

    private float STROKE_WIDTH = 40.0f;
    private float x1 = 0, x2 = 0;
    private float y1 = 0, y2 = 0;

    private float translateY = 0;
    private final Paint paint;

    public LineGraphic(
            GraphicOverlay overlay,
            String color,
            float x1, float y1,
            float x2, float y2) {

        super(overlay);
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;

        paint = new Paint();
        paint.setStrokeWidth(STROKE_WIDTH);
        paint.setColor(Color.parseColor(color));

    }

    public LineGraphic(
            GraphicOverlay overlay,
            String color,
            float x1, float y1,
            float x2, float y2,
            float STROKE_WIDTH) {

        super(overlay);
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;

        paint = new Paint();
        paint.setStrokeWidth(STROKE_WIDTH);
        paint.setColor(Color.parseColor(color));

    }

    @Override
    public void draw(Canvas canvas) {
        drawLine(canvas);
    }

    // drawLine function
    public void drawLine(Canvas canvas) {
        // Gets average z for the current body line
        canvas.drawLine(
                translateX(x1),
                translateY(y1),
                translateX(x2),
                translateY(y2),
                paint);
    }

    public float getTranslateY(float y) {
        return translateY(y);
    }
}
