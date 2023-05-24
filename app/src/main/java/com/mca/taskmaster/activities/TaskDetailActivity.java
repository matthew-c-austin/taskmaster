package com.mca.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.mca.taskmaster.MainActivity;
import com.mca.taskmaster.R;

public class TaskDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        Intent callingIntent = getIntent();
        String taskNameString = null;
        if (callingIntent != null) {
            taskNameString = callingIntent.getStringExtra(MainActivity.TASK_NAME_EXTRAS_TAG);

            TextView taskNameTextView = findViewById(R.id.text_view_task_detail_activity_task_title);

            if (taskNameString != null) {
                taskNameTextView.setText(taskNameString);
            }
        }
    }
}