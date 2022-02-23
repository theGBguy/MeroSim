/*
 * Created by Chiranjeevi Pandey on 2/23/22, 9:41 AM
 * Copyright (c) 2022. Some rights reserved.
 * Last modified: 2022/02/23
 *
 * Licensed under GNU General Public License v3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gbsoft.merosim.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.gbsoft.merosim.MeroSimApp;
import com.gbsoft.merosim.R;
import com.gbsoft.merosim.data_source.Repository;
import com.gbsoft.merosim.databinding.ActivityMainBinding;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;

import java.lang.ref.WeakReference;

/*
 * Main entrance of this application.
 */

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;

    // to use custom hamburger menu icon and back icon
    private final NavController.OnDestinationChangedListener onDestinationChangedListener =
            (controller, destination, arguments) -> {
                ActionBar actionBar = getSupportActionBar();
                if (actionBar == null) return;

                int destinationId = destination.getId();
                if (destinationId == R.id.nav_home || destinationId == R.id.nav_recharge)
                    actionBar.setHomeAsUpIndicator(R.drawable.ic_round_menu_24);
                else
                    actionBar.setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_ios_24);
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // resets back to the default theme by replacing the splash theme
        setTheme(R.style.Theme_MeroSim_NoActionBar);
        super.onCreate(savedInstanceState);

        // sets activity's layout
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);

        // makes rounded corner toolbar
        MaterialShapeDrawable toolbarShapeDrawable = (MaterialShapeDrawable) binding.appBarMain.toolbar.getBackground();
        toolbarShapeDrawable.setShapeAppearanceModel(
                toolbarShapeDrawable.getShapeAppearanceModel()
                        .toBuilder()
                        .setBottomLeftCorner(CornerFamily.ROUNDED, 32)
                        .setBottomRightCorner(CornerFamily.ROUNDED, 32)
                        .build()
        );

        // makes rounded corner nav drawer
        MaterialShapeDrawable navShapeDrawable = (MaterialShapeDrawable) binding.navView.getBackground();
        navShapeDrawable.setShapeAppearanceModel(
                navShapeDrawable.getShapeAppearanceModel()
                        .toBuilder()
                        .setTopRightCorner(CornerFamily.ROUNDED, 56)
                        .setBottomRightCorner(CornerFamily.ROUNDED, 56)
                        .build()
        );

        // toggles visibility of greeting view in the nav drawer's header
        // according to the cached user name; if empty then hide, show otherwise.
        Repository repository = new Repository(
                ((MeroSimApp) getApplication()).getExecutor(),
                ((MeroSimApp) getApplication()).getMainThreadHandler()
        );
        String userName = repository.getUserName(this);
        TextView greeting = binding.navView.getHeaderView(0).findViewById(R.id.tv_greeting);
        if (TextUtils.isEmpty(userName)) {
            greeting.setVisibility(View.GONE);
        } else {
            greeting.setText(getString(R.string.greeting_txt, userName));
        }

        // configures the nav destination and overall navigation
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_recharge)
                .setOpenableLayout(binding.drawerLayout)
                .build();
        WeakReference<MainActivity> mainActivityWR = new WeakReference<>(this);
        navController = Navigation.findNavController(mainActivityWR.get(), R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(mainActivityWR.get(), navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, initializationStatus -> Log.d("MainActivity", "Ads initialization : " + initializationStatus));
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navController.addOnDestinationChangedListener(onDestinationChangedListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        navController.removeOnDestinationChangedListener(onDestinationChangedListener);
    }
}