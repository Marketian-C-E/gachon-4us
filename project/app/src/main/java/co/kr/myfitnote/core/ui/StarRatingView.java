package co.kr.myfitnote.core.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import co.kr.myfitnote.R;

public class StarRatingView extends LinearLayout {
    private ImageView[] stars;

    public StarRatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.star_rating_view, this, true);
        stars = new ImageView[4];
        stars[0] = findViewById(R.id.star1);
        stars[1] = findViewById(R.id.star2);
        stars[2] = findViewById(R.id.star3);
        stars[3] = findViewById(R.id.star4);
    }

    public void setStarRating(int n) {
        n = 5 - n;
        for (int i = 0; i < stars.length; i++) {
            if (i < n) {
                stars[i].setImageResource(R.drawable.ic_filled_star);
            } else {
                stars[i].setImageResource(R.drawable.ic_empty_star);
            }
        }
    }
}