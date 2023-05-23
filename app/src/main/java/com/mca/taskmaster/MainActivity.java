package com.mca.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mca.taskmaster.activities.AddTaskActivity;
import com.mca.taskmaster.activities.AllTasksActivity;
import com.mca.taskmaster.activities.SettingsActivity;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

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

    public void setupAddTaskButton() {
        Button goToAddTaskFormButton = (Button) findViewById(R.id.add_task_activity_button);

        goToAddTaskFormButton.setOnClickListener(v -> {
            Intent goToAddTaskFormIntent = new Intent(MainActivity.this, AddTaskActivity.class);

            startActivity(goToAddTaskFormIntent);
        });
    }

    public void setupAllTasksButton() {
        Button goToAllTasksButton = (Button) findViewById(R.id.all_tasks_activity_button);

        goToAllTasksButton.setOnClickListener(v -> {
            Intent goToAllTasksIntent = new Intent(MainActivity.this, AllTasksActivity.class);

            startActivity(goToAllTasksIntent);
        });
    }
}