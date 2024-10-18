package co.kr.myfitnote.measurement;

import android.os.Bundle;

import co.kr.myfitnote.account.data.model.Client;
import co.kr.myfitnote.model.Exercise;
import co.kr.myfitnote.model.TestResult;

public interface MeasurementEventListener {
    public void onFragmentChange(int fragmentNum);
    public void nextFragment();
    public void nextFragment(Bundle bundle);

    /**
     * 설정 화면 단계에서 설정 완료 시 액티비티로 전달하기 위한 메소드
     * @param bundle
     */
    public void setOptions(Bundle bundle);

    public void restart();
    public void pause();
    public boolean isPause();

    public TestResult getTestResult();
    public Exercise getExercise();
    public void setStatus(int status);

    public void sendDataToServer();

    public Client getClient();
}
