package com.mca.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mca.taskmaster.R;

public class SettingsActivity extends AppCompatActivity {
    public static final String USERNAME_TAG = "username";

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        populateUsernameEditText(preferences);
        setupSaveButton(preferences);
    }

    public void populateUsernameEditText(SharedPreferences preferences) {
        String username = preferences.getString(USERNAME_TAG, "");
        ((EditText) findViewById(R.id.usernameEditText)).setText(username);
    }

    public void setupSaveButton(SharedPreferences preferences) {
        Button saveButton = findViewById(R.id.button_settings_activity_save);
        saveButton.setOnClickListener(v -> {
            // creating an editor because SharedPreferences is read-only
            SharedPreferences.Editor preferenceEditor = preferences.edit(); // this fails if you declare "preferences" within the onCreate()

            // grabbing the string we want to save from the user input
            EditText usernameEditText = findViewById(R.id.usernameEditText);
            String usernameString = usernameEditText.getText().toString();

            // save the string to shared preferences
            preferenceEditor.putString(USERNAME_TAG, usernameString);
            preferenceEditor.apply(); // Nothing will happen if you don't do this!

            Toast.makeText(SettingsActivity.this, "Settings saved!", Toast.LENGTH_SHORT).show();
        });
    }
}