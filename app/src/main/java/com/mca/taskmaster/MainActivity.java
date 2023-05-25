package com.mca.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mca.taskmaster.activities.AddTaskActivity;
import com.mca.taskmaster.activities.AllTasksActivity;
import com.mca.taskmaster.activities.SettingsActivity;
import com.mca.taskmaster.activities.TaskDetailActivity;
import com.mca.taskmaster.adapter.TaskListRecyclerViewAdapter;
import com.mca.taskmaster.models.Task;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TASK_NAME_EXTRAS_TAG = "taskName";

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    setupSettingsButton();
    setupAddTaskButton();
    setupAllTasksButton();
    }

    @Override
    protected void onResume() {
        super.onResume();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String username = preferences.getString(SettingsActivity.USERNAME_TAG, "");

        if (!username.isEmpty()) {
            String myTasksTitleTextView = username + "'s Tasks";
            ((TextView) findViewById(R.id.my_tasks_title)).setText(myTasksTitleTextView);
        }
    }

    public void setupSettingsButton() {
        ((FloatingActionButton) findViewById(R.id.settingsButton)).setOnClickListener(v -> {
            Intent goToSettingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(goToSettingsIntent);
        });
    }

    public void setupRecyclerView(List<Task> tasks) {
        RecyclerView taskListRecyclerView = (RecyclerView) findViewById(R.id.mainActivityTaskListRecyclerView);

        RecyclerView.LayoutManager taskListLayoutManager = new LinearLayoutManager(this);

        TaskListRecyclerViewAdapter adapter = new TaskListRecyclerViewAdapter();
        taskListRecyclerView.setAdapter(adapter);

        List<Task> taskList = new ArrayList<>();
        taskList.add(new Task("Lab: 28 - RecyclerView", "It's a lab. Fun.", Task.TaskStatus.IN_PROGRESS));
        taskList.add(new Task("Code Challenge: Class 28", "Quick! Sort!", Task.TaskStatus.COMPLETE));
        taskList.add(new Task("Learning Journal: Class 28", "Journal time.", Task.TaskStatus.ASSIGNED));

    }



    public void setupTaskButton(Button goToTaskButton) {
        goToTaskButton.setOnClickListener(v -> {
            Intent goToTaskIntent = new Intent(MainActivity.this, TaskDetailActivity.class);
            String taskName = goToTaskButton.getText().toString();
            goToTaskIntent.putExtra(TASK_NAME_EXTRAS_TAG, taskName);

            startActivity(goToTaskIntent);
        });
    }

    public void setupAddTaskButton() {
        Button goToAddTaskFormButton = (Button) findViewById(R.id.button_main_activity_add_task);

        goToAddTaskFormButton.setOnClickListener(v -> {
            Intent goToAddTaskFormIntent = new Intent(MainActivity.this, AddTaskActivity.class);

            startActivity(goToAddTaskFormIntent);
        });
    }

    public void setupAllTasksButton() {
        Button goToAllTasksButton = (Button) findViewById(R.id.button_main_activity_all_tasks);

        goToAllTasksButton.setOnClickListener(v -> {
            Intent goToAllTasksIntent = new Intent(MainActivity.this, AllTasksActivity.class);

            startActivity(goToAllTasksIntent);
        });
    }
}