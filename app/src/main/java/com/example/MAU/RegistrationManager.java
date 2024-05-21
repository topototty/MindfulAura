package com.example.MAU;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.MAU.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

public class RegistrationManager extends AppCompatActivity {
    EditText emailEditText, passwordEditText, loginEditText;
    Button authButton, chooseImageButton;
    ProgressBar progressBar;
    TextView clickToLogin;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference userRef = db.collection("users");

    private static final Pattern LOGIN_PATTERN = Pattern.compile("^(?=.*[A-Z])[A-Za-z]{6,18}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[!@#$%^&*])[A-Za-z0-9!@#$%^&*]{8,}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginEditText = findViewById(R.id.loginEditText);
        progressBar = findViewById(R.id.progressBar);
        authButton = findViewById(R.id.authButton);
        clickToLogin = findViewById(R.id.clickToLogin);

        firebaseAuth = FirebaseAuth.getInstance();

        clickToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginManager.class);
                startActivity(intent);
                finish();
            }
        });

        authButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String login = loginEditText.getText().toString().trim();

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEditText.setError("Неверный формат адреса электронной почты");
                    progressBar.setVisibility(View.GONE);
                } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
                    passwordEditText.setError("Пароль должен быть не менее 8 символов, содержать хотя бы одну цифру и один спецсимвол");
                    progressBar.setVisibility(View.GONE);
                } else if (!LOGIN_PATTERN.matcher(login).matches()) {
                    loginEditText.setError("Логин должен быть не менее 6 и не более 18 символов, содержать только латинские буквы и хотя бы одну заглавную букву");
                    progressBar.setVisibility(View.GONE);
                } else {
                    registrationUser(email, password, login);
                }
            }
        });
    }

    private void registrationUser(String email, String password, String displayName) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(displayName)
                                .build();

                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            User newUser = new User(user.getEmail(), user.getUid(), displayName, null);
                                            userRef.add(newUser).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            progressBar.setVisibility(View.GONE);
                                                            Toast.makeText(RegistrationManager.this, "Пользователь " + user.getEmail() + " зарегистрирован!", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    })

                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            progressBar.setVisibility(View.GONE);
                                                            Toast.makeText(RegistrationManager.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(RegistrationManager.this, "Ошибка установки имени пользователя", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(RegistrationManager.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
