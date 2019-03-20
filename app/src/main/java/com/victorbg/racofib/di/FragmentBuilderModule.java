package com.victorbg.racofib.di;

import com.victorbg.racofib.view.ProfileModal;
import com.victorbg.racofib.view.ui.exams.FragmentAllExams;
import com.victorbg.racofib.view.ui.grades.GradesFragment;
import com.victorbg.racofib.view.ui.home.HomeFragment;
import com.victorbg.racofib.view.ui.main.MainBottomNavigationView;
import com.victorbg.racofib.view.ui.notes.NotesFragment;
import com.victorbg.racofib.view.ui.schedule.ScheduleFragment;
import com.victorbg.racofib.view.ui.subjects.SubjectDetailFragment;
import com.victorbg.racofib.view.ui.subjects.SubjectsFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuilderModule {
    @ContributesAndroidInjector
    abstract NotesFragment contributeNotesFragment();

    @ContributesAndroidInjector
    abstract HomeFragment contributeHomeFragment();

    @ContributesAndroidInjector
    abstract SubjectsFragment contributeSubjectsFragment();

    @ContributesAndroidInjector
    abstract ScheduleFragment contributeScheduleFragment();

    @ContributesAndroidInjector
    abstract ProfileModal contributeProfileModal();

    @ContributesAndroidInjector
    abstract MainBottomNavigationView contributeMainBottomNavigationView();

    @ContributesAndroidInjector
    abstract FragmentAllExams contributeFragmentAllExams();

    @ContributesAndroidInjector
    abstract SubjectDetailFragment contributeSubjectDetailFragment();

    @ContributesAndroidInjector
    abstract GradesFragment contributeGradesFragment();

}
