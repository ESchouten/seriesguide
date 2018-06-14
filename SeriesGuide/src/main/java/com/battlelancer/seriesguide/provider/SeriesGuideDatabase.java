package com.battlelancer.seriesguide.provider;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import com.battlelancer.seriesguide.provider.SeriesGuideContract.EpisodeSearch;
import com.battlelancer.seriesguide.provider.SeriesGuideContract.EpisodeSearchColumns;
import com.battlelancer.seriesguide.provider.SeriesGuideContract.Episodes;
import com.battlelancer.seriesguide.provider.SeriesGuideContract.EpisodesColumns;
import com.battlelancer.seriesguide.provider.SeriesGuideContract.ListItemsColumns;
import com.battlelancer.seriesguide.provider.SeriesGuideContract.Lists;
import com.battlelancer.seriesguide.provider.SeriesGuideContract.ListsColumns;
import com.battlelancer.seriesguide.provider.SeriesGuideContract.MoviesColumns;
import com.battlelancer.seriesguide.provider.SeriesGuideContract.SeasonsColumns;
import com.battlelancer.seriesguide.provider.SeriesGuideContract.Shows;
import com.battlelancer.seriesguide.provider.SeriesGuideContract.ShowsColumns;
import com.battlelancer.seriesguide.settings.NotificationSettings;
import com.battlelancer.seriesguide.util.DBUtils;
import com.battlelancer.seriesguide.util.TimeTools;
import com.uwetrottmann.androidutils.AndroidUtils;
import java.util.Calendar;
import java.util.TimeZone;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;
import timber.log.Timber;

import static com.battlelancer.seriesguide.provider.SeriesGuideContract.ActivityColumns;
import static com.battlelancer.seriesguide.provider.SeriesGuideContract.ListItems;
import static com.battlelancer.seriesguide.provider.SeriesGuideContract.Movies;
import static com.battlelancer.seriesguide.provider.SeriesGuideContract.Seasons;

