package co.kr.myfitnote.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import co.kr.myfitnote.R;
import co.kr.myfitnote.game.GameMain;

public class PhysicalTestMenuActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical_test_menu);


        findViewById(R.id.seat_down_up_btn).setOnClickListener(this);
        findViewById(R.id.walk_btn).setOnClickListener(this);
        findViewById(R.id.game_btn).setOnClickListener(this);
        findViewById(R.id.single_leg_stance_test_btn).setOnClickListener(this);
        findViewById(R.id.go_sppb_btn).setOnClickListener(this);
        findViewById(R.id.menu1).setOnClickListener(this);
        findViewById(R.id.menu2).setOnClickListener(this);
        findViewById(R.id.menu3).setOnClickListener(this);
        findViewById(R.id.menu4).setOnClickListener(this);
        findViewById(R.id.menu5).setOnClickListener(this);
    }

    private void setupToolBar(){
        Toolbar titleBar = findViewById(R.id.title_bar);
        titleBar.findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        setSupportActionBar(titleBar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.menu1:
            case R.id.seat_down_up_btn:
                Intent it = new Intent(getApplicationContext(), SeatDownUpTestActivity.class);
                startActivity(it);
                break;
            case R.id.menu2:
            case R.id.walk_btn:
                Intent it2 = new Intent(getApplicationContext(), WalkTestActivity.class);
                startActivity(it2);
                break;
            case R.id.menu3:
            case R.id.game_btn:
                Intent it3 = new Intent(getApplicationContext(), GameMain.class);
                startActivity(it3);
                break;

            case R.id.menu4:
            case R.id.single_leg_stance_test_btn:
                Intent it4 = new Intent(getApplicationContext(), SingleLegStanceTestActivity.class);
                startActivity(it4);
                break;
            case R.id.menu5:
            case R.id.go_sppb_btn:

                Intent it5 = new Intent(getApplicationContext(), SPPBActivity.class);
                startActivity(it5);
                break;

        }

    }
}