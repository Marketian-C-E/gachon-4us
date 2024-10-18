package co.kr.myfitnote.game.model;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.widget.ImageView;

public abstract class GameObject {

    public static final int GREEN = 0;
    public static final int RED = 1;
    public static final int COLOR_MAX = 1;

    private ImageView gridImageView;
    private int gridSize;
    private long clickedAt;


    public abstract void draw(Bitmap bitmap, Point point, float density);
    public abstract void hide(Bitmap bitmap);

    public abstract void setHideMS(int hideMS);
    public abstract int getHideMS();
    public abstract void setShowMS(int showMs);
    public abstract int getShowMS();



    public Point resize(float density){
        return null;
    }

    public Point getCircleCenterPoint(int width, int height,  int gridSize){
        int gridItemWidth = width / gridSize;
        int gridItemHeight = height / gridSize;
        int cx = gridItemWidth / 2;
        int cy = gridItemHeight / 2;
        return new Point(cx, cy);
    }
}
