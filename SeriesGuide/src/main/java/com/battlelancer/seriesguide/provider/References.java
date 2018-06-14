package com.battlelancer.seriesguide.provider;

import android.provider.BaseColumns;

import static com.battlelancer.seriesguide.provider.SeriesGuideDatabase.QUERY_REFERENCES;

public final class References {

    public static final String SHOW_ID = QUERY_REFERENCES + Tables.SHOWS + "(" + BaseColumns._ID + ")";
    public static final String SEASON_ID = QUERY_REFERENCES + Tables.SEASONS + "(" + BaseColumns._ID + ")";
    public static final String LIST_ID = QUERY_REFERENCES + Tables.LISTS + "(" + SeriesGuideContract.Lists.LIST_ID + ")";

    public References() {

    }

}
