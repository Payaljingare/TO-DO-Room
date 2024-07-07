package com.example.to_doapp;




import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddEditTaskActivity extends AppCompatActivity {

    private EditText titleEditText, descriptionEditText, dueDateEditText;
    private Spinner prioritySpinner;
    private Button saveButton;
    private AppDatabase appDatabase;
    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        dueDateEditText = findViewById(R.id.dueDateEditText);
        prioritySpinner = findViewById(R.id.prioritySpinner);
        saveButton = findViewById(R.id.saveButton);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.priority_levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(adapter);

        appDatabase = AppDatabase.getInstance(this);

        final int taskId = getIntent().getIntExtra("taskId", -1);
        if (taskId != -1) {
            loadTask(taskId);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTask(taskId);
            }
        });
    }

    private void loadTask(final int taskId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                task = appDatabase.taskDao().getTaskById(taskId);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (task != null) {
                            titleEditText.setText(task.getTitle());
                            descriptionEditText.setText(task.getDescription());
                            dueDateEditText.setText(task.getDueDate());
                            prioritySpinner.setSelection(getPriorityIndex(task.getPriority()));
                        }
                    }
                });
            }
        }).start();
    }

    private void saveTask(final int taskId) {
        final String title = titleEditText.getText().toString();
        final String description = descriptionEditText.getText().toString();
        final String dueDate = dueDateEditText.getText().toString();
        final String priority = prioritySpinner.getSelectedItem().toString();

        if (title.isEmpty() || description.isEmpty() || dueDate.isEmpty() || priority.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (taskId == -1) {
                    task = new Task();
                }
                task.setTitle(title);
                task.setDescription(description);
                task.setDueDate(dueDate);
                task.setPriority(priority);

                if (taskId == -1) {
                    appDatabase.taskDao().insert(task);
                } else {
                    appDatabase.taskDao().update(task);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddEditTaskActivity.this, "Task saved", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                });
            }
        }).start();
    }

    private int getPriorityIndex(String priority) {
        switch (priority) {
            case "High":
                return 0;
            case "Medium":
                return 1;
            case "Low":
                return 2;
            default:
                return 0;
        }
    }
}


