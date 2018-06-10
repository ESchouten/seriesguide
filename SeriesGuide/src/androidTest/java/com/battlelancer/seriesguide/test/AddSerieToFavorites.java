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
import android.support.test.uiautomator.UiDevice;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.widget.GridView;

import com.battlelancer.seriesguide.R;
import com.battlelancer.seriesguide.ui.ShowsActivity;

import org.hamcrest.Matcher;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AddSerieToFavorites {

    @Rule
    public ActivityTestRule<ShowsActivity> showsActivityActivityTestRule = new ActivityTestRule<ShowsActivity>(ShowsActivity.class);

    private String serieNameTest1 = "Prison Break";
    private String serieNameTest2 = "Game of Thrones";
    private String serieNameTest3 = "House";

    @Test
    public void Test1_addSerieToFavoritesList() {
        onView(withId(R.id.buttonShowsAdd)).perform(click());
        onView(withId(R.id.editTextSearchBar)).perform(typeText(serieNameTest1), pressImeActionButton(), closeSoftKeyboard());
        onView(allOf(withId(R.id.textViewAddTitle), withText(serieNameTest1))).perform(click());
        onView(withId(R.id.buttonPositive)).perform(click());

        getActivity().finish();

        onView(allOf(withId(R.id.seriesname), withText(serieNameTest1))).check(matches(isDisplayed()));
    }

    @Test
    public void Test2_systemCanNotLoadSerieList() {
        turnWifiOn(false);

        onView(withId(R.id.buttonShowsAdd)).perform(click());
        onView(withId(R.id.editTextSearchBar)).perform(typeText(serieNameTest2), pressImeActionButton(), closeSoftKeyboard());
        onView(withId(R.id.emptyViewAdd)).check(matches(isDisplayed()));

        turnWifiOn(true);

        getActivity().finish();
    }

    @Test
    public void Test3_cancelAddingSerieToFavoritesList() {
        onView(withId(R.id.buttonShowsAdd)).perform(click());
        onView(withId(R.id.editTextSearchBar)).perform(typeText(serieNameTest3), pressImeActionButton(), closeSoftKeyboard());
        onView(allOf(withId(R.id.textViewAddTitle), withText(serieNameTest3))).perform(click());
        onView(withId(R.id.buttonNegative)).perform(click());

        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mDevice.pressBack();
        onView(allOf(withId(R.id.seriesname), withText(serieNameTest3))).check(doesNotExist());

        getActivity().finish();
    }

    @Test
    public void Test4_canNotAddSerieToFavoritesList() {
        onView(withId(R.id.buttonShowsAdd)).perform(click());
        onView(withId(R.id.editTextSearchBar)).perform(typeText(serieNameTest1), pressImeActionButton(), closeSoftKeyboard());
        onView(allOf(withId(R.id.textViewAddTitle), withText(serieNameTest1))).perform(click());
        onView(withId(R.id.buttonNegative)).check(doesNotExist());

        getActivity().finish();
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

    private void turnWifiOn(boolean state) {
        WifiManager wifi = (WifiManager) showsActivityActivityTestRule.getActivity().getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(state);
    }

}
