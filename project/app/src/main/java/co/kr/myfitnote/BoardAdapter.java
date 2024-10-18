package co.kr.myfitnote;

import static com.bumptech.glide.Glide.with;

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

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.ViewHolder> {

    private List<BoardItem> mData;
    private LayoutInflater mInflater;
    private OnItemClickListener mListener; // 아이템 클릭 리스너

    // 데이터 모델 클래스
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, content;
        ImageView thumbnail;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textViewTitle);
            content = itemView.findViewById(R.id.textViewContent);
            thumbnail = itemView.findViewById(R.id.imageViewThumbnail);

            // 아이템 클릭 이벤트 설정
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClicked(position);
                    }
                }
            });
        }
    }

    // 생성자
    public BoardAdapter(Context context, List<BoardItem> data, OnItemClickListener listener) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mListener = listener;
    }

    // 아이템 레이아웃을 가져와 뷰 홀더에 연결
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.board_item, parent, false);
        return new ViewHolder(view);
    }

    // 뷰 홀더에 데이터 바인딩
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BoardItem item = mData.get(position);
        holder.title.setText(item.getTitle());
        holder.content.setText(item.getContent());
//        if (!item.getThumbnail().equals("")) {
//            Glide.with(holder.itemView).load(item.getThumbnail()).into(holder.thumbnail);
//        }else{
//            holder.thumbnail.setImageResource(R.drawable.strawberry);
//        }
    }

    // 아이템 개수 반환
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public interface OnItemClickListener {
        void onItemClick(BoardItem item);
    }

    // 아이템 클릭 시 호출되는 메서드
    private void onItemClicked(int position) {
        if (mListener != null) {
            mListener.onItemClick(mData.get(position));
        }
    }

}
