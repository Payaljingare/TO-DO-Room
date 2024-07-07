package com.example.to_doapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks = new ArrayList<>();
    private List<Task> selectedTasks = new ArrayList<>();
    private OnTaskClickListener listener;

    public TaskAdapter(OnTaskClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.titleTextView.setText(task.getTitle());
        holder.descriptionTextView.setText(task.getDescription());
        holder.dueDateTextView.setText(task.getDueDate());
        holder.priorityTextView.setText(task.getPriority());
        holder.checkBox.setOnCheckedChangeListener(null); // Clear previous listeners
        holder.checkBox.setChecked(selectedTasks.contains(task));
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedTasks.add(task);
                } else {
                    selectedTasks.remove(task);
                }
                listener.onTaskSelectionChanged(selectedTasks);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    public List<Task> getSelectedTasks() {
        return selectedTasks;
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView, descriptionTextView, dueDateTextView, priorityTextView;
        private CheckBox checkBox;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            dueDateTextView = itemView.findViewById(R.id.dueDateTextView);
            priorityTextView = itemView.findViewById(R.id.priorityTextView);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }

    public interface OnTaskClickListener {
        void onTaskSelectionChanged(List<Task> selectedTasks);
    }
}
