package co.kr.myfitnote.views.fragments;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import co.kr.myfitnote.R;

/**
 * TODO: document your custom view class.
 */
public class SearchItem extends LinearLayout {

    String name;
    String birth;
    String result;
    String lastDate;
    int status;

    public SearchItem(Context context) {
        super(context);
        init( null, 0);
    }

    public SearchItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public SearchItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        View rootView = li.inflate(R.layout.examination_item, this, true);
        addView(rootView);

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.SearchItem, defStyle, 0);

        name = a.getString(R.styleable.SearchItem_name);
        birth = a.getString(R.styleable.SearchItem_birth);
        result = a.getString(R.styleable.SearchItem_result);
        lastDate = a.getString(R.styleable.SearchItem_last_date);
        status = a.getInt(R.styleable.SearchItem_status, 0);


        a.recycle();
    }



}