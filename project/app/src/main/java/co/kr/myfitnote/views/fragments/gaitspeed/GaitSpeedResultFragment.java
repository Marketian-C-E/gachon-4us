package co.kr.myfitnote.views.fragments.gaitspeed;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.kr.myfitnote.R;

public class GaitSpeedResultFragment extends Fragment {
    private String TAG = "GaitSpeedResultFragment";
    private TextView secondsView;
    private TextView gradeView;

    double time = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            time = getArguments().getDouble("second");
            Log.e(TAG, "Argument: " + time);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gait_speed_result, container, false);
        secondsView = view.findViewById(R.id.seconds_view);
        secondsView.setText(String.format("%.2f", time / 1000.0));

        return view;
    }
}