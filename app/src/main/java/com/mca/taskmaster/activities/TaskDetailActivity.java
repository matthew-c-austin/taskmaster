package com.mca.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.TaskStatus;
import com.mca.taskmaster.MainActivity;
import com.mca.taskmaster.R;
import com.mca.taskmaster.utils.TaskStatusUtility;

import java.io.File;

public class TaskDetailActivity extends AppCompatActivity {
    public static final String TAG = "task_detail_activity_tag";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        Intent callingIntent = getIntent();
        String taskNameString = null;
        String taskAttachmentKeyString = null;
        String taskDescriptionString = null;
        String taskStatusString = null;
        TaskStatus status = null;

        if (callingIntent != null) {
            taskNameString = callingIntent.getStringExtra(MainActivity.TASK_NAME_EXTRAS_TAG);

            TextView taskNameTextView = findViewById(R.id.text_view_task_detail_activity_task_title);

            if (taskNameString != null) {
                taskNameTextView.setText(taskNameString);
            }

            taskAttachmentKeyString = callingIntent.getStringExtra(MainActivity.TASK_ATTACHMENT_EXTRA_TAG);

            if (taskAttachmentKeyString != null && !taskAttachmentKeyString.isEmpty()) {
                 Amplify.Storage.downloadFile(
                        taskAttachmentKeyString,
                        new File(getApplication().getFilesDir(), taskAttachmentKeyString),
                        success -> {
                            ImageView taskImageView = findViewById(R.id.image_view_task_detail_attachment);
                            taskImageView.setImageBitmap(BitmapFactory.decodeFile(success.getFile().getPath()));
                        },
                        failure -> {
                            Log.e(TAG, "Unable to get image with S3 key because " + failure.getMessage());
                        }
                );
            }

            taskStatusString = callingIntent.getStringExtra(MainActivity.TASK_STATUS_EXTRAS_TAG);
            status = TaskStatus.valueOf(taskStatusString);

            TextView taskStatusTextView = findViewById(R.id.taskStatusTextView);

            if (taskStatusString != null) {
                taskStatusTextView.setText(TaskStatusUtility.taskStatusToString(status));
            }

            taskDescriptionString = callingIntent.getStringExtra(MainActivity.TASK_DESCRIPTION_EXTRAS_TAG);

            TextView taskDetailsTextView = findViewById(R.id.taskDetailTextView);

            if (taskDescriptionString != null) {
                taskDetailsTextView.setText(taskDescriptionString);
            }

        }
    }
}