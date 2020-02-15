package com.victorbg.racofib.view.ui.main;

import android.util.SparseArray;
import android.view.View;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.victorbg.racofib.R;
import com.victorbg.racofib.view.widgets.bottom.BottomBarNavigator;

public class MainBottomBarRules {

  public static SparseArray<BottomBarNavigator.Rule> getMainActivityRules() {
    SparseArray<BottomBarNavigator.Rule> result = new SparseArray<>();

    result.put(
        R.id.homeFragment,
        new BottomBarNavigator.Rule(
            BottomAppBar.FAB_ALIGNMENT_MODE_CENTER,
            BottomBarNavigator.NAVIGATION_MODE_NAVIGATION,
            R.menu.main_menu,
            R.drawable.ic_add_black_24dp,
            View.GONE));

    result.put(
        R.id.subjectsFragment,
        new BottomBarNavigator.Rule(
            BottomAppBar.FAB_ALIGNMENT_MODE_CENTER,
            BottomBarNavigator.NAVIGATION_MODE_NAVIGATION,
            R.menu.main_menu,
            R.drawable.ic_add_black_24dp,
            View.GONE));

    result.put(
        R.id.scheduleFragment,
        new BottomBarNavigator.Rule(
            BottomAppBar.FAB_ALIGNMENT_MODE_CENTER,
            BottomBarNavigator.NAVIGATION_MODE_NAVIGATION,
            R.menu.main_menu,
            R.drawable.ic_add_black_24dp,
            View.GONE));

    result.put(
        R.id.notesFragment,
        new BottomBarNavigator.Rule(
            BottomAppBar.FAB_ALIGNMENT_MODE_CENTER,
            BottomBarNavigator.NAVIGATION_MODE_NAVIGATION,
            R.menu.notes_menu,
            R.drawable.ic_add_black_24dp,
            View.GONE));

    result.put(
        R.id.gradesFragment,
        new BottomBarNavigator.Rule(
            BottomAppBar.FAB_ALIGNMENT_MODE_CENTER,
            BottomBarNavigator.NAVIGATION_MODE_NAVIGATION,
            R.menu.main_menu,
            R.drawable.ic_add_black_24dp,
            View.VISIBLE));

    result.put(
        R.id.allExamsFragment,
        new BottomBarNavigator.Rule(
            BottomAppBar.FAB_ALIGNMENT_MODE_CENTER,
            BottomBarNavigator.NAVIGATION_MODE_BACK,
            R.menu.main_menu,
            R.drawable.ic_add_black_24dp,
            View.GONE));

    result.put(
        R.id.subjectDetailFragment,
        new BottomBarNavigator.Rule(
            BottomAppBar.FAB_ALIGNMENT_MODE_CENTER,
            BottomBarNavigator.NAVIGATION_MODE_BACK,
            R.menu.main_menu,
            R.drawable.ic_add_black_24dp,
            View.GONE));

    result.put(
        R.id.noteDetailFragment,
        new BottomBarNavigator.Rule(
            BottomAppBar.FAB_ALIGNMENT_MODE_END,
            BottomBarNavigator.NAVIGATION_MODE_BACK,
            R.menu.main_menu,
            R.drawable.ic_favorite_border_white,
            View.GONE));

    return result;
  }
}
