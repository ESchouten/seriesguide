package com.battlelancer.seriesguide.test;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
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
public class AutomaticBackups {

    private UiDevice device;

    @Rule
    public ActivityTestRule<ShowsActivity> showsActivityActivityTestRule = new ActivityTestRule<ShowsActivity>(ShowsActivity.class);

    @Test
    public void Test1_turnBackupsOnWithoutPermission() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        onView(withContentDescription(R.string.drawer_open)).perform(click());
        onView(withText(R.string.preferences)).perform(click());
        onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(8).perform(scrollTo(), click());
        onView(withId(R.id.switchAutoBackup)).perform(click());
        onView(withId(R.id.switchAutoBackup)).perform(click());

        UiObject2 btnDeny = device.wait(Until.findObject(By.res("com.android.packageinstaller", "permission_deny_button")),
                500);
        btnDeny.click();

        onView(allOf(withId(android.support.design.R.id.snackbar_text), withText(R.string.autobackup_permission_missing)))
                .check(matches(isDisplayed()));
        onView(withId(R.id.switchAutoBackup)).check(matches(not(isChecked())));

        getActivity().finish();
    }

    @Test
    public void Test2_turnBackupsOnWithPermission() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        onView(withContentDescription(R.string.drawer_open)).perform(click());
        onView(withText(R.string.preferences)).perform(click());
        onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(8).perform(scrollTo(), click());
        onView(withId(R.id.switchAutoBackup)).perform(click());

        UiObject2 btnAllow = device.wait(Until.findObject(By.res("com.android.packageinstaller", "permission_allow_button")),
                500);
        btnAllow.click();

        onView(withId(R.id.switchAutoBackup)).check(matches(isChecked()));
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
