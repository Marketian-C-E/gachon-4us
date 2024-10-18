package co.kr.myfitnote.model;

import android.graphics.Color;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Graph {
    private float x;
    private  float y;

    private int color = Color.WHITE;
    private int circleColor = Color.WHITE;
    private int valueTextColor = Color.WHITE;
    private int xAxisColor = Color.WHITE;
    private int axisLeft = Color.WHITE;
    private int axisRight = Color.WHITE;
    private int legendColor = Color.WHITE;
    private int descriptionColor = Color.WHITE;

    public Graph(float x, float y){
        this.x = x;
        this.y = y;
    }


}
