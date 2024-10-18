package co.kr.myfitnote.core.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import co.kr.myfitnote.R;

public class ButtonWithImageAndText extends RelativeLayout {

    private ImageButton buttonImage;
    private TextView buttonText;

    public ButtonWithImageAndText(Context context) {
        super(context);
        init(context);
    }

    public ButtonWithImageAndText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ButtonWithImageAndText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.website, this);

        buttonImage = findViewById(R.id.button_image);
        buttonText = findViewById(R.id.button_text);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open website
                String url = "https://your-website-url.com"; // Change this to your desired website URL
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                getContext().startActivity(intent);
            }
        });
    }

    public void setImage(int resId) {
        buttonImage.setImageResource(resId);
    }

    // Set Image with url
    public void setImage(String url) {
        // Load image from url
         Glide.with(getContext()).load(url).into(buttonImage);
    }

    public void setText(String text) {
        buttonText.setText(text);
    }
}
