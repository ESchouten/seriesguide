package com.battlelancer.seriesguide.test;

import android.provider.Settings;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.battlelancer.seriesguide.R;
import com.battlelancer.seriesguide.ui.ShowsActivity;
import com.linkedin.android.testbutler.TestButler;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddSerieToFavorites {

    @Rule
    public ActivityTestRule<ShowsActivity> showsActivityActivityTestRule = new ActivityTestRule<ShowsActivity>(ShowsActivity.class);

    @Test
    public void addSerieToFavoritesList() {
        String serieName = "Prison Break";
        String littleBitOfSynopsis = "Due to a political";

        onView(withId(R.id.buttonShowsAdd)).perform(click());
        onView(withId(R.id.editTextSearchBar)).perform(typeText(serieName), pressImeActionButton());
        onView(allOf(withId(R.id.textViewAddTitle), withText(serieName))).perform(click());
        onView(withId(R.id.buttonPositive)).perform(click());
    }

    @Test
    public void systemCanNotLoadSerieList() {
        String serieName = "Game of Thrones";

        TestButler.setWifiState(false);

        onView(withId(R.id.buttonShowsAdd)).perform(click());
        onView(withId(R.id.editTextSearchBar)).perform(typeText(serieName), pressImeActionButton());
        onView(withId(R.id.emptyViewAdd)).check(matches(isDisplayed()));
    }

}
