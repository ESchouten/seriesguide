package com.battlelancer.seriesguide.test;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import com.battlelancer.seriesguide.R;
import com.battlelancer.seriesguide.ui.ShowsActivity;

import org.hamcrest.Matcher;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AddSerieToFavorites {

    @Rule
    public ActivityTestRule<ShowsActivity> showsActivityActivityTestRule = new ActivityTestRule<ShowsActivity>(ShowsActivity.class);

    @Test
    public void Test1_addSerieToFavoritesList() {
        String serieName = "Prison Break";

        onView(withId(R.id.buttonShowsAdd)).perform(click());
        onView(withId(R.id.editTextSearchBar)).perform(typeText(serieName), pressImeActionButton(), closeSoftKeyboard());
        onView(allOf(withId(R.id.textViewAddTitle), withText(serieName))).perform(click());
        onView(withId(R.id.buttonPositive)).perform(click());

        getActivity().finish();
    }

    @Test
    public void Test2_systemCanNotLoadSerieList() {
        String serieName = "Game of Thrones";

        WifiManager wifi = (WifiManager) showsActivityActivityTestRule.getActivity().getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(false);

        onView(withId(R.id.buttonShowsAdd)).perform(click());
        onView(withId(R.id.editTextSearchBar)).perform(typeText(serieName), pressImeActionButton(), closeSoftKeyboard());
        onView(withId(R.id.emptyViewAdd)).check(matches(isDisplayed()));

        wifi.setWifiEnabled(true);

        getActivity().finish();
    }

    @Test
    public void Test3_cancelAddingSerieToFavoritesList() {
        String serieName = "House";
        String partOfSynopsis = "Ruthless and cunning";

        onView(withId(R.id.buttonShowsAdd)).perform(click());
        onView(withId(R.id.editTextSearchBar)).perform(typeText(serieName), pressImeActionButton(), closeSoftKeyboard());
        onView(allOf(withId(R.id.textViewAddTitle), withText(serieName))).perform(click());
        onView(withId(R.id.buttonNegative)).perform(click());

        getActivity().finish();

        onView(allOf(withId(R.id.seriesname), withText(serieName))).check(doesNotExist());
    }

    private static Activity getActivity() {
        final Activity[] currentActivity = new Activity[1];
        onView(allOf(withId(android.R.id.content), isDisplayed())).perform(new ViewAction() {
            @Override
            public Matcher getConstraints() {
                return isAssignableFrom(View.class);
            }

            @Override
            public String getDescription() {
                return "Getting activity";
            }

            @Override
            public void perform(UiController uiController, View view) {
                if (view.getContext() instanceof Activity) {
                    Activity activity1 = ((Activity) view.getContext());
                    currentActivity[0] = activity1;
                }
            }
        });
        return currentActivity[0];
    }

}
