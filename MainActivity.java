package com.example.to_doapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.to_doapp.AddEditTaskActivity;
import com.example.to_doapp.AppDatabase;
import com.example.to_doapp.TaskAdapter;

import java.util.List;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private AppDatabase appDatabase;
    private Button deleteTasksButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(this);
        recyclerView.setAdapter(taskAdapter);

        appDatabase = AppDatabase.getInstance(this);
        loadTasks();

        Button addTaskButton = findViewById(R.id.addTaskButton);
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        deleteTasksButton = findViewById(R.id.deleteTasksButton);
        deleteTasksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelectedTasks();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadTasks();
        }
    }

    private void loadTasks() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Task> tasks = appDatabase.taskDao().getAllTasks();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        taskAdapter.setTasks(tasks);
                        logTasks(); // Call logTasks to log the current state of the database
                    }
                });
            }
        }).start();
    }

    private void logTasks() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Task> tasks = appDatabase.taskDao().getAllTasks();
                for (Task task : tasks) {
                    Log.d("MainActivity", "Task ID: " + task.getId() + ", Title: " + task.getTitle()
                            + ", Description: " + task.getDescription() + ", Due Date: " + task.getDueDate()
                            + ", Priority: " + task.getPriority());
                }
            }
        }).start();
    }

    @Override
    public void onTaskSelectionChanged(List<Task> selectedTasks) {
        deleteTasksButton.setEnabled(!selectedTasks.isEmpty());
    }

    private void deleteSelectedTasks() {
        final List<Task> selectedTasks = taskAdapter.getSelectedTasks();
        if (selectedTasks.isEmpty()) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Integer> taskIds = new ArrayList<>();
                for (Task task : selectedTasks) {
                    taskIds.add(task.getId());
                }
                appDatabase.taskDao().deleteTasks(taskIds);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Selected tasks deleted", Toast.LENGTH_SHORT).show();
                        loadTasks();
                    }
                });
            }
        }).start();
    }
}
