package co.kr.myfitnote.cm.ui.measurement;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import co.kr.myfitnote.R;
import co.kr.myfitnote.views.fragments.result.ExerciseSeriesData;

public class RomResultCardAdapter extends RecyclerView.Adapter<RomResultCardAdapter.ViewHolder> {

    private ArrayList<ExerciseSeriesData> romResultList;

    private String unitOfAngle = "°(도)";

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView image;
        private final TextView value, date;

        public ViewHolder(View view) {
            super(view);

            image = view.findViewById(R.id.image);
            value = view.findViewById(R.id.value);
            date = view.findViewById(R.id.date);
            // Define click listener for the ViewHolder's View
        }

    }

    public RomResultCardAdapter(ArrayList<ExerciseSeriesData> romResultList) {
        this.romResultList = romResultList;

        // iterate through the list
        for (ExerciseSeriesData data : romResultList) {
            Log.e("RomResultCardAdapter", "RomResultCardAdapter: " + data.getDate() + " " + data.getImageUrl());

        }
    }

    @NonNull
    @Override
    public RomResultCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rom_result_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        ExerciseSeriesData data = romResultList.get(position);

        holder.value.setText(data.getValue() + unitOfAngle);
        holder.date.setText(data.getDate());
        // holder.image.setImageResource(R.drawable.ic_stand);
        Glide.with(holder.itemView.getContext())
                .load(data.getImageUrl())
                .into(holder.image);
    }


    @Override
    public int getItemCount() {
        return romResultList.size();
    }
}
