package com.battlelancer.seriesguide.provider;

public final class Qualified {

    public static final String SHOWS_ID = Tables.SHOWS + "." + SeriesGuideContract.Shows._ID;
    public static final String SHOWS_LAST_EPISODE = Tables.SHOWS + "." + SeriesGuideContract.Shows.LASTWATCHEDID;
    public static final String SHOWS_NEXT_EPISODE = Tables.SHOWS + "." + SeriesGuideContract.Shows.NEXTEPISODE;
    public static final String EPISODES_ID = Tables.EPISODES + "." + SeriesGuideContract.Episodes._ID;
    public static final String EPISODES_SHOW_ID = Tables.EPISODES + "." + SeriesGuideContract.Shows.REF_SHOW_ID;
    public static final String SEASONS_ID = Tables.SEASONS + "." + SeriesGuideContract.Seasons._ID;
    public static final String SEASONS_SHOW_ID = Tables.SEASONS + "." + SeriesGuideContract.Shows.REF_SHOW_ID;
    public static final String LIST_ITEMS_REF_ID = Tables.LIST_ITEMS + "." + SeriesGuideContract.ListItems.ITEM_REF_ID;

    public Qualified() {

    }

}
