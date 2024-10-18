package co.kr.myfitnote.views.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import co.kr.myfitnote.R;

public class PhysicalStatusView extends LinearLayout {
    private Resources resources = getResources();
    private String text;
    private TextView tv;

    private int level;
    public PhysicalStatusView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(), R.layout.view_physical_status_view, this);
        tv = (TextView) findViewById(R.id.status);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.PhysicalStatusView, 0, 0);
        try {
            setLevel(a.getInt(R.styleable.PhysicalStatusView_level, 0));
            text = (String) a.getText(R.styleable.PhysicalStatusView_android_text);
            tv.setText(text);

        }catch(Exception err){
            err.printStackTrace();
            a.recycle();
        }

    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        Paint textPaint;
//        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        textPaint.setTextSize(40);
//        textPaint.setTextAlign(Paint.Align.CENTER);
//        textPaint.setColor(resources.getColor(R.color.white));
//        canvas.drawText(text, getWidth()/2, getHeight()/2, textPaint);
//    }

    public void setLevel(int level) {
        this.level = level;
        switch (level){
            case 1:
                tv.setBackground(resources.getDrawable(R.drawable.gradient_green_physical_card));
//                setBackground(resources.getDrawable(R.drawable.gradient_green_physical_card));
                break;
            case 2:
                tv.setBackground(resources.getDrawable(R.drawable.gradient_red_physical_card));
//                setBackground(R.drawable.gradient_red_physical_card);
//                setBackground(resources.getDrawable(R.drawable.gradient_red_physical_card));
                break;
        }
        invalidate();
        requestLayout();
    }


}
