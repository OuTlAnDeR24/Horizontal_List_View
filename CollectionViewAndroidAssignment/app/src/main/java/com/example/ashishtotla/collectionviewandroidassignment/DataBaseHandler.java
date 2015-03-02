package com.example.ashishtotla.collectionviewandroidassignment;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by ashishtotla on 28-02-2015.
 */
public class DataBaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "musicData";

    // Contacts table name
    private static final String TABLE_DATA = "Data";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String NAME = "Name";
    private static final String ARTIST = "Artist";
    private static final String ALBUM = "Album";


    // Create Table statement
    private String CREATE_DATA_TABLE = "CREATE TABLE " + TABLE_DATA + "("
            + NAME + " TEXT,"
            + ARTIST + " TEXT," +  ALBUM + " TEXT" + ")";

    private Context context;
    private SQLiteDatabase db;

    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_DATA_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA);

        // Create tables again
        onCreate(db);
    }

    public boolean CheckIfEmpty(){

        db = getWritableDatabase();

        db.beginTransaction();
        Cursor mCursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_DATA, null);
        Boolean isEmpty;

        if (mCursor != null)
        {
            mCursor.moveToFirst();
            if(mCursor.getInt(0) == 0){
                isEmpty = true;
            }
            else{
                isEmpty = false;
            }
        }
        else
        {
            isEmpty = true;
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        return isEmpty;
    }

    public void AddData(SQLiteDatabase db){

        String mCSVFile = "sample_music_data.csv";
        AssetManager manager = context.getAssets();
        InputStream inStream = null;
        try {
            inStream = manager.open(mCSVFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader buffer = new BufferedReader(new InputStreamReader(inStream));

        String line = "";
        db.beginTransaction();
        int id=0;
        try {
            while ((line = buffer.readLine()) != null) {
                String[] columns = line.split(",");
                ContentValues cv = new ContentValues(3);
                cv.put(KEY_ID,id);
                cv.put(NAME, columns[0].trim());
                cv.put(ARTIST, columns[1].trim());
                cv.put(ALBUM, columns[2].trim());
                String insert = "INSERT INTO "+ TABLE_DATA+" (Name, Artist, Album) VALUES('"+columns[0]+"','"+columns[1]+"','"+columns[2]+"')";
                db.execSQL(insert);
                //db.insert(TABLE_DATA, null, cv);
                id++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public ArrayList<String> GetArtists(){
        db = getWritableDatabase();

        ArrayList<String> artists = new ArrayList<String>();
        db.beginTransaction();

        Cursor mCursor = db.rawQuery("SELECT DISTINCT " + ARTIST+ " FROM " + TABLE_DATA, null);
        mCursor.moveToFirst();
        mCursor.moveToNext();
        do{
            artists.add(mCursor.getString(0));
        }while(mCursor.moveToNext());

        db.endTransaction();
        return artists;
    }

    public ArrayList<String> GetAlbums(){
        db = getWritableDatabase();

        ArrayList<String> albums = new ArrayList<String>();
        db.beginTransaction();

        Cursor mCursor = db.rawQuery("SELECT DISTINCT " + ALBUM + " FROM " + TABLE_DATA, null);
        mCursor.moveToFirst();
        mCursor.moveToNext();
        do{
            albums.add(mCursor.getString(0));
        }while(mCursor.moveToNext());

        db.endTransaction();
        return albums;
    }

    public ArrayList<String> GetSongsByArtist(String artist){
        ArrayList<String> songs = new ArrayList<String>();
        db.beginTransaction();

        Cursor mCursor = db.rawQuery("SELECT DISTINCT " + NAME+ " FROM " + TABLE_DATA+" WHERE "+ ARTIST + " = '"+artist+"'", null);
        mCursor.moveToFirst();
        do{
            songs.add(mCursor.getString(0));
        }while(mCursor.moveToNext());

        db.endTransaction();
        return songs;
    }

    public ArrayList<String> GetSongsByAlbum(String album){
        ArrayList<String> songs = new ArrayList<String>();
        db.beginTransaction();

        Cursor mCursor = db.rawQuery("SELECT DISTINCT " + NAME+ " FROM " + TABLE_DATA+" WHERE "+ ALBUM + " = '"+album+"'", null);
        mCursor.moveToFirst();
        do{
            songs.add(mCursor.getString(0));
        }while(mCursor.moveToNext());

        db.endTransaction();
        return songs;
    }

}
