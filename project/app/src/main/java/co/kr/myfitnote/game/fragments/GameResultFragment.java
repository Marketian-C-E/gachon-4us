package co.kr.myfitnote.game.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import co.kr.myfitnote.R;
import co.kr.myfitnote.views.HomeActivity;

public class GameResultFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_result, container, false);

        int successCount = getArguments().getInt("successCount");
        int greenBallCount = getArguments().getInt("greenBallCount");
        int failCount = getArguments().getInt("failCount");
        int totalTimeSEC = getArguments().getInt("totalTimeSEC");

        TextView successCountView = view.findViewById(R.id.success_count_view);
        successCountView.setText(successCount+"/"+greenBallCount);

        TextView failCountView = view.findViewById(R.id.fail_count_view);
        failCountView.setText(failCount+"");

        TextView totalTimeView = view.findViewById(R.id.totatl_time_view);
        totalTimeView.setText(totalTimeSEC+"");

        view.findViewById(R.id.home_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), HomeActivity.class);
               getContext().startActivity(intent);
               getActivity().finish();
            }
        });

        return view;
    }
}
