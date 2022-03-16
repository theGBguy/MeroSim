/*
 * Created by Chiranjeevi Pandey on 3/10/22, 3:34 PM
 * Copyright (c) 2022. Some rights reserved.
 * Last modified: 2022/02/28
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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.gbsoft.merosim.BuildConfig;
import com.gbsoft.merosim.R;
import com.gbsoft.merosim.data_source.PrefsUtils;
import com.gbsoft.merosim.databinding.ActivityMainBinding;
import com.gbsoft.merosim.utils.Utils;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;

import java.lang.ref.WeakReference;

/*
 * Main entrance of this application.
 */

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "MainActivity";
    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;
    private ActivityMainBinding binding;
    private TextView tvGreeting;
    private InterstitialAd interstitialAd;
    private int nameColor;

    public final MutableLiveData<SpannableString> userName = new MutableLiveData<>();
    public final MutableLiveData<Boolean> showIntro = new MutableLiveData<>(false);

    // to use custom hamburger menu icon and back icon
    private final NavController.OnDestinationChangedListener onDestinationChangedListener =
            (controller, destination, arguments) -> {
                ActionBar actionBar = getSupportActionBar();
                if (actionBar == null) return;
                int destinationId = destination.getId();
                if (destinationId == R.id.nav_home || destinationId == R.id.nav_recharge) {
                    actionBar.setHomeAsUpIndicator(R.drawable.ic_round_menu_24);
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                } else {
                    actionBar.setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_ios_24);
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // resets back to the default theme by replacing the splash theme
        setTheme(R.style.Theme_MeroSim_NoActionBar);
        super.onCreate(savedInstanceState);

        // sets activity's layout
        binding = ActivityMainBinding.inflate(getLayoutInflater());
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

        // configures the nav destination and overall navigation
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_recharge)
                .setOpenableLayout(binding.drawerLayout)
                .build();
        WeakReference<MainActivity> mainActivityWR = new WeakReference<>(this);
        navController = Navigation.findNavController(mainActivityWR.get(), R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(mainActivityWR.get(), navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, initializationStatus ->
                Log.d("MainActivity", "Ads initialization : " + initializationStatus));
        loadInterstitialAds();

        tvGreeting = binding.navView.getHeaderView(0).findViewById(R.id.tv_greeting);
        tvGreeting.setOnClickListener(v ->
                Utils.askUserName(MainActivity.this, binding.drawerLayout));

        nameColor = ContextCompat.getColor(this,
                Utils.isNightMode(getResources()) ? R.color.md_theme_dark_primary : R.color.md_theme_dark_primaryContainer);

        // ask the user name in first usage and
        // change the greeting text appropriately
        String userName = PrefsUtils.getUserName(this);
        if (userName.equals(PrefsUtils.NAME_UNKNOWN)) {
            tvGreeting.setText(getString(R.string.greeting_txt));
            Utils.askUserName(MainActivity.this, binding.drawerLayout);
        } else {
            tvGreeting.setText(getStyledGreeting(userName));
        }
    }

    public void loadInterstitialAds() {
        // BuildConfig.NAMASTE_INTERSTITIAL_AD_UNIT_ID
        // test ad unit id : ca-app-pub-3940256099942544/1033173712
        InterstitialAd.load(this, BuildConfig.INTERSTITIAL_AD_UNIT_ID, new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                interstitialAd = null;
                Log.d(TAG, loadAdError.getMessage());
                showIntro.setValue(true);
            }

            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                MainActivity.this.interstitialAd = interstitialAd;
                MainActivity.this.interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        MainActivity.this.interstitialAd = null;
                        Log.d(TAG, "The ad was dismissed.");
                        showIntro.setValue(true);
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                        MainActivity.this.interstitialAd = null;
                        Log.d(TAG, "The ad failed to show.");
                        showIntro.setValue(true);
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();
                        Log.d(TAG, "The ad was shown.");
                    }
                });
            }
        });
    }

    public InterstitialAd getInterstitialAd() {
        return interstitialAd;
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
        PrefsUtils.getDefaultSharedPrefs(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        navController.removeOnDestinationChangedListener(onDestinationChangedListener);
        PrefsUtils.getDefaultSharedPrefs(this).unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key == null) return;
        if (key.equals(PrefsUtils.KEY_USER_NAME)) {
            String name = PrefsUtils.getUserName(this);
            tvGreeting.setText(getStyledGreeting(name));
        }
    }

    private CharSequence getStyledGreeting(String name) {
        SpannableString styledName = new SpannableString(name);
        styledName.setSpan(
                new ForegroundColorSpan(nameColor),
                0,
                name.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        styledName.setSpan(
                new RelativeSizeSpan(1.5f),
                0,
                name.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        userName.setValue(styledName);
        SpannableStringBuilder greeting = new SpannableStringBuilder(getText(R.string.greeting_txt));
        greeting.append("\n");
        greeting.append(styledName);
        return greeting;
    }
}