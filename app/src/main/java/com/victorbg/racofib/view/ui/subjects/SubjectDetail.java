package com.victorbg.racofib.view.ui.subjects;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.victorbg.racofib.R;
import com.victorbg.racofib.data.model.subject.Subject;
import com.victorbg.racofib.data.repository.base.Resource;
import com.victorbg.racofib.databinding.ActivitySubjectBinding;
import com.victorbg.racofib.di.injector.Injectable;
import com.victorbg.racofib.view.base.BaseActivity;
import com.victorbg.racofib.view.ui.subjects.pager.SubjectActivitiesFragment;
import com.victorbg.racofib.view.ui.subjects.pager.SubjectContentsFragments;
import com.victorbg.racofib.view.ui.subjects.pager.SubjectInfoFragment;
import com.victorbg.racofib.view.widgets.ContentLoadingProgressBar;
import com.victorbg.racofib.viewmodel.SubjectDetailViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SubjectDetail extends BaseActivity implements Injectable {

  public static final String SUBJECT_OBJECT_KEY = "SubjectObject";

  @BindView(R.id.tabs)
  TabLayout tabLayout;

  @BindView(R.id.progress_subject)
  ContentLoadingProgressBar progressBar;

  @BindView(R.id.viewPager)
  ViewPager viewPager;

  @BindView(R.id.toolbar)
  Toolbar toolbar;

  @BindView(R.id.fab)
  FloatingActionButton fab;

  private Subject subject;

  @Inject ViewModelProvider.Factory viewModelFactory;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ActivitySubjectBinding binding =
        DataBindingUtil.setContentView(this, R.layout.activity_subject);

    ButterKnife.bind(this, binding.getRoot());

    SubjectDetailViewModel viewModel =
        ViewModelProviders.of(this, viewModelFactory).get(SubjectDetailViewModel.class);

    getSubject();

    binding.setSubject(subject);

    setSupportActionBar(toolbar);
    setTitle(null);

    viewModel.getSubject(subject.shortName).observe(this, this::handleSubjectResource);
  }

  private void getSubject() {
    if (getIntent().getExtras() != null
        && getIntent().getExtras().containsKey(SUBJECT_OBJECT_KEY)) {
      subject = getIntent().getExtras().getParcelable(SUBJECT_OBJECT_KEY);
    }

    if (subject == null) {
      Toast.makeText(this, getString(R.string.error_retrieving_subject_data), Toast.LENGTH_SHORT)
          .show();
      finish();
    }
  }

  private void handleSubjectResource(Resource<Subject> resource) {
    switch (resource.status) {
      case LOADING:
        progressBar.show();
        break;
      case ERROR:
        progressBar.hide();
        Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
        break;
      case SUCCESS:
        progressBar.hide();
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

    SubjectPagerAdapter adapter = new SubjectPagerAdapter(getSupportFragmentManager(), fragments);
    viewPager.setAdapter(adapter);
    tabLayout.setupWithViewPager(viewPager);
  }

  private class SubjectPagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> fragments;

    SubjectPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
      super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
      this.fragments = fragments;
    }

    @NotNull
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

  @OnClick(R.id.back_button)
  public void back(View v) {
    finishAfterTransition();
  }

  @Override
  protected int getLightTheme() {
    return R.style.AppTheme_SubjectDetail_Light;
  }

  @Override
  protected int getDarkTheme() {
    return R.style.AppTheme_SubjectDetail_Dark;
  }
}
