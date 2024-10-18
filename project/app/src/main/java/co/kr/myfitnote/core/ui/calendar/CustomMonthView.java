package co.kr.myfitnote.core.ui.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.MonthView;

import co.kr.myfitnote.R;

/**
 * CustomMonthView with canvas
 */
public class CustomMonthView extends MonthView {

    int TEXT_SIZE = 14;
    float mPointRadius = 50;

    Paint mSelectedPaint = new Paint();
    Paint mCurDayTextPaint = new Paint();

    int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public CustomMonthView(Context context) {
        super(context);

        // set color to paint R.color.GREEN_1
        mSelectedPaint.setColor(getResources().getColor(R.color.GREEN_1));
        mSelectedPaint.setAntiAlias(true);
        mSelectedPaint.setStyle(Paint.Style.FILL);
        mSelectedPaint.setTextAlign(Paint.Align.CENTER);
        mSelectedPaint.setFakeBoldText(true);
        mSelectedPaint.setTextSize(dipToPx(context, TEXT_SIZE));

        // Text paint;
        mCurDayTextPaint.setAntiAlias(true);
        mCurDayTextPaint.setTextAlign(Paint.Align.CENTER);
        mCurDayTextPaint.setColor(Color.RED);
        mCurDayTextPaint.setFakeBoldText(true);
        mCurDayTextPaint.setTextSize(dipToPx(context, TEXT_SIZE));
        mCurDayTextPaint.setColor(Color.parseColor("#4a5660"));
    }

    /**
     * draw select calendar
     *
     * @param canvas    canvas
     * @param calendar  select calendar
     * @param x         calendar item x start point
     * @param y         calendar item y start point
     * @param hasScheme is calendar has scheme?
     * @return if return true will call onDrawScheme again
     */
    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme) {
        canvas.drawRect(x , y , x + mItemWidth , y + mItemHeight, mSelectedPaint);
        return true;
    }

    /**
     * draw scheme if calendar has scheme
     *
     * @param canvas   canvas
     * @param calendar calendar has scheme
     * @param x        calendar item x start point
     * @param y        calendar item y start point
     */
    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x, int y) {
        canvas.drawCircle(x + mItemWidth / 2, y + mItemHeight / 2, mPointRadius, mSelectedPaint);
    }

    /**
     * draw text
     *
     * @param canvas     canvas
     * @param calendar   calendar
     * @param x          calendar item x start point
     * @param y          calendar item y start point
     * @param hasScheme  is calendar has scheme?
     * @param isSelected is calendar selected?
     */
    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme, boolean isSelected) {
        float baselineY = mTextBaseLine + y;
        int cx = x + mItemWidth / 2;
        canvas.drawText(String.valueOf(calendar.getDay()),
                cx,
                baselineY,
                calendar.isCurrentDay() ? mCurDayTextPaint :
                        calendar.isCurrentMonth() ? mCurDayTextPaint : mCurDayTextPaint);
    }
}