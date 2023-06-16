package com.mca.taskmaster.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.provider.OpenableColumns;
import android.net.Uri;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mca.taskmaster.R;
import com.mca.taskmaster.utils.TaskStatusUtility;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AddTaskActivity extends AppCompatActivity {
    public static final String TAG = "add_task_tag";
    ActivityResultLauncher<Intent> activityResultLauncher;
    InputStream pickedFileInputStream;
    String pickedFileName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        setupFloatingAddFileButton();
        activityResultLauncher = getActivityResultLauncher();
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

    private void uploadInputStreamToS3(InputStream inputStream, String fileName) {
        Amplify.Storage.uploadInputStream(
                fileName,
                inputStream,
                success -> {
                    Log.i(TAG, "Successfully uploaded. Key: " + success.getKey());
                },
                failure -> {
                    Log.i(TAG, "Could not upload " + fileName + " :" + failure.getMessage());
                }
        );
    }

    private void setupFloatingAddFileButton() {
        FloatingActionButton addFileFloatingButton = findViewById(R.id.floating_action_button_add_task_activity_add_file);
        addFileFloatingButton.setOnClickListener(view -> {
            Intent filePickingIntent = new Intent(Intent.ACTION_GET_CONTENT);
            filePickingIntent.setType("*/*");
            filePickingIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/jpg", "image/png", "image/jpeg"});
            //startActivity(filePickingIntent);
            activityResultLauncher.launch(filePickingIntent);
        });
    }

    private ActivityResultLauncher<Intent> getActivityResultLauncher() {
        ActivityResultLauncher<Intent> pickingIntent = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Uri pickedFileUri = result.getData().getData();
                            try {
                                pickedFileInputStream = getContentResolver().openInputStream(pickedFileUri);
                                pickedFileName = getFileNameFromUri(pickedFileUri);
                                Log.i(TAG, "Successfully took input stream from file. Filename: " + pickedFileName + " Uri: " + pickedFileUri);
                                uploadInputStreamToS3(pickedFileInputStream, pickedFileName);
                            } catch (FileNotFoundException fnfe){
                                Log.e(TAG, "Could not pick file: " + fnfe.getMessage(), fnfe);
                            }
                        } else
                            Log.e(TAG, "Activity result error.");
                    }
                }
        );
        return pickingIntent;
    }

    // Taken from https://stackoverflow.com/a/25005243/16889809
    @SuppressLint("Range")
    public String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}