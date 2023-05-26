package com.mca.taskmaster.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.mca.taskmaster.dao.TaskDao;
import com.mca.taskmaster.models.Task;

@Database(entities = {Task.class}, version = 1)
public abstract class TaskmasterDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();
}
