package com.victorbg.racofib.view.ui.subjects;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.databinding.ActivitySubjectBinding;
import com.victorbg.racofib.databinding.FragmentSubjectBinding;
import com.victorbg.racofib.di.injector.Injectable;
import com.victorbg.racofib.view.base.BaseFragment;
import com.victorbg.racofib.view.ui.subjects.pager.SubjectActivitiesFragment;
import com.victorbg.racofib.view.ui.subjects.pager.SubjectContentsFragments;
import com.victorbg.racofib.view.ui.subjects.pager.SubjectInfoFragment;
import com.victorbg.racofib.view.widgets.ContentLoadingProgressBar;
import com.victorbg.racofib.viewmodel.SubjectDetailViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class SubjectDetailFragment extends BaseFragment implements Injectable {

    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.progress_subject)
    ContentLoadingProgressBar progressBar;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    private Subject subject;

    private FragmentSubjectBinding binding;
    private SubjectDetailViewModel viewModel;
    private LiveData<Resource<Subject>> subjectLiveData;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_subject, container, false);

        ButterKnife.bind(this, binding.getRoot());

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SubjectDetailViewModel.class);

        if (!getSubject()) {
            Toast.makeText(getContext(), getString(R.string.error_retrieving_subject_data), Toast.LENGTH_SHORT).show();
            getMainActivity().popBack();
        }

        subjectLiveData = viewModel.getSubject(subject.shortName);
        subjectLiveData.observe(this, this::handleSubjectResource);

        binding.setSubject(subject);

        return binding.getRoot();
    }

    private boolean getSubject() {
        subject =  SubjectDetailFragmentArgs.fromBundle(getArguments()).getSubject();
        return true;
    }

    private void handleSubjectResource(Resource<Subject> resource) {
        switch (resource.status) {
            case LOADING:
                progressBar.show();
                break;
            case ERROR:
                progressBar.hide();
                Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                break;
            case SUCCESS:
                progressBar.hide();
                //Copy color from the local subject object
                resource.data.color = subject.color;
                populateViewPager(resource.data);
                break;
        }
    }

    private void populateViewPager(Subject subject) {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(SubjectInfoFragment.newInstance(subject));
//        fragments.add(SubjectGradesFragment.newInstance(subject.shortName));
        fragments.add(SubjectContentsFragments.newInstance(subject));
        fragments.add(SubjectActivitiesFragment.newInstance(subject));

        SubjectPagerAdapter adapter = new SubjectPagerAdapter(getActivity().getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
//                    case 1:
//                        fab.show();
//                        break;
                    default:


                }
            }
        });
    }


    private class SubjectPagerAdapter extends FragmentStatePagerAdapter {

        private final List<Fragment> fragments;

        public SubjectPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                default:
                case 0:
                    return getString(R.string.info_subject_tab_title);
//                case 1:
//                    return "Notas";
                case 1:
                    return getString(R.string.content_subject_tab_title);
                case 2:
                    return getString(R.string.activities_subject_tab_title);
            }
        }
    }

}
