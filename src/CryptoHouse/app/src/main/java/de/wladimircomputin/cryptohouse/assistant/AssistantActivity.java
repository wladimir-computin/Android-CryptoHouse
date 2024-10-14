package de.wladimircomputin.cryptohouse.assistant;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import de.wladimircomputin.cryptohouse.R;
import de.wladimircomputin.cryptohouse.ui.PagerAdapterTitleProvider;

public class AssistantActivity extends AppCompatActivity {
    FragmentStateAdapter pagerAdapter;
    public ViewPager2 pager;
    public DeviceSetupPack pack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistant);
        pager = findViewById(R.id.pager);
        pagerAdapter = new AssisstanPageAdapter(this);
        pager.setAdapter(pagerAdapter);
        pager.setUserInputEnabled(false);
        pager.setOffscreenPageLimit(2);
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
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

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, pager, (tab, position) -> {
            tab.setText(((PagerAdapterTitleProvider)pagerAdapter).getTitle(position));
        }
        ).attach();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    public void nextPage() {
        pager.setCurrentItem(pager.getCurrentItem() + 1, true);
    }

    public void setPage(int page){
        pager.setCurrentItem(page, true);
    }

    public void setProgessAnimateDelayed(ProgressBar pb, int progress, int delay){
        runOnUiThread(() -> {
            new Handler().postDelayed(() -> {
                setProgressAnimate(pb,progress);
            }, delay);
        });
    }

    private void setProgressAnimate(ProgressBar pb, int progressTo) {
        if(progressTo > 300)
            progressTo = 300;
        ObjectAnimator animation = ObjectAnimator.ofInt(pb, "progress", pb.getProgress(), progressTo);
        animation.setDuration(300);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }
}
