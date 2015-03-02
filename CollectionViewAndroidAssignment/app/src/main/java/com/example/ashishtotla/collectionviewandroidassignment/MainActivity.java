package com.example.ashishtotla.collectionviewandroidassignment;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ActionMenuView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


public class MainActivity extends Activity {

    private Context context;
    private SQLiteDatabase mWritableDataBase;
    private SQLiteDatabase mReadableDataBase;
    private DataBaseHandler mDataBaseHandler;
    SharedPreferences sharedPreferences;

    private String GroupBy = "Artist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mDataBaseHandler = new DataBaseHandler(context);
        mWritableDataBase = mDataBaseHandler.getWritableDatabase();

        if(mDataBaseHandler.CheckIfEmpty()){
            mDataBaseHandler.AddData(mWritableDataBase);
        }

        DisplayData();

    }

    public void DisplayData(){

        if(sharedPreferences.getString("GROUP_BY","Artist").equals("Artist")) {
            ArrayList<String> artists = mDataBaseHandler.GetArtists();
            ArrayList<PlaceholderFragment> mFragments = new ArrayList<PlaceholderFragment>();
            for (int i = 0; i < artists.size(); i++) {
                PlaceholderFragment myFragment = new PlaceholderFragment();
                Bundle mBundle = new Bundle();
                mBundle.putInt("MAX_SONGS", sharedPreferences.getInt("MAX_SONGS", 3));
                System.out.println(sharedPreferences.getInt("MAX_SONGS",3)+"====>"+sharedPreferences.getString("GROUP_BY","Artist"));
                mBundle.putString("GROUP_BY", artists.get(i));
                mBundle.putStringArrayList("SONGS", mDataBaseHandler.GetSongsByArtist(artists.get(i)));
                myFragment.setArguments(mBundle);
                getFragmentManager().beginTransaction()
                        .add(R.id.scrollLinear, myFragment, artists.get(i))
                        .commit();
            }

        }
        else if(sharedPreferences.getString("GROUP_BY","Album").equals("Album")){
            ArrayList<String> albums = mDataBaseHandler.GetAlbums();
            ArrayList<PlaceholderFragment> mFragments = new ArrayList<PlaceholderFragment>();
            for (int i = 0; i < albums.size(); i++) {
                PlaceholderFragment myFragment = new PlaceholderFragment();
                Bundle mBundle = new Bundle();
                mBundle.putInt("MAX_SONGS", sharedPreferences.getInt("MAX_SONGS", 3));
                mBundle.putString("GROUP_BY", albums.get(i));
                mBundle.putStringArrayList("SONGS", mDataBaseHandler.GetSongsByAlbum(albums.get(i)));
                myFragment.setArguments(mBundle);
                getFragmentManager().beginTransaction()
                        .add(R.id.scrollLinear, myFragment, albums.get(i))
                        .commit();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(context);
        View mCustomView = mInflater.inflate(R.layout.custom_action_bar, null);
        Spinner groupBySpinner = (Spinner) mCustomView.findViewById(R.id.groupBySpinner);
        groupBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!parent.getItemAtPosition(position).toString().equals("Group By")) {
                    sharedPreferences.edit().putString("GROUP_BY", parent.getItemAtPosition(position).toString()).commit();
                    finish();
                    startActivity(getIntent());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Spinner maxSongsSpinner = (Spinner) mCustomView.findViewById(R.id.maxSongsSpinner);
        maxSongsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!parent.getItemAtPosition(position).toString().equals("Songs")) {
                    sharedPreferences.edit().putInt("MAX_SONGS", Integer.parseInt(parent.getItemAtPosition(position).toString())).commit();
                    finish();
                    startActivity(getIntent());
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        return true;
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment(){

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            String GroupBy = getArguments().getString("GROUP_BY");
            ArrayList<String> songs = getArguments().getStringArrayList("SONGS");
            int maxSongs = getArguments().getInt("MAX_SONGS");

            TextView textGroupBy = (TextView) rootView.findViewById(R.id.name);
            textGroupBy.setWidth(getResources().getDisplayMetrics().widthPixels);
            textGroupBy.setPadding(0,10,0,10);
            textGroupBy.setGravity(Gravity.LEFT);
            textGroupBy.setText(GroupBy);

            LinearLayout parentLinearLayout = (LinearLayout) rootView.findViewById(R.id.parentLinear);

            int songIndex=0;
            for(int i=0;i<Math.ceil(songs.size())/maxSongs;i++){
                LinearLayout linearLayout = new LinearLayout(getActivity());
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(10,10,10,10);
                linearLayout.setLayoutParams(layoutParams);
                for(int j=0;j<maxSongs;j++){
                    if(songIndex<songs.size()) {
                        TextView textView = new TextView(getActivity());
                        LinearLayout.LayoutParams layoutParamstext = new LinearLayout.LayoutParams((int)(getResources().getDisplayMetrics().widthPixels/1.5), LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParamstext.setMargins(20, 10, 10, 10);
                        textView.setGravity(Gravity.LEFT);
                        textView.setMaxLines(1);
                        textView.setPadding(10,10,0,10);
                        textView.setBackgroundColor(Color.WHITE);
                        textView.setLayoutParams(layoutParamstext);
                        textView.setText(songs.get(songIndex));
                        songIndex++;
                        linearLayout.addView(textView);
                    }
                }
                parentLinearLayout.addView(linearLayout);
            }

            return rootView;
        }

    }
}
