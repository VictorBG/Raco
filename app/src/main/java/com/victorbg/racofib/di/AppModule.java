package com.victorbg.racofib.di;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;

import com.victorbg.racofib.AppRaco;
import com.victorbg.racofib.data.api.ApiManager;
import com.victorbg.racofib.data.api.ApiService;
import com.victorbg.racofib.data.database.AppDatabase;
import com.victorbg.racofib.data.database.dao.ExamDao;
import com.victorbg.racofib.data.database.dao.NotesDao;
import com.victorbg.racofib.data.database.dao.SubjectScheduleDao;
import com.victorbg.racofib.data.database.dao.SubjectsDao;
import com.victorbg.racofib.data.database.dao.UserDao;
import com.victorbg.racofib.data.sp.PrefManager;

import javax.inject.Singleton;

import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;
import dagger.Module;
import dagger.Provides;

@Module(subcomponents = ViewModelSubcomponent.class)
public class AppModule {

    @Singleton
    @Provides
    public AppDatabase provideAppDatabase(Application context) {
        return Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "racofib-database")
                .allowMainThreadQueries()
                .build();
    }

    @Singleton
    @Provides
    public ApiService provideApi(PrefManager prefManager) {
        return ApiManager.create(prefManager);
    }

    @Singleton
    @Provides
    public UserDao getUserDao(AppDatabase appDatabase) {
        return appDatabase.userDao();
    }

    @Singleton
    @Provides
    public SubjectsDao getSubjectsDao(AppDatabase appDatabase) {
        return appDatabase.subjectsDao();
    }

    @Singleton
    @Provides
    public SubjectScheduleDao getSubjectScheduleDao(AppDatabase appDatabase) {
        return appDatabase.subjectScheduleDao();
    }

    @Singleton
    @Provides
    public NotesDao getNotesDao(AppDatabase appDatabase) {
        return appDatabase.notesDao();
    }

    @Singleton
    @Provides
    public ExamDao getExamDao(AppDatabase appDatabase) {
        return appDatabase.examDao();
    }

    @Singleton
    @Provides
    ViewModelProvider.Factory provideViewModelFactory(
            ViewModelSubcomponent.Builder viewModelSubComponent) {
        return new AppViewModelFactory(viewModelSubComponent.build());
    }

    @Provides
    AppRaco provideAppRaco(Application appRaco) {
        return (AppRaco) appRaco;
    }

    @Provides
    Context provideContext(Application appRaco) {
        return appRaco;
    }


}
