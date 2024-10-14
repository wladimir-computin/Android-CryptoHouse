package de.wladimircomputin.cryptohouse.devicecontrols;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

import de.wladimircomputin.cryptohouse.ui.PagerAdapterTitleProvider;

public class CustomPagerAdapter extends FragmentStateAdapter implements PagerAdapterTitleProvider {

        private final List<DeviceControlsSubFragment> fragments;

        public CustomPagerAdapter(@NonNull FragmentManager fragmentManager, Lifecycle lifecycle, List<DeviceControlsSubFragment> fragments) {
            super(fragmentManager, lifecycle);
            this.fragments = fragments;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }

        @Override
        public String getTitle(int position) {
            return fragments.get(position).name;
        }
    }