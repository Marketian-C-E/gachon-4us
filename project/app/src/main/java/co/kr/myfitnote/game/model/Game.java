package co.kr.myfitnote.game.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Build;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import co.kr.myfitnote.game.Publisher;
import co.kr.myfitnote.game.Subscribe;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Game implements Publisher{

    public static final int START = 1000;
    public static final int PAUSE = 1001;
    public static final int TIMER = 2000;
    public static final int COUNT = 2001;

    private final double RANDOM_MIN_MS = 500;
    private final double RANDOM_MAX_MS = 1000;

    private int status = START;
    private int gridSize = 3;
    private ImageView gridImageView;

    private Bitmap noObjectBitmap;
    private float density;
    private int gridId = -1;


    private GameObject gameObject;
//    private int count;

    private List<Subscribe> subscribes = new ArrayList<>();
    private List<Point> gridLocation = new ArrayList<>();
    private GameResult gameResult = new GameResult();

    public Game(int gridSize, int totalTimeSEC){
        this.gridSize = gridSize;
        gameResult.setTotalTimeSEC(totalTimeSEC);
    }

    public void setGridImageView(ImageView gridImageView){
        this.gridImageView = gridImageView;
        initPoint();
    }

    public boolean isHit(float x, float y){

        if(gridId == -1) return false;

        int gridItemWidth = gridImageView.getWidth() / gridSize;
        int gridItemHeight = gridImageView.getHeight() / gridSize;
        Point point = gridLocation.get(gridId);
        if (x >= point.x && x < point.x + gridItemWidth &&
                y >= point.y && y < point.y + gridItemHeight) {
            return true;
        }
        return false;


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setSuccessCount(int count){
        gameResult.setSuccessCount(count);
        notify(Game.COUNT,null);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setFailCount(int count){
        gameResult.setFailCount(count);
        notify(Game.COUNT,null);
    }



    /**
     * 게임시작
     */

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void start(){
        setStatus(Game.START);
        notify(status,null);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void pause(boolean isPause){
        setStatus(Game.PAUSE);
        notify(status, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void drawObject(int gridId){
        Random r = new Random();
        int colorId = r.nextInt(GameObject.COLOR_MAX + 1);

        Point point = gridLocation.get(gridId);
        setGridId(gridId);

        int nRandom = (int) (RANDOM_MIN_MS + (RANDOM_MAX_MS - RANDOM_MIN_MS) * r.nextDouble());

        gameResult.addShowBall(colorId);
        switch (colorId){
            case GameObject.GREEN:
                gameObject = new GreenBall(gridImageView, gridSize);
                gameObject.setHideMS(nRandom);
                gameObject.setShowMS(nRandom);
                gameObject.draw(noObjectBitmap, point, density);

                notify(Game.TIMER,null);
                break;
            case GameObject.RED:
                gameObject = new RedBall(gridImageView, gridSize);
                gameObject.setHideMS(nRandom);
                gameObject.setShowMS(nRandom);
                gameObject.draw(noObjectBitmap, point, density);

                notify(Game.TIMER,null);

                break;
        }
    }

    public void hideObject(){
        gameObject.hide(noObjectBitmap);
        setGridId(-1);
    }

    private void initPoint(){
        int startX = 0;
        int startY = 0;
        int gridItemWidth = gridImageView.getWidth() / gridSize;
        int gridItemHeight = gridImageView.getHeight() / gridSize;

        for(int i=0; i<gridSize; i++){
            for(int j=0; j<gridSize; j++){

                gridLocation.add(new Point(startX,startY));
                startX += gridItemWidth;
            }
            startX = 0;
            startY += gridItemHeight;
        }

    }


    /**
     * 그리드 그리기
     */
    public void draw(){
        int imageViewWidth = gridImageView.getWidth();
        int imageViewHeight = gridImageView.getHeight();


        Bitmap bitmap = Bitmap.createBitmap(imageViewWidth,imageViewHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#8a88ae"));
        paint.setStrokeWidth(7);

        int xWidth = imageViewWidth / gridSize;
        int yWidth = imageViewHeight / gridSize;

        int startX = xWidth;
        int startY = yWidth;

        for(int i=0; i<gridSize-1; i++){

            //가로선 그리기
            canvas.drawLine(0,startY,imageViewWidth,startY,paint);
            //세로선그리기
            canvas.drawLine(startX,0,startX,imageViewHeight,paint);
            startX += xWidth;
            startY += yWidth;

        }

        noObjectBitmap = bitmap;
        gridImageView.setImageBitmap(bitmap);
    }

    @Override
    public void subscribe(Subscribe subscribe) {
        subscribes.add(subscribe);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void notify(int status,String data) {
        subscribes.forEach(subscribe -> {
            subscribe.update(status, data);
        });
    }
}
