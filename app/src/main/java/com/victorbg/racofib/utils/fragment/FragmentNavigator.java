package com.victorbg.racofib.utils.fragment;

import android.app.Activity;

import com.victorbg.racofib.R;
import com.victorbg.racofib.view.base.BaseFragment;
import com.victorbg.racofib.view.ui.grades.GradesFragment;
import com.victorbg.racofib.view.ui.home.HomeFragment;
import com.victorbg.racofib.view.ui.notes.NotesFragment;
import com.victorbg.racofib.view.ui.schedule.ScheduleFragment;
import com.victorbg.racofib.view.ui.subjects.SubjectsFragment;

import java.util.Objects;
import java.util.Stack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FragmentNavigator {

    private static final String HOME = "HomeFragment";
    private static final String NOTES = "NotesFragment";
    private static final String SCHEDULE = "ScheduleFragment";
    private static final String SUBJECTS = "SubjectsFragment";
    private static final String GRADES = "GradesFragment";


    private final FragmentManager fragmentManager;
    private BaseFragment selectedFragment;

    private Stack<Integer> fragmentsIds = new Stack<>();
    private String lastTagSelected = null;


    public FragmentNavigator(Activity activity) {
        this.fragmentManager = ((AppCompatActivity) activity).getSupportFragmentManager();
    }


    public boolean propagateBackClick() {
        if (selectedFragment == null) return false;
        return selectedFragment.onBackPressed();
    }

    public boolean popBack() {

        if (fragmentsIds.empty()) {
            return false;
        } else {
            fragmentsIds.pop();
            if (fragmentsIds.empty()) {
                return false;
            } else {
                replaceFragment(fragmentsIds.pop());
                return true;
            }
        }
    }

    public int getCurrentFragmentId() {
        if (fragmentsIds.empty()) return 0;
        return fragmentsIds.peek();
    }

    public void replaceFragment(int id) {

        fragmentsIds.push(id);

        switch (id) {
            case R.id.homeFragment:
            default:
                replaceFragment(HOME);
                break;
            case R.id.notesFragment:
                replaceFragment(NOTES);
                break;
            case R.id.timetableFragment:
                replaceFragment(SCHEDULE);
                break;
            case R.id.subjectsFragment:
                replaceFragment(SUBJECTS);
                break;
            case R.id.gradesFragment:
                replaceFragment(GRADES);
                break;
        }
    }

    public void replaceFragment(String tag) {

        //Perform operations to hide/show and add the fragment.

        Fragment fr = fragmentManager.findFragmentByTag(tag);


        if (fr != null) {
            fragmentManager.beginTransaction().show(fr).commit();
        } else {
            fr = getFragment(tag);
            fragmentManager.beginTransaction().add(R.id.contentContainer, fr, tag).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
        }

        if (lastTagSelected != null) {
            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(lastTagSelected)).commit();
        }

        selectedFragment = (BaseFragment) fr;
        lastTagSelected = tag;

        if (!Objects.equals(tag, HOME) && fragmentManager.findFragmentByTag(HOME) != null) {
            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(HOME)).commit();
            return;
        }

        if (!Objects.equals(tag, NOTES) && fragmentManager.findFragmentByTag(NOTES) != null) {
            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(NOTES)).commit();
            return;
        }

        if (!Objects.equals(tag, SCHEDULE) && fragmentManager.findFragmentByTag(SCHEDULE) != null) {
            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(SCHEDULE)).commit();
            return;
        }

        if (!Objects.equals(tag, SUBJECTS) && fragmentManager.findFragmentByTag(SUBJECTS) != null) {
            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(SUBJECTS)).commit();
            return;
        }

        if (!Objects.equals(tag, GRADES) && fragmentManager.findFragmentByTag(GRADES) != null) {
            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(GRADES)).commit();
            return;
        }
    }


    private BaseFragment getFragment(String key) {
        switch (key) {
            default:
            case HOME:
                return new HomeFragment();
            case NOTES:
                return new NotesFragment();
            case SCHEDULE:
                return new ScheduleFragment();
            case SUBJECTS:
                return new SubjectsFragment();
            case GRADES:
                return new GradesFragment();
        }
    }

    public Fragment getSelectedFragment() {
        return selectedFragment;
    }

    public void onFabSelected() {
        ((BaseFragment) selectedFragment).onFabSelected();
    }

    public void onQuery(String query) {
        ((BaseFragment) selectedFragment).onQuery(query);
    }

    public void onFilterSelected() {
        ((BaseFragment) selectedFragment).onFilterSelected();
    }
}
