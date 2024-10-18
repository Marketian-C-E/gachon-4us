package co.kr.myfitnote.cm.ui.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import co.kr.myfitnote.R;
import co.kr.myfitnote.account.data.model.User;
import co.kr.myfitnote.core.utils.PreferencesManager;
import co.kr.myfitnote.core.utils.ToolbarManager;

public class CmHomeActivity extends AppCompatActivity {
    /**
     * 기업 관리자용 홈 화면
     */
    private String TAG = "CmHomeActivity";

    PreferencesManager preferencesManager;
    User user;

    Toolbar toolbar;
    ToolbarManager toolbarManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cm_home);

        preferencesManager = PreferencesManager.getInstance(this);
        user = preferencesManager.getUser();

        toolbar = findViewById(R.id.cm_toolbar);
        toolbarManager = new ToolbarManager(this, toolbar);

        setSupportActionBar(toolbar);
        // disable display show title
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                                                           .findFragmentById(R.id.cm_nav_host_fragment);
        NavController navController = navHostFragment.getNavController();


        BottomNavigationView bottomNav = findViewById(R.id.cm_bottom_nav);

        NavigationUI.setupWithNavController(bottomNav, navController);
        // Set up the ActionBar with the NavController
        NavigationUI.setupActionBarWithNavController(this, navController);

        // navController 목적지 변경 시 발생되는 리스너
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                int id = destination.getId();

                toolbarManager.setToolbarTitle("4US 건강헬스");
                showToolbar(id != R.id.homeFragment2);
                showBottomNavigation(id == R.id.homeFragment2 ||
                                     id == R.id.informationFragment ||
                                     id == R.id.companyLinkFragment2 ||
                                     id == R.id.boardFragment);

                // Change toolbar title by destination id
                switch (id){
                    case R.id.clientCreateFragment:
                        toolbarManager.setToolbarTitle("신규 이용자 등록");
                        break;
                    case R.id.clientSearchFragment:
                        toolbarManager.setToolbarTitle("이용자 검색");
                        break;
                    case R.id.measurementMenuFragment:
                        toolbarManager.setToolbarTitle("측정 메뉴");
                        break;
                    case R.id.measurementResultListFragment:
                    case R.id.measurementFinalResultFragment:
                        toolbarManager.setToolbarTitle("측정 결과");
                        break;
                    case R.id.romMenuFragment2:
                        toolbarManager.setToolbarTitle("ROM 메뉴");
                        break;
                    case R.id.poseMenuFragment2:
                        toolbarManager.setToolbarTitle("자세 메뉴");
                        break;
                    case R.id.makePrescriptionFragment:
                        toolbarManager.setToolbarTitle("운동 프로그램 처방");
                        break;
                    case R.id.privacyFragment:
                        toolbarManager.setToolbarTitle("개인정보 처리방침");
                        break;
                    case R.id.policyFragment:
                        toolbarManager.setToolbarTitle("이용약관");
                        break;
                }
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
                        navController.navigate(R.id.homeFragment2);
                        // When user click bottom_home_btn, change bottom_home_btn icon color
                        break;
                    case R.id.companylink_btn:
                        navController.navigate(R.id.companyLinkFragment2);
                        break;
                    case R.id.information_btn:
                        navController.navigate(R.id.informationFragment);
                        break;
                    case R.id.communication_btn:
                        navController.navigate(R.id.boardFragment);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }


    private void showBottomNavigation(boolean show) {
        // Show your bottom navigation bar here
        // For example, if you are using a BottomNavigationView
        // you can use setVisibility(View.VISIBLE)
        BottomNavigationView bottomNav = findViewById(R.id.cm_bottom_nav);
        if (show) {
            bottomNav.setVisibility(View.VISIBLE);
        } else {
            bottomNav.setVisibility(View.GONE);
        }
    }

    public void showToolbar(boolean show) {
        if (toolbar != null) {
            if (show) {
                toolbar.setVisibility(View.VISIBLE);
            } else {
                toolbar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        // This method is called when the up button is pressed. Just return the
        // Nav Controller's navigateUp() here.
        NavController navController = Navigation.findNavController(this, R.id.cm_nav_host_fragment);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}