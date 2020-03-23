package com.example.yiting.fragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.yiting.R;
import com.example.yiting.fragment.order.OrderFragment1;
import com.example.yiting.fragment.order.OrderFragment2;
import com.example.yiting.fragment.order.OrderFragment3;

import java.util.ArrayList;

import butterknife.BindView;

public class OrderFragment extends BaseFragment {

    @BindView(R.id.tab_layout)
    com.flyco.tablayout.SlidingTabLayout tabLayout;
    @BindView(R.id.vp)
    ViewPager vp;

    private OrderFragment1 fragment1;
    private OrderFragment2 fragment2;
    private OrderFragment3 fragment3;
    private ArrayList<Fragment> fragments;
    private String[] titles = new String[]{"全部订单", "预约订单", "发布订单"};

    @Override
    protected void initView() {
        fragments = new ArrayList<>();
        fragment1 = new OrderFragment1();
        fragment2 = new OrderFragment2();
        fragment3 = new OrderFragment3();
        fragments.add(fragment1);
        fragments.add(fragment2);
        fragments.add(fragment3);

        vp.setAdapter(new MyPagerAdapter(getChildFragmentManager()));
        tabLayout.setViewPager(vp);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_order;
    }


    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
