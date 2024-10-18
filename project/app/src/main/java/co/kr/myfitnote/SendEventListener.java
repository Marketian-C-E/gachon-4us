package co.kr.myfitnote;

import android.os.Bundle;

import co.kr.myfitnote.model.Exercise;
import co.kr.myfitnote.model.TestResult;

public interface SendEventListener {
    public void onFragmentChange(int fragmentNum);
    public void restart();
    public void pause();
    public boolean isPause();
    public void nextFragment();
    public void nextFragment(Bundle bundle);
    public TestResult getTestResult();
    public Exercise getExercise();
    public void setStatus(int status);

}
