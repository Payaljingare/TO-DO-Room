package com.example.to_doapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    Task getTaskById(int taskId);

    @Query("SELECT * FROM tasks")
    List<Task> getAllTasks();

    @Query("DELETE FROM tasks WHERE id IN (:taskIds)")
    void deleteTasks(List<Integer> taskIds); // Add this method
}
