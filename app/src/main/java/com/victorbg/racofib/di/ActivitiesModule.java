package com.victorbg.racofib.di;

import com.victorbg.racofib.view.MainActivity;
import com.victorbg.racofib.view.StartActivity;
import com.victorbg.racofib.view.base.BaseActivity;
import com.victorbg.racofib.view.base.BaseThemeActivity;
import com.victorbg.racofib.view.ui.exams.DialogExamDetail;
import com.victorbg.racofib.view.ui.grades.GradeDialog;
import com.victorbg.racofib.view.ui.login.LoginActivity;
import com.victorbg.racofib.view.ui.notes.NotesFavoritesActivity;
import com.victorbg.racofib.view.ui.settings.ColorSettingsActivity;
import com.victorbg.racofib.view.ui.settings.SettingsActivity;
import com.victorbg.racofib.view.ui.subjects.SubjectDetail;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivitiesModule {
    @ContributesAndroidInjector(modules = FragmentBuilderModule.class)
    abstract MainActivity contributeMainActivity();

    @ContributesAndroidInjector
    abstract StartActivity contirbuteStartActivity();

    @ContributesAndroidInjector
    abstract LoginActivity contirbuteLoginActivity();

    @ContributesAndroidInjector
    abstract SubjectDetail contirbuteSubjectDetail();

    @ContributesAndroidInjector
    abstract NotesFavoritesActivity contirbuteNotesFavoritesActivity();

    @ContributesAndroidInjector
    abstract BaseThemeActivity contirbuteBaseThemeActivity();

    @ContributesAndroidInjector
    abstract BaseActivity contirbuteBaseActivity();

    @ContributesAndroidInjector
    abstract SettingsActivity contirbuteSettingsActivity();

    @ContributesAndroidInjector
    abstract ColorSettingsActivity contirbuteColorSettingsActivity();

    @ContributesAndroidInjector
    abstract DialogExamDetail contirbuteDialogExamDetail();

    @ContributesAndroidInjector
    abstract GradeDialog contirbuteGradeDialog();

}
