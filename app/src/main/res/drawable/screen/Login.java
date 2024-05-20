package dev.sudhanshu.fininfocom.screen;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import dev.sudhanshu.fininfocom.PreSaveUser;
import dev.sudhanshu.fininfocom.databinding.LoginLayoutBinding;
import dev.sudhanshu.fininfocom.models.User;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class Login extends AppCompatActivity {

    LoginLayoutBinding binding ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Realm
        Realm.init(this);

        // Define Realm configuration
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("myrealm.realm")
                .schemaVersion(1)
                .build();
        Realm.setDefaultConfiguration(config);

        // Pre-save the user
        PreSaveUser.preSaveUser();

        // handling view click
        listener();

    }

    private void listener() {
        binding.Login.setOnClickListener(view -> {
            validateInput();
        });

        binding.password.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String password = s.toString();
                if (password.isEmpty()) {
                    // If password is empty, clearing the strength text
                    binding.passwordStrength.setText("");
                } else {
                    // If password is not empty, updating UI based on password strength
                    PasswordStrength strength = getPasswordStrength(password);
                    switch (strength) {
                        case WEAK:
                            binding.passwordStrength.setText("Weak password");
                            break;
                        case STRONG:
                            binding.passwordStrength.setText("Strong password");
                            break;
                        case VERY_STRONG:
                            binding.passwordStrength.setText("Very strong password");
                            break;
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });


    }

    // validating input fields
    private void validateInput() {
        if (binding.email.getText().toString().trim().isEmpty()) {
            binding.email.requestFocus();
            binding.email.setError("Please enter your email");
        }else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.email.getText().toString().trim()).matches()) {
            binding.email.requestFocus();
            binding.email.setError("Please enter a valid email address");
        }else if (binding.password.getText().toString().trim().isEmpty()) {
            binding.password.requestFocus();
            binding.password.setError("Please enter your password");
        }else {
            verifyCredentialAndLogIn();
        }

    }

    // verifying pre saved user credential from realm db
    private void verifyCredentialAndLogIn() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                Realm realm = Realm.getDefaultInstance();
                User user = realm.where(User.class).equalTo("username", binding.email.getText().toString().trim()).findFirst();
                boolean loginSuccess = user != null && user.getPassword().equals(binding.password.getText().toString().trim());
                realm.close();
                return loginSuccess;
            }

            @Override
            protected void onPostExecute(Boolean loginSuccess) {
                if (loginSuccess) {
                    moveToHome();
                } else {
                    Toast.makeText(Login.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    // moving to home after login success
    private void moveToHome() {

        Intent intent = new Intent(this, Home.class);
        List<Pair<View, String>> pairs = new ArrayList<>();
        pairs.add(new android.util.Pair<>(binding.appLogo, "titleTransition"));
        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(this, pairs.toArray(new android.util.Pair[0]));
        startActivity(intent, activityOptions.toBundle());

    }


    // password strength indicator
    public PasswordStrength getPasswordStrength(String password) {
        boolean containsAlphabet = false;
        boolean containsNumber = false;
        boolean containsSpecialCharacter = false;

        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                containsAlphabet = true;
            } else if (Character.isDigit(c)) {
                containsNumber = true;
            } else if (!Character.isLetterOrDigit(c)) {
                containsSpecialCharacter = true;
            }
        }

        if (containsAlphabet && containsNumber && containsSpecialCharacter && password.length() > 6) {
            return PasswordStrength.VERY_STRONG;
        } else if ((containsAlphabet && containsNumber) || (containsAlphabet && containsSpecialCharacter)) {
            return PasswordStrength.STRONG;
        } else {
            return PasswordStrength.WEAK;
        }
    }

    public enum PasswordStrength {
        WEAK,
        STRONG,
        VERY_STRONG
    }

}