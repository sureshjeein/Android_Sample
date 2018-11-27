package com.jpmc.samplealbum.samplealbumlist.roomdatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM task")
    List<Task> getAll();

    @Insert
    void insert(Task task);

    @Query("DELETE FROM task")
    void deleteTable();
}
