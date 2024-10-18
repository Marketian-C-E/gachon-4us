package co.kr.myfitnote.game.model;


import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameResult{

    private String type = "EYEHAND";
    private int successCount = 0;
    private int failCount = 0;
    private int totalTimeSEC;
    private List<Long> clickedTime = new ArrayList<>();
    private List<String> clickedBall = new ArrayList<>();
    private List<Integer> showBalls = new ArrayList<>();

    public void addClickedTime(long time){
        clickedTime.add(time);
    }

    public void addClickedBall(String color){
        clickedBall.add(color);
    }

    public void addShowBall(int ball){
        showBalls.add(ball);
    }

    public int getGreenBallCount(){
        int cnt =0;
        for(int ball : showBalls){
            if(GameObject.GREEN == ball){
                cnt++;
            }
        }

        return cnt;
    }

    public String getType() {
        return type;
    }
}