public class SeriesGuideDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "seriesdatabase";

    public static final int DBVER_FAVORITES = 17;

    public static final int DBVER_NEXTAIRDATETEXT = 18;

    public static final int DBVER_SETOTALCOUNT = 19;

    public static final int DBVER_SYNC = 20;

    public static final int DBVER_AIRTIMECOLUMN = 21;

    public static final int DBVER_PERSHOWUPDATEDATE = 22;

    public static final int DBVER_HIDDENSHOWS = 23;

    public static final int DBVER_AIRTIMEREFORM = 24;

    public static final int DBVER_NEXTAIRDATEMS = 25;

    public static final int DBVER_COLLECTED = 26;

    public static final int DBVER_IMDBIDSLASTEDIT = 27;

    public static final int DBVER_LISTS = 28;

    public static final int DBVER_GETGLUE_CHECKIN_FIX = 29;

    public static final int DBVER_ABSOLUTE_NUMBERS = 30;

    public static final int DBVER_31_LAST_WATCHED_ID = 31;

    public static final int DBVER_32_MOVIES = 32;

    public static final int DBVER_33_IGNORE_ARTICLE_SORT = 33;
    
    // Query builder syntax
    static final String QUERY_SELECT = "SELECT ";
    static final String QUERY_FROM = "FROM ";
    static final String QUERY_WHERE = "WHERE ";
    static final String QUERY_LEFTOUTERJOIN = "LEFT OUTER JOIN ";
    static final String QUERY_ON = "ON ";
    static final String QUERY_AS = "AS ";
    static final String QUERY_UNIONSELECT = "UNION SELECT ";
    static final String QUERY_REFERENCES = "REFERENCES ";
    static final String QUERY_ONCONFLICTREPLACE = "ON CONFLICT REPLACE ";
    static final String QUERY_CREATETABLE = "CREATE TABLE ";
    static final String QUERY_INSERTORIGNOREINTO = "INSERT OR IGNORE INTO ";
    static final String QUERY_JOIN = "JOIN ";
    static final String QUERY_ALTERTABLE = "ALTER TABLE ";

    /**
     * Changes for trakt v2 compatibility, also for storing ratings offline.
     *
     * Shows:
     *
     * <ul>
     *
     * <li>changed release time encoding
     *
     * <li>changed release week day encoding
     *
     * <li>first release date now includes time
     *
     * <li>added time zone
     *
     * <li>added rating votes
     *
     * <li>added user rating
     *
     * </ul>
     *
     * Episodes:
     *
     * <ul>
     *
     * <li>added rating votes
     *
     * <li>added user rating
     *
     * </ul>
     *
     * Movies:
     *
     * <ul>
     *
     * <li>added user rating
     *
     * </ul>
     */
    public static final int DBVER_34_TRAKT_V2 = 34;

    /**
     * Added activity table to store recently watched episodes.
     */
    public static final int DBVER_35_ACTIVITY_TABLE = 35;

    /**
     * Support for re-ordering lists: added new column to lists table.
     */
    public static final int DBVER_36_ORDERABLE_LISTS = 36;

    /**
     * Added language column to shows table.
     */
    public static final int DBVER_37_LANGUAGE_PER_SERIES = 37;

    /**
     * Added trakt id column to shows table.
     */
    private static final int DBVER_38_SHOW_TRAKT_ID = 38;

    /**
     * Added last watched time and unwatched counter to shows table.
     */
    private static final int DBVER_39_SHOW_LAST_WATCHED = 39;

    /**
     * Add {@link Shows#NOTIFY} flag to shows table.
     */
    private static final int DBVER_40_NOTIFY_PER_SHOW = 40;

    /**
     * Add {@link Episodes#LAST_UPDATED} flag to episodes table.
     */
    private static final int DBVER_41_EPISODE_LAST_UPDATED = 41;

    public static final int DATABASE_VERSION = DBVER_41_EPISODE_LAST_UPDATED;

    private static final String CREATE_SHOWS_TABLE = QUERY_CREATETABLE + Tables.SHOWS
            + " ("

            + BaseColumns._ID + " INTEGER PRIMARY KEY,"

            + ShowsColumns.TITLE + " TEXT NOT NULL,"

            + ShowsColumns.TITLE_NOARTICLE + " TEXT,"

            + ShowsColumns.OVERVIEW + " TEXT DEFAULT '',"

            + ShowsColumns.ACTORS + " TEXT DEFAULT '',"

            + ShowsColumns.RELEASE_TIME + " INTEGER,"

            + ShowsColumns.RELEASE_WEEKDAY + " INTEGER,"

            + ShowsColumns.RELEASE_COUNTRY + " TEXT,"

            + ShowsColumns.RELEASE_TIMEZONE + " TEXT,"

            + ShowsColumns.FIRST_RELEASE + " TEXT,"

            + ShowsColumns.GENRES + " TEXT DEFAULT '',"

            + ShowsColumns.NETWORK + " TEXT DEFAULT '',"

            + ShowsColumns.RATING_GLOBAL + " REAL,"

            + ShowsColumns.RATING_VOTES + " INTEGER,"

            + ShowsColumns.RATING_USER + " INTEGER,"

            + ShowsColumns.RUNTIME + " TEXT DEFAULT '',"

            + ShowsColumns.STATUS + " TEXT DEFAULT '',"

            + ShowsColumns.CONTENTRATING + " TEXT DEFAULT '',"

            + ShowsColumns.NEXTEPISODE + " TEXT DEFAULT '',"

            + ShowsColumns.POSTER + " TEXT DEFAULT '',"

            + ShowsColumns.NEXTAIRDATEMS + " INTEGER,"

            + ShowsColumns.NEXTTEXT + " TEXT DEFAULT '',"

            + ShowsColumns.IMDBID + " TEXT DEFAULT '',"

            + ShowsColumns.TRAKT_ID + " INTEGER DEFAULT 0,"

            + ShowsColumns.FAVORITE + " INTEGER DEFAULT 0,"

            + ShowsColumns.NEXTAIRDATETEXT + " TEXT DEFAULT '',"

            + ShowsColumns.HEXAGON_MERGE_COMPLETE + " INTEGER DEFAULT 1,"

            + ShowsColumns.HIDDEN + " INTEGER DEFAULT 0,"

            + ShowsColumns.LASTUPDATED + " INTEGER DEFAULT 0,"

            + ShowsColumns.LASTEDIT + " INTEGER DEFAULT 0,"

            + ShowsColumns.LASTWATCHEDID + " INTEGER DEFAULT 0,"

            + ShowsColumns.LASTWATCHED_MS + " INTEGER DEFAULT 0,"

            + ShowsColumns.LANGUAGE + " TEXT DEFAULT '',"

            + ShowsColumns.UNWATCHED_COUNT + " INTEGER DEFAULT " + DBUtils.UNKNOWN_UNWATCHED_COUNT
            + ","

            + ShowsColumns.NOTIFY + " INTEGER DEFAULT 1"

            + ");";

    private static final String CREATE_SEASONS_TABLE = QUERY_CREATETABLE + Tables.SEASONS
            + " ("

            + BaseColumns._ID + " INTEGER PRIMARY KEY,"

            + SeasonsColumns.COMBINED + " INTEGER,"

            + ShowsColumns.REF_SHOW_ID + " TEXT " + References.SHOW_ID + ","

            + SeasonsColumns.WATCHCOUNT + " INTEGER DEFAULT 0,"

            + SeasonsColumns.UNAIREDCOUNT + " INTEGER DEFAULT 0,"

            + SeasonsColumns.NOAIRDATECOUNT + " INTEGER DEFAULT 0,"

            + SeasonsColumns.TAGS + " TEXT DEFAULT '',"

            + SeasonsColumns.TOTALCOUNT + " INTEGER DEFAULT 0"

            + ");";

    private static final String CREATE_EPISODES_TABLE = QUERY_CREATETABLE + Tables.EPISODES
            + " ("

            + BaseColumns._ID + " INTEGER PRIMARY KEY,"

            + EpisodesColumns.TITLE + " TEXT NOT NULL,"

            + EpisodesColumns.OVERVIEW + " TEXT,"

            + EpisodesColumns.NUMBER + " INTEGER DEFAULT 0,"

            + EpisodesColumns.SEASON + " INTEGER DEFAULT 0,"

            + EpisodesColumns.DVDNUMBER + " REAL,"

            + SeasonsColumns.REF_SEASON_ID + " TEXT " + References.SEASON_ID + ","

            + ShowsColumns.REF_SHOW_ID + " TEXT " + References.SHOW_ID + ","

            + EpisodesColumns.WATCHED + " INTEGER DEFAULT 0,"

            + EpisodesColumns.DIRECTORS + " TEXT DEFAULT '',"

            + EpisodesColumns.GUESTSTARS + " TEXT DEFAULT '',"

            + EpisodesColumns.WRITERS + " TEXT DEFAULT '',"

            + EpisodesColumns.IMAGE + " TEXT DEFAULT '',"

            + EpisodesColumns.FIRSTAIREDMS + " INTEGER DEFAULT -1,"

            + EpisodesColumns.COLLECTED + " INTEGER DEFAULT 0,"

            + EpisodesColumns.RATING_GLOBAL + " REAL,"

            + EpisodesColumns.RATING_VOTES + " INTEGER,"

            + EpisodesColumns.RATING_USER + " INTEGER,"

            + EpisodesColumns.IMDBID + " TEXT DEFAULT '',"

            + EpisodesColumns.LAST_EDITED + " INTEGER DEFAULT 0,"

            + EpisodesColumns.ABSOLUTE_NUMBER + " INTEGER,"

            + EpisodesColumns.LAST_UPDATED + " INTEGER DEFAULT 0"

            + ");";

    private static final String CREATE_SEARCH_TABLE = "CREATE VIRTUAL TABLE "
            + Tables.EPISODES_SEARCH + " USING fts4("

            // set episodes table as external content table
            + "content='" + Tables.EPISODES + "',"

            + EpisodeSearchColumns.TITLE + ","

            + EpisodeSearchColumns.OVERVIEW

            + ");";

    /** Some Android 4.0 devices do not support FTS4, despite being standard since 3.0. */
    private static final String CREATE_SEARCH_TABLE_API_ICS = "CREATE VIRTUAL TABLE "
            + Tables.EPISODES_SEARCH + " USING FTS3("

            + EpisodeSearchColumns.TITLE + " TEXT,"

            + EpisodeSearchColumns.OVERVIEW + " TEXT"

            + ");";

    private static final String CREATE_LISTS_TABLE = QUERY_CREATETABLE + Tables.LISTS
            + " ("

            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"

            + ListsColumns.LIST_ID + " TEXT NOT NULL,"

            + ListsColumns.NAME + " TEXT NOT NULL,"

            + ListsColumns.ORDER + " INTEGER DEFAULT 0,"

            + "UNIQUE (" + ListsColumns.LIST_ID + ") " + QUERY_ONCONFLICTREPLACE

            + ");";

    private static final String CREATE_LIST_ITEMS_TABLE = QUERY_CREATETABLE + Tables.LIST_ITEMS
            + " ("

            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"

            + ListItemsColumns.LIST_ITEM_ID + " TEXT NOT NULL,"

            + ListItemsColumns.ITEM_REF_ID + " TEXT NOT NULL,"

            + ListItemsColumns.TYPE + " INTEGER NOT NULL,"

            + ListsColumns.LIST_ID + " TEXT " + References.LIST_ID + ","

            + "UNIQUE (" + ListItemsColumns.LIST_ITEM_ID + ") " + QUERY_ONCONFLICTREPLACE

            + ");";

    private static final String CREATE_MOVIES_TABLE = QUERY_CREATETABLE + Tables.MOVIES
            + " ("

            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"

            + MoviesColumns.TMDB_ID + " INTEGER NOT NULL,"

            + MoviesColumns.IMDB_ID + " TEXT,"

            + MoviesColumns.TITLE + " TEXT,"

            + MoviesColumns.TITLE_NOARTICLE + " TEXT,"

            + MoviesColumns.POSTER + " TEXT,"

            + MoviesColumns.GENRES + " TEXT,"

            + MoviesColumns.OVERVIEW + " TEXT,"

            + MoviesColumns.RELEASED_UTC_MS + " INTEGER,"

            + MoviesColumns.RUNTIME_MIN + " INTEGER DEFAULT 0,"

            + MoviesColumns.TRAILER + " TEXT,"

            + MoviesColumns.CERTIFICATION + " TEXT,"

            + MoviesColumns.IN_COLLECTION + " INTEGER DEFAULT 0,"

            + MoviesColumns.IN_WATCHLIST + " INTEGER DEFAULT 0,"

            + MoviesColumns.PLAYS + " INTEGER DEFAULT 0,"

            + MoviesColumns.WATCHED + " INTEGER DEFAULT 0,"

            + MoviesColumns.RATING_TMDB + " REAL DEFAULT 0,"

            + MoviesColumns.RATING_VOTES_TMDB + " INTEGER DEFAULT 0,"

            + MoviesColumns.RATING_TRAKT + " INTEGER DEFAULT 0,"

            + MoviesColumns.RATING_VOTES_TRAKT + " INTEGER DEFAULT 0,"

            + MoviesColumns.RATING_USER + " INTEGER,"

            + MoviesColumns.LAST_UPDATED + " INTEGER,"

            + "UNIQUE (" + MoviesColumns.TMDB_ID + ") " + QUERY_ONCONFLICTREPLACE

            + ");";

    private static final String CREATE_ACTIVITY_TABLE = QUERY_CREATETABLE + Tables.ACTIVITY
            + " ("
            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + ActivityColumns.EPISODE_TVDB_ID + " TEXT NOT NULL,"
            + ActivityColumns.SHOW_TVDB_ID + " TEXT NOT NULL,"
            + ActivityColumns.TIMESTAMP_MS + " INTEGER NOT NULL,"
            + "UNIQUE (" + ActivityColumns.EPISODE_TVDB_ID + ") " + QUERY_ONCONFLICTREPLACE
            + ");";

    private final Context context;

    public SeriesGuideDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SHOWS_TABLE);

        db.execSQL(CREATE_SEASONS_TABLE);

        db.execSQL(CREATE_EPISODES_TABLE);

        if (AndroidUtils.isJellyBeanOrHigher()) {
            db.execSQL(CREATE_SEARCH_TABLE);
        } else {
            db.execSQL(CREATE_SEARCH_TABLE_API_ICS);
        }

        db.execSQL(CREATE_LISTS_TABLE);

        db.execSQL(CREATE_LIST_ITEMS_TABLE);

        db.execSQL(CREATE_MOVIES_TABLE);

        db.execSQL(CREATE_ACTIVITY_TABLE);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Timber.d("Can't downgrade from version %s to %s", oldVersion, newVersion);
        onResetDatabase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Timber.d("Upgrading from %s to %s", oldVersion, newVersion);

        // run necessary upgrades
        int version = oldVersion;
        switch (version) {
            case 16:
                upgradeToSeventeen(db);
            case 17:
                upgradeToEighteen(db);
            case 18:
                upgradeToNineteen(db);
            case 19:
                upgradeToTwenty(db);
            case 20:
                upgradeToTwentyOne(db);
            case 21:
                upgradeToTwentyTwo(db);
            case 22:
                upgradeToTwentyThree(db);
            case 23:
                upgradeToTwentyFour(db);
            case 24:
                upgradeToTwentyFive(db);
            case 25:
                upgradeToTwentySix(db);
            case 26:
                upgradeToTwentySeven(db);
            case 27:
                upgradeToTwentyEight(db);
            case 28:
                // GetGlue column not required any longer
            case 29:
                upgradeToThirty(db);
            case 30:
                upgradeToThirtyOne(db);
            case DBVER_31_LAST_WATCHED_ID:
                upgradeToThirtyTwo(db);
            case DBVER_32_MOVIES:
                upgradeToThirtyThree(db);
            case DBVER_33_IGNORE_ARTICLE_SORT:
                upgradeToThirtyFour(db);
            case DBVER_34_TRAKT_V2:
                upgradeToThirtyFive(db);
            case DBVER_35_ACTIVITY_TABLE:
                upgradeToThirtySix(db);
            case DBVER_36_ORDERABLE_LISTS:
                upgradeToThirtySeven(db);
            case DBVER_37_LANGUAGE_PER_SERIES:
                upgradeToThirtyEight(db);
            case DBVER_38_SHOW_TRAKT_ID:
                upgradeToThirtyNine(db);
            case DBVER_39_SHOW_LAST_WATCHED:
                upgradeToForty(db, context);
            case DBVER_40_NOTIFY_PER_SHOW:
                upgradeToFortyOne(db);
                version = DBVER_41_EPISODE_LAST_UPDATED;
        }

        // drop all tables if version is not right
        Timber.d("After upgrade at version %s", version);
        if (version != DATABASE_VERSION) {
            onResetDatabase(db);
        }
    }

    /**
     * Drops all tables and creates an empty database.
     */
    private void onResetDatabase(SQLiteDatabase db) {
        Timber.w("Resetting database");
        db.execSQL("DROP TABLE IF EXISTS " + Tables.SHOWS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.SEASONS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.EPISODES);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.LISTS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.LIST_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.MOVIES);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.ACTIVITY);

        db.execSQL("DROP TABLE IF EXISTS " + Tables.EPISODES_SEARCH);

        onCreate(db);
    }

    /**
     * See {@link #DBVER_41_EPISODE_LAST_UPDATED}.
     */
    private static void upgradeToFortyOne(SQLiteDatabase db) {
        if (isTableColumnMissing(db, Tables.EPISODES, Episodes.LAST_UPDATED)) {
            db.execSQL(QUERY_ALTERTABLE + Tables.EPISODES + " ADD COLUMN "
                    + Episodes.LAST_UPDATED + " INTEGER DEFAULT 0;");
        }
    }

    /**
     * See {@link #DBVER_40_NOTIFY_PER_SHOW}.
     */
    private static void upgradeToForty(SQLiteDatabase db, Context context) {
        if (isTableColumnMissing(db, Tables.SHOWS, Shows.NOTIFY)) {
            db.execSQL(QUERY_ALTERTABLE + Tables.SHOWS + " ADD COLUMN "
                    + Shows.NOTIFY + " INTEGER DEFAULT 1;");

            // check if notifications should be enabled only for favorite shows
            // noinspection deprecation
            boolean favoritesOnly = NotificationSettings.isNotifyAboutFavoritesOnly(context);
            if (favoritesOnly) {
                // disable notifications for all but favorite shows
                ContentValues values = new ContentValues();
                values.put(Shows.NOTIFY, false);
                db.update(Tables.SHOWS, values, Shows.SELECTION_NOT_FAVORITES, null);
            }
        }
    }

    /**
     * See {@link #DBVER_39_SHOW_LAST_WATCHED}.
     */
    private static void upgradeToThirtyNine(SQLiteDatabase db) {
        if (isTableColumnMissing(db, Tables.SHOWS, Shows.LASTWATCHED_MS)) {
            db.execSQL(QUERY_ALTERTABLE + Tables.SHOWS + " ADD COLUMN "
                    + Shows.LASTWATCHED_MS + " INTEGER DEFAULT 0;");
        }
        if (isTableColumnMissing(db, Tables.SHOWS, Shows.UNWATCHED_COUNT)) {
            db.execSQL(QUERY_ALTERTABLE + Tables.SHOWS + " ADD COLUMN "
                    + Shows.UNWATCHED_COUNT + " INTEGER DEFAULT " + DBUtils.UNKNOWN_UNWATCHED_COUNT
                    + ";");
        }
    }

    /**
     * See {@link #DBVER_38_SHOW_TRAKT_ID}.
     */
    private static void upgradeToThirtyEight(SQLiteDatabase db) {
        if (isTableColumnMissing(db, Tables.SHOWS, Shows.TRAKT_ID)) {
            db.execSQL(QUERY_ALTERTABLE + Tables.SHOWS + " ADD COLUMN "
                    + Shows.TRAKT_ID + " INTEGER DEFAULT 0;");
        }
    }

    /**
     * See {@link #DBVER_37_LANGUAGE_PER_SERIES}.
     */
    private static void upgradeToThirtySeven(SQLiteDatabase db) {
        if (isTableColumnMissing(db, Tables.SHOWS, Shows.LANGUAGE)) {
            db.execSQL(QUERY_ALTERTABLE + Tables.SHOWS + " ADD COLUMN "
                    + Shows.LANGUAGE + " TEXT DEFAULT '';");
        }
    }

    /**
     * See {@link #DBVER_36_ORDERABLE_LISTS}.
     */
    private static void upgradeToThirtySix(SQLiteDatabase db) {
        if (isTableColumnMissing(db, Tables.LISTS, Lists.ORDER)) {
            db.execSQL(QUERY_ALTERTABLE + Tables.LISTS + " ADD COLUMN "
                    + Lists.ORDER + " INTEGER DEFAULT 0;");
        }
    }

    /**
     * See {@link #DBVER_35_ACTIVITY_TABLE}.
     */
    private static void upgradeToThirtyFive(SQLiteDatabase db) {
        if (!isTableExisting(db, Tables.ACTIVITY)) {
            db.execSQL(CREATE_ACTIVITY_TABLE);
        }
    }

    /**
     * See {@link #DBVER_34_TRAKT_V2}.
     */
    private static void upgradeToThirtyFour(SQLiteDatabase db) {
        // add new columns
        db.beginTransaction();
        try {
            // shows
            if (isTableColumnMissing(db, Tables.SHOWS, Shows.RELEASE_TIMEZONE)) {
                db.execSQL(QUERY_ALTERTABLE + Tables.SHOWS + " ADD COLUMN "
                        + Shows.RELEASE_TIMEZONE + " TEXT;");
            }
            if (isTableColumnMissing(db, Tables.SHOWS, Shows.RATING_VOTES)) {
                db.execSQL(QUERY_ALTERTABLE + Tables.SHOWS + " ADD COLUMN "
                        + Shows.RATING_VOTES + " INTEGER;");
            }
            if (isTableColumnMissing(db, Tables.SHOWS, Shows.RATING_USER)) {
                db.execSQL("ALTER TABLE " + Tables.SHOWS + " ADD COLUMN "
                        + Shows.RATING_USER + " INTEGER;");
            }

            // episodes
            if (isTableColumnMissing(db, Tables.EPISODES, Episodes.RATING_VOTES)) {
                db.execSQL(QUERY_ALTERTABLE + Tables.EPISODES + " ADD COLUMN "
                        + Episodes.RATING_VOTES + " INTEGER;");
            }
            if (isTableColumnMissing(db, Tables.EPISODES, Episodes.RATING_USER)) {
                db.execSQL(QUERY_ALTERTABLE + Tables.EPISODES + " ADD COLUMN "
                        + Episodes.RATING_USER + " INTEGER;");
            }

            // movies
            if (isTableColumnMissing(db, Tables.MOVIES, Movies.RATING_USER)) {
                db.execSQL(QUERY_ALTERTABLE + Tables.MOVIES + " ADD COLUMN "
                        + Movies.RATING_USER + " INTEGER;");
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        // migrate existing data to new formats
        Cursor query = db.query(Tables.SHOWS,
                new String[] { Shows._ID, Shows.RELEASE_TIME, Shows.RELEASE_WEEKDAY }, null, null,
                null, null, null);

        // create calendar, set to custom time zone
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT-08:00"));
        ContentValues values = new ContentValues();

        db.beginTransaction();
        try {
            while (query.moveToNext()) {
                // time changed from ms to encoded local time
                long timeOld = query.getLong(1);
                int timeNew;
                if (timeOld != -1) {
                    calendar.setTimeInMillis(timeOld);
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    timeNew = hour * 100 + minute;
                } else {
                    timeNew = -1;
                }
                values.put(Shows.RELEASE_TIME, timeNew);

                // week day changed from string to int
                String weekDayOld = query.getString(2);
                int weekDayNew = TimeTools.parseShowReleaseWeekDay(weekDayOld);
                values.put(Shows.RELEASE_WEEKDAY, weekDayNew);

                db.update(Tables.SHOWS, values, Shows._ID + "=" + query.getInt(0), null);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            query.close();
        }
    }

    /**
     * Add shows and movies title column without articles.
     */
    private static void upgradeToThirtyThree(SQLiteDatabase db) {
        /*
        Add new columns. Added existence checks as 14.0.3 update botched upgrade process.
         */
        if (isTableColumnMissing(db, Tables.SHOWS, Shows.TITLE_NOARTICLE)) {
            db.execSQL(QUERY_ALTERTABLE + Tables.SHOWS + " ADD COLUMN " + Shows.TITLE_NOARTICLE
                    + " TEXT;");
        }
        if (isTableColumnMissing(db, Tables.MOVIES, Movies.TITLE_NOARTICLE)) {
            db.execSQL(QUERY_ALTERTABLE + Tables.MOVIES + " ADD COLUMN " + Movies.TITLE_NOARTICLE
                    + " TEXT;");
        }

        // shows
        Cursor shows = db.query(Tables.SHOWS, new String[] { Shows._ID, Shows.TITLE }, null, null,
                null, null, null);
        ContentValues newTitleValues = new ContentValues();
        if (shows != null) {
            db.beginTransaction();
            try {
                while (shows.moveToNext()) {
                    // put overwrites previous value
                    newTitleValues.put(Shows.TITLE_NOARTICLE,
                            DBUtils.trimLeadingArticle(shows.getString(1)));
                    db.update(Tables.SHOWS, newTitleValues, Shows._ID + "=" + shows.getInt(0),
                            null);
                }

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            shows.close();
        }

        newTitleValues.clear();

        // movies
        Cursor movies = db.query(Tables.MOVIES, new String[] { Movies._ID, Movies.TITLE }, null,
                null, null, null, null);
        if (movies != null) {
            db.beginTransaction();
            try {
                while (movies.moveToNext()) {
                    // put overwrites previous value
                    newTitleValues.put(Movies.TITLE_NOARTICLE,
                            DBUtils.trimLeadingArticle(movies.getString(1)));
                    db.update(Tables.MOVIES, newTitleValues, Movies._ID + "=" + movies.getInt(0),
                            null);
                }

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            movies.close();
        }
    }

    /**
     * Add movies table.
     */
    private static void upgradeToThirtyTwo(SQLiteDatabase db) {
        if (!isTableExisting(db, Tables.MOVIES)) {
            db.execSQL(CREATE_MOVIES_TABLE);
        }
    }

    // Must be watched and have an airdate
    private static final String LATEST_SELECTION = Episodes.WATCHED + "=1 AND "
            + Episodes.FIRSTAIREDMS + "!=-1 AND " + Shows.REF_SHOW_ID + "=?";

    // Latest aired first (ensures we get specials), if equal sort by season,
    // then number
    private static final String LATEST_ORDER = Episodes.FIRSTAIREDMS + " DESC,"
            + Episodes.SEASON + " DESC,"
            + Episodes.NUMBER + " DESC";

    /**
     * Add {@link Shows} column to store the last watched episode id for better prediction of next
     * episode.
     */
    private static void upgradeToThirtyOne(SQLiteDatabase db) {
        if (isTableColumnMissing(db, Tables.SHOWS, Shows.LASTWATCHEDID)) {
            db.execSQL(QUERY_ALTERTABLE + Tables.SHOWS + " ADD COLUMN " + Shows.LASTWATCHEDID
                    + " INTEGER DEFAULT 0;");
        }

        // pre populate with latest watched episode ids
        ContentValues values = new ContentValues();
        final Cursor shows = db.query(Tables.SHOWS, new String[] {
                Shows._ID,
        }, null, null, null, null, null);
        if (shows != null) {
            db.beginTransaction();
            try {
                while (shows.moveToNext()) {
                    final String showId = shows.getString(0);
                    final Cursor highestWatchedEpisode = db.query(Tables.EPISODES, new String[] {
                            Episodes._ID
                    }, LATEST_SELECTION, new String[] {
                            showId
                    }, null, null, LATEST_ORDER);

                    if (highestWatchedEpisode != null) {
                        if (highestWatchedEpisode.moveToFirst()) {
                            values.put(Shows.LASTWATCHEDID, highestWatchedEpisode.getInt(0));
                            db.update(Tables.SHOWS, values, Shows._ID + "=?", new String[] {
                                    showId
                            });
                            values.clear();
                        }

                        highestWatchedEpisode.close();
                    }
                }

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }

            shows.close();
        }
    }

    /**
     * Add {@link Episodes} column to store absolute episode number.
     */
    private static void upgradeToThirty(SQLiteDatabase db) {
        if (isTableColumnMissing(db, Tables.EPISODES, Episodes.ABSOLUTE_NUMBER)) {
            db.execSQL(QUERY_ALTERTABLE + Tables.EPISODES + " ADD COLUMN "
                    + Episodes.ABSOLUTE_NUMBER + " INTEGER;");
        }
    }

    /**
     * Add tables to store lists and list items.
     */
    private static void upgradeToTwentyEight(SQLiteDatabase db) {
        db.execSQL(CREATE_LISTS_TABLE);

        db.execSQL(CREATE_LIST_ITEMS_TABLE);
    }

    /**
     * Add {@link Episodes} columns for storing its IMDb id and last time of edit on theTVDB.com.
     * Add {@link Shows} column for storing last time of edit as well.
     */
    private static void upgradeToTwentySeven(SQLiteDatabase db) {
        db.execSQL(QUERY_ALTERTABLE + Tables.SHOWS + " ADD COLUMN " + ShowsColumns.LASTEDIT
                + " INTEGER DEFAULT 0;");
        db.execSQL(QUERY_ALTERTABLE + Tables.EPISODES + " ADD COLUMN " + EpisodesColumns.IMDBID
                + " TEXT DEFAULT '';");
        db.execSQL(QUERY_ALTERTABLE + Tables.EPISODES + " ADD COLUMN " + EpisodesColumns.LAST_EDITED
                + " INTEGER DEFAULT 0;");
    }

    /**
     * Add a {@link Episodes} column for storing whether an episode was collected in digital or
     * physical form.
     */
    private static void upgradeToTwentySix(SQLiteDatabase db) {
        db.execSQL(QUERY_ALTERTABLE + Tables.EPISODES + " ADD COLUMN " + EpisodesColumns.COLLECTED
                + " INTEGER DEFAULT 0;");
    }

    /**
     * Add a {@link Shows} column for storing the next air date in ms as integer data type rather
     * than as text.
     */
    private static void upgradeToTwentyFive(SQLiteDatabase db) {
        db.execSQL(QUERY_ALTERTABLE + Tables.SHOWS + " ADD COLUMN " + ShowsColumns.NEXTAIRDATEMS
                + " INTEGER DEFAULT 0;");
    }

    /**
     * Adds a column to the {@link Tables#EPISODES} table to store the airdate and possibly time in
     * milliseconds.
     */
    private static void upgradeToTwentyFour(SQLiteDatabase db) {
        db.execSQL(QUERY_ALTERTABLE + Tables.EPISODES + " ADD COLUMN " + EpisodesColumns.FIRSTAIREDMS
                + " INTEGER DEFAULT -1;");

        // populate the new column from existing data
        final Cursor shows = db.query(Tables.SHOWS, new String[] {
                Shows._ID
        }, null, null, null, null, null);

        while (shows.moveToNext()) {
            final String showId = shows.getString(0);

            //noinspection deprecation
            final Cursor episodes = db.query(Tables.EPISODES, new String[] {
                    Episodes._ID, Episodes.FIRSTAIRED
            }, Shows.REF_SHOW_ID + "=?", new String[] {
                    showId
            }, null, null, null);

            db.beginTransaction();
            try {
                ContentValues values = new ContentValues();
                ZoneId defaultShowTimeZone = TimeTools.getDateTimeZone(null);
                LocalTime defaultShowReleaseTime = TimeTools.getShowReleaseTime(-1);
                String deviceTimeZone = TimeZone.getDefault().getID();
                while (episodes.moveToNext()) {
                    String firstAired = episodes.getString(1);
                    long episodeAirtime = TimeTools.parseEpisodeReleaseDate(null,
                            defaultShowTimeZone, firstAired, defaultShowReleaseTime, null, null,
                            deviceTimeZone);

                    values.put(Episodes.FIRSTAIREDMS, episodeAirtime);
                    db.update(Tables.EPISODES, values, Episodes._ID + "=?", new String[] {
                            episodes.getString(0)
                    });
                    values.clear();
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }

            episodes.close();
        }

        shows.close();
    }

    /**
     * Adds a column to the {@link Tables#SHOWS} table similar to the favorite boolean, but to allow
     * hiding shows.
     */
    private static void upgradeToTwentyThree(SQLiteDatabase db) {
        db.execSQL(QUERY_ALTERTABLE + Tables.SHOWS + " ADD COLUMN " + ShowsColumns.HIDDEN
                + " INTEGER DEFAULT 0;");
    }

    /**
     * Add a column to store the last time a show has been updated to allow for more precise control
     * over which shows should get updated. This is in conjunction with a 7 day limit when a show
     * will get updated regardless if it has been marked as updated or not.
     */
    private static void upgradeToTwentyTwo(SQLiteDatabase db) {
        db.execSQL(QUERY_ALTERTABLE + Tables.SHOWS + " ADD COLUMN " + ShowsColumns.LASTUPDATED
                + " INTEGER DEFAULT 0;");
    }

    private static void upgradeToTwentyOne(SQLiteDatabase db) {
        db.execSQL(QUERY_ALTERTABLE + Tables.SHOWS + " ADD COLUMN " + ShowsColumns.RELEASE_COUNTRY
                + " TEXT DEFAULT '';");
    }

    private static void upgradeToTwenty(SQLiteDatabase db) {
        db.execSQL(
                QUERY_ALTERTABLE + Tables.SHOWS + " ADD COLUMN " + ShowsColumns.HEXAGON_MERGE_COMPLETE
                        + " INTEGER DEFAULT 1;");
    }

    /**
     * In version 19 the season integer column totalcount was added.
     */
    private static void upgradeToNineteen(SQLiteDatabase db) {
        db.execSQL(QUERY_ALTERTABLE + Tables.SEASONS + " ADD COLUMN " + SeasonsColumns.TOTALCOUNT
                + " INTEGER DEFAULT 0;");
    }

    /**
     * In version 18 the series text column nextairdatetext was added.
     */
    private static void upgradeToEighteen(SQLiteDatabase db) {
        db.execSQL(QUERY_ALTERTABLE + Tables.SHOWS + " ADD COLUMN " + ShowsColumns.NEXTAIRDATETEXT
                + " TEXT DEFAULT '';");

        // convert status text to 0/1 integer
        final Cursor shows = db.query(Tables.SHOWS, new String[] {
                Shows._ID, Shows.STATUS
        }, null, null, null, null, null);
        final ContentValues values = new ContentValues();
        String status;

        db.beginTransaction();
        try {
            while (shows.moveToNext()) {
                status = shows.getString(1);
                if (status.length() == 10) {
                    status = "1";
                } else if (status.length() == 5) {
                    status = "0";
                } else {
                    status = "";
                }
                values.put(Shows.STATUS, status);
                db.update(Tables.SHOWS, values, Shows._ID + "=?", new String[] {
                        shows.getString(0)
                });
                values.clear();
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        shows.close();
    }

    /**
     * In version 17 the series boolean column favorite was added.
     */
    private static void upgradeToSeventeen(SQLiteDatabase db) {
        db.execSQL(QUERY_ALTERTABLE + Tables.SHOWS + " ADD COLUMN " + ShowsColumns.FAVORITE
                + " INTEGER DEFAULT 0;");
    }

    /**
     * Drops the current {@link Tables#EPISODES_SEARCH} table and re-creates it with current data
     * from {@link Tables#EPISODES}.
     */
    public static void rebuildFtsTable(SQLiteDatabase db) {
        if (!recreateFtsTable(db)) {
            return;
        }

        if (AndroidUtils.isJellyBeanOrHigher()) {
            rebuildFtsTableJellyBean(db);
        } else {
            rebuildFtsTableIcs(db);
        }
    }

    /**
     * Works with FTS4 search table.
     */
    private static void rebuildFtsTableJellyBean(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            try {
                db.execSQL(QUERY_INSERTORIGNOREINTO + Tables.EPISODES_SEARCH
                        + "(" + Tables.EPISODES_SEARCH + ") VALUES('rebuild')");
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteException e) {
            Timber.e(e, "rebuildFtsTableJellyBean: failed to populate table.");
            DBUtils.postDatabaseError(e);
        }
    }

    /**
     * Works with FTS3 search table.
     */
    private static void rebuildFtsTableIcs(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            try {
                db.execSQL(
                        QUERY_INSERTORIGNOREINTO + Tables.EPISODES_SEARCH
                                + "(docid," + Episodes.TITLE + "," + Episodes.OVERVIEW + ")"
                                + " " + QUERY_SELECT + Episodes._ID + "," + Episodes.TITLE
                                + "," + Episodes.OVERVIEW
                                + " " + QUERY_FROM + Tables.EPISODES + ";");
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteException e) {
            Timber.e(e, "rebuildFtsTableIcs: failed to populate table.");
            // try to build a basic table with only episode titles
            rebuildBasicFtsTableIcs(db);
        }
    }

    /**
     * Similar to {@link #rebuildFtsTableIcs(SQLiteDatabase)}. However only inserts the episode
     * title, not the overviews to conserve space.
     */
    private static void rebuildBasicFtsTableIcs(SQLiteDatabase db) {
        if (!recreateFtsTable(db)) {
            return;
        }

        try {
            db.beginTransaction();
            try {
                db.execSQL(
                        QUERY_INSERTORIGNOREINTO + Tables.EPISODES_SEARCH
                                + "(docid," + Episodes.TITLE + ")"
                                + " " + QUERY_SELECT + Episodes._ID + "," + Episodes.TITLE
                                + " " + QUERY_FROM + Tables.EPISODES + ";");
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteException e) {
            Timber.e(e, "rebuildBasicFtsTableIcs: failed to populate table.");
            DBUtils.postDatabaseError(e);
        }
    }

    private static boolean recreateFtsTable(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            try {
                db.execSQL("drop table if exists " + Tables.EPISODES_SEARCH);
                if (AndroidUtils.isJellyBeanOrHigher()) {
                    db.execSQL(CREATE_SEARCH_TABLE);
                } else {
                    db.execSQL(CREATE_SEARCH_TABLE_API_ICS);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            return true;
        } catch (SQLiteException e) {
            Timber.e(e, "recreateFtsTable: failed.");
            DBUtils.postDatabaseError(e);
            return false;
        }
    }

    @Nullable
    public static Cursor search(String selection, String[] selectionArgs, SQLiteDatabase db) {
        // select
        // _id,episodetitle,episodedescription,number,season,watched,seriestitle
        // from (
        // (select _id as sid,seriestitle from series)
        // join
        // (select
        // _id,episodedescription,series_id,episodetitle,number,season,watched
        // from(select rowid,snippet(searchtable) as episodedescription from
        // searchtable where searchtable match 'QUERY')
        // join (select
        // _id,series_id,episodetitle,number,season,watched from episodes)
        // on _id=rowid)
        // on sid=series_id)

        StringBuilder query = new StringBuilder();
        // select final result columns
        query.append(QUERY_SELECT);
        query.append(Episodes._ID).append(",");
        query.append(Episodes.TITLE).append(",");
        query.append(Episodes.OVERVIEW).append(",");
        query.append(Episodes.NUMBER).append(",");
        query.append(Episodes.SEASON).append(",");
        query.append(Episodes.WATCHED).append(",");
        query.append(Shows.TITLE);

        query.append(" " + QUERY_FROM);
        query.append("(");

        // join all shows...
        query.append("(");
        query.append(QUERY_SELECT).append(BaseColumns._ID).append(" as sid,").append(Shows.TITLE);
        query.append(" " + QUERY_FROM).append(Tables.SHOWS);
        query.append(")");

        query.append(" " + QUERY_JOIN);

        // ...with matching episodes
        query.append("(");
        query.append(QUERY_SELECT);
        query.append(Episodes._ID).append(",");
        query.append(Episodes.TITLE).append(",");
        query.append(Episodes.OVERVIEW).append(",");
        query.append(Episodes.NUMBER).append(",");
        query.append(Episodes.SEASON).append(",");
        query.append(Episodes.WATCHED).append(",");
        query.append(Shows.REF_SHOW_ID);
        query.append(" " + QUERY_FROM);
        // join searchtable results...
        query.append("(");
        query.append(QUERY_SELECT);
        query.append(EpisodeSearch._DOCID).append(",");
        query.append("snippet(" + Tables.EPISODES_SEARCH + ",'<b>','</b>','...')").append(" AS ")
                .append(Episodes.OVERVIEW);
        query.append(" " + QUERY_FROM).append(Tables.EPISODES_SEARCH);
        query.append(" " + QUERY_WHERE).append(Tables.EPISODES_SEARCH).append(" MATCH ?");
        query.append(")");
        query.append(" " + QUERY_JOIN);
        // ...with episodes table
        query.append("(");
        query.append(QUERY_SELECT);
        query.append(Episodes._ID).append(",");
        query.append(Episodes.TITLE).append(",");
        query.append(Episodes.NUMBER).append(",");
        query.append(Episodes.SEASON).append(",");
        query.append(Episodes.WATCHED).append(",");
        query.append(Shows.REF_SHOW_ID);
        query.append(" " + QUERY_FROM).append(Tables.EPISODES);
        query.append(")");
        query.append(" " + QUERY_ON).append(Episodes._ID).append("=").append(EpisodeSearch._DOCID);

        query.append(")");
        query.append(" " + QUERY_ON).append("sid=").append(Shows.REF_SHOW_ID);
        query.append(")");

        // append given selection
        if (selection != null) {
            query.append(" " + QUERY_WHERE);
            query.append("(").append(selection).append(")");
        }

        // ordering
        query.append(" ORDER BY ");
        query.append(Shows.SORT_TITLE).append(",");
        query.append(Episodes.SEASON).append(" ASC,");
        query.append(Episodes.NUMBER).append(" ASC");

        // ensure to strip double quotation marks (would break the MATCH query)
        String searchTerm = selectionArgs[0];
        if (searchTerm != null) {
            searchTerm = searchTerm.replace("\"", "");
        }
        // search for anything starting with the given search term
        selectionArgs[0] = "\"" + searchTerm + "*\"";

        try {
            return db.rawQuery(query.toString(), selectionArgs);
        } catch (SQLiteException e) {
            Timber.e(e, "search: failed, database error.");
            return null;
        }
    }

    @Nullable
    public static Cursor getSuggestions(String searchTerm, SQLiteDatabase db) {
        String query = "select _id," + Episodes.TITLE + " " + QUERY_AS
                + SearchManager.SUGGEST_COLUMN_TEXT_1 + "," + Shows.TITLE + " " + QUERY_AS
                + SearchManager.SUGGEST_COLUMN_TEXT_2 + "," + "_id " + QUERY_AS
                + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID
                + " " + QUERY_FROM + " ((select _id " + QUERY_AS + "sid," + Shows.TITLE + " " + QUERY_FROM + Tables.SHOWS + ")"
                + " " + QUERY_JOIN
                + "(" + QUERY_SELECT + "_id," + Episodes.TITLE + "," + Shows.REF_SHOW_ID
                + " " + QUERY_FROM + "(select docid" + " " + QUERY_FROM + Tables.EPISODES_SEARCH
                + " " + QUERY_WHERE + Tables.EPISODES_SEARCH + " match " + "?)"
                + " " + QUERY_JOIN
                + "(" + QUERY_SELECT + " _id," + Episodes.TITLE + "," + Shows.REF_SHOW_ID + " " + QUERY_FROM + " episodes)"
                + QUERY_ON + "_id=docid)"
                + QUERY_ON + "sid=" + Shows.REF_SHOW_ID + ")";

        // ensure to strip double quotation marks (would break the MATCH query)
        if (searchTerm != null) {
            searchTerm = searchTerm.replace("\"", "");
        }

        try {
            // search for anything starting with the given search term
            return db.rawQuery(query, new String[] {
                    "\"" + searchTerm + "*\""
            });
        } catch (SQLiteException e) {
            Timber.e(e, "getSuggestions: failed, database error.");
            return null;
        }
    }

    /**
     * Checks whether a table exists in the given database.
     */
    private static boolean isTableExisting(SQLiteDatabase db, String table) {
        Cursor cursor = db.query("sqlite_master", new String[] { "name" },
                "type='table' AND name=?", new String[] { table }, null, null, null, "1");
        if (cursor == null) {
            return false;
        }
        boolean isTableExisting = cursor.getCount() > 0;
        cursor.close();
        return isTableExisting;
    }

    /**
     * Checks whether the given column exists in the given table of the given database.
     */
    private static boolean isTableColumnMissing(SQLiteDatabase db, String table, String column) {
        Cursor cursor = db.query(table, null, null, null, null, null, null, "1");
        if (cursor == null) {
            return true;
        }
        boolean isColumnExisting = cursor.getColumnIndex(column) != -1;
        cursor.close();
        return !isColumnExisting;
    }
}
