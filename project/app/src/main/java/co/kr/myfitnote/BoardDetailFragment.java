package co.kr.myfitnote;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BoardDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BoardDetailFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "title";
    private static final String ARG_PARAM2 = "content";
    private static final String ARG_PARAM3 = "thumbnail";
    private static final String ARG_PARAM4 = "writer";
    private static final String ARG_PARAM5 = "date";

    // TODO: Rename and change types of parameters
    private String title;
    private String content;
    private String thumbnail;
    private String writer;
    private String date;

    public BoardDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     */
    // TODO: Rename and change types and number of parameters
    public static BoardDetailFragment newInstance(String title, String content, String thumbnail, String writer, String date) {
        BoardDetailFragment fragment = new BoardDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, title);
        args.putString(ARG_PARAM2, content);
        args.putString(ARG_PARAM3, thumbnail);
        args.putString(ARG_PARAM4, writer);
        args.putString(ARG_PARAM5, date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_PARAM1);
            content = getArguments().getString(ARG_PARAM2);
            thumbnail = getArguments().getString(ARG_PARAM3);
            writer = getArguments().getString(ARG_PARAM4);
            date = getArguments().getString(ARG_PARAM5);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_board_detail, container, false);
        // ImageView imageViewThumbnail = view.findViewById(R.id.imageViewThumbnail);
        TextView textViewTitle = view.findViewById(R.id.textViewTitle);
        TextView textViewContent = view.findViewById(R.id.textViewContent);
        TextView textViewwriter = view.findViewById(R.id.writer);
        TextView textViewdate = view.findViewById(R.id.date);

        // Set title, content, and thumbnail image here
        // You may use a library like Glide to load images
        // Glide.with(this).load(thumbnail).into(imageViewThumbnail);
         textViewTitle.setText(title);
         textViewContent.setText(content);
         textViewwriter.setText(writer);
         textViewdate.setText(date);

        return view;
    }
}