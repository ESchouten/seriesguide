package com.battlelancer.seriesguide.test;

import android.app.Activity;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.rule.ActivityTestRule;
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

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PreventSpoilers {

    @Rule
    public ActivityTestRule<ShowsActivity> showsActivityActivityTestRule = new ActivityTestRule<ShowsActivity>(ShowsActivity.class);

    private String serieName = "Prison Break";

    @Test
    public void Test1_turnSpoilerPreventingOn() {
        onView(withContentDescription(R.string.drawer_open)).perform(click());
        onView(withText(R.string.preferences)).perform(click());
        onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(14).perform(scrollTo(), click());
        onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(7).perform(scrollTo(), click());

        getActivity().finish();

        onView(allOf(withId(R.id.seriesname), withText(serieName))).perform(click());
        onView(withText(R.string.no_spoilers)).check(matches(isDisplayed()));

        getActivity().finish();
    }

    @Test
    public void Test2_turnSpoilerPreventingOff() {
        onView(withContentDescription(R.string.drawer_open)).perform(click());
        onView(withText(R.string.preferences)).perform(click());
        onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(14).perform(scrollTo(), click());
        onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(7).perform(scrollTo(), click());

        getActivity().finish();

        onView(allOf(withId(R.id.seriesname), withText(serieName))).perform(click());
        onView(withText(R.string.no_spoilers)).check(doesNotExist());

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

}
