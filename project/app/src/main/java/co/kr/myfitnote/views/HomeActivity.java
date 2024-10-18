package co.kr.myfitnote.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;

import co.kr.myfitnote.R;
import co.kr.myfitnote.model.User;

public class HomeActivity extends AppCompatActivity{
    static final private String TAG = "HomeActivity";

    TextView userAuthToken;

    SharedPreferences pref;
    String userData;

    LinearLayout menu1, menu2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        pref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        userData = pref.getString("userData", "haven't token yet");
        User user = gson.fromJson(userData, User.class);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph()).build();

        Toolbar toolbar = findViewById(R.id.toolbar);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNav, navController);

        // navController 목적지 변경 시 발생되는 리스너
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                int id = destination.getId();
                switch (id){
                    case R.id.homeFragment:
                        toolbar.setVisibility(View.GONE);
                        break;
                }
            }
        });

        // bottomTabNavigation item 변경 시 발생되는 리스너
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.bottom_home_btn:
                        Log.d(TAG, "bottom_home_btn");
                        navController.navigate(R.id.homeFragment);
                        break;
                    case R.id.bottom_tutorial_btn:
                        Log.d(TAG, "bottom_tutorial_btn");
                        navController.navigate(R.id.exerciseScreenFragment);
                        break;

                    default:
                        break;
                }
                return false;
            }
        });

    }

}