package co.kr.myfitnote.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import co.kr.myfitnote.R;

public class SPPBActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sppb);

        findViewById(R.id.go_feat_together_btn).setOnClickListener(this);
        findViewById(R.id.go_semi_tandem_btn).setOnClickListener(this);
        findViewById(R.id.go_full_tandem_btn).setOnClickListener(this);
        findViewById(R.id.go_repeated_chair_stance_btn).setOnClickListener(this);
        findViewById(R.id.menu1).setOnClickListener(this);
        findViewById(R.id.menu2).setOnClickListener(this);
        findViewById(R.id.menu3).setOnClickListener(this);
        findViewById(R.id.menu4).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.menu1:
            case R.id.menu2:
            case R.id.menu3:
            case R.id.go_feat_together_btn:
            case R.id.go_semi_tandem_btn:
            case R.id.go_full_tandem_btn:
                Toast.makeText(getApplicationContext(), "click", Toast.LENGTH_LONG).show();
                Intent it = new Intent(getApplicationContext(), StandingBalanceActivity.class);
                startActivity(it);

                break;

            case R.id.menu4:
            case R.id.go_repeated_chair_stance_btn:
                Intent it2 = new Intent(getApplicationContext(), RepeatedChairStancesActivity.class);
                startActivity(it2);
                break;
        }
    }

}