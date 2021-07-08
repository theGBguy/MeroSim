/*
 * Copyright 2021 Chiranjeevi Pandey Some rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Last modified: 2021/05/31
 */

package com.gbsoft.merosim;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.gbsoft.merosim.databinding.ActivityMainBinding;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_MeroSim_NoActionBar);
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);

        MaterialShapeDrawable toolbarShapeDrawable = (MaterialShapeDrawable) binding.appBarMain.toolbar.getBackground();
        toolbarShapeDrawable.setShapeAppearanceModel(
                toolbarShapeDrawable.getShapeAppearanceModel()
                        .toBuilder()
                        .setBottomLeftCorner(CornerFamily.ROUNDED, 32)
                        .setBottomRightCorner(CornerFamily.ROUNDED, 32)
                        .build()
        );

        MaterialShapeDrawable navShapeDrawable = (MaterialShapeDrawable) binding.navView.getBackground();
        navShapeDrawable.setShapeAppearanceModel(
                navShapeDrawable.getShapeAppearanceModel()
                        .toBuilder()
                        .setTopRightCorner(CornerFamily.ROUNDED, 56)
                        .setBottomRightCorner(CornerFamily.ROUNDED, 56)
                        .build()
        );

        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_recharge)
                .setOpenableLayout(binding.drawerLayout)
                .build();

        WeakReference<MainActivity> mainActivityWR = new WeakReference<>(this);
        navController = Navigation.findNavController(mainActivityWR.get(), R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(mainActivityWR.get(), navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}