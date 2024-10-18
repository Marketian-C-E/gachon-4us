package co.kr.myfitnote.cm.ui.prescription;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.kr.myfitnote.R;

/**
 * 처방전 생성 결과
 */
public class PrescriptionResultFragment extends Fragment {

    public PrescriptionResultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_prescription_result, container, false);
    }
}