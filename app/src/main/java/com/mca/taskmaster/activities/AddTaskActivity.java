package com.mca.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.mca.taskmaster.R;

public class AddTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Button addTaskButton = (Button) findViewById(R.id.add_task_button);

        addTaskButton.setOnClickListener(v -> {
            ((TextView) findViewById(R.id.submitted_text_view)).setText(R.string.submitted_confirmation);
        });
    }
}