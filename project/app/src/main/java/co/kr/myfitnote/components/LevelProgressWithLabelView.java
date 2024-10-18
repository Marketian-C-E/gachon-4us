package co.kr.myfitnote.components;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LevelProgressWithLabelView extends LinearLayout {

    private LevelProgressView customRectView;
    private TextView labelTextView;

    public LevelProgressWithLabelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);
        customRectView = new LevelProgressView(getContext(), null);
        labelTextView = new TextView(getContext());
        labelTextView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        addView(customRectView);
        addView(labelTextView);
    }

    public void setLevel(int level) {
        customRectView.setLevel(level);
        labelTextView.setText("Level: " + level);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Optionally, you can add custom drawing here if needed.
    }
}
