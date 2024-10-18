package co.kr.myfitnote.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import co.kr.myfitnote.model.TestLog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import co.kr.myfitnote.R;
import co.kr.myfitnote.model.FloodFill;
import co.kr.myfitnote.model.User;
import co.kr.myfitnote.core.utils.CommonUtil;

public class CmprhResultFragment extends Fragment {
    static final private String TAG = "CmprhResult";
    private FloodFill floodFill;
    private Bitmap originBitmap;
    private Bitmap destinationBitmap;

    private int positiveColor = Color.parseColor("#ED7D31");
    private int negativeColor = Color.parseColor("#A9D18E");

    private int curLeftThighColor = negativeColor;
    private int curRightThighColor = negativeColor;

    private int curLeftCalfColor = negativeColor;
    private int curRightCalfColor = negativeColor;

    private int curLeftAnkleColor = negativeColor;
    private int curRightAnkleColor = negativeColor;

    private int curChestColor = negativeColor;

    private TextView tv_walk_grade, tv_seatup_grade, tv_single_leg_grade;

    private LinearLayout walkLayout, singlelegLayout, seatedupLayout;
    private TextView singlegstance_count_view, walk_count_view, seatedup_count_view;
    private TextView singlegstance_grade_view, walk_grade_view, seatedup_grade_view;

    private TextView tv_date;
    private TextView tv_name, tv_sex, tv_birth, tv_height, tv_weight;

    private Button btn_move_result3;
    private Button btn_capture_screen;
    private NavController navController;

    SharedPreferences pref;
    String userData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_cmprh_result, container, false);

        navController = NavHostFragment.findNavController(this);

        pref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        userData = pref.getString("userData", "haven't token yet");
        User user = gson.fromJson(userData, User.class);


        walkLayout = rootView.findViewById(R.id.walkLayout);
        singlelegLayout = rootView.findViewById(R.id.singlelegLayout);
        seatedupLayout = rootView.findViewById(R.id.seatedupLayout);

        singlegstance_count_view = rootView.findViewById(R.id.singlegstance_count_view);
        walk_count_view = rootView.findViewById(R.id.walk_count_view);
        seatedup_count_view = rootView.findViewById(R.id.seatup_count_view);

        singlegstance_grade_view = rootView.findViewById(R.id.grade_view);
        walk_grade_view = rootView.findViewById(R.id.walk_grade_view);
        seatedup_grade_view = rootView.findViewById(R.id.seatup_grade_view);

        tv_date = rootView.findViewById(R.id.tv_date);
        tv_date.setText("날 짜 : " + CommonUtil.getCurrentYearMonthDay());

        btn_move_result3 = rootView.findViewById(R.id.btn_move_result3);
        btn_move_result3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_cmprhResultFragment_to_resultPage3);
            }
        });

        btn_capture_screen = rootView.findViewById(R.id.btn_capture_screen);
        btn_capture_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeScreenshot();
            }
        });

        tv_name = rootView.findViewById(R.id.tv_name);
        tv_sex = rootView.findViewById(R.id.tv_sex);
        tv_birth = rootView.findViewById(R.id.tv_birth);
        tv_height = rootView.findViewById(R.id.tv_height);
        tv_weight = rootView.findViewById(R.id.tv_weight);

        tv_name.setText("이름: " + user.getName());
        tv_sex.setText("성별: " + user.getSex());
        tv_birth.setText("생년월일: " +  user.getBirthDate());
        tv_height.setText("신장(cm): " +  user.getHeight());
        tv_weight.setText("무게(kg): " + user.getWeight());

