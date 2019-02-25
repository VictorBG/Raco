package com.victorbg.racofib.di;

import com.victorbg.racofib.view.MainActivity;
import com.victorbg.racofib.view.StartActivity;
import com.victorbg.racofib.view.base.BaseActivity;
import com.victorbg.racofib.view.base.BaseThemeActivity;
import com.victorbg.racofib.view.ui.exams.AllExamsActivity;
import com.victorbg.racofib.view.ui.exams.ExamDetail;
import com.victorbg.racofib.view.ui.login.LoginActivity;
import com.victorbg.racofib.view.ui.notes.NoteDetail;
import com.victorbg.racofib.view.ui.notes.NotesFavoritesActivity;
import com.victorbg.racofib.view.ui.settings.SettingsActivity;
import com.victorbg.racofib.view.ui.subjects.SubjectDetail;
import com.victorbg.racofib.view.widgets.bottom.MaterialBottomSheetDialogFragment;

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
    abstract NoteDetail contirbuteNoteDetail();

    @ContributesAndroidInjector
    abstract BaseThemeActivity contirbuteBaseThemeActivity();

    @ContributesAndroidInjector
    abstract BaseActivity contirbuteBaseActivity();

    @ContributesAndroidInjector
    abstract ExamDetail contirbuteExamDetail();

    @ContributesAndroidInjector
    abstract AllExamsActivity contirbuteAllExamsActivity();

    @ContributesAndroidInjector
    abstract SettingsActivity contirbuteSettingsActivity();

}
