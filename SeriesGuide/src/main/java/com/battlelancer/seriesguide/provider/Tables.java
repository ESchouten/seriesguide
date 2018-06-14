package com.battlelancer.seriesguide.provider;

import static com.battlelancer.seriesguide.provider.SeriesGuideDatabase.QUERY_AS;
import static com.battlelancer.seriesguide.provider.SeriesGuideDatabase.QUERY_FROM;
import static com.battlelancer.seriesguide.provider.SeriesGuideDatabase.QUERY_LEFTOUTERJOIN;
import static com.battlelancer.seriesguide.provider.SeriesGuideDatabase.QUERY_ON;
import static com.battlelancer.seriesguide.provider.SeriesGuideDatabase.QUERY_SELECT;
import static com.battlelancer.seriesguide.provider.SeriesGuideDatabase.QUERY_UNIONSELECT;

public final class Tables {

    public static final String SHOWS = "series";

    public static final String SEASONS = "seasons";

    public static final String EPISODES = "episodes";

    public static final String SHOWS_JOIN_EPISODES_ON_LAST_EPISODE = SHOWS + " " + QUERY_LEFTOUTERJOIN + EPISODES
            + " " + QUERY_ON + Qualified.SHOWS_LAST_EPISODE + "=" + Qualified.EPISODES_ID;

    public static final String SHOWS_JOIN_EPISODES_ON_NEXT_EPISODE = SHOWS + " " + QUERY_LEFTOUTERJOIN + EPISODES
            + " " + QUERY_ON + Qualified.SHOWS_NEXT_EPISODE + "=" + Qualified.EPISODES_ID;

    public static final String SEASONS_JOIN_SHOWS = SEASONS + " " + QUERY_LEFTOUTERJOIN + SHOWS
            + " " + QUERY_ON + Qualified.SEASONS_SHOW_ID + "=" + Qualified.SHOWS_ID;

    public static final String EPISODES_JOIN_SHOWS = EPISODES + " " + QUERY_LEFTOUTERJOIN + SHOWS
            + " " + QUERY_ON + Qualified.EPISODES_SHOW_ID + "=" + Qualified.SHOWS_ID;

    public static final String EPISODES_SEARCH = "searchtable";

    public static final String LISTS = "lists";

    public static final String LIST_ITEMS = "listitems";

    public static final String LIST_ITEMS_WITH_DETAILS = "("
            // shows
            + QUERY_SELECT + Selections.SHOWS_COLUMNS + " " + QUERY_FROM
            + "("
            + Selections.LIST_ITEMS_SHOWS
            + " " + QUERY_LEFTOUTERJOIN + SHOWS
            + " " + QUERY_ON + Qualified.LIST_ITEMS_REF_ID + "=" + Qualified.SHOWS_ID
            + ")"
            // seasons
            + " " + QUERY_UNIONSELECT + Selections.SEASONS_COLUMNS + " " + QUERY_FROM
            + "("
            + Selections.LIST_ITEMS_SEASONS
            + " " + QUERY_LEFTOUTERJOIN + "(" + SEASONS_JOIN_SHOWS + ") AS " + SEASONS
            + " " + QUERY_ON + Qualified.LIST_ITEMS_REF_ID + "=" + Qualified.SEASONS_ID
            + ")"
            // episodes
            + " " + QUERY_UNIONSELECT + Selections.EPISODES_COLUMNS + " " + QUERY_FROM
            + "("
            + Selections.LIST_ITEMS_EPISODES
            + " " + QUERY_LEFTOUTERJOIN + "(" + EPISODES_JOIN_SHOWS + ")" + QUERY_AS + " " + EPISODES
            + " " + QUERY_ON + Qualified.LIST_ITEMS_REF_ID + "=" + Qualified.EPISODES_ID
            + ")"
            //
            + ")";

    public static final String MOVIES = "movies";

    public static final String ACTIVITY = "activity";

    public Tables() {

    }

}
