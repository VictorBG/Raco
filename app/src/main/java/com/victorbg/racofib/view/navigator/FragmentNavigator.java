package com.victorbg.racofib.view.navigator;

import android.content.Context;
import android.util.Log;

import com.victorbg.racofib.R;
import com.victorbg.racofib.view.ui.home.HomeFragment;
import com.victorbg.racofib.view.ui.notes.NotesFragment;

import java.util.ArrayDeque;

import androidx.annotation.IdRes;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FragmentNavigator {

    private static final String TAG = "FragmentNavigator";


    private FragmentManager mFragmentManager;
    private int mContainerId;
    private Toolbar toolbar;

    private ArrayDeque<Integer> mBackStack = new ArrayDeque<>();

    private int lastNavigatedId = -1;

    private Context context;

    private static final int enterAnim = R.anim.nav_default_enter_anim;
    private static final int exitAnim = R.anim.nav_default_exit_anim;
    private static final int popEnterAnim = R.anim.nav_default_pop_enter_anim;
    private static final int popExitAnim = R.anim.nav_default_pop_exit_anim;


    public FragmentNavigator(Context context, FragmentManager fragmentManager, int containerId, Toolbar toolbar) {
        this.context = context;
        this.mFragmentManager = fragmentManager;
        this.mContainerId = containerId;
        this.toolbar = toolbar;
    }

    public boolean navigate(@IdRes int destination) {
        if (mFragmentManager.isStateSaved()) {
            Log.i(TAG, "Ignoring navigate() call: FragmentManager has already"
                    + " saved its state");
            return false;
        }

        //Prevents recreation of fragments
        if (lastNavigatedId == destination) {
            return true;
        }
        lastNavigatedId = destination;


        final Fragment frag = createDestination(destination);

        if (frag == null) {
            Log.i(TAG, "Ignoring navigate() call: created fragment is null");
            return false;
        }

        final FragmentTransaction ft = mFragmentManager.beginTransaction();

        ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim);

        ft.replace(mContainerId, frag);
        ft.setPrimaryNavigationFragment(frag);

        ft.addToBackStack(Integer.toString(destination));

        ft.setReorderingAllowed(true);
        ft.commit();
        mBackStack.add(destination);
        setTitle(destination);

        return true;
    }

    public boolean popBackStack() {
        if (mBackStack.isEmpty()) {
            return false;
        }
        if (mFragmentManager.isStateSaved()) {
            Log.i(TAG, "Ignoring popBackStack() call: FragmentManager has already"
                    + " saved its state");
            return false;
        }
        boolean popped = false;
        if (mFragmentManager.getBackStackEntryCount() > 0) {
            mFragmentManager.popBackStack();
            popped = true;
            mBackStack.removeLast();
            if (mBackStack.size() > 0) {
                lastNavigatedId = mBackStack.getLast();
            } else {
                return false;
            }
            setTitle(lastNavigatedId);
        }

        return popped;
    }

    private Fragment createDestination(int destination) {
        switch (destination) {
            case R.id.notes_drawer:
                return new NotesFragment();
            case R.id.home_drawer:
                return new HomeFragment();
        }
        return null;
    }

    private void setTitle(int destination) {
        switch (destination) {
            case R.id.notes_drawer:
                toolbar.setTitle("Avisos");
                break;
            case R.id.home_drawer:
                toolbar.setTitle("Home");
                break;
        }
    }

    public void initialNavigate() {
        navigate(R.id.home_drawer);
        setTitle(R.id.home_drawer);
    }

    public long getLastItemId() {
        return lastNavigatedId;
    }
}
