package com.darkarmed.chesttrackerforclashroyale;

import android.content.SharedPreferences;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Toolbar mToolbar;
    private TextView mToolbarTitle;
    private GridView mGridView;
    private GridViewAdapter mAdapter;
    private Set<String> mUsers;
    private String mUser;
    private List<Chest> mChests;
    private String mSequence;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        setTitle(getString(R.string.title));

        mGridView = (GridView) findViewById(R.id.gridview);

        mSequence = getString(R.string.chest_sequence);
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadUsers();

        loadChests();

        mAdapter = new GridViewAdapter(this, mChests);
        mGridView.setAdapter(mAdapter);

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
                    saveChests();
                    mUser = arrayAdapter.getItem(position);
                    saveUsers();
                    loadChests();

                    mAdapter = new GridViewAdapter(getApplicationContext(), mChests);
                    mGridView.setAdapter(mAdapter);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        saveChests();
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

    private void loadUsers() {
        SharedPreferences userPref = getPreferences(MODE_PRIVATE);
        mUser = userPref.getString("CURRENT_USER", "");

        if (mUser.equalsIgnoreCase("")) {
            mUser = "Hog Rider";
            userPref.edit().putString("CURRENT_USER", mUser).commit();
        }

        mUsers = userPref.getStringSet("USERS", null);

        if (mUsers == null) {
            mUsers = new HashSet<>();
            mUsers.add(mUser);
            mUsers.add("Goblin");
            mUsers.add("Barbarian");
            userPref.edit().putStringSet("USERS", mUsers).commit();
        }
    }

    private void saveUsers() {
        SharedPreferences userPref = getPreferences(MODE_PRIVATE);
        userPref.edit().putString("CURRENT_USER", mUser).putStringSet("USERS", mUsers).commit();
    }

    private boolean loadChests() {
        SharedPreferences chestPref = getSharedPreferences(mUser, MODE_PRIVATE);
        String json = chestPref.getString(getString(R.string.chest_seq_key), "");

        if (json.equalsIgnoreCase("")) {
            mChests = getChestList(mSequence);
            return false;
        } else {
            Log.d(TAG, json);
            mChests = new Gson().fromJson(json, new TypeToken<List<Chest>>() {
            }.getType());
            return true;
        }
    }

    private boolean saveChests() {
        SharedPreferences chestPref = getSharedPreferences(mUser, MODE_PRIVATE);
        String json = new Gson().toJson(mAdapter.getItems());
        chestPref.edit().putString(getString(R.string.chest_seq_key), json).commit();

        Log.d(TAG, json);
        Log.d(TAG, chestPref.getString(getString(R.string.chest_seq_key), ""));

        return true;
    }

    private List<Chest> getChestList(String seq) {
        List<Chest> chests = new ArrayList<>();

        for (int i = 0; i < seq.length(); ++i) {
            chests.add(new Chest(i, seq.charAt(i)));
        }

        return chests;
    }
}
