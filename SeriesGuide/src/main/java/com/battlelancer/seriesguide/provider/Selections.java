package com.battlelancer.seriesguide.provider;

import static com.battlelancer.seriesguide.provider.SeriesGuideDatabase.QUERY_AS;
import static com.battlelancer.seriesguide.provider.SeriesGuideDatabase.QUERY_FROM;
import static com.battlelancer.seriesguide.provider.SeriesGuideDatabase.QUERY_SELECT;
import static com.battlelancer.seriesguide.provider.SeriesGuideDatabase.QUERY_WHERE;

public final class Selections {

    public static final String LIST_ITEMS_SHOWS = "(" + QUERY_SELECT + Selections.LIST_ITEMS_COLUMNS_INTERNAL
            + " " + QUERY_FROM + Tables.LIST_ITEMS
            + " " + QUERY_WHERE + SeriesGuideContract.ListItems.SELECTION_SHOWS + ")"
            + " " + QUERY_AS + Tables.LIST_ITEMS;

    public static final String LIST_ITEMS_SEASONS = "(" + QUERY_SELECT + Selections.LIST_ITEMS_COLUMNS_INTERNAL
            + " " + QUERY_FROM + Tables.LIST_ITEMS
            + " " + QUERY_WHERE + SeriesGuideContract.ListItems.SELECTION_SEASONS + ")"
            + " " + QUERY_AS + Tables.LIST_ITEMS;

    public static final String LIST_ITEMS_EPISODES = "(" + QUERY_SELECT + Selections.LIST_ITEMS_COLUMNS_INTERNAL
            + " " + QUERY_FROM + Tables.LIST_ITEMS
            + " " + QUERY_WHERE + SeriesGuideContract.ListItems.SELECTION_EPISODES + ")"
            + " " + QUERY_AS + Tables.LIST_ITEMS;

    public static final String LIST_ITEMS_COLUMNS_INTERNAL =
            SeriesGuideContract.ListItems._ID + " " + QUERY_AS + "listitem_id,"
                    + SeriesGuideContract.ListItems.LIST_ITEM_ID + ","
                    + SeriesGuideContract.Lists.LIST_ID + ","
                    + SeriesGuideContract.ListItems.TYPE + ","
                    + SeriesGuideContract.ListItems.ITEM_REF_ID;

    public static final String COMMON_LIST_ITEMS_COLUMNS =
            // from list items table
            "listitem_id " + QUERY_AS + SeriesGuideContract.ListItems._ID + ","
                    + SeriesGuideContract.ListItems.LIST_ITEM_ID + ","
                    + SeriesGuideContract.Lists.LIST_ID + ","
                    + SeriesGuideContract.ListItems.TYPE + ","
                    + SeriesGuideContract.ListItems.ITEM_REF_ID + ","
                    // from shows table
                    + SeriesGuideContract.Shows.TITLE + ","
                    + SeriesGuideContract.Shows.TITLE_NOARTICLE + ","
                    + SeriesGuideContract.Shows.POSTER + ","
                    + SeriesGuideContract.Shows.NETWORK + ","
                    + SeriesGuideContract.Shows.STATUS + ","
                    + SeriesGuideContract.Shows.FAVORITE + ","
                    + SeriesGuideContract.Shows.RELEASE_WEEKDAY + ","
                    + SeriesGuideContract.Shows.RELEASE_TIMEZONE + ","
                    + SeriesGuideContract.Shows.RELEASE_COUNTRY + ","
                    + SeriesGuideContract.Shows.LASTWATCHED_MS + ","
                    + SeriesGuideContract.Shows.UNWATCHED_COUNT;

    public static final String SHOWS_COLUMNS = COMMON_LIST_ITEMS_COLUMNS + ","
            + Qualified.SHOWS_ID + " " + QUERY_AS + SeriesGuideContract.Shows.REF_SHOW_ID + ","
            + SeriesGuideContract.Shows.OVERVIEW + ","
            + SeriesGuideContract.Shows.RELEASE_TIME + ","
            + SeriesGuideContract.Shows.NEXTTEXT + ","
            + SeriesGuideContract.Shows.NEXTAIRDATETEXT + ","
            + SeriesGuideContract.Shows.NEXTAIRDATEMS;

    public static final String SEASONS_COLUMNS = COMMON_LIST_ITEMS_COLUMNS + ","
            + SeriesGuideContract.Shows.REF_SHOW_ID + ","
            + SeriesGuideContract.Seasons.COMBINED + " " + QUERY_AS + SeriesGuideContract.Shows.OVERVIEW + ","
            + SeriesGuideContract.Shows.RELEASE_TIME + ","
            + SeriesGuideContract.Shows.NEXTTEXT + ","
            + SeriesGuideContract.Shows.NEXTAIRDATETEXT + ","
            + SeriesGuideContract.Shows.NEXTAIRDATEMS;

    public static final String EPISODES_COLUMNS = COMMON_LIST_ITEMS_COLUMNS + ","
            + SeriesGuideContract.Shows.REF_SHOW_ID + ","
            + SeriesGuideContract.Episodes.TITLE + " " + QUERY_AS + SeriesGuideContract.Shows.OVERVIEW + ","
            + SeriesGuideContract.Episodes.FIRSTAIREDMS + " " + QUERY_AS + SeriesGuideContract.Shows.RELEASE_TIME + ","
            + SeriesGuideContract.Episodes.SEASON + " " + QUERY_AS + SeriesGuideContract.Shows.NEXTTEXT + ","
            + SeriesGuideContract.Episodes.NUMBER + " " + QUERY_AS + SeriesGuideContract.Shows.NEXTAIRDATETEXT + ","
            + SeriesGuideContract.Episodes.FIRSTAIREDMS + " " + QUERY_AS + SeriesGuideContract.Shows.NEXTAIRDATEMS;

    public Selections() {

    }

}
