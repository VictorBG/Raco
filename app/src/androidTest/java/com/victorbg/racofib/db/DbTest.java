package com.victorbg.racofib.db;

import com.victorbg.racofib.data.database.AppDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.InstrumentationRegistry;

abstract public class DbTest {
    protected AppDatabase db;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void initDb() {
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDatabase.class).build();
    }

    @After
    public void closeDb() {
        db.close();
    }
}