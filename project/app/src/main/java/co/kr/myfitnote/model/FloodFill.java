package co.kr.myfitnote.model;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;

import java.util.LinkedList;
import java.util.Queue;

public class FloodFill {
    public static final int CHEST_TYPE = 0;
    public static final int LEFT_THIGH_TYPE = 1;
    public static final int RIGHT_THIGH_TYPE = 2;
    public static final int RIGHT_CALF_TYPE = 3;
    public static final int LEFT_CALF_TYPE = 4;
    public static final int LEFT_ANKLE_TYPE = 5;
    public static final int RIGHT_ANKLE_TYPE = 6;

    private Point[] chestPoints = {
            new Point(260,325),
            new Point(325,325),
            new Point(260,375),
            new Point(325,375),
            new Point(260,420),
            new Point(325,420),
            new Point(260,470),
            new Point(325,470)
    };

    private Point[] leftThighPoints = {
            new Point(325,600),
            new Point(315,630),
            new Point(340,630),
            new Point(365,630),
            new Point(340,720),
            new Point(393,720),
    };

    private Point[] rightThighPoints = {
            new Point(267,600),
            new Point(257,650),
            new Point(276,650),
            new Point(230,650),
            new Point(194,700),
            new Point(255,730)
    };

    private Point[] rightCalfPoints = {
            new Point(257,800),
            new Point(235,800),
            new Point(210,880),
            new Point(245,890),
            new Point(200,905),
            new Point(237,1027)
    };

    private Point[]leftCalfPoints = {
            new Point(355,1025),
            new Point(355,1027),
            new Point(365,980),
            new Point(350,965),
            new Point(356,855),
            new Point(363,825)
    };

    private Point[] leftAnklePoints = {
            new Point(360,1055),
    };

    private Point[] rightAnklePoints = {
            new Point(230, 1055)
    };

    public Bitmap floodFill(Bitmap bmp,Point pt, int replacementColor) {
        bmp = bmp.copy(bmp.getConfig(), true);
        Queue q = new LinkedList();

        q.add(pt);

        boolean[][] visited = new boolean[bmp.getWidth()][bmp.getHeight()];
        for(int x = 0; x < bmp.getWidth(); x++){
            for(int y =0; y<bmp.getHeight(); y++){
                visited[x][y] = false;
            }
        }

        while (q.size() > 0) {

            Point n = (Point) q.poll();

            if (isCheckColor(bmp.getPixel(n.x, n.y)) && !visited[n.x][n.y]){
                bmp.setPixel(n.x, n.y, replacementColor);
                visited[n.x][n.y] = true;
                q.add(new Point(n.x, n.y));
            }
            if(isCheckColor(bmp.getPixel(n.x+1, n.y)) && !visited[n.x+1][n.y]){ //오른쪽
                bmp.setPixel(n.x+1, n.y, replacementColor);
                visited[n.x+1][n.y] = true;
                q.add(new Point(n.x+1, n.y));
            }

            if(isCheckColor(bmp.getPixel(n.x-1, n.y)) && !visited[n.x-1][n.y]){ //왼쪽
                bmp.setPixel(n.x-1, n.y, replacementColor);
                visited[n.x-1][n.y] = true;
                q.add(new Point(n.x-1, n.y));
            }
            if(isCheckColor(bmp.getPixel(n.x, n.y-1)) && !visited[n.x][n.y-1]){ //위
                bmp.setPixel(n.x, n.y-1, replacementColor);
                visited[n.x][n.y-1] = true;
                q.add(new Point(n.x, n.y-1));
            }

            if(n.y+1<bmp.getHeight() && isCheckColor(bmp.getPixel(n.x, n.y+1)) && !visited[n.x][n.y+1]){ //아래
                bmp.setPixel(n.x, n.y+1, replacementColor);
                visited[n.x][n.y+1] = true;
                q.add(new Point(n.x, n.y+1));
            }


        }
        return bmp;
    }

    public Bitmap find(Bitmap bmp,Point pt, int replacementColor) {
        bmp = bmp.copy(bmp.getConfig(), true);
        for(int i=pt.x; i<pt.x+10; i++){
            for(int j=pt.y; j<pt.y+10; j++){
                bmp.setPixel(i,j, Color.YELLOW);
            }
        }

        return bmp;
    }

    public Bitmap fillColor(Bitmap bitmap, int type,  int replaceColor){
        Point[] points = null;
        switch (type){
            case CHEST_TYPE:
                points = chestPoints;
                break;
            case LEFT_THIGH_TYPE:
                points = leftThighPoints;
                break;
            case RIGHT_THIGH_TYPE:
                points = rightThighPoints;
                break;
            case RIGHT_CALF_TYPE:
                points = rightCalfPoints;
                break;
            case LEFT_CALF_TYPE:
                points = leftCalfPoints;
                break;
            case LEFT_ANKLE_TYPE:
                points = leftAnklePoints;
                break;
            case RIGHT_ANKLE_TYPE:
                points = rightAnklePoints;
                break;

        }
        for(Point pt : points){

            bitmap = floodFill(bitmap, pt, replaceColor);
        }
        return bitmap;
    }


    private boolean isCheckColor(int color){
        if(color == -855310){
            return false;
        }

        if(color == -1){
            return false;
        }
        if(color == -2368549){
            return false;
        }

        return true;
    }
}
