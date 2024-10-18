package co.kr.myfitnote.game;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import co.kr.myfitnote.R;
import co.kr.myfitnote.SendEventListener;
import co.kr.myfitnote.game.fragments.GameResultFragment;
import co.kr.myfitnote.game.fragments.GameStartedFragment;
import co.kr.myfitnote.model.Exercise;
import co.kr.myfitnote.model.ManagerFragment;
import co.kr.myfitnote.model.TestResult;
import co.kr.myfitnote.views.fragments.ExplanationTextFragment;
import co.kr.myfitnote.views.fragments.ExplanationVideoFragment;

public class GameMain extends AppCompatActivity implements SendEventListener {
//    private final String TAG = "GameMain";

    private ManagerFragment managerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_main);

        managerFragment = new ManagerFragment();
        ExplanationVideoFragment explanationVideoFragment = new ExplanationVideoFragment(
                "눈/손 협응기능",
//                "http://nfa.kspo.or.kr/common/site/www/front/movie_zip/kind2_11/kind2_11.mp4",
                "menu3",
                Color.parseColor("#302b63"),
                Color.parseColor("#232a47"), 60);
//        explanationVideoFragment.setGridSizeVisible(View.VISIBLE);
        managerFragment.add(explanationVideoFragment);

        ExplanationTextFragment explanationTextFragment = new ExplanationTextFragment("눈/손 협응기능","지금부터 눈/손협응성검사를 시작합니다.",Color.parseColor("#302b63"), Color.parseColor("#232a47"));
        explanationTextFragment.setButtonVisible(View.GONE);
        managerFragment.add(explanationTextFragment);
//        managerFragment.add(new GameSettingFragment());
        managerFragment.add(new GameStartedFragment("눈/손 협응기능"));
        managerFragment.add(new GameResultFragment());

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, managerFragment.getCurrentFragment()).commit();
    }

    @Override
    public void nextFragment(Bundle bundle) {
        int index = managerFragment.getIndex() + 1;
        if(index < managerFragment.getSize() ) {


            managerFragment.setIndex(index);
            managerFragment.getCurrentFragment().setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, managerFragment.getCurrentFragment()).commit();

        }
    }

    @Override
    public void nextFragment() {
        int index = managerFragment.getIndex() + 1;
        if(index < managerFragment.getSize() ) {

            managerFragment.setIndex(index);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, managerFragment.getCurrentFragment()).commit();

        }
    }

    @Override
    public TestResult getTestResult() {
        return null;
    }

    @Override
    public Exercise getExercise() {
        return null;
    }

    @Override
    public void setStatus(int status) {

    }

    @Override
    public void onFragmentChange(int fragmentNum) {

    }

    @Override
    public void restart() {

    }

    @Override
    public void pause() {

    }

    @Override
    public boolean isPause() {
        return false;
    }



}