package co.kr.myfitnote.cm.ui.client;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import co.kr.myfitnote.R;
import co.kr.myfitnote.account.data.model.Client;
import co.kr.myfitnote.core.ui.StarRatingView;
import co.kr.myfitnote.measurement.data.ClientMeasurementData;
import co.kr.myfitnote.measurement.data.ClientMeasurementResultData;

public class ClientResultAdapter extends RecyclerView.Adapter<ClientResultAdapter.ClientResultViewHolder> {
    static final private String TAG = "ClientResultAdapter";
    private List<ClientMeasurementData> resultList;

    private List<ClientMeasurementData> checkedItems = new ArrayList<>();

    private NavController navController;

    public ClientResultAdapter(List<ClientMeasurementData> results, NavController navController){
        this.resultList = results;
        this.navController = navController;
    }

    @NonNull
    @Override
    public ClientResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cm_client_measurement_result_item, parent, false);
        return new ClientResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientResultViewHolder holder, int position) {
        ClientMeasurementData data = resultList.get(position);

//        holder.gradeTV.setText(data.getGrade());
        holder.measurementTV.setText(data.getExercise());
        holder.didDateTV.setText(data.getCreated_at());
        holder.checkbox.setChecked(data.isChecked());

        // if data.getGrade can be integer, parse it to integer and set it to starRatingView
        try {
            int grade = Integer.parseInt(data.getGrade());
            holder.starRatingView.setStarRating(grade);
        } catch (NumberFormatException e) {
            holder.starRatingView.setStarRating(0);
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public List<ClientMeasurementData> getCheckedClientMeasurements(){
        checkedItems.clear();
        for (ClientMeasurementData result : resultList) {
            if (result.isChecked()) {
                checkedItems.add(result);
            }
        }
        return checkedItems;
    }

    class ClientResultViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkbox;
        TextView gradeTV;
        TextView didDateTV;
        TextView measurementTV;
        StarRatingView starRatingView;

        public ClientResultViewHolder(@NonNull View itemView) {
            super(itemView);

            checkbox = itemView.findViewById(R.id.item_checkbox);
//            gradeTV = itemView.findViewById(R.id.grade_tv);
            didDateTV = itemView.findViewById(R.id.did_date_tv);
            measurementTV = itemView.findViewById(R.id.measurement_tv);
            starRatingView = itemView.findViewById(R.id.star_rating_view);

            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "Checkbox Clicked! => " + v.getId());
                    resultList.get(getAdapterPosition()).setChecked(checkbox.isChecked());
                }
            });
        }
    }

    public void updateData(List<ClientMeasurementData> newData) {
        this.resultList = newData;
        notifyDataSetChanged();
    }
}
