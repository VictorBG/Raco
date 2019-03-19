package com.victorbg.racofib.utils.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.SparseArray;

import com.victorbg.racofib.R;
import com.victorbg.racofib.utils.ConsumableBoolean;
import com.victorbg.racofib.view.base.BaseFragment;

import java.util.Stack;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import timber.log.Timber;

public class FragNav {

    private final FragmentManager fragmentManager;
    private final Context context;
    private BaseFragment selectedFragment;

    private Stack<Integer> fragmentsIds = new Stack<>();
    private SparseArray<Fragment> fragmentArray = new SparseArray<>();
    private String lastTagSelected = null;

    private boolean overrideFragment = false;

    private final ConsumableBoolean popNextTransaction = new ConsumableBoolean(false);

    public FragNav(Activity activity) {
        this.fragmentManager = ((AppCompatActivity) activity).getSupportFragmentManager();
        this.context = activity;
    }

    public FragNav setOverride(boolean override) {
        this.overrideFragment = override;
        return this;
    }

    public FragNav addFragment(int id, Fragment fragment) {
        internalAddFragment(id, fragment);
        return this;
    }

    private void internalAddFragment(int id, Fragment fragment) {
        if (fragmentArray.indexOfKey(id) < 0) {
            fragmentArray.put(id, fragment);
        } else {
            if (overrideFragment) {
                fragmentArray.put(id, fragment);
            } else {
                Timber.w("A fragment with that ID already exists. If you want to override it, call setOverride(true)");
            }
        }
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

    public void replaceByFragment(int id, Fragment fragment) {
        fragmentsIds.push(id);
        String tag = context.getResources().getResourceEntryName(id);
        fragmentManager.beginTransaction().add(R.id.contentContainer, fragment, tag).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
        popNextTransaction.setValue(true);
        lastTagSelected = tag;
        selectedFragment = (BaseFragment) fragment;
    }

    public void replaceFragment(int id) {
        replaceFragment(id, null);
    }

    public void replaceFragment(int id, @Nullable Bundle arguments) {

        if (fragmentArray.indexOfKey(id) < 0) {
            Timber.w("Trying to navigate to a non existent id");
            return;
        }

        if (!fragmentsIds.empty() && fragmentsIds.peek() == id) {
            Timber.w("Trying to navigate to the most recent fragment, cannot be added again");
            return;
        }

        fragmentsIds.push(id);

        Fragment arrayFragment = fragmentArray.get(id);
        String tag = context.getResources().getResourceEntryName(id);

        Fragment fr = fragmentManager.findFragmentByTag(tag);

        if (fr != null) {
            if (arguments != null) {
                fr.setArguments(arguments);
            }
            fragmentManager.beginTransaction().show(fr).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
        } else {
            if (arguments != null) {
                arrayFragment.setArguments(arguments);
            }
            fragmentManager.beginTransaction().add(R.id.contentContainer, arrayFragment, tag).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
        }

        if (lastTagSelected != null) {
            if (popNextTransaction.getValue()) {
                fragmentManager.beginTransaction().detach(fragmentManager.findFragmentByTag(lastTagSelected)).commit();
            } else {
                fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(lastTagSelected)).commit();
            }
        }

        selectedFragment = (BaseFragment) (fr == null ? arrayFragment : fr);
        lastTagSelected = tag;
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
