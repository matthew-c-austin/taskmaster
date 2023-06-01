package com.mca.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Team;
import com.mca.taskmaster.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SettingsActivity extends AppCompatActivity {
    public static final String USERNAME_TAG = "username";
    public static final String TEAM_TAG = "team";
    public static final String TAG = "settings_activity";
    Spinner taskTeamSpinner = null;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        populateUsernameEditText(preferences);
        populateTeamSpinner(preferences);
        setupSaveButton(preferences);
    }

    public void populateUsernameEditText(SharedPreferences preferences) {
        String username = preferences.getString(USERNAME_TAG, "");
        ((EditText) findViewById(R.id.usernameEditText)).setText(username);
    }

    public void populateTeamSpinner(SharedPreferences preferences) {
        String teamString = preferences.getString(TEAM_TAG, "");
        CompletableFuture<List<Team>> teamsFuture = new CompletableFuture<>();
        List<Team> teamList = new ArrayList<>();
        List<String> teamListAsString = new ArrayList<>();
        taskTeamSpinner = findViewById(R.id.spinner_settings_team);
        Amplify.API.query(
                ModelQuery.list(Team.class),
                success -> {
                    Log.i(TAG, "Read Teams successfully");
                    for (Team team : success.getData()) {
                        teamList.add(team);
                    }
                    teamsFuture.complete(teamList);
                    runOnUiThread(() -> {
                        teamListAsString.add("All");
                        for (Team team : teamList)
                            teamListAsString.add(team.getName());
                        taskTeamSpinner.setAdapter(new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_spinner_item,
                                teamListAsString
                        ));
                        if (!teamString.isEmpty()) {
                            taskTeamSpinner.setSelection(teamListAsString.indexOf(teamString));
                        }
                    });
                },
                failure -> Log.i(TAG, "Failed to read Teams")
        );
    }

    public void setupSaveButton(SharedPreferences preferences) {
        Button saveButton = findViewById(R.id.button_settings_activity_save);
        saveButton.setOnClickListener(v -> {
            // creating an editor because SharedPreferences is read-only
            SharedPreferences.Editor preferenceEditor = preferences.edit(); // this fails if you declare "preferences" within the onCreate()

            // grabbing the string we want to save from the user input
            EditText usernameEditText = findViewById(R.id.usernameEditText);
            String usernameString = usernameEditText.getText().toString();
            String teamString = taskTeamSpinner.getSelectedItem().toString();

            // save the string to shared preferences
            preferenceEditor.putString(USERNAME_TAG, usernameString);
            preferenceEditor.putString(TEAM_TAG, teamString);
            preferenceEditor.apply(); // Nothing will happen if you don't do this!

            Toast.makeText(SettingsActivity.this, "Settings saved!", Toast.LENGTH_SHORT).show();
        });
    }
}