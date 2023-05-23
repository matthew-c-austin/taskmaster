package com.mca.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.mca.taskmaster.activities.AddTaskActivity;
import com.mca.taskmaster.activities.AllTasksActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button goToAddTaskFormButton = (Button) findViewById(R.id.add_task_activity_button);

        goToAddTaskFormButton.setOnClickListener(v -> {
            Intent goToAddTaskFormIntent = new Intent(MainActivity.this, AddTaskActivity.class);

            startActivity(goToAddTaskFormIntent);
        });

        Button goToAllTasksButton = (Button) findViewById(R.id.all_tasks_activity_button);

        goToAllTasksButton.setOnClickListener(v -> {
            Intent goToAllTasksIntent = new Intent(MainActivity.this, AllTasksActivity.class);

            startActivity(goToAllTasksIntent);
        });
    }
}