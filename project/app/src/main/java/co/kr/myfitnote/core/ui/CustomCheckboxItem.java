package co.kr.myfitnote.core.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import co.kr.myfitnote.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class CustomCheckboxItem extends LinearLayout {

    private RadioGroup radioGroup;
    private RadioButton checkBox1;
    private RadioButton checkBox2;
    private TextView textView1;
    private TextView textView2;
    private LinearLayout linear1;
    private LinearLayout linear2;

    public CustomCheckboxItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomCheckboxItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.custom_checkbox_item, this, true);
        radioGroup = findViewById(R.id.radioGroup);
        checkBox1 = findViewById(R.id.checkbox1);
        checkBox2 = findViewById(R.id.checkbox2);
        textView1 = findViewById(R.id.textview1);
        textView2 = findViewById(R.id.textview2);
        linear1 = findViewById(R.id.linear1);
        linear2 = findViewById(R.id.linear2);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomCheckboxItem, 0, 0);
        try {
            String text1 = a.getString(R.styleable.CustomCheckboxItem_text1_label);
            String text2 = a.getString(R.styleable.CustomCheckboxItem_text2_label);
            setText1(text1);
            setText2(text2);
        } finally {
            a.recycle();
        }

        // when checkBox1 is clicked
        checkBox1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                linear1.performClick();
            }
        });
        linear1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox1.setChecked(true);
                setChecked(R.id.checkbox1);
                linear1.setSelected(true);
                linear2.setSelected(false);
                checkBox1.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white)));
                checkBox2.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.b3b3b3)));
                textView1.setTextColor(ContextCompat.getColor(context, R.color.white));
                textView2.setTextColor(ContextCompat.getColor(context, R.color.b3b3b3));
            }
        });

        // when checkBox2 is clicked
        checkBox2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                linear2.performClick();
            }
        });
        linear2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox2.setChecked(true);
                setChecked(R.id.checkbox2);
                linear2.setSelected(true);
                linear1.setSelected(false);
                checkBox2.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white)));
                checkBox1.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.b3b3b3)));
                textView2.setTextColor(ContextCompat.getColor(context, R.color.white));
                textView1.setTextColor(ContextCompat.getColor(context, R.color.b3b3b3));
            }
        });
    }

    public void onCheckbox1Clicked() {
        checkBox1.setChecked(true);
        setChecked(R.id.checkbox1);
        linear1.setSelected(true);
        linear2.setSelected(false);
        checkBox1.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.white)));
        checkBox2.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.b3b3b3)));
        textView1.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        textView2.setTextColor(ContextCompat.getColor(getContext(), R.color.b3b3b3));
    }


    public void setChecked(int id) {
        radioGroup.check(id);
    }

    public int getChecked() {
        return radioGroup.getCheckedRadioButtonId();
    }

    public void setText1(String text) {
        textView1.setText(text);
    }

    public String getText1() {
        return textView1.getText().toString();
    }

    public void setText2(String text) {
        textView2.setText(text);
    }

    public String getText2() {
        return textView2.getText().toString();
    }

    public String getCheckedItemLabel() {
        int checkedId = radioGroup.getCheckedRadioButtonId();
        if (checkedId == R.id.checkbox1) {
            return getText1();
        } else if (checkedId == R.id.checkbox2) {
            return getText2();
        } else {
            return null;
        }
    }
}