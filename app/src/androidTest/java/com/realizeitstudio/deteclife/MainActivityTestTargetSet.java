package com.realizeitstudio.deteclife;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTestTargetSet {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void mainActivityTestAddTarget() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        ViewInteraction appCompatButton = onView(
//                allOf(withId(R.id.button_record_view_statistics), withText("View Statistics"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(R.id.linearlayout_main_container),
//                                        0),
//                                3),
//                        isDisplayed()));
//        appCompatButton.perform(click())
// ;
        // 1. 進入 statistic page
        onView(withId(R.id.button_record_view_statistics)).perform(click());


        // 2. 進入 plan page
        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.navigation_plan),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.navbutton_main),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        // 2-1. 增加一個新項目
        // (1) 點下 top item 變成 edit mode
        ViewInteraction constraintLayout = onView(
                allOf(withId(R.id.constraintlayout_plan_top_viewmode),
                        childAtPosition(
                                allOf(withId(R.id.constraintlayout_plan_top_item),
                                        childAtPosition(
                                                withId(R.id.recyclerview_plan_daily),
                                                0)),
                                0),
                        isDisplayed()));
        constraintLayout.perform(click());

        // (2) 點下 edit icon 選擇 task
        ViewInteraction frameLayout = onView(
                allOf(withId(R.id.framelayout_plan_top_editmode_icon),
                        childAtPosition(
                                allOf(withId(R.id.constraintlayout_plan_top_editmode),
                                        childAtPosition(
                                                withId(R.id.constraintlayout_plan_top_item),
                                                1)),
                                4),
                        isDisplayed()));
        frameLayout.perform(click());


        // (3) 選擇一個 task (用 position 加)
//        ViewInteraction recyclerView = onView(
//                allOf(withId(R.id.recyclerview_tasklist),
//                        childAtPosition(
//                                withClassName(is("android.support.constraint.ConstraintLayout")),
//                                1)));
//        recyclerView.perform(actionOnItemAtPosition(1, click()));

        // (3) 選擇 Eat 的 task
        // 並回到 set target 的第一頁
        onView(allOf(withId(R.id.textview_categorytask_task_name), withText("Eat"))).perform(click());


        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.imageview_plan_top_editmode_save),
                        childAtPosition(
                                allOf(withId(R.id.constraintlayout_plan_top_editmode),
                                        childAtPosition(
                                                withId(R.id.constraintlayout_plan_top_item),
                                                1)),
                                1),
                        isDisplayed()));
        appCompatImageView.perform(click());


        // ************************ Check result of set target ************************ //
//        onView(withId(R.id.recyclerview_plan_daily)).check(matches(isDisplayed()));
//        onView(withId(R.id.constraintlayout_plan_main_item)).check(matches(isDisplayed()));
//        onView(withId(R.id.textview_plan_task_name)).check(matches(isDisplayed()));

        onView(allOf(withId(R.id.textview_plan_task_name), withText("Eat"))).check(matches(isDisplayed()));


        /* (if not found) message:
            (with id: com.realizeitstudio.deteclife:id/textview_plan_task_name and with text: is "Sleep")'
            matches multiple views in the hierarchy.
         *
         * (e.g) 下面這句會 fail
         * onView(allOf(withId(R.id.textview_plan_task_name), withText("Sleep"))).check(matches(not(isDisplayed())));
        **/

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
