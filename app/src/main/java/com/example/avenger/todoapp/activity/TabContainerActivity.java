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
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
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

        private final FullListActivity fullListActivity;
        private final FullListMapActivity fullListMapActivity;

        public SectionsPagerAdapter(FragmentManager fm, FullListActivity fullListActivity, FullListMapActivity fullListMapActivity) {
            super(fm);
            this.fullListActivity = fullListActivity;
            this.fullListMapActivity = fullListMapActivity;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return fullListActivity;
                case 1:
                    return fullListMapActivity;
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
