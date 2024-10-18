package de.wladimircomputin.cryptohouse.importexport;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;

import de.wladimircomputin.cryptohouse.MainActivity;
import de.wladimircomputin.cryptohouse.R;
import de.wladimircomputin.cryptohouse.databinding.FragmentBackupRestoreBinding;
import de.wladimircomputin.cryptohouse.ui.PagerAdapterTitleProvider;

public class ImportExportFragment extends Fragment {
    FragmentStateAdapter pagerAdapter;
    FragmentBackupRestoreBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentBackupRestoreBinding.inflate(inflater, container, false);
        //setHasOptionsMenu(true);
        //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.device_manager);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.backup_restore);
        pagerAdapter = new ImportExportPageAdapter(this.getActivity());
        binding.pager.setAdapter(pagerAdapter);
        binding.pager.setOffscreenPageLimit(2);
        binding.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                ((FocusListener)pagerAdapter.createFragment(position)).onSelected();
                for(int page = 0; page < pagerAdapter.getItemCount(); page++){
                    if(page != position) {
                        ((FocusListener) pagerAdapter.createFragment(position)).onUnselected();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        new TabLayoutMediator(binding.tabLayout, binding.pager, (tab, position) -> {
            tab.setText(((PagerAdapterTitleProvider)pagerAdapter).getTitle(position));
        }
        ).attach();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity mainActivity = (MainActivity) getActivity();
        Uri importUri = mainActivity.getImportUri();
        if (importUri != null){
            binding.pager.setCurrentItem(1);
        }
    }
}
