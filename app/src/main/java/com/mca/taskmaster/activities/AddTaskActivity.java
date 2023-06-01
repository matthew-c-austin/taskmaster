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
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.TaskStatus;
import com.amplifyframework.datastore.generated.model.Team;
import com.mca.taskmaster.R;
import com.mca.taskmaster.utils.TaskStatusUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AddTaskActivity extends AppCompatActivity {
    public static final String TAG = "add_task_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        CompletableFuture<List<Team>> teamsFuture = new CompletableFuture<>();
        List<Team> teamList = new ArrayList<>();
        List<String> teamListAsString = new ArrayList<>();
        Spinner taskTeamSpinner = findViewById(R.id.spinner_add_task_team);
        Amplify.API.query(
                ModelQuery.list(Team.class),
                success -> {
                    Log.i(TAG, "Read Teams successfully");
                    for (Team team : success.getData()) {
                        teamList.add(team);
                    }
                    teamsFuture.complete(teamList);
                    runOnUiThread(() -> {
                        for (Team team : teamList)
                            teamListAsString.add(team.getName());
                        taskTeamSpinner.setAdapter(new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_spinner_item,
                                teamListAsString
                        ));
                    });
                },
                failure -> Log.i(TAG, "Failed to read Teams")
        );

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

            String teamString = taskTeamSpinner.getSelectedItem().toString();
            Team team = teamList.stream().filter(e -> e.getName().equals(teamString)).collect(Collectors.toList()).get(0);

            Task newTask = Task.builder()
                    .title(titleEditText.getText().toString())
                    .body(descriptionEditText.getText().toString())
                    .status(newStatus)
                    .team(team)
                    .build();

            Amplify.API.mutate(
                    ModelMutation.create(newTask),
                    success -> Log.i(TAG, "AddTaskActivity.onCreate(): added a task"),
                    failure -> Log.i(TAG, "AddTaskActivity.onCreate(): failed to add a task")
            );

            taskTeamSpinner.setSelection(0);
            ((EditText)findViewById(R.id.my_task_input)).setText("");
            ((EditText)findViewById(R.id.task_description_input)).setText("");
            taskStatusSpinner.setSelection(0);

            titleEditText.requestFocus();

            Toast.makeText(AddTaskActivity.this, "Task saved!", Toast.LENGTH_SHORT).show();

            ((TextView) findViewById(R.id.submitted_text_view)).setText(R.string.submitted_confirmation);
        });
    }
}