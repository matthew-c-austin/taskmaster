package com.mca.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Team;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mca.taskmaster.activities.AddTaskActivity;
import com.mca.taskmaster.activities.AllTasksActivity;
import com.mca.taskmaster.activities.SettingsActivity;
import com.mca.taskmaster.adapter.TaskListRecyclerViewAdapter;
import com.amplifyframework.datastore.generated.model.Task;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "main_activity_tag";
    public static final String TASK_NAME_EXTRAS_TAG = "taskName";
    public static final String TASK_STATUS_EXTRAS_TAG = "taskStatus";
    public static final String TASK_DESCRIPTION_EXTRAS_TAG = "taskDescription";
    List<Task> tasks = new ArrayList<>();
    TaskListRecyclerViewAdapter taskListRecyclerViewAdapter;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hardcoded teams
        Team workTeam =  Team.builder()
                .name("Work")
                .build();
        Amplify.API.mutate(
                ModelMutation.create(workTeam),
                success -> Log.i(TAG, "Added"),
                failure -> Log.i(TAG, "Failed")
        );

        setupTasksFromDatabase();
        setupSettingsButton();
        setupRecyclerView();
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

        setupTasksFromDatabase();
        taskListRecyclerViewAdapter.updateTasksData(tasks);
    }

    public void setupTasksFromDatabase() {
        tasks.clear();
        Amplify.API.query(
                ModelQuery.list(Task.class),
                success -> {
                    Log.i(TAG, "Read products successfully");
                    for (Task task : success.getData())
                        tasks.add(task);
                    runOnUiThread(() -> taskListRecyclerViewAdapter.notifyDataSetChanged());
        },
                failure -> Log.i(TAG, "Failed to read products")
        );

    }

    public void setupSettingsButton() {
        ((FloatingActionButton) findViewById(R.id.settingsButton)).setOnClickListener(v -> {
            Intent goToSettingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(goToSettingsIntent);
        });
    }

    public void setupRecyclerView() {
        RecyclerView taskListRecyclerView = (RecyclerView) findViewById(R.id.mainActivityTaskListRecyclerView);

        RecyclerView.LayoutManager taskListLayoutManager = new LinearLayoutManager(this);
        taskListRecyclerView.setLayoutManager(taskListLayoutManager);

        taskListRecyclerViewAdapter = new TaskListRecyclerViewAdapter(tasks, this);
        taskListRecyclerView.setAdapter(taskListRecyclerViewAdapter);

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