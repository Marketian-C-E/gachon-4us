package co.kr.myfitnote.client;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

/**
 * DisplayLogger is a class that logs the data to the screen.
 */

public class DisplayLogger {
    private static final String TAG = "DisplayLogger";

    private Context context;
    private TextView loggerView;

    public DisplayLogger(Context context, TextView loggerView) {
        this.context = context;
        this.loggerView = loggerView;
    }

    public void displayToScreen(String message) {
        // loggerView.setText with message
        loggerView.setText(message);
    }

    public void setUseLogging(boolean useLogging) {
        // set useLogging to useLogging
        if (useLogging){
            loggerView.setVisibility(View.VISIBLE);
        }else{
            loggerView.setVisibility(View.INVISIBLE);
        }
    }
}
