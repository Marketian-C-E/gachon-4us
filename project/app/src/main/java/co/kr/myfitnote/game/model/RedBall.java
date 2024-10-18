package co.kr.myfitnote.game.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.widget.ImageView;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedBall extends GameObject{


    private int hideMS;
    private int showMS;
    private ImageView gridImageView;
    private int gridSize;

    public RedBall(ImageView gridImageView, int gridSize){
        this.gridImageView = gridImageView;
        this.gridSize = gridSize;

    }

    @Override
    public void draw(Bitmap noObjectBitmap, Point point, float density) {
        Bitmap bitmap = noObjectBitmap.copy(noObjectBitmap.getConfig(),true);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(7);
        paint.setAntiAlias(true);
        int radius = (int) (gridImageView.getWidth() / gridSize / density);
        Point centerPoint = getCircleCenterPoint(gridImageView.getWidth(), gridImageView.getHeight(), gridSize);

        Log.e("poapo","radius: "+ radius);
        canvas.drawCircle(point.x + centerPoint.x, point.y + centerPoint.y, radius, paint);

        gridImageView.setImageBitmap(bitmap);
    }

    @Override
    public void hide(Bitmap noObjectBitmap) {
        gridImageView.setImageBitmap(noObjectBitmap);
    }
}
