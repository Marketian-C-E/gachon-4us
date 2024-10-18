package co.kr.myfitnote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import co.kr.myfitnote.R;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ViewHolder> {

    private List<ClassItem> dataList;
    private Context context;

    public ClassAdapter(List<ClassItem> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.classitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClassItem item = dataList.get(position);
        holder.titleTextView.setText(item.getName());
        holder.dateTextView.setText(item.getDate());
        holder.participantsTextView.setText(item.getpartipants() + "명 참여중");
        // Set thumbnail image here, you may use a library like Glide to load images
        Glide.with(context).load(item.getThumbnailUrl()).into(holder.thumbnailImageView);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnailImageView;
        TextView titleTextView, dateTextView, participantsTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailImageView = itemView.findViewById(R.id.thumbnail);
            titleTextView = itemView.findViewById(R.id.title);
            dateTextView = itemView.findViewById(R.id.date);
            participantsTextView = itemView.findViewById(R.id.participants);
        }
    }
}