//        tv_walk_grade = rootView.findViewById(R.id.tv_walk_grade);
//        tv_seatup_grade = rootView.findViewById(R.id.tv_seatup_grade);
//        tv_single_leg_grade = rootView.findViewById(R.id.tv_single_leg_grade);

        Drawable drawable = getActivity().getResources().getDrawable(R.drawable.body);
        ImageView imageView = rootView.findViewById(R.id.body_image);
        originBitmap = ((BitmapDrawable)drawable).getBitmap();

        Bundle data = getArguments();
        floodFill = new FloodFill();
        Type type = new TypeToken<HashMap<String, TestLog>>() {}.getType();
        HashMap<String, TestLog> finalData = new Gson().fromJson(data.getString("finalData"), type);

        for (Map.Entry<String, TestLog> entry : finalData.entrySet()) {
            String key = entry.getKey();
            TestLog value = entry.getValue();

            int grade = Integer.parseInt(value.getGrade());
            String _value = value.getValue();

            Log.e(TAG, key + " " + grade + "=>" + _value);
            // 걷기, 앉았다 일어서기, 외발서기
            if (value.getExercise().equals("걷기")){
                walkLayout.setVisibility(View.VISIBLE);
                walk_grade_view.setText(String.valueOf(grade));
                walk_count_view.setText(_value);
//                tv_walk_grade.setText(grade + " 등급");
                int color = positiveColor;
                if(grade > 1){
                    color = negativeColor;

                }

                if(destinationBitmap == null)
                    destinationBitmap = floodFill.fillColor(originBitmap, FloodFill.LEFT_THIGH_TYPE, color);
                else
                    destinationBitmap = floodFill.fillColor(destinationBitmap, FloodFill.LEFT_THIGH_TYPE, color);

                destinationBitmap = floodFill.fillColor(destinationBitmap, FloodFill.LEFT_THIGH_TYPE, color);
                destinationBitmap = floodFill.fillColor(destinationBitmap, FloodFill.RIGHT_THIGH_TYPE, color);
                destinationBitmap = floodFill.fillColor(destinationBitmap, FloodFill.RIGHT_CALF_TYPE, color);
                destinationBitmap = floodFill.fillColor(destinationBitmap, FloodFill.LEFT_CALF_TYPE, color);

            }

            if (value.getExercise().equals("앉았다 일어서기")){
                seatedup_grade_view.setText(String.valueOf(grade));
                seatedup_count_view.setText(_value);
                seatedupLayout.setVisibility(View.VISIBLE);
                int color = positiveColor;
                if(grade > 1){
                    color = negativeColor;

                }

                if(destinationBitmap == null)
                    destinationBitmap = floodFill.fillColor(originBitmap, FloodFill.LEFT_THIGH_TYPE, color);
                else
                    destinationBitmap = floodFill.fillColor(destinationBitmap, FloodFill.LEFT_THIGH_TYPE, color);

                destinationBitmap = floodFill.fillColor(destinationBitmap, FloodFill.RIGHT_THIGH_TYPE, color);
                destinationBitmap = floodFill.fillColor(destinationBitmap, FloodFill.RIGHT_CALF_TYPE, color);
                destinationBitmap = floodFill.fillColor(destinationBitmap, FloodFill.LEFT_CALF_TYPE, color);
                destinationBitmap = floodFill.fillColor(destinationBitmap, FloodFill.CHEST_TYPE, color);

            }

            if (value.getExercise().equals("외발서기")){
                singlelegLayout.setVisibility(View.VISIBLE);
                singlegstance_count_view.setText(_value);

                if (grade == 1){
                    singlegstance_grade_view.getBackground().setTint(getResources().getColor(R.color.light_blue_400));
                }else{
                    singlegstance_grade_view.getBackground().setTint(getResources().getColor(R.color.n_red));
                }
                singlegstance_grade_view.setText(grade == 1 ? "Passed" : "Failed");
                // 0 일때 Fail, 1일 때 Pass
                int color = positiveColor;
                if(grade == 0){
                    color = negativeColor;
                }
                if(destinationBitmap == null)
                    destinationBitmap = floodFill.fillColor(originBitmap, FloodFill.LEFT_THIGH_TYPE, color);
                else
                    destinationBitmap = floodFill.fillColor(destinationBitmap, FloodFill.LEFT_THIGH_TYPE, color);
                destinationBitmap = floodFill.fillColor(destinationBitmap, FloodFill.RIGHT_THIGH_TYPE, color);
                destinationBitmap = floodFill.fillColor(destinationBitmap, FloodFill.CHEST_TYPE, color);
                destinationBitmap = floodFill.fillColor(destinationBitmap, FloodFill.LEFT_ANKLE_TYPE, color);
                destinationBitmap = floodFill.fillColor(destinationBitmap, FloodFill.RIGHT_ANKLE_TYPE, color);
            }
        }

        imageView.setImageBitmap(destinationBitmap);
        return rootView;
    }

    private void takeScreenshot() {
        Date now = new Date();
        String nowStr = UUID.randomUUID().toString();
//        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
//            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + nowStr + ".jpg";
            String mPath;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
                mPath= getActivity().getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/" + nowStr + ".jpg";
            }else {
                mPath= Environment.getExternalStorageDirectory().toString() + "/" + nowStr + ".jpg";
            }

            // create bitmap screen capture
            View v1 = getActivity().getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }


}