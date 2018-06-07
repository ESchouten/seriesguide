package com.battlelancer.seriesguide.test;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.battlelancer.seriesguide.R;
import com.battlelancer.seriesguide.ui.SeriesGuidePreferences;
import com.battlelancer.seriesguide.ui.ShowsActivity;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AutomaticBackups {

    @Rule
    public ActivityTestRule<ShowsActivity> showsActivityActivityTestRule = new ActivityTestRule<ShowsActivity>(ShowsActivity.class);

    @Test
    public void Test1_turnAutomaticBackupsOn() {
        onView(withContentDescription(R.string.drawer_open)).perform(click());
        onView(withText(R.string.preferences)).perform(click());
        onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(8).perform(scrollTo(), click());
        onView(withId(R.id.switchAutoBackup)).perform(click());
        onView(withId(R.id.switchAutoBackup)).perform(click());
//        onView(withText()).perform(click());
        onView(withId(R.id.switchAutoBackup)).check(matches(isChecked()));
    }

}
