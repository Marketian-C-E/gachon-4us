package co.kr.myfitnote.core.utils;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import co.kr.myfitnote.R;

public class ToolbarManager {
    private AppCompatActivity activity;
    private Toolbar toolbar;

    public ToolbarManager(AppCompatActivity activity, Toolbar toolbar) {
        this.activity = activity;
        this.toolbar = toolbar;
    }

    public void setToolbarTitle(String title) {
        TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(title);
    }

    public void setBackArrowVisibility(boolean isVisible) {
        if (isVisible) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }
}