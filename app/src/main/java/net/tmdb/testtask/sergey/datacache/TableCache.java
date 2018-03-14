package net.tmdb.testtask.sergey.datacache;


import android.provider.BaseColumns;

public class TableCache {
    public static final class TableEntry implements BaseColumns {

        public static final String TABLE_NAME = "data";
        public static final String COLUMN_MOVIEID = "movieid";
        //public static final String COLUMN_QUERY = "query";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_USERRATING = "userrating";
        public static final String COLUMN_POSTER_PATH = "posterpath";
        public static final String COLUMN_PLOT_SYNOPSIS = "overview";
    }
}
