package co.kr.myfitnote.core.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.widget.ScrollView;

public class BitmapUtil {
    static public Bitmap getBitmapFromView(View view) {
        // Create a Bitmap with the same dimensions as the View
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);

        // Draw the view inside the Bitmap
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    static public Bitmap getBitmapFromView(ScrollView scrollView) {
        // Get the total height of the ScrollView's content
        int totalHeight = scrollView.getChildAt(0).getHeight();

        // Create a Bitmap with the same width as the ScrollView and the total height
        Bitmap bitmap = Bitmap.createBitmap(scrollView.getWidth(), totalHeight, Bitmap.Config.ARGB_8888);

        // Draw the ScrollView's content inside the Bitmap
        Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);

        return bitmap;
    }
}
