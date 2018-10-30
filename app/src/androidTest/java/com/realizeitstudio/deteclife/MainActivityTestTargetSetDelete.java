package com.realizeitstudio.deteclife;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.*;
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
public class MainActivityTestTargetSetDelete {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void mainActivityTestRecordA() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 1. 進入 statistic page
        onView(withId(R.id.button_record_view_statistics)).perform(click());


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 2. 進入 plan page
        onView(
                allOf(withId(R.id.navigation_plan),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.navbutton_main),
                                        0),
                                1),
                        isDisplayed())).perform(click());

        // 2-1. 增加一個新項目
        // (1) 點下 top item 變成 edit mode
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ViewInteraction constraintLayoutEditAdd = onView(
                allOf(withId(R.id.constraintlayout_plan_top_viewmode),
                        childAtPosition(
                                allOf(withId(R.id.constraintlayout_plan_top_item),
                                        childAtPosition(
                                                withId(R.id.recyclerview_plan_daily),
                                                0)),
                                0),
                        isDisplayed()));
        constraintLayoutEditAdd.perform(click());

        // (2) 點下 edit icon 選擇 task
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

        // (3) 選擇 tasklist 的第一個，並回到 set target 的第一頁
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.recyclerview_tasklist),
                        childAtPosition(
                                withClassName(is("android.support.constraint.ConstraintLayout")),
                                1)));
        recyclerView.perform(actionOnItemAtPosition(1, click()));

//        // (4) 選擇 seekbar 的時間
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        ViewInteraction recyclerViewTopSetSeekbar = onView(
//                allOf(withId(R.id.recyclerview_tasklist),
//                        childAtPosition(
//                                withClassName(is("android.support.constraint.ConstraintLayout")),
//                                1)));
//        recyclerViewTopSetSeekbar.perform(actionOnItemAtPosition(1, click()));

        // (5) save
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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


        // 2-2. 刪除一個項目
        // (1)點下 top item 變成 edit mode
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ViewInteraction constraintLayoutEditDelete = onView(
                allOf(withId(R.id.constraintlayout_plan_top_viewmode),
                        childAtPosition(
                                allOf(withId(R.id.constraintlayout_plan_top_item),
                                        childAtPosition(
                                                withId(R.id.recyclerview_plan_daily),
                                                0)),
                                0),
                        isDisplayed()));
        constraintLayoutEditDelete.perform(click());

        // (2) 按下垃圾桶
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        onView(withId(R.id.button_record_view_statistics)).perform(click());

        ViewInteraction appCompatImageViewEditDelete = onView(
                allOf(withId(R.id.imageview_plan_task_delete_hint),
                        childAtPosition(
                                allOf(withId(R.id.framelayout_plan_task_delete_hint),
                                        childAtPosition(
                                                allOf(withId(R.id.constraintlayout_plan_main_item),
                                                        childAtPosition(
                                                                withId(R.id.recyclerview_plan_daily),
                                                                2)),
                                                0)),
                                0)));

        appCompatImageViewEditDelete.perform(click());

        // (3) 按下 save
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ViewInteraction appCompatImageViewDeleteSave = onView(
                allOf(withId(R.id.imageview_plan_top_editmode_save),
                        childAtPosition(
                                allOf(withId(R.id.constraintlayout_plan_top_editmode),
                                        childAtPosition(
                                                withId(R.id.constraintlayout_plan_top_item),
                                                1)),
                                1),
                        isDisplayed()));
        appCompatImageViewDeleteSave.perform(click());


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
