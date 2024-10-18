package co.kr.myfitnote.core.ui;

import static androidx.databinding.adapters.TextViewBindingAdapter.setText;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import co.kr.myfitnote.R;

public class AgreeCheckboxView extends LinearLayout {
    /**
     * 개인정보 동의, 이용약관 동의와 같이 좌측에는 텍스트 우측에는 버튼이 구성되어 있는 컴포넌트
     */

    private TextView textView, show_document;
    private CheckBox checkBox;

    public AgreeCheckboxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.agree_checkbox_item, this, true);
        textView = findViewById(R.id.text);
        checkBox = findViewById(R.id.checkbox);
        show_document = findViewById(R.id.show_document);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AgreeCheckboxItem, 0, 0);
        try {
            String text = a.getString(R.styleable.AgreeCheckboxItem_text);
            textView.setText(text);
        } finally {
            a.recycle();
        }
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener) {
        checkBox.setOnCheckedChangeListener(listener);
    }

    public void setOnClickShowDocumentListener(OnClickListener listener) {
        show_document.setOnClickListener(listener);
    }

}
