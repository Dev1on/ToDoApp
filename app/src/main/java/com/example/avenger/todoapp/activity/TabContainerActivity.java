package com.example.avenger.todoapp.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.avenger.todoapp.R;
import com.example.avenger.todoapp.model.Todo;

import java.util.ArrayList;

public class TabContainerActivity extends AppCompatActivity implements FullListActivity.TodosUpdatedInList, FullListMapActivity.TodosUpdatedInMap {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_container_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FullListActivity fullListActivity = new FullListActivity();
        FullListMapActivity fullListMapActivity = new FullListMapActivity();

        // Create the adapter that will return a fragment for each of the two
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), fullListActivity, fullListMapActivity);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    }

    @Override
    public void updateTodosMap(ArrayList<Todo> todos) {
        FullListMapActivity mapActivity = (FullListMapActivity) mSectionsPagerAdapter.getItem(1);
        mapActivity.fillWithTodosAfterChangesInList(todos);
    }

    @Override
    public void updateTodosList(ArrayList<Todo> todos) {
        FullListActivity listActivity = (FullListActivity) mSectionsPagerAdapter.getItem(0);
        listActivity.updateViewAfterChangesInMap(todos);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private FullListActivity fullListActivity;
        private FullListMapActivity fullListMapActivity;

        public SectionsPagerAdapter(FragmentManager fm, FullListActivity fullListActivity, FullListMapActivity fullListMapActivity) {
            super(fm);
            this.fullListActivity = fullListActivity;
            this.fullListMapActivity = fullListMapActivity;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    FullListActivity tab1 = fullListActivity;
                    return tab1;
                case 1:
                    FullListMapActivity tab2 = fullListMapActivity;
                    return tab2;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Todo List View";
                case 1:
                    return "Todo Map View";
            }
            return null;
        }
    }
}
