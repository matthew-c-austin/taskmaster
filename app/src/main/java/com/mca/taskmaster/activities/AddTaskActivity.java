package com.mca.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.TaskStatus;
import com.mca.taskmaster.R;
import com.mca.taskmaster.utils.TaskStatusUtility;

import java.util.List;

public class AddTaskActivity extends AppCompatActivity {
    public static final String TAG = "add_task_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        List<String> statusList = TaskStatusUtility.getTaskStatusList();

        Spinner taskStatusSpinner = findViewById(R.id.spinner_add_task_status);
        taskStatusSpinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                statusList
        ));

        Button addTaskButton = (Button) findViewById(R.id.button_add_task_activity_add_task);

        addTaskButton.setOnClickListener(v -> {
            EditText titleEditText = findViewById(R.id.my_task_input);
            EditText descriptionEditText = findViewById(R.id.task_description_input);

            TaskStatus newStatus = TaskStatusUtility.taskStatusFromString(taskStatusSpinner.getSelectedItem().toString());

            Task newTask = Task.builder()
                    .title(titleEditText.getText().toString())
                    .body(descriptionEditText.getText().toString())
                    .status(newStatus)
                    .build();

            Amplify.API.mutate(
                    ModelMutation.create(newTask),
                    success -> Log.i(TAG, "AddTaskActivity.onCreate(): added a task"),
                    failure -> Log.i(TAG, "AddTaskActivity.onCreate(): failed to add a task")
            );

            ((EditText)findViewById(R.id.my_task_input)).setText("");
            ((EditText)findViewById(R.id.task_description_input)).setText("");
            taskStatusSpinner.setSelection(0);

            titleEditText.requestFocus();

            Toast.makeText(AddTaskActivity.this, "Task saved!", Toast.LENGTH_SHORT).show();

            ((TextView) findViewById(R.id.submitted_text_view)).setText(R.string.submitted_confirmation);
        });
    }
}