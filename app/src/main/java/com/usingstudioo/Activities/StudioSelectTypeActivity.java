package com.usingstudioo.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.usingstudioo.Activities.Fragments.RangeFragment;
import com.usingstudioo.Activities.Fragments.ScaleFragment;
import com.usingstudioo.Utils.CustomViewPager;
import com.usingstudioo.R;

import java.util.List;
import java.util.Vector;

import static com.usingstudioo.Constants.Constants.kData;
import static com.usingstudioo.Constants.Constants.kRange;
import static com.usingstudioo.Constants.Constants.kRangeName;
import static com.usingstudioo.Constants.Constants.kScale;
import static com.usingstudioo.Constants.Constants.kScaleName;
import static com.usingstudioo.Constants.Constants.kType;

public class StudioSelectTypeActivity extends AppCompatActivity implements RangeFragment.RangeSelectInterface, ScaleFragment.ScaleSelectInterface{

    private String[] mTitles = {"Select Scale","Select Range"};
    private SegmentTabLayout mTabLayout;
    private CustomViewPager mViewPager;
    private int rangeId,scaleId;
    private String rangeName,scaleName;

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof RangeFragment) {
            RangeFragment rangeFragment = (RangeFragment) fragment;
            rangeFragment.setRangeSelectListener(this);
        } else if(fragment instanceof ScaleFragment){
            ScaleFragment scaleFragment = (ScaleFragment) fragment;
            scaleFragment.setScaleSelectListener(this);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_select);
        scaleId = getIntent().getIntExtra(kScale,0);
        rangeId = getIntent().getIntExtra(kRange,0);
        rangeName = getIntent().getStringExtra(kRangeName);
        scaleName = getIntent().getStringExtra(kScaleName);

        mTabLayout = findViewById(R.id.segment_tab_layout);
        mTabLayout.setTabData(mTitles);

        mViewPager = findViewById( R.id.page_view_pager);
        List<Fragment> fragments = new Vector<>();
        /*fragments.add(Fragment.instantiate(this, ScaleFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, RangeFragment.class.getName()));*/
        fragments.add(ScaleFragment.newInstance(scaleId));
        fragments.add(RangeFragment.newInstance(rangeId));
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(0);
        mViewPager.addOnPageChangeListener(pageListener);
        mViewPager.setPagingEnabled(false);

        mTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mViewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) { }
        });

        findViewById(R.id.bt_cancel).setOnClickListener(v -> finish());
        findViewById(R.id.bt_done).setOnClickListener(v -> {
            Intent data = new Intent();
            //set the data to pass back
            data.putExtra(kData,rangeName);
            data.putExtra(kType,scaleName);
            data.putExtra(kRange,rangeId);
            data.putExtra(kScale,scaleId);
            setResult(RESULT_OK, data);
            finish();
        });
    }

    private ViewPager.OnPageChangeListener pageListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

        @Override
        public void onPageSelected(int position) {
            mTabLayout.setCurrentTab(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) { }
    };

    @Override
    public void rangeSelect(int id, String name) {
        rangeId = id;
        rangeName = name;
    }

    @Override
    public void scaleSelect(int id, String name) {
        scaleId = id;
        scaleName = name;
    }

    /**
     * View pager adapter
     */
    public class MyPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragments;

        MyPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            //noinspection deprecation
            super(fm);
            mFragments = fragments;
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }
}
