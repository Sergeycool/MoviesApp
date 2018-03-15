package net.tmdb.testtask.sergey.datacache;


import android.provider.BaseColumns;

public class TableCache {
    public static final class TableEntry implements BaseColumns {

        static final String TABLE_NAME = "data";
        static final String COLUMN_MOVIEID = "movieid";
        static final String COLUMN_DATE = "date";
        static final String COLUMN_TITLE = "title";
        static final String COLUMN_USERRATING = "userrating";
        static final String COLUMN_POSTER_PATH = "posterpath";
        static final String COLUMN_PLOT_SYNOPSIS = "overview";
    }
}
