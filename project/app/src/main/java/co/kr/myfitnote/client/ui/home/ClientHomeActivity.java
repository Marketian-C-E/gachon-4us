package co.kr.myfitnote.client.ui.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import co.kr.myfitnote.R;
import co.kr.myfitnote.account.data.model.User;
import co.kr.myfitnote.core.utils.PreferencesManager;

public class ClientHomeActivity extends AppCompatActivity {

    private String TAG = "ClientHomeActivity";

    PreferencesManager preferencesManager;
    User user;
    String client_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_home);

//        client_id = getIntent().getStringExtra("client_id");
//        Log.e(TAG, "client_id => " + client_id);

        preferencesManager = PreferencesManager.getInstance(this);
//        user = preferencesManager.getUser();
//        user = new User();
//        user.setUsername(client_id);
//        Log.e(TAG, "User infomation => "  + user.getUsername());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.client_nav_host_fragment);
        NavController navController = navHostFragment.getNavController();


        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph()).build();
//        Toolbar toolbar = findViewById(R.id.cm_toolbar);

        BottomNavigationView bottomNav = findViewById(R.id.client_bottom_nav);

        NavigationUI.setupWithNavController(bottomNav, navController);
//        NavigationUI.setupWithNavController(
//                toolbar, navController, appBarConfiguration);

        // navController 목적지 변경 시 발생되는 리스너
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                int id = destination.getId();
                // Show Bottom navigation if only id is R.id.home, otherwise hide it
                if (id == R.id.clientHomeFragment || id == R.id.analysisFragment) {
                    showBottomNavigation();
                } else {
                    hideBottomNavigation();
                }
//                showBottomNavigation();
//                switch (id){
//                    case R.id.measurementMenuFragment:
//                        hideBottomNavigation();
//                        break;
//                }
            }
        });

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                /**
                 * Bottom Navigation 클릭 시 처리되는 함수
                 */
                switch (item.getItemId()){
                    case R.id.bottom_home_btn:
                        navController.navigate(R.id.clientHomeFragment);
                        // When user click bottom_home_btn, change bottom_home_btn icon color
                        break;
                    case R.id.bottom_tutorial_btn:
                        navController.navigate(R.id.analysisFragment);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    private void hideBottomNavigation() {
        // Hide your bottom navigation bar here
        // For example, if you are using a BottomNavigationView
        // you can use setVisibility(View.GONE)
        BottomNavigationView bottomNav = findViewById(R.id.client_bottom_nav);
        bottomNav.setVisibility(View.GONE);
    }

    private void showBottomNavigation() {
        // Show your bottom navigation bar here
        // For example, if you are using a BottomNavigationView
        // you can use setVisibility(View.VISIBLE)
        BottomNavigationView bottomNav = findViewById(R.id.client_bottom_nav);
        bottomNav.setVisibility(View.VISIBLE);
    }
}