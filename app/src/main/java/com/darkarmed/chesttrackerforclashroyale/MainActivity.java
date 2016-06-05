package com.darkarmed.chesttrackerforclashroyale;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity
        implements GuiderFragment.OnFragmentInteractionListener,
        TrackerFragment.OnFragmentInteractionListener {
    private static final String TAG = "MainActivity";
    private Toolbar mToolbar;
    private TextView mToolbarTitle;
    private ViewPager mViewPager;
    private ChestTrackerPagerAdapter mPagerAdapter;
    private FragmentManager mFragmentManager;
    private Set<String> mUsers;
    private String mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbarTitle = (TextView) findViewById(R.id.toolbar_title);

//        setTitle(getString(R.string.title));
        setTitle("");

    }

    @Override
    protected void onResume() {
        super.onResume();

        loadUsers();

        if (mUsers.size() > 1) {
            Spinner mSpinner = (Spinner) findViewById(R.id.users_spinner);
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, new ArrayList<>(mUsers));

            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpinner.setAdapter(arrayAdapter);
            mSpinner.setSelection(arrayAdapter.getPosition(mUser));
            mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (mUser != arrayAdapter.getItem(position)) {
//                        saveChests();
                        mUser = arrayAdapter.getItem(position);
//                        saveUsers();
//                        loadChests();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        mFragmentManager = getSupportFragmentManager();
        GuiderFragment gf = GuiderFragment.newInstance("hengji", "miaoji");
        TrackerFragment tf = TrackerFragment.newInstance(mUser, "miaoji");
        mPagerAdapter = new ChestTrackerPagerAdapter(mFragmentManager, gf, tf);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);

//        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                mAdapter.open(position);
//            }
//        });
//
//        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                mAdapter.lock(position);
//                return false;
//            }
//        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        saveUsers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        if (mToolbarTitle != null) {
            mToolbar.setTitle(title);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void loadUsers() {
        SharedPreferences userPref = getPreferences(MODE_PRIVATE);
        mUser = userPref.getString("CURRENT_USER", "");

        if (mUser.equalsIgnoreCase("")) {
            mUser = "Hog Rider";
        }

        mUsers = userPref.getStringSet("USERS", null);

        if (mUsers == null) {
            mUsers = new HashSet<>();
            mUsers.add(mUser);
            mUsers.add("Goblin");
            mUsers.add("Barbarian");
        }
    }

    private void saveUsers() {
        SharedPreferences userPref = getPreferences(MODE_PRIVATE);
        userPref.edit().putString("CURRENT_USER", mUser).putStringSet("USERS", mUsers).commit();
    }
}
