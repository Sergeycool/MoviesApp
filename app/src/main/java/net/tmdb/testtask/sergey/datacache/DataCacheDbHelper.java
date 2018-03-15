package net.tmdb.testtask.sergey.datacache;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import net.tmdb.testtask.sergey.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class DataCacheDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cache.db";

    private static final int DATABASE_VERSION = 1;

    private static final String LOGTAG = "DATA_CACHE";

    private SQLiteOpenHelper dbhandler;
    private SQLiteDatabase db;

    public DataCacheDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open(){
        Log.i(LOGTAG, "Database Opened");
        db = dbhandler.getWritableDatabase();
    }

    public void close(){
        Log.i(LOGTAG, "Database Closed");
        dbhandler.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_CACHE_TABLE = "CREATE TABLE " + TableCache.TableEntry.TABLE_NAME + " (" +
                TableCache.TableEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TableCache.TableEntry.COLUMN_MOVIEID + " INTEGER, " +
                TableCache.TableEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                TableCache.TableEntry.COLUMN_DATE + " TEXT NOT NULL, " +
                TableCache.TableEntry.COLUMN_USERRATING + " REAL NOT NULL, " +
                TableCache.TableEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                TableCache.TableEntry.COLUMN_PLOT_SYNOPSIS + " TEXT NOT NULL" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_CACHE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TableCache.TableEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }

    public void addQuery(Movie movie){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TableCache.TableEntry.COLUMN_MOVIEID, movie.getId());
        values.put(TableCache.TableEntry.COLUMN_TITLE, movie.getOriginalTitle());
        values.put(TableCache.TableEntry.COLUMN_DATE, movie.getReleaseDate());
        values.put(TableCache.TableEntry.COLUMN_USERRATING, movie.getVoteAverage());
        values.put(TableCache.TableEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        values.put(TableCache.TableEntry.COLUMN_PLOT_SYNOPSIS, movie.getOverview());


        db.insert(TableCache.TableEntry.TABLE_NAME, null, values);
        db.close();
    }

    public List<Movie> getAllQueries(String query){
        String[] columns = {
                TableCache.TableEntry._ID,
                TableCache.TableEntry.COLUMN_MOVIEID,
                TableCache.TableEntry.COLUMN_DATE,
                TableCache.TableEntry.COLUMN_TITLE,
                TableCache.TableEntry.COLUMN_USERRATING,
                TableCache.TableEntry.COLUMN_POSTER_PATH,
                TableCache.TableEntry.COLUMN_PLOT_SYNOPSIS

        };

        List<Movie> cacheList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TableCache.TableEntry.TABLE_NAME,
                columns,
                "title = ?",
                new String[]{query},
                null,
                null,
                null);

        if (cursor.moveToFirst()){
            do {
                Movie movie = new Movie();
                movie.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(TableCache.TableEntry.COLUMN_MOVIEID))));
                movie.setReleaseDate(cursor.getString(cursor.getColumnIndex(TableCache.TableEntry.COLUMN_DATE)));
                movie.setOriginalTitle(cursor.getString(cursor.getColumnIndex(TableCache.TableEntry.COLUMN_TITLE)));
                movie.setVoteAverage(Double.parseDouble(cursor.getString(cursor.getColumnIndex(TableCache.TableEntry.COLUMN_USERRATING))));
                movie.setPosterPath(cursor.getString(cursor.getColumnIndex(TableCache.TableEntry.COLUMN_POSTER_PATH)));
                movie.setOverview(cursor.getString(cursor.getColumnIndex(TableCache.TableEntry.COLUMN_PLOT_SYNOPSIS)));

                cacheList.add(movie);

            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return cacheList;
    }

}

