package com.victorbg.racofib.data.database.dao;

import com.victorbg.racofib.data.model.user.User;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Single;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserDao {

    @Query("select * from RacoUsers ")
    Single<User> getUser();

    @Insert(onConflict = REPLACE)
    void insert(User user);

    @Delete
    void delete(User user);

    @Query("delete from RacoUsers")
    void truncate();
}
